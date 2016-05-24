package com.grasea.ble;

import android.support.annotation.Nullable;

import com.grasea.ble.controller.BleDevice;

/**
 * Created by Alan Ding on 2016/5/19.
 */
public abstract class GrandroidConnectionListener implements GrandroidBle.ConnectionListener<BleDevice> {
    public static final int ERROR_BLE_SERVICE_CONNECT_FAILED = 1;

    @Override
    public void onGattConnectSuccess(BleDevice controller) {
    }

    @Override
    public void onGattDisconnected(BleDevice controller) {
        onDeviceDisconnected(controller);
    }

    @Override
    public void onGattDiscovered(BleDevice controller) {
        onDeviceReady(controller);
    }

    @Override
    public void onBluetoothLeServiceConnectFailed() {
        onFailed(ERROR_BLE_SERVICE_CONNECT_FAILED);
    }

    @Override
    public void onBluetoothLeServiceDisconnect() {

    }

    @Override
    public void onBluetoothLeServiceConnected() {

    }

    public abstract void onDeviceReady(@Nullable BleDevice controller);

    public abstract void onDeviceDisconnected(@Nullable BleDevice controller);

    public abstract void onFailed(int errorCode);


}
