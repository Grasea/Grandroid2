package com.grasea.grandroid.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.grasea.grandroid.ble.annotations.MethodBinder;
import com.grasea.grandroid.ble.annotations.NameBinder;
import com.grasea.grandroid.ble.controller.BaseBleDevice;
import com.grasea.grandroid.ble.controller.BleDevice;
import com.grasea.grandroid.ble.scanner.DeviceScanner;

import java.util.ArrayList;

/**
 * Created by Alan Ding on 2016/5/16.
 */
//TODO 加入Profile機制，可快速設定各種行為
public class GrandroidBle {

    //    private Context context;
    private static GrandroidBle ourInstance = new GrandroidBle();

    public static GrandroidBle getInstance() {
        return ourInstance;
    }

    private static BluetoothLeService bluetoothLeService;

    private DeviceScanner scanner;
    private ConnectionListener connectionListener;
    private ArrayList<BaseBleDevice> bleControllers;
    private static InitHelper initHelper;

    private GrandroidBle() {
        bleControllers = new ArrayList<>();
    }

    @Nullable
    public synchronized static BleDevice with(String address) {
        for (BaseBleDevice controller : getInstance().getControllers()) {
            if (controller.getAddress().equals(address)) {
                if (controller instanceof BleDevice) {
                    return (BleDevice) controller;
                }
            }
        }
        return null;
    }

    public static boolean init(Context context, ConnectionListener connectionListener) {
        Config.loge("startScan to init. Library version :" + BuildConfig.GrandroidBleVersion);
//        getInstance().context = context;
        getInstance().setConnectionListener(connectionListener);
        initHelper = new InitHelper(context);
        boolean init = initHelper.init(getInstance().connectionListener);
        if (init) {
            getInstance().setScanner(new DeviceScanner().setBluetoothAdapter(initHelper.getBluetoothAdapter()));
        }
        return init;
    }

    public static void enableDebugLog(boolean enable) {
        Config.DEBUG = enable;
    }

    @Nullable
    public static String getName(String uuid) {
        return NameBinder.getInstance().getName(uuid);
    }

    public static void destroy() {
        getInstance().getDeviceScanner().stopScan();
        getInstance().clearControllers();
        MethodBinder.unbindAll();
        initHelper.destroy();
        initHelper = null;
    }

    public GrandroidBle bind(String deviceAddress, Object object) {
        MethodBinder.bind(deviceAddress, object);
        return this;
    }

    /**
     * Bind UUID to String Name.
     *
     * @param object
     * @return
     */
    public GrandroidBle bindName(Object object) {
        NameBinder.bind(object);
        return this;
    }


    public DeviceScanner getDeviceScanner() {
        return scanner;
    }

    public DeviceScanner setScanner(DeviceScanner scanner) {
        this.scanner = scanner;
        Config.logd("DeviceScanner Ready.");
        return this.scanner;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return initHelper.getBluetoothAdapter();
    }

    public void addDevice(BluetoothDevice bluetoothDevice) {
        Config.logd("Add device:" + bluetoothDevice.getName() + ", service is null?" + (bluetoothLeService == null));
        BleDevice device = new BleDevice.Builder(bluetoothLeService).setDevice(bluetoothDevice).build();
        if (!bleControllers.contains(device)) {
            bleControllers.add(device);
        }
    }

    public ArrayList<BaseBleDevice> getControllers() {
        return bleControllers;
    }

    public void clearControllers() {
        bleControllers.clear();
    }

    private void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public interface ConnectionListener<T extends BaseBleDevice> {

        void onGattConnectSuccess(T controller);

        void onGattDisconnected(T controller);

        void onGattDiscovered(T controller);

        void onBluetoothLeServiceConnectFailed();

        void onBluetoothLeServiceDisconnect();

        void onBluetoothLeServiceConnected();

        void onReadRssiValue(int rssiValue);
    }

    private static class InitHelper {
        private Context context;
        private ConnectionListener connectionListener;
        private BluetoothAdapter bluetoothAdapter;

        public InitHelper(Context context) {
            this.context = context;
        }

        public boolean init(ConnectionListener connectionListener) {
            this.connectionListener = connectionListener;
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager == null) {
                Config.loge("Unable to initialize BluetoothManager.");
                return false;
            }
            bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null) {
                Config.loge("Unable to obtain a BluetoothAdapter.");
                return false;
            }
            Intent gattServiceIntent = new Intent(context, BluetoothLeService.class);
            context.bindService(gattServiceIntent, serviceConnection, Activity.BIND_AUTO_CREATE);
            Config.loge("startScan to Bind service.");
            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
            intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
            intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
            intentFilter.addAction(BluetoothLeService.ACTION_READ_RSSI);
            context.registerReceiver(mGattUpdateReceiver, intentFilter);
            return true;
        }

        public void destroy() {
            if (serviceConnection != null && context != null) {
                context.unbindService(serviceConnection);
            }
            if (mGattUpdateReceiver != null && context != null) {
                context.unregisterReceiver(mGattUpdateReceiver);
            }
            context = null;
        }

        public BluetoothAdapter getBluetoothAdapter() {
            return bluetoothAdapter;
        }

        /**
         * Listen Gatt status.
         */
        private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                final String action = intent.getAction();
                Bundle extra = intent.getBundleExtra(BluetoothLeService.EXTRA_DATA);
                final String address = extra.getString(BluetoothLeService.EXTRA_DEVICE_ADDRESS);
                Config.logi("Receive Broadcast from :" + address);
                ArrayList<BaseBleDevice> controllers = getInstance().getControllers();
                if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
//                mConnected = true;
                    Config.loge("ACTION_GATT_CONNECTED");
                    if (controllers != null && connectionListener != null && !address.isEmpty()) {
                        for (BaseBleDevice bleController : controllers) {
                            if (bleController.getAddress().equals(address)) {
                                bleController.onGattServicesConnected();
                                connectionListener.onGattConnectSuccess(bleController);
                                break;
                            }
                        }
                    }
                } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
//                mConnected = false;
                    Config.loge("ACTION_GATT_DISCONNECTED");
                    if (controllers != null && connectionListener != null && !address.isEmpty()) {
                        for (BaseBleDevice bleController : controllers) {
                            if (bleController.getAddress().equals(address)) {
                                bleController.onGattServicesDisconnected();
                                connectionListener.onGattDisconnected(bleController);
                                break;
                            }
                        }
                    }
                } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                    Config.loge("ACTION_GATT_SERVICES_DISCOVERED");

                    if (controllers != null && connectionListener != null && !address.isEmpty()) {
                        for (BaseBleDevice bleController : controllers) {
                            if (bleController.getAddress().equals(address)) {
                                bleController.onGattServicesDiscovered();
                                connectionListener.onGattDiscovered(bleController);
                                break;
                            }
                        }
                    }else{

                    }
                } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                    String serviceUUID = extra.getString(BluetoothLeService.EXTRA_SERVICE_UUID, "");
                    String channelUUID = extra.getString(BluetoothLeService.EXTRA_CHANNEL_UUID, "");
                    byte[] data = extra.getByteArray("data");
                    MethodBinder binder = MethodBinder.getBinder(address);
                    if (binder != null) {
                        binder.onBLEDataReceive(serviceUUID, channelUUID, data);
                    }
                } else if (BluetoothLeService.ACTION_READ_RSSI.equals(action)) {
                    int rssi = extra.getInt(BluetoothLeService.EXTRA_DEVICE_RSSI, 0);
                    if (connectionListener != null) {
                        connectionListener.onReadRssiValue(rssi);
                    }
                } else {
                    Config.loge("UNKNOW ACTION:" + action);
                }
            }
        };
        /**
         * Listen BluetoothLeService Connection.
         */
        private final ServiceConnection serviceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
                if (!bluetoothLeService.initialize()) {
                    Config.loge("Unable to initialize Bluetooth");
                    if (connectionListener != null) {
                        connectionListener.onBluetoothLeServiceConnectFailed();
                    }
                } else {
                    if (connectionListener != null) {
                        connectionListener.onBluetoothLeServiceConnected();
                    }
                }

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                bluetoothLeService = null;
                if (connectionListener != null) {
                    connectionListener.onBluetoothLeServiceDisconnect();
                }
            }
        };
    }
}
