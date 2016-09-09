/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.service;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Rovers
 */
public abstract class SynchronizedHandlerService extends HandlerService {

    protected int taskID = 0;
    protected ConcurrentLinkedQueue<Bundle> queue;

    @Override
    public synchronized int onStartCommand(Intent intent, int flags, int startId) {
        Bundle b = intent.getExtras();
        if (b == null) {
            b = new Bundle();
        }
        queue.add(b);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        queue = new ConcurrentLinkedQueue<Bundle>();
        taskID = 0;
    }

    @Override
    protected long getServiceInterval() {
        return 0;
    }

    @Override
    protected final boolean execute() {
        Bundle b = null;
        int tid=0;
        try {
            while ((b = queue.poll()) != null) {
                tid = ++taskID;
                if (!executeTask(tid, b)) {
                    onInterrupted(tid, b, queue);
                    break;
                }
            }
        } catch (Exception ex) {
            onError(tid, b, queue, ex);
            Log.e("grandroid", null, ex);
        }
        return false;
    }

    protected abstract boolean executeTask(int taskID, Bundle b);

    protected abstract void onInterrupted(int taskID, Bundle b, ConcurrentLinkedQueue<Bundle> queue);

    protected void onError(int taskID, Bundle b, ConcurrentLinkedQueue<Bundle> queue, Exception t) {

    }
}
