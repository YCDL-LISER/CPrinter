package net.lingin.max.android.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.text.TextUtils;

import com.inuker.bluetooth.library.BluetoothClient;

import net.lingin.max.android.logger.Log;
import net.lingin.max.android.ui.model.BluetoothDeviceDTO;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BluetoothClientFactory {

    /**
     * 低功耗蓝牙
     */
    private static BluetoothClient bluetoothClient;

    /**
     * 经典蓝牙
     */
    private static ClassicBluetoothClient classicBluetoothClient;

    private static BluetoothHeadset bluetoothHeadset;

    private static BluetoothAdapter mBluetoothAdapter;

    public static BluetoothClient getBluetoothClient(Context context) {
        if (bluetoothClient == null) {
            bluetoothClient = new BluetoothClient(context);
        }
        return bluetoothClient;
    }

    @Nullable
    public static BluetoothDevice getRemoteDevice(String mac) {
        if (!TextUtils.isEmpty(mac)) {
            BluetoothAdapter adapter = getBluetoothAdapter();
            if (adapter != null) {
                return adapter.getRemoteDevice(mac);
            }
        }
        return null;
    }

    public static ClassicBluetoothClient getClassicBluetoothClient() {
        if (classicBluetoothClient == null) {
            classicBluetoothClient = new ClassicBluetoothClient();
        }
        return classicBluetoothClient;
    }

    public static BluetoothAdapter getBluetoothAdapter() {
        if (mBluetoothAdapter == null) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        return mBluetoothAdapter;
    }

    public static BluetoothDeviceDTO getConnectedBlt(Context context) {
        BluetoothDeviceDTO bluetoothDeviceDTO = new BluetoothDeviceDTO("蓝牙未连接", "");
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.getProfileProxy(context, mProfileListener, BluetoothProfile.HEADSET);
        if (bluetoothHeadset == null) {
            return bluetoothDeviceDTO;
        }
        List<BluetoothDevice> devices = bluetoothHeadset.getConnectedDevices();
        for (final BluetoothDevice dev : devices) {
            if (bluetoothHeadset.isAudioConnected(dev)) {
                bluetoothDeviceDTO = new BluetoothDeviceDTO(dev.getName(), dev.getAddress());
            }
        }
        return bluetoothDeviceDTO;
    }

    private static BluetoothProfile.ServiceListener mProfileListener = new BluetoothProfile.ServiceListener() {

        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            Log.i("监听到信息");
            List<BluetoothDevice> connectedDevices = proxy.getConnectedDevices();
            connectedDevices.forEach(bluetoothDevice -> {
                Log.i("蓝牙：" + bluetoothDevice.getAddress());
            });
            if (profile == BluetoothProfile.HEADSET) {
                bluetoothHeadset = (BluetoothHeadset) proxy;
            }
        }

        @Override
        public void onServiceDisconnected(int profile) {
            if (profile == BluetoothProfile.HEADSET) {
                bluetoothHeadset = null;
            }
        }
    };
}
