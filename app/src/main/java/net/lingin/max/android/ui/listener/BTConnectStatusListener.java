package net.lingin.max.android.ui.listener;

import android.bluetooth.BluetoothDevice;

import com.inuker.bluetooth.library.receiver.listener.BluetoothClientListener;

public abstract class BTConnectStatusListener extends BluetoothClientListener {

    public abstract void onConnectStatusChanged(BluetoothDevice bluetoothDevice, int status);

    @Override
    public void onSyncInvoke(Object... args) {
        BluetoothDevice bluetoothDevices = (BluetoothDevice) args[0];
        int status = (int) args[1];
        onConnectStatusChanged(bluetoothDevices, status);
    }
}
