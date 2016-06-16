package com.grasea.grandroid.ble;

import android.app.Activity;

import com.grasea.grandroid.ble.annotations.OnBleDataReceive;

/**
 * Created by Alan Ding on 2016/5/16.
 */
public class Test extends Activity {
    @OnBleDataReceive(serviceUUID = "SDAFAGASFA00", characteristicUUID = "usdiuaskafjkl")
    public void onReceive(byte[] data) {

    }

    @OnBleDataReceive(serviceUUID = "SDAFAGASFA00", characteristicUUIDs = {"usdiuaskafjkl", ""})
    public void onReceiveData(byte[] data) {

    }

    public void onCreate() {

//        BluetoothManager systemService = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        BluetoothAdapter adapter = systemService.getAdapter();
//        DeviceScanner deviceScanner = new DeviceScanner(adapter) {
//            @Override
//            public void onDeviceFind(BluetoothDevice device, int rssi, byte[] scanRecord) {
//                super.onDeviceFind(device, rssi, scanRecord);
                GrandroidBle.with("").connect();
//                if (GrandroidBle.with(device.getAddress()).isConnecting()) {
//                    GrandroidBle.with(device.getAddress()).findService("SDAFAGASFA00").getChannel("usdiuaskafjkl").startListenBleData();
//                    //Send
//                    GrandroidBle.with(device.getAddress()).send("SDAFAGASFA00", "usdiuaskafjkl", "Protocol1");
//                    //Send
//                    GrandroidBle.with(device.getAddress()).findService("SDAFAGASFA00").getChannel("usdiuaskafjkl").send("Protocol1");
//                }
//            }
//        };
//        deviceScanner.filterUUID("AAAAA1234657654");
    }
}
