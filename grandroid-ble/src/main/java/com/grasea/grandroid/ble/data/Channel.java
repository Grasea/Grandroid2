package com.grasea.grandroid.ble.data;

import android.bluetooth.BluetoothGattCharacteristic;

import com.grasea.grandroid.ble.BluetoothLeService;
import com.grasea.grandroid.ble.Config;
import com.grasea.grandroid.ble.controller.BaseBleDevice;

import java.math.BigInteger;

/**
 * Created by Alan Ding on 2016/5/13.
 */
public class Channel implements CharacteristicHandler {
    private BaseBleDevice controller;
    private BluetoothGattCharacteristic characteristic;
    private String uuid;
    private int properties;

    public Channel(BaseBleDevice controller, BluetoothGattCharacteristic characteristic) {
        this.controller = controller;
        this.characteristic = characteristic;
        uuid = characteristic.getUuid().toString();
        properties = characteristic.getProperties();
    }

    public String getUUID() {
        return characteristic.getUuid().toString();
    }

    /**
     * if you have use @OnBleDataReceive(sevcieUUID = "..." , characteristicUUID /characteristicUUIDs = ....) ,them MethodBinder will auto to startListen.
     *
     * @return
     */
    @Override
    public boolean startListenBleData() {
        BluetoothLeService bluetoothLeService = controller.getBluetoothLeService();

        if (characteristic == null) {
            return false;
        }
        bluetoothLeService.setCharacteristicNotification(controller,
                characteristic, true);
        bluetoothLeService.readCharacteristic(controller, characteristic);
        return true;
    }

    @Override
    public boolean stopListenBleData() {

        BluetoothLeService bluetoothLeService = controller.getBluetoothLeService();

        if (characteristic == null) {
            return false;
        }
        bluetoothLeService.setCharacteristicNotification(controller,
                characteristic, false);
        bluetoothLeService.readCharacteristic(controller, characteristic);
        return true;
    }

    @Override
    public boolean isSendChannel() {
        return ((properties & BluetoothGattCharacteristic.PROPERTY_WRITE) |
                (properties & BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) > 0;
    }

    @Override
    public boolean isReadChannel() {
        return (properties & BluetoothGattCharacteristic.PROPERTY_READ) > 0;
    }

    @Override
    public boolean isNotificationChannel() {
        return (properties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0;
    }

    public int getProperties() {
        return properties;
    }

    @Override
    public BluetoothGattCharacteristic getGattCharacteristic() {
        return characteristic;
    }

    @Override
    public boolean send(String protocol) {
        byte[] b = new BigInteger(protocol, 16).toByteArray();
        return send(b);
    }

    @Override
    public boolean send(byte[] protocol) {
//        try {
//            Thread.sleep(300);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        BluetoothLeService bluetoothLeService = controller.getBluetoothLeService();
        BluetoothGattCharacteristic gattCharacteristic = getGattCharacteristic();
        bluetoothLeService.setCharacteristicNotification(controller,
                gattCharacteristic, true);
        gattCharacteristic.setValue(protocol);

        return bluetoothLeService.writeCharacteristic(controller, gattCharacteristic);
    }

    @Override
    public String toString() {
        return "Channel{" +
                "uuid='" + uuid + '\'' +
                '}';
    }
}
