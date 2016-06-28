/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.actions;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;

/**
 *
 * @author Rovers
 */
public class BackAction extends ContextAction {

    /**
     * 
     * @param context
     */
    public BackAction(Context context) {
        super(context);
    }

    /**
     * 
     * @param context
     * @param actionName
     */
    public BackAction(Context context, String actionName) {
        super(context, actionName);
    }

    @Override
    public boolean execute(Context context) {
//        new Thread(new Runnable()    {
//
//            @Override
//            public void run() {
        KeyEvent backEvtDown = new KeyEvent(KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_BACK);
        KeyEvent backEvtUp = new KeyEvent(KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_BACK);
        ((Activity) context).dispatchKeyEvent(backEvtDown);
        ((Activity) context).dispatchKeyEvent(backEvtUp);
//            }
//        }).start();
        return true;
    }
}
