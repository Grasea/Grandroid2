package com.grasea.ble.controller;

import android.bluetooth.BluetoothDevice;

import com.grasea.ble.BluetoothLeService;

/**
 * Created by Alan Ding on 2016/5/13.
 */
public interface BleController {

    void onGattServicesDiscovered();

    void onGattServicesConnected();

    void onGattServicesDisconnected();

    boolean connect();

    void disconnect();

    boolean send(String serviceUUID, String channelUUID, String protocol);

    boolean send(String serviceUUID, String channelUUID, byte[] protocol);

    BleDevice.ConnectionState getState();

    String getAddress();

    String getName();

    BluetoothDevice getBluetoothDevice();

    BluetoothLeService getBluetoothLeService();


}
