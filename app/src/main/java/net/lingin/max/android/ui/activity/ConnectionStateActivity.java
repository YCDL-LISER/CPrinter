/*
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
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import net.lingin.max.android.R;
import net.lingin.max.android.service.BluetoothClientFactory;
import net.lingin.max.android.service.ClassicBluetoothClient;
import net.lingin.max.android.ui.base.BaseActivity;
import net.lingin.max.android.ui.listener.BTConnectStatusListener;
import net.lingin.max.android.utils.ToastUtils;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

public class ConnectionStateActivity extends BaseActivity {

    private static final String TAG = ConnectionStateActivity.class.getName();

    public static final int REQUEST_ENABLE_BT = 2;

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.blueToothGroupListView)
    QMUIGroupListView blueToothGroupListView;

    @BindView(R.id.bluetoothScan)
    Button bluetoothScan;

    private QMUIGroupListView.Section pairedSection;

    private QMUIGroupListView.Section searchedSection;

    private SearchRequest searchRequest;

    private SearchResponse searchResponse;

    private Set<String> removeDuplication = new HashSet<>(16);

    private BluetoothClient bluetoothClient;

    private ClassicBluetoothClient classicBluetoothClient;

    private BluetoothStateListener bluetoothStateListener;

    private BluetoothBondListener bluetoothBondListener;

    private BTConnectStatusListener btConnectStatusListener;

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

        bluetoothClient = BluetoothClientFactory.getBluetoothClient(this);

        // 初始化Section对象
        initSection();

        searchRequest = new SearchRequest.Builder()
                .searchBluetoothClassicDevice(3000, 3) // 先扫经典蓝牙设备3次，每次3s
                .searchBluetoothClassicDevice(2000) // 再扫BLE设备2s
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

        initBleConnectStatusListener();

        initPairedClickListener();

        initSearchedClickListener();
    }

    @Override
    protected void onView() {
        initTopBar();
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
        bluetoothClient.unregisterBluetoothStateListener(bluetoothStateListener);
        bluetoothClient.unregisterBluetoothBondListener(bluetoothBondListener);
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

    */
/**
     * 扫描
     *
     * @param v 视图
     *//*

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
        if (!bluetoothClient.isBleSupported()) {
            ToastUtils.show("设备不支持蓝牙");
        } else {
            if (!bluetoothClient.isBluetoothOpened()) {
                bluetoothClient.openBluetooth();
            }
        }
    }

    private void registerBluetoothMonitor() {
        bluetoothClient.registerBluetoothStateListener(bluetoothStateListener);
        bluetoothClient.registerBluetoothBondListener(bluetoothBondListener);
    }

    private void initSection() {
        pairedSection = QMUIGroupListView.newSection(this)
                .setTitle("已连接设备");
        searchedSection = QMUIGroupListView.newSection(this)
                .setTitle("已搜索设备");
    }

    private void searchBluetoothDevices() {
        bluetoothClient.search(searchRequest, searchResponse);
    }

    private void stopSearchBluetoothDevices() {
        bluetoothClient.stopSearch();
    }

    private void initTopBar() {
        mTopBar.addLeftBackImageButton().setOnClickListener(view -> {
            finish();
            overridePendingTransition(R.anim.slide_still, R.anim.slide_out_right);
        });
        mTopBar.setTitle("连接设备");
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
            }
        };
    }

    private void initSearchedClickListener() {
        searchedClickListener = view -> {
            // 连接状态
            if (view instanceof QMUICommonListItemView) {
                QMUICommonListItemView qmuiView = (QMUICommonListItemView) view;
                CharSequence address = qmuiView.getDetailText();

                // 蓝牙连接
                checkBluetoothState(address.toString());
                connectBluetooth(address.toString(), btConnectStatusListener);

                */
/*Intent intent = new Intent();
                intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
                setResult(Activity.RESULT_OK, intent);
                finish();*//*

            }
        };
    }

    private void checkBluetoothState(String mac) {
        int bondState = bluetoothClient.getBondState(mac);
        Log.i(TAG, "绑定状态：" + bondState);
        int connectStatus = bluetoothClient.getConnectStatus(mac);
        Log.i(TAG, "连接状态：" + connectStatus);
    }

    private void connectBluetooth(String mac, BTConnectStatusListener btConnectStatusListener) {
        classicBluetoothClient = BluetoothClientFactory.getClassicBluetoothClient(mac, btConnectStatusListener);
        classicBluetoothClient.connect();
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
                        ToastUtils.show("蓝牙连接成功");
                        break;
                    default:
                        break;
                }
            }
        };
    }
}
*/
