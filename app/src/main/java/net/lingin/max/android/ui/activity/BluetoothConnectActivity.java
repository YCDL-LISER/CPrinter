package net.lingin.max.android.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.BluetoothLog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;

import net.lingin.max.android.R;
import net.lingin.max.android.service.BluetoothClientFactory;
import net.lingin.max.android.ui.base.BaseActivity;
import net.lingin.max.android.utils.ToastUtils;

import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

public class BluetoothConnectActivity extends BaseActivity {

    private static final String TAG = BluetoothConnectActivity.class.getName();

    public static final String EXTRA_DEVICE_ADDRESS = "address";

    public static final int REQUEST_ENABLE_BT = 2;

    public static final int REQUEST_CONNECT_DEVICE = 3;

    private BluetoothClient mClient;

    @BindView(R.id.blueToothGroupListView)
    QMUIGroupListView blueToothGroupListView;

    @BindView(R.id.bluetoothScan)
    Button bluetoothScan;

    private QMUIGroupListView.Section pairedSection;

    private QMUIGroupListView.Section searchedSection;

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected int onLayout() {
        return R.layout.activity_bluetooth_connect;
    }

    @Override
    protected void onObject() {
        requestSelfPermission(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, (authorize, permissions) -> {
            Log.i(TAG, permissions.toString());
        });

        mClient = BluetoothClientFactory.getBluetoothClient(this);
        scanBluetoothDevices();

        // 发现设备时注册广播
        IntentFilter findingFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mFindBlueToothReceiver, findingFilter);

        // 发现完成后注册广播
        IntentFilter findedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mFindBlueToothReceiver, findedFilter);
    }

    @Override
    protected void onView() {
        initListItem();
        // 初始化化蓝牙
        initBluetooth();
    }

    @Override
    protected void onData() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消注册的广播接收器
        this.unregisterReceiver(mFindBlueToothReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                // 蓝牙已打开
                addBluetoothDevices();
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
        bluetoothScan.setText("停止");
        discoverBluetoothDevices();
    }

    /**
     * 初始化蓝牙
     */
    private void initBluetooth() {
        // 获取本地蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // 如果适配器为空，则不支持蓝牙
        if (mBluetoothAdapter == null) {
            ToastUtils.show("设备不支持蓝牙");
        } else {
            // 如果BT未开启，则请求启用它。
            // setupChat()方法将在onActivityResult期间被调用
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else {
                checkBluetoothConnectStatus();
                addBluetoothDevices();
            }
        }
    }

    private void checkBluetoothConnectStatus() {
        /*mBluetoothAdapter.getProfileProxy(this, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceDisconnected(int profile) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                List<BluetoothDevice> mDevices = proxy.getConnectedDevices();
                if (mDevices != null && mDevices.size() > 0) {
                    for (BluetoothDevice device : mDevices) {
                        addSection(pairedSection, device, null);
                    }
                }
            }
        }, BluetoothProfile.HEADSET);*/
    }

    private void initListItem() {
        pairedSection = QMUIGroupListView.newSection(this)
                .setTitle("已连接设备");
        searchedSection = QMUIGroupListView.newSection(this)
                .setTitle("已搜索设备");
    }

    private void addBluetoothDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        for (BluetoothDevice bluetoothDevice : pairedDevices) {
            addSection(searchedSection, bluetoothDevice, searchedClickListener);
        }
        pairedSection.addTo(blueToothGroupListView);
        searchedSection.addTo(blueToothGroupListView);
    }

    private void addSection(QMUIGroupListView.Section section, BluetoothDevice bluetoothDevice, View.OnClickListener onClickListener) {
        Log.i(TAG, "蓝牙名称：" + bluetoothDevice.getName() + " 蓝牙状态：" + bluetoothDevice.getBondState());
        if (TextUtils.isEmpty(bluetoothDevice.getName())) {
            return;
        }
        QMUICommonListItemView listItemView = blueToothGroupListView.createItemView(bluetoothDevice.getName());
        listItemView.setDetailText(bluetoothDevice.getAddress());
        listItemView.setOrientation(QMUICommonListItemView.VERTICAL);
        section.addItemView(listItemView, onClickListener);
    }

    /**
     * 发现蓝牙设备
     */
    private void discoverBluetoothDevices() {
        // 如果我们已经发现了，停止它
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // 从蓝牙适配器请求发现
        mBluetoothAdapter.startDiscovery();
    }

    private void scanBluetoothDevices() {
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000, 3)   // 先扫BLE设备3次，每次3s
                .searchBluetoothClassicDevice(5000) // 再扫经典蓝牙5s
                .searchBluetoothLeDevice(2000)      // 再扫BLE设备2s
                .build();
        mClient.search(request, new SearchResponse() {
            @Override
            public void onSearchStarted() {

            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                BluetoothLog.v(String.format("beacon for %s\n%s", device.getAddress(), device.getAddress()));
            }

            @Override
            public void onSearchStopped() {

            }

            @Override
            public void onSearchCanceled() {

            }
        });
    }

    /**
     * 广播接收器
     */
    private BroadcastReceiver mFindBlueToothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 当发现发现设备时
                // 从intent中获取蓝牙设备对象
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 如果已经配对了，跳过它，因为它已经被列出了
                if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                    addSection(searchedSection, bluetoothDevice, searchedClickListener);
                    addListItem(searchedSection);
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Log.i(TAG, "蓝牙设备搜索结束");
                bluetoothScan.setText("扫描");
            }
        }
    };

    private void addListItem(QMUIGroupListView.Section section) {
        section.removeFrom(blueToothGroupListView);
        section.addTo(blueToothGroupListView);
    }

    private View.OnClickListener searchedClickListener = view -> {
        // 连接状态
        if (view instanceof QMUICommonListItemView) {
            QMUICommonListItemView qmuiView = (QMUICommonListItemView) view;
            CharSequence address = qmuiView.getDetailText();
            Log.i(TAG, "选择蓝牙：" + address);

            BluetoothDevice searchedBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address.toString());
            searchedBluetoothDevice.connectGatt(this, true, new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    Log.i(TAG, "" + status + " " + newState);
                }
            });

            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };
}
