package net.lingin.max.android.service;

import android.content.Context;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.utils.BluetoothUtils;

import net.lingin.max.android.ui.listener.BTConnectStatusListener;

public class BluetoothClientFactory {

    /**
     * 低功耗蓝牙
     */
    private static BluetoothClient bluetoothClient;

    /**
     * 经典蓝牙
     */
    private static ClassicBluetoothClient classicBluetoothClient;

    public static BluetoothClient getBluetoothClient(Context context) {
        if (bluetoothClient == null) {
            bluetoothClient = new BluetoothClient(context);
        }
        return bluetoothClient;
    }

    public static ClassicBluetoothClient getClassicBluetoothClient(String mac, BTConnectStatusListener btConnectStatusListener) {
        if (classicBluetoothClient == null) {
            classicBluetoothClient = new ClassicBluetoothClient(BluetoothUtils.getRemoteDevice(mac), btConnectStatusListener);
        }
        return classicBluetoothClient;
    }
}
