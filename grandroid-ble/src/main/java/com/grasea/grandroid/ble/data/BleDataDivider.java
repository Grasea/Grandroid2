package com.grasea.ble.data;


import android.support.annotation.Nullable;

/**
 * Created by Alan Ding on 2016/5/16.
 */
public interface BleDataDivider {
    void onBLEDataReceive(String serviceUUID, String channelUUID,@Nullable byte[] data);

}
