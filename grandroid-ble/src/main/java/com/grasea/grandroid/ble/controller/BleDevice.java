package com.grasea.grandroid.ble.controller;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.support.annotation.Nullable;

import com.grasea.grandroid.ble.BluetoothLeService;
import com.grasea.grandroid.ble.Config;
import com.grasea.grandroid.ble.data.GattServiceChannelHandler;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Alan Ding on 2016/5/13.
 */
public class BleDevice extends BaseBleDevice {

    private ConnectionState state = ConnectionState.Uninitialized;

    private BluetoothDevice bluetoothDevice;

    private BluetoothLeService bluetoothLeService;


    private BleDevice(BluetoothLeService bluetoothLeService, BluetoothDevice bluetoothDevice) {
        this.bluetoothLeService = bluetoothLeService;
        this.bluetoothDevice = bluetoothDevice;
        init();
    }

    public
    @Nullable
    GattServiceChannelHandler findService(String serviceUUID) {
        return serviceHandlerMap.get(serviceUUID);
    }

    private void init() {
        state = ConnectionState.Disconnected;
    }

    @Override
    public void onGattServicesDiscovered() {
        // Show all the supported services and characteristics on the user interface.
        serviceHandlerMap.clear();
        Config.logd("on GattServicesDiscovered");
        List<BluetoothGattService> supportedGattServices = bluetoothLeService.getSupportedGattServices(this);
        if (supportedGattServices != null) {
            for (BluetoothGattService service : supportedGattServices) {
                Config.logi("Add a Service:" + service.getUuid().toString());
                serviceHandlerMap.put(service.getUuid().toString(), new GattServiceChannelHandler(this, service));
            }
            state = ConnectionState.Connecting;
        } else {

        }

    }

    @Override
    public void onGattServicesConnected() {

    }

    @Override
    public void onGattServicesDisconnected() {
        state = ConnectionState.Disconnected;
    }

    @Override
    public ConnectionState getState() {
        return state;
    }

    public boolean isConnecting() {
        return state == ConnectionState.Connecting;
    }

    @Override
    public boolean connect() {
        if (bluetoothLeService != null) {
            bluetoothLeService.connect(this);
            return true;
        }
        return false;
    }

    public boolean connect(int delay, boolean autoConnect) {
        if (bluetoothLeService != null) {
            bluetoothLeService.connect(this, delay, autoConnect);
            return true;
        }
        return false;
    }

    @Override
    public void disconnect() {
        if (bluetoothLeService != null) {
            bluetoothLeService.disconnect(this);
        }
    }

    @Override
    public boolean send(String serviceUUID, String channelUUID, String protocol) {
        return findService(serviceUUID).getChannel(channelUUID).send(protocol);
    }

    @Override
    public boolean send(String serviceUUID, String channelUUID, byte[] protocol) {
        return findService(serviceUUID).getChannel(channelUUID).send(protocol);
    }

    @Override
    public String getAddress() {
        return bluetoothDevice.getAddress();
    }

    @Override
    public String getName() {
        return bluetoothDevice.getName();
    }

    @Override
    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    @Override
    public BluetoothLeService getBluetoothLeService() {
        return bluetoothLeService;
    }

    public static class Builder {
        private BluetoothDevice device;
        private BluetoothLeService bluetoothLeService;

        public Builder(BluetoothLeService bluetoothLeService) {
            this.bluetoothLeService = bluetoothLeService;
        }

        public Builder setDevice(BluetoothDevice device) {
            this.device = device;
            return this;
        }

        public BleDevice build() {
            return new BleDevice(bluetoothLeService, device);
        }
    }

    public enum ConnectionState {
        Uninitialized, Disconnected, Connecting
    }


}
