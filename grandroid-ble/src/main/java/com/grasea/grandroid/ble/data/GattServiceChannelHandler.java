package com.grasea.ble.data;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.support.annotation.Nullable;

import com.grasea.ble.controller.BaseBleDevice;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Alan Ding on 2016/5/13.
 */
public class GattServiceChannelHandler {
    protected BluetoothGattService service;
    protected BaseBleDevice controller;

    public GattServiceChannelHandler(BaseBleDevice controller, BluetoothGattService service) {
        this.service = service;
        this.controller = controller;
    }

    public ArrayList<Channel> getChannels() {
        ArrayList<Channel> results = new ArrayList<>();
        for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
            results.add(new Channel(controller, characteristic));
        }
        return results;
    }

    @Nullable
    public Channel getChannel(String uuid) {
        if (contains(uuid)) {
            return new Channel(controller, service.getCharacteristic(UUID.fromString(uuid)));
        } else {
            return null;
        }
    }

    public boolean contains(String uuid) {
        return (service.getCharacteristic(UUID.fromString(uuid)) != null);
    }

    public BluetoothGattService getService() {
        return service;
    }
}
