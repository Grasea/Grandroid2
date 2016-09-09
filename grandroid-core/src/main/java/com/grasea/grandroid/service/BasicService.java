/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.grasea.grandroid.data.DataAgent;


/**
 * @author Rovers
 */
public class BasicService extends Service {

    /**
     *
     */
    protected DataAgent dataAgent;

    /**
     * @param arg0
     * @return
     */
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * @return
     */
    public DataAgent getData() {
        if (dataAgent == null) {
            dataAgent = new DataAgent(this);
        }

        return dataAgent;
    }
}
