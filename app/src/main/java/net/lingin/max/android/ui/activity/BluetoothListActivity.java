package net.lingin.max.android.ui.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.lingin.max.android.R;
import net.lingin.max.android.ui.base.BaseActivity;
import net.lingin.max.android.utils.ToastUtils;

import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

public class BluetoothListActivity extends BaseActivity {

    private static final String TAG = BluetoothListActivity.class.getName();

    public static final String EXTRA_DEVICE_ADDRESS = "address";

    public static final int REQUEST_ENABLE_BT = 2;

    public static final int REQUEST_CONNECT_DEVICE = 3;

    @BindView(R.id.lvPairedDevices)
    ListView lvPairedDevice;

    private BluetoothAdapter mBluetoothAdapter;

    private ArrayAdapter<String> devicesArrayAdapter;

    @Override
    protected int onLayout() {
        return R.layout.activity_bluetooth_list;
    }

    @Override
    protected void onObject() {
        // 发现设备时注册广播
        IntentFilter findingFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mFindBlueToothReceiver, findingFilter);

        // 发现完成后注册广播
        IntentFilter findedFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mFindBlueToothReceiver, findedFilter);

        // 初始化化蓝牙
        initBluetooth();
    }

    @Override
    protected void onView() {
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
                initArrayAdapter();
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
    @OnClick(R.id.btBluetoothScan)
    public void onScanClick(View v) {
//        v.setVisibility(View.GONE);
        discoverBluetoothDevices();
    }

    /**
     * 广播接收器
     */
    private final BroadcastReceiver mFindBlueToothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 当发现发现设备时
                // 从intent中获取蓝牙设备对象
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 如果已经配对了，跳过它，因为它已经被列出了
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    devicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // 发现完成后，更改活动标题
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_bluetooth_device);
                Log.i(TAG, "finish discovery" + (devicesArrayAdapter.getCount() - 2));
                if (devicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_bluetooth_device_found).toString();
                    devicesArrayAdapter.add(noDevices);
                }
            }
        }
    };

    // 设备点击监听
    private AdapterView.OnItemClickListener mDeviceClickListener = (av, v, arg2, arg3) -> {
        String info = ((TextView) v).getText().toString();
        String noDevices = getResources().getText(R.string.none_paired).toString();
        String noNewDevice = getResources().getText(R.string.none_bluetooth_device_found).toString();
        Log.i(TAG, info);
        if (!info.equals(noDevices) && !info.equals(noNewDevice) && !info.equals(getString(R.string.str_title_newdev)) && !info.equals(getString(R.string.str_title_pairedev))) {
            mBluetoothAdapter.cancelDiscovery();
            String address = info.substring(info.length() - 17);
            // 创建结果intent并包含MAC地址
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            // 设置结果并完成此Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

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
                initArrayAdapter();
            }
        }
    }

    /**
     * 初始化array adapters。一个用于已配对的设备，另一个用于新发现的设备
     */
    protected void initArrayAdapter() {
        devicesArrayAdapter = new ArrayAdapter<>(this, R.layout.bluetooth_list_item);
        lvPairedDevice.setAdapter(devicesArrayAdapter);
        lvPairedDevice.setOnItemClickListener(mDeviceClickListener);
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // 如果有配对设备，请将每个设备添加到ArrayAdapter
        devicesArrayAdapter.add(getString(R.string.str_title_pairedev));
        if (pairedDevices.size() > 0) {
            //  tvPairedDevice.setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                devicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            devicesArrayAdapter.add(noDevices);
        }
    }

    /**
     * 发现蓝牙设备
     */
    private void discoverBluetoothDevices() {
        // 在标题中显示扫描
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scaning);
        // 打开新设备的子标题
        //tvNewDevice.setVisibility(View.VISIBLE);

        if (devicesArrayAdapter == null) {
            ToastUtils.show("设备不支持蓝牙");
            return;
        }
        devicesArrayAdapter.add(getString(R.string.str_title_newdev));
        // lvNewDevice.setVisibility(View.VISIBLE);
        // 如果我们已经发现了，停止它
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // 从蓝牙适配器请求发现
        mBluetoothAdapter.startDiscovery();
    }
}
