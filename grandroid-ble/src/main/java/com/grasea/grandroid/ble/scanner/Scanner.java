package com.grasea.ble.scanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

/**
 * Created by Alan Ding on 2016/5/16.
 */
public interface Scanner {
    Scanner setScanResultHandler(ScanResultHandler scanResultHandler);

    ScanResultHandler getScanResultHandler();

    long getTimeout();

    boolean isScanning();

    void startScan();

    void stopScan();

    void onDeviceFind(BluetoothDevice device, int rssi, byte[] scanRecord);

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    void onDeviceFailed(int errorCode);

}
