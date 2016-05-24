package com.grasea.ble.scanner;

import android.bluetooth.BluetoothDevice;

import java.util.Timer;

/**
 * Created by Alan Ding on 2016/5/19.
 */
public abstract class BaseScanner implements Scanner {
    protected ScanResultHandler scanResultHandler;
    protected boolean scanning = false;
    protected Timer timer = new Timer("ScannerTimer");


}
