package net.lingin.max.android.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.Constants;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.receiver.listener.BluetoothBondListener;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.qmuiteam.qmui.widget.QMUILoadingView;
import com.qmuiteam.qmui.widget.QMUITopBarLayout;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListSectionHeaderFooterView;

import net.lingin.max.android.R;
import net.lingin.max.android.model.BluetoothDeviceDTO;
import net.lingin.max.android.service.BluetoothClientFactory;
import net.lingin.max.android.service.ClassicBluetoothClient;
import net.lingin.max.android.ui.adapter.BluetoothLinearItemAdapter;
import net.lingin.max.android.ui.base.BaseActivity;
import net.lingin.max.android.ui.listener.BTConnectStatusListener;
import net.lingin.max.android.utils.ToastUtils;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BluetoothConnectActivity extends BaseActivity {

    private static final String TAG = BluetoothConnectActivity.class.getName();

    public static final int REQUEST_ENABLE_BT = 2;

    @BindView(R.id.topbar)
    QMUITopBarLayout mTopBar;

    @BindView(R.id.shfv_connected)
    QMUIGroupListSectionHeaderFooterView connectedSectionHeaderFooter;

    @BindView(R.id.rv_connected)
    RecyclerView connectedRecyclerView;

    @BindView(R.id.shfv_searched)
    QMUIGroupListSectionHeaderFooterView searchedSectionHeaderFooter;

    @BindView(R.id.rv_searched)
    RecyclerView searchedRecyclerView;

    @BindView(R.id.bluetoothScan)
    Button bluetoothScan;

    private Set<String> removeDuplication = new HashSet<>(16);

    private BluetoothClient bluetoothClient;

    private ClassicBluetoothClient classicBluetoothClient;

    private BluetoothLinearItemAdapter connectedItemAdapter;

    private BluetoothLinearItemAdapter searchedItemAdapter;

    private BluetoothStateListener bluetoothStateListener;

    private BluetoothBondListener bluetoothBondListener;

    private BTConnectStatusListener btConnectStatusListener;

    private BluetoothLinearItemAdapter.OnItemClickListener connectedClickListener;

    private BluetoothLinearItemAdapter.OnItemClickListener searchedClickListener;

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

        classicBluetoothClient = BluetoothClientFactory.getClassicBluetoothClient();

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
        initSectionHeaderFooter();
        initRecyclerView();
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

    private void searchBluetoothDevices() {
        bluetoothClient.search(new SearchRequest.Builder()
                        .searchBluetoothClassicDevice(3000, 3) // 先扫经典蓝牙设备3次，每次3s
                        .searchBluetoothClassicDevice(2000) // 再扫BLE设备2s
                        .build()
                , new SearchResponse() {
                    @Override
                    public void onSearchStarted() {
                        bluetoothScan.setText("停止");
                        if (searchedItemAdapter.getItemCount() == 0) {
//                            BluetoothDeviceDTO searchedDTO = new BluetoothDeviceDTO("未搜索到蓝牙", "");
//                            searchedItemAdapter.addBluetooth(searchedDTO);
                        }
                    }

                    @SuppressLint("CheckResult")
                    @Override
                    public void onDeviceFounded(SearchResult searchResult) {
                        boolean isNotDuplication = removeDuplication.add(searchResult.getAddress());
                        if (isNotDuplication) {
                            BluetoothDeviceDTO searchedDTO = new BluetoothDeviceDTO(searchResult.getName(), searchResult.getAddress());
                            searchedItemAdapter.addBluetooth(searchedDTO);
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
                });
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

    private void initSectionHeaderFooter() {
        connectedSectionHeaderFooter.setText("已连接设备");
        searchedSectionHeaderFooter.setText("已搜索设备");
    }

    @SuppressLint("CheckResult")
    private void initRecyclerView() {
        VirtualLayoutManager connectedManager = new VirtualLayoutManager(this);
        LayoutHelper connectedHelper = new LinearLayoutHelper();
        connectedRecyclerView.setLayoutManager(connectedManager);
        // 分割线
        connectedRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        connectedItemAdapter = new BluetoothLinearItemAdapter(this, connectedHelper);
        connectedItemAdapter.addBluetooth(addConnectedDevice());
        connectedItemAdapter.addOnClickListener(connectedClickListener);
        connectedRecyclerView.setAdapter(connectedItemAdapter);

        VirtualLayoutManager searchedManager = new VirtualLayoutManager(this);
        LayoutHelper searchedHelper = new LinearLayoutHelper();
        searchedRecyclerView.setLayoutManager(searchedManager);
        // 分割线
        searchedRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        searchedItemAdapter = new BluetoothLinearItemAdapter(this, searchedHelper);
        // 设置点击监听
        searchedItemAdapter.addOnClickListener(searchedClickListener);
        searchedRecyclerView.setAdapter(searchedItemAdapter);
    }

    private BluetoothDeviceDTO addConnectedDevice() {
        BluetoothDevice bluetoothDevice = classicBluetoothClient.getConnectedBluetoothDevice();
        if (bluetoothDevice != null) {
            removeDuplication.add(bluetoothDevice.getAddress());
            return new BluetoothDeviceDTO(bluetoothDevice.getName(), bluetoothDevice.getAddress());
        }
        return new BluetoothDeviceDTO("设备未连接", "");
    }

    private void initPairedClickListener() {
        connectedClickListener = (view, postion) -> {
            // 连接状态
            if (view instanceof QMUICommonListItemView) {
                QMUICommonListItemView qmuiView = (QMUICommonListItemView) view;
                CharSequence address = qmuiView.getDetailText();
                // 断开连接
                classicBluetoothClient.disconnect();
            }
        };
    }

    @SuppressLint("CheckResult")
    private void initSearchedClickListener() {
        searchedClickListener = (view, postion) -> {
            // 连接状态
            if (view instanceof QMUICommonListItemView) {
                QMUICommonListItemView qmuiView = (QMUICommonListItemView) view;
                CharSequence address = qmuiView.getDetailText();

                // 设置Item样式
                qmuiView.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CUSTOM);
                qmuiView.addAccessoryCustomView(new QMUILoadingView(this));

                Observable.fromCallable(() -> {
                    btConnectStatusListener.setPostion(postion);
                    connectBluetooth(address.toString(), btConnectStatusListener);
                    return "订阅";
                }).compose(this.bindToLifecycle())
                        .subscribeOn(Schedulers.single())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(s -> {
                            qmuiView.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_NONE);
                            qmuiView.addAccessoryCustomView(new QMUILoadingView(this));
                        });
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
        BluetoothDevice remoteDevice = BluetoothClientFactory.getRemoteDevice(mac);
        if (remoteDevice != null) {
            classicBluetoothClient.connect(remoteDevice, btConnectStatusListener);
        }
    }

    private void initBleConnectStatusListener() {
        btConnectStatusListener = new BTConnectStatusListener() {

            @Override
            public void onConnectStatusChanged(BluetoothDevice bluetoothDevice, int status, int postion) {
                Log.i(TAG, "蓝牙连接监听：" + bluetoothDevice.getName() + " " + bluetoothDevice.getAddress() + " " + status);
                switch (status) {
                    case Constants.STATUS_DEVICE_CONNECTED:
                        searchedItemAdapter.deleteBluetooth(postion);
                        connectedItemAdapter.initBluetoothes();
                        connectedItemAdapter.addBluetooth(new BluetoothDeviceDTO(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                        ToastUtils.show("设备连接成功");
                        break;
                    case Constants.STATUS_DEVICE_DISCONNECTED:
                        ToastUtils.show("设备连接失败");
                        break;
                    default:
                        break;
                }
            }
        };
    }

/*    private void registerBoradcastReceiver() {
        //注册监听
        IntentFilter stateChangeFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        IntentFilter connectedFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter disConnectedFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(stateChangeReceiver, stateChangeFilter);
        registerReceiver(stateChangeReceiver, connectedFilter);
        registerReceiver(stateChangeReceiver, disConnectedFilter);
    }

    private void unRegisterBoradcastReceiver() {
        unregisterReceiver(stateChangeReceiver);
    }

    private BroadcastReceiver stateChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                //连接上了
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i(TAG, "设备已连接：" + device.getName() + " " + device.getAddress());
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //蓝牙连接被切断
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i(TAG, "设备已连接已断开：" + device.getName() + " " + device.getAddress());
            }
        }
    };*/
}
