package com.grasea.ble.scanner;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Alan Ding on 2016/5/19.
 */
public abstract class BaseScanResultHandler implements ScanResultHandler {
    public static final long DEFAULT_TIMEOUT = 15000;
    protected long timeout = DEFAULT_TIMEOUT;
}
