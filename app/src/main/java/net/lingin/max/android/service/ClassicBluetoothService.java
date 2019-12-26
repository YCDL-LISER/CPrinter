package net.lingin.max.android.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import net.lingin.max.android.logger.Log;

/**
 * @author Administrator
 * @date 2019/12/26 11:53
 */
public class ClassicBluetoothService extends Service {

    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(String.format("BluetoothService onCreate"));
        this.context = getApplicationContext();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
