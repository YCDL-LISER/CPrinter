package net.lingin.max.android.net.io;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.inuker.bluetooth.library.Constants;

import net.lingin.max.android.ui.listener.BTConnectStatusListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.Vector;

/**
 * 经典蓝牙连接
 */
public class ClassicBluetoothSocket {

    private static final String TAG = ClassicBluetoothSocket.class.getName();

    public UUID SOCKET_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static final String NAME = "ClassicBluetoothSocket";

    private BluetoothDevice bluetoothDevice;

    private BTConnectStatusListener btConnectStatusListener;

    private BluetoothSocket mSocket;

    private InputStream inputStream;

    private OutputStream outputStream;

    public ClassicBluetoothSocket(BluetoothDevice bluetoothDevice, BTConnectStatusListener btConnectStatusListener) {
        this.bluetoothDevice = bluetoothDevice;
        this.btConnectStatusListener = btConnectStatusListener;
    }

    private void initSocketStream() throws IOException {
        this.mSocket.isConnected();
        this.inputStream = this.mSocket.getInputStream();
        this.outputStream = this.mSocket.getOutputStream();
    }

    public boolean isConnected() {
        if (this.mSocket == null) {
            return false;
        }
        return this.mSocket.isConnected();
    }

    public void openPort() {
        SOCKET_UUID = UUID.randomUUID();
        try {
            mSocket = bluetoothDevice.createRfcommSocketToServiceRecord(SOCKET_UUID);
            mSocket.connect();
            initSocketStream();
            btConnectStatusListener.invokeSync(bluetoothDevice, Constants.STATUS_DEVICE_CONNECTED);
        } catch (IOException e) {
            e.printStackTrace();
            btConnectStatusListener.invokeSync(bluetoothDevice, Constants.STATUS_UNKNOWN);
        }
    }

    public void writeData(byte[] data) {
        if (mSocket != null && outputStream != null && data != null && data.length > 0) {
            try {
                outputStream.write(data, 0, data.length);
                outputStream.flush();
            } catch (IOException var3) {
                Log.e(TAG, "立即发送数据时发生异常: ", var3);
            }
        }
    }

    public void writeDataImmediately(Vector<Byte> data) {
        this.writeDataImmediately(data, 0, data.size());
    }

    public void writeDataImmediately(Vector<Byte> data, int offset, int len) {
        if (mSocket != null && outputStream != null && data != null && data.size() > 0) {
            try {
                outputStream.write(convertVectorByteToBytes(data), offset, len);
                outputStream.flush();
            } catch (IOException var5) {
                Log.e(TAG, "立即发送数据时发生异常: ", var5);
            }
        }
    }

    protected byte[] convertVectorByteToBytes(Vector<Byte> data) {
        byte[] sendData = new byte[data.size()];
        if (data.size() > 0) {
            for (int i = 0; i < data.size(); ++i) {
                sendData[i] = data.get(i);
            }
        }
        return sendData;
    }

    public int readData(byte[] bytes) throws IOException {
        if (inputStream == null) {
            return -1;
        } else if (inputStream.available() > 0) {
            return inputStream.read(bytes);
        } else {
            return inputStream.available() == -1 ? -1 : 0;
        }
    }

    public boolean closePort() {
        try {
            this.closeConn();
            return true;
        } catch (IOException var2) {
            Log.e(TAG, "Close port error! ", var2);
            return false;
        }
    }

    private void closeConn() throws IOException {
        if (inputStream != null) {
            inputStream.close();
            inputStream = null;
        }

        if (outputStream != null) {
            outputStream.close();
            outputStream = null;
        }

        if (mSocket != null) {
            mSocket.close();
            mSocket = null;
        }
    }
}
