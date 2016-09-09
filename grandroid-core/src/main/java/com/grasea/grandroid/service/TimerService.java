/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.service;

import android.content.Intent;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Rovers
 */
public abstract class TimerService extends BasicService {

    /**
     *
     */
    protected Timer timer;
    
    /**
     *
     */
    public TimerService() {
        super();
        
    }
    
    /**
     *
     */
    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer("TimerService");
        timer.scheduleAtFixedRate(new TimerTask() {
            
            public void run() {
                execute();
            }
        }, 0l, getServiceInterval());
    }
    
    /**
     *
     * @param intent
     * @param startId
     */
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            
            public void run() {
                execute();
            }
        }, 0l, getServiceInterval());
    }
    
    /**
     *
     */
    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }

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
}
