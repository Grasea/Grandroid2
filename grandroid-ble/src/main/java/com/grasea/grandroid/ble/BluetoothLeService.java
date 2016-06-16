/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grasea.grandroid.ble;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.grasea.grandroid.ble.controller.BaseBleDevice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private HashMap<BaseBleDevice, BluetoothGatt> gattsMap;
    private int mConnectionState = STATE_DISCONNECTED;

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.grasea.grandroid.ble.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.grasea.grandroid.ble.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.grasea.grandroid.ble.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.grasea.grandroid.ble.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.grasea.grandroid.ble.EXTRA_DATA";
    public final static String EXTRA_DEVICE_ADDRESS = "address";
    public final static String EXTRA_SERVICE_UUID = "serviceUUID";
    public final static String EXTRA_CHANNEL_UUID = "characteristicUUID";

    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            Config.loge("[" + gatt.getDevice().getAddress().toString() + "] onReliableWriteCompleted status:" + status);
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Config.loge("[" + characteristic.getUuid().toString() + "] onCharacteristicWrite_ status:" + status);
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Config.logd("At Service Connection State Change:" + newState);
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction, gatt.getDevice().getAddress());
                Log.i(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
                Log.i(TAG, "Attempting to startScan findService discovery:" +
                        gatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction, gatt.getDevice().getAddress());
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Config.logi("[" + gatt.getDevice().getAddress() + "]onServicesDiscovered");
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED, gatt.getDevice().getAddress());
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Config.logi("[" + gatt.getDevice().getAddress() + "]onCharacteristicRead");
                broadcastUpdate(ACTION_DATA_AVAILABLE, gatt.getDevice().getAddress(), characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Config.logi("[" + gatt.getDevice().getAddress() + "]onCharacteristicChanged");
            broadcastUpdate(ACTION_DATA_AVAILABLE, gatt.getDevice().getAddress(), characteristic);
        }
    };

    private void broadcastUpdate(final String action, String deviceAddress) {
        final Intent intent = new Intent(action);
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_DEVICE_ADDRESS, deviceAddress);
        intent.putExtra(EXTRA_DATA, bundle);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, String deviceAddress,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        final byte[] data = characteristic.getValue();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_DEVICE_ADDRESS, deviceAddress);
        bundle.putString(EXTRA_SERVICE_UUID, characteristic.getService().getUuid().toString());
        bundle.putString(EXTRA_CHANNEL_UUID, characteristic.getUuid().toString());
        if (data != null && data.length > 0) {
            bundle.putByteArray("data", data);
        }
        intent.putExtra(EXTRA_DATA, bundle);
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        gattsMap = new HashMap<>();
        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param controller The BLEController of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(BaseBleDevice controller) {
        if (mBluetoothAdapter == null || controller.getAddress() == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        BluetoothGatt bluetoothGatt = findBluetoothGatt(controller);

        // Previously connected device.  Try to reconnect.
        if (controller.getAddress() != null && controller.getAddress().equals(controller.getAddress())
                && bluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (bluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(controller.getAddress());
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        bluetoothGatt = device.connectGatt(this, false, mGattCallback);
        gattsMap.put(controller, bluetoothGatt);
        Log.d(TAG, "Trying to create a new connection.");
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect(BaseBleDevice controller) {
        BluetoothGatt bluetoothGatt = findBluetoothGatt(controller);
        if (mBluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        bluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        for (BluetoothGatt bluetoothGatt : gattsMap.values()) {
            bluetoothGatt.close();
        }
        gattsMap.clear();
        gattsMap = null;
    }

    public BluetoothGatt findBluetoothGatt(BaseBleDevice controller) {
        BluetoothGatt bluetoothGatt = gattsMap.get(controller);
        return bluetoothGatt;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BaseBleDevice controller, BluetoothGattCharacteristic characteristic) {
        BluetoothGatt bluetoothGatt = findBluetoothGatt(controller);
        if (mBluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        Config.logi("startScan read readCharacteristic: " + GattAttributes.lookup(characteristic.getUuid().toString(), ""));
        bluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BaseBleDevice controller, BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        BluetoothGatt bluetoothGatt = findBluetoothGatt(controller);
        if (mBluetoothAdapter == null || bluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        boolean b = bluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        Config.loge("[" + characteristic.getUuid().toString() + "] Notification enabled:" + (enabled) + ", result: " + b);

        List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
        for (BluetoothGattDescriptor dp : descriptors) {
            dp.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            bluetoothGatt.writeDescriptor(dp);
        }
//        if (isNotificationChannel) {
//            Config.logi("[" + characteristic.getUuid().toString() + "]set notifyDescriptor Enable");
////        List<BluetoothGattDescriptor> descriptors = characteristic.getDescriptors();
////        for (BluetoothGattDescriptor bgd : descriptors) {
////            Config.loge("BluetoothGattDescriptor uuid[" + bgd.getUuid().toString() + "]");
////        }
//            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
//                    UUID.fromString(GattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
//            if (descriptor != null) {
//                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                bluetoothGatt.writeDescriptor(descriptor);
//            }
//        }
    }

    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices(BaseBleDevice controller) {
        BluetoothGatt bluetoothGatt = findBluetoothGatt(controller);
        if (bluetoothGatt == null) return null;

        return bluetoothGatt.getServices();
    }

    public synchronized boolean writeCharacteristic(BaseBleDevice controller, BluetoothGattCharacteristic gattCharacteristic) {
        BluetoothGatt bluetoothGatt = findBluetoothGatt(controller);
//        //check mBluetoothGatt is available
        if (bluetoothGatt == null) {
            Log.e(TAG, "lost connection");
            return false;
        }
//        BluetoothGattService Service = mBluetoothGatt.getService(your Services);
//        if (Service == null) {
//            Log.e(TAG, "findService not found!");
//            return false;
//        }
//        BluetoothGattCharacteristic charac = Service
//                .getCharacteristic(your characteristic);
        if (gattCharacteristic == null) {
            Log.e(TAG, "char not found!");
            return false;
        }

//        byte[] value = new byte[1];
//        value[0] = (byte) (21 & 0xFF);
//        gattCharacteristic.setValue(value);
        boolean status = bluetoothGatt.writeCharacteristic(gattCharacteristic);
        return status;
    }

    public int getConnectionState() {
        return mConnectionState;
    }
}
