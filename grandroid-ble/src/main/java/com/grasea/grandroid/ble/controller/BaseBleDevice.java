package com.grasea.ble.controller;

import com.grasea.ble.data.GattServiceChannelHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Alan Ding on 2016/5/20.
 */
public abstract class BaseBleDevice implements BleController {
    protected ConcurrentHashMap<String, GattServiceChannelHandler> serviceHandlerMap = new ConcurrentHashMap<>();

    public ArrayList<GattServiceChannelHandler> getServiceHandlers() {
        ArrayList<GattServiceChannelHandler> gattServiceChannelHandlerArrayList = new ArrayList<>();
        Iterator<GattServiceChannelHandler> iterator = serviceHandlerMap.values().iterator();
        while (iterator.hasNext()) {
            GattServiceChannelHandler next = iterator.next();
            gattServiceChannelHandlerArrayList.add(next);
        }
        return gattServiceChannelHandlerArrayList;
    }
}
