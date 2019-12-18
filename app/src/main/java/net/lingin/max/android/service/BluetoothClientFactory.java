package net.lingin.max.android.service;

import android.content.Context;

import com.inuker.bluetooth.library.BluetoothClient;

public class BluetoothClientFactory {

    private static BluetoothClient bluetoothClient;

    public static BluetoothClient getBluetoothClient(Context context) {
        if (bluetoothClient == null) {
            synchronized (BluetoothClientFactory.class) {
                if (bluetoothClient == null) {
                    return new BluetoothClient(context);
                }
            }
        }
        return bluetoothClient;
    }

}
