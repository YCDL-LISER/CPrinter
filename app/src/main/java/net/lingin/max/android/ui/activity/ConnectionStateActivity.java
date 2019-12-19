package net.lingin.max.android.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import net.lingin.max.android.R;
import net.lingin.max.android.net.io.ClassicBluetoothSocket;
import net.lingin.max.android.service.BluetoothClientFactory;
import net.lingin.max.android.ui.base.BaseActivity;
import net.lingin.max.android.ui.listener.BTConnectStatusListener;
import net.lingin.max.android.utils.ToastUtils;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

public class ConnectionStateActivity extends BaseActivity {

    private static final String TAG = ConnectionStateActivity.class.getName();

    public static final String EXTRA_DEVICE_ADDRESS = "address";

    public static final int REQUEST_ENABLE_BT = 2;

    private BluetoothClient mClient;

    @BindView(R.id.blueToothGroupListView)
    QMUIGroupListView blueToothGroupListView;

    @BindView(R.id.bluetoothScan)
    Button bluetoothScan;

    private QMUIGroupListView.Section pairedSection;

    private QMUIGroupListView.Section searchedSection;

    private SearchRequest searchRequest;

    private SearchResponse searchResponse;

    private Set<String> removeDuplication = new HashSet<>(16);

    private ClassicBluetoothSocket classicBluetoothSocket;

    private BluetoothStateListener bluetoothStateListener;

    private BluetoothBondListener bluetoothBondListener;

    private BTConnectStatusListener btConnectStatusListener;

    private BleConnectOptions bleConnectOptions;

    private BleConnectResponse bleConnectResponse;

    private View.OnClickListener pairedClickListener;

    private View.OnClickListener searchedClickListener;

    @Override
    protected int onLayout() {
        return R.layout.activity_bluetooth_connect;
    }

    @Override
    protected void onObject() {
        // 申请权限
        requestSelfPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, (authorize, permissions) -> {
            Log.i(TAG, permissions.toString());
        });

        mClient = BluetoothClientFactory.getBluetoothClient(this);

        // 初始化Section对象
        initSection();

        searchRequest = new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000, 3)   // 先扫BLE设备3次，每次3s
                .searchBluetoothClassicDevice(3000, 3) // 再扫经典蓝牙5s
//                .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
                .build();

        searchResponse = new SearchResponse() {
            @Override
            public void onSearchStarted() {
                bluetoothScan.setText("停止");
            }

            @Override
            public void onDeviceFounded(SearchResult searchResult) {
                boolean isNotDuplication = removeDuplication.add(searchResult.getAddress());
                if (isNotDuplication) {
                    addListItemView(searchedSection, searchResult.device, searchedClickListener);
                    addToListView(searchedSection);
                }
            }

            @Override
            public void onSearchStopped() {
                bluetoothScan.setText("扫描");
            }

            @Override
            public void onSearchCanceled() {
                bluetoothScan.setText("扫描");
            }
        };

        bluetoothStateListener = new BluetoothStateListener() {
            @Override
            public void onBluetoothStateChanged(boolean openOrClosed) {
                Log.i(TAG, "蓝牙状态切换：" + openOrClosed);
            }
        };

        bluetoothBondListener = new BluetoothBondListener() {
            @Override
            public void onBondStateChanged(String mac, int bondState) {
                // bondState = Constants.BOND_NONE, BOND_BONDING, BOND_BONDED
            }
        };

        initBleConnectResponse();

        initBleConnectStatusListener();

        bleConnectOptions = new BleConnectOptions.Builder()
                .setConnectRetry(3)   // 连接如果失败重试3次
                .setConnectTimeout(30000)   // 连接超时30s
                .setServiceDiscoverRetry(3)  // 发现服务如果失败重试3次
                .setServiceDiscoverTimeout(20000)  // 发现服务超时20s
                .build();

        initBleConnectResponse();

        initPairedClickListener();

        initSearchedClickListener();
    }

    @Override
    protected void onView() {

    }

    @Override
    protected void onData() {
        // 检查蓝牙
        checkBluetooth();

        // 注册蓝牙监听器
        registerBluetoothMonitor();

        // 搜索蓝牙设备
        searchBluetoothDevices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClient.unregisterBluetoothStateListener(bluetoothStateListener);
        mClient.unregisterBluetoothBondListener(bluetoothBondListener);
        classicBluetoothSocket.closePort();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                // 蓝牙已打开
            } else {
                // 蓝牙未打开
                ToastUtils.show(R.string.bluetooth_is_not_enabled);
            }
        }
    }

    /**
     * 扫描
     *
     * @param v 视图
     */
    @OnClick(R.id.bluetoothScan)
    public void onScanClick(View v) {
        CharSequence scanText = bluetoothScan.getText();
        if ("扫描".contentEquals(scanText)) {
            checkBluetooth();
            searchBluetoothDevices();
        } else if ("停止".contentEquals(scanText)) {
            stopSearchBluetoothDevices();
        }
    }

    private void checkBluetooth() {
        if (!mClient.isBleSupported()) {
            ToastUtils.show("设备不支持蓝牙");
        } else {
            if (!mClient.isBluetoothOpened()) {
                mClient.openBluetooth();
            }
        }
    }

    private void registerBluetoothMonitor() {
        mClient.registerBluetoothStateListener(bluetoothStateListener);
        mClient.registerBluetoothBondListener(bluetoothBondListener);
    }

    private void initSection() {
        pairedSection = QMUIGroupListView.newSection(this)
                .setTitle("已连接设备");
        searchedSection = QMUIGroupListView.newSection(this)
                .setTitle("已搜索设备");
    }

    private void searchBluetoothDevices() {
        mClient.search(searchRequest, searchResponse);
    }

    private void stopSearchBluetoothDevices() {
        mClient.stopSearch();
    }

    private void addListItemView(QMUIGroupListView.Section section, BluetoothDevice bluetoothDevice, View.OnClickListener onClickListener) {
        Log.i(TAG, "蓝牙名称：" + bluetoothDevice.getName() + " 蓝牙地址：" + bluetoothDevice.getAddress());
        QMUICommonListItemView listItemView = blueToothGroupListView.createItemView(bluetoothDevice.getName());
        listItemView.setDetailText(bluetoothDevice.getAddress());
        listItemView.setOrientation(QMUICommonListItemView.VERTICAL);
        section.addItemView(listItemView, onClickListener);
    }

    private void addToListView(QMUIGroupListView.Section section) {
        section.removeFrom(blueToothGroupListView);
        section.addTo(blueToothGroupListView);
    }

    private void initPairedClickListener() {
        pairedClickListener = view -> {
            // 连接状态
            if (view instanceof QMUICommonListItemView) {
                QMUICommonListItemView qmuiView = (QMUICommonListItemView) view;
                CharSequence address = qmuiView.getDetailText();
                Log.i(TAG, "选择已连接蓝牙：" + address);
            }
        };
    }

    private void initSearchedClickListener() {
        searchedClickListener = view -> {
            // 连接状态
            if (view instanceof QMUICommonListItemView) {
                QMUICommonListItemView qmuiView = (QMUICommonListItemView) view;
                CharSequence address = qmuiView.getDetailText();
                Log.i(TAG, "选择未连接蓝牙：" + address);

                // 蓝牙连接
                checkBluetoothState(address.toString());
                connectBluetooth(address.toString(), btConnectStatusListener);

                /*Intent intent = new Intent();
                intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
                setResult(Activity.RESULT_OK, intent);
                finish();*/
            }
        };
    }

    private void checkBluetoothState(String mac) {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.i(TAG, "本机不支持低功耗蓝牙");
        }
        int bondState = mClient.getBondState(mac);
        Log.i(TAG, "绑定状态：" + bondState);
        int connectStatus = mClient.getConnectStatus(mac);
        Log.i(TAG, "连接状态：" + connectStatus);
    }

    private void connectBluetooth(String mac, BTConnectStatusListener btConnectStatusListener) {
        /*if (classicBluetoothSocket != null) {
            if (!classicBluetoothSocket.isConnected()) {
                Log.i(TAG, "蓝牙正在连接...无需重新连接：");
                return;
            }
        }*/

        classicBluetoothSocket = new ClassicBluetoothSocket(BluetoothUtils.getRemoteDevice(mac), btConnectStatusListener);
        classicBluetoothSocket.openPort();

    }

    private void initBleConnectResponse() {
        bleConnectResponse = (code, data) -> {
            if (data == null) {
                Log.i(TAG, "蓝牙连接状态：" + code + " ");
                return;
            }
            if (Constants.REQUEST_SUCCESS == code) {
                Log.i(TAG, "蓝牙连接响应：" + code + " " + data.toString());

                /*data.getServices()
                        .stream()
                        .flatMap(bleGattService -> {
                            bleGattService.getCharacters()
                                    .stream())
                        });*/
            }
        };
    }

    private void initBleConnectStatusListener() {
        btConnectStatusListener = new BTConnectStatusListener() {
            @Override
            public void onConnectStatusChanged(BluetoothDevice bluetoothDevice, int status) {
                Log.i(TAG, "蓝牙连接监听：" + bluetoothDevice.getName() + " " + bluetoothDevice.getAddress() + " " + status);
                switch (status) {
                    case Constants.STATUS_DEVICE_CONNECTED:
                        addListItemView(pairedSection, bluetoothDevice, pairedClickListener);
                        addToListView(pairedSection);
                        break;
                    default:
                        break;
                }
            }
        };
    }
}
