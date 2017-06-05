package com.grasea.grandroid.ble.data;

import android.bluetooth.BluetoothGattCharacteristic;

import com.grasea.grandroid.ble.BluetoothLeService;

/**
 * Created by Alan Ding on 2016/5/13.
 */
public interface CharacteristicHandler {
    boolean startListenBleData();

    boolean stopListenBleData();


    boolean isSendChannel();

    boolean isReadChannel();

    boolean isNotificationChannel();

    BluetoothGattCharacteristic getGattCharacteristic();

    boolean send(String protocol);

    boolean send(byte[] protocol);

    boolean readRssi();

}
