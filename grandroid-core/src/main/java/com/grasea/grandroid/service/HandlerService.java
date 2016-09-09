/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.service;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

/**
 *
 * @author Rovers
 */
public abstract class HandlerService extends BasicService {

    /**
     *
     */
    protected Handler handler;
    protected Bundle bundle;
    protected boolean isRunning;
    protected boolean isServed;

    /**
     *
     */
    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("HandlerService");
        thread.start();
        handler = new Handler(thread.getLooper());
    }

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {
        if (!isServed) {
            isServed = true;
            if (!isRunning) {
                isRunning = true;
                if (intent != null) {
                    bundle = intent.getExtras();
                }
                handler.postDelayed(run, 0);
            }
        }
        return START_STICKY;
    }

    /**
     *
     */
    @Override
    public void onDestroy() {
        handler.removeCallbacks(run);
        super.onDestroy();
    }
    protected Runnable run = new Runnable() {
        public void run() {
            if (execute() && getServiceInterval() > 0) {
                //Log.d("dishpage", "stop 600 sec to redo");
                isRunning = false;
                handler.postDelayed(run, getServiceInterval());
            } else {
                isServed = false;
                isRunning = false;
                HandlerService.this.stopSelf();
            }
        }
    };

    /**
     *
     * @return
     */
    protected abstract long getServiceInterval();

    /**
     *
     * @return
     */
    protected abstract boolean execute();

    public Bundle getBundle() {
        return bundle;
    }
}
