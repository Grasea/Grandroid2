package com.grasea.ble.scanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

/**
 * Created by Alan Ding on 2016/5/18.
 */
public interface ScanResultHandler {
    long getTimeout();

    ScanResultHandler setTimeout(long timeout);

    boolean onTimeout(Scanner scanner);

    void onDeviceFind(BluetoothDevice device, int rssi, byte[] scanRecord);

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void onDeviceFailed(int errorCode);
}
