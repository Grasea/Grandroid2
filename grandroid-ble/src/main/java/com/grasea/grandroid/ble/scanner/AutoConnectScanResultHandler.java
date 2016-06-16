package com.grasea.grandroid.ble.scanner;

import android.bluetooth.BluetoothDevice;
import android.os.Build;

import com.grasea.grandroid.ble.Config;
import com.grasea.grandroid.ble.GrandroidBle;
import com.grasea.grandroid.ble.controller.BleDevice;

/**
 * Created by Alan Ding on 2016/5/19.
 */
public class AutoConnectScanResultHandler extends BaseScanResultHandler {
    public AutoConnectScanResultHandler() {
    }

    public AutoConnectScanResultHandler(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public long getTimeout() {
        return timeout;
    }

    @Override
    public AutoConnectScanResultHandler setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    @Override
    public boolean onTimeout(Scanner scanner) {
        return true;
    }

    @Override
    public void onDeviceFind(BluetoothDevice device, int rssi, byte[] scanRecord) {
        BleDevice bleDevice = GrandroidBle.with(device.getAddress());
        Config.logi("[AutoConnectScanResultHandler] device is null? :" + (bleDevice != null) + ", isConnecting:" + bleDevice.isConnecting());

        if (bleDevice != null && !bleDevice.isConnecting()) {
            Config.logi("[AutoConnectScanResultHandler] connect :" + device.getAddress());
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                bleDevice.getBluetoothDevice().setPairingConfirmation(true);
//            }
            bleDevice.connect();
        }
    }

    @Override
    public void onDeviceFailed(int errorCode) {

    }
}
