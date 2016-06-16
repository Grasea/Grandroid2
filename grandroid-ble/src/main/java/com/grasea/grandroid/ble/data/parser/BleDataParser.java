package com.grasea.grandroid.ble.data.parser;

/**
 * Created by Alan Ding on 2016/5/24.
 */
public interface BleDataParser<T> {
    T parse(byte[] data);
}
