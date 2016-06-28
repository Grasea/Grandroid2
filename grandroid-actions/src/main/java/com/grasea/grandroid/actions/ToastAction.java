/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.actions;

import android.content.Context;
import android.widget.Toast;

/**
 *
 * @author Rovers
 */
public class ToastAction extends ContextAction {

    /**
     * 
     */
    protected String msg;
    /**
     * 
     */
    protected int length;

    /**
     * 
     * @param context
     * @param actionName
     */
    public ToastAction(Context context, String actionName) {
        super(context, actionName);
        msg = "";
        length = Toast.LENGTH_SHORT;
    }

    /**
     * 
     * @param context
     */
    public ToastAction(Context context) {
        super(context);
        msg = "";
        length = Toast.LENGTH_SHORT;
    }

    /**
     * 
     * @param msg
     * @return
     */
    public ToastAction setMessage(String msg) {
        this.msg = msg;
        return this;
    }

    /**
     * 
     * @param length
     * @return  
     */
    public ToastAction setLength(int length) {
        this.length = length;
        return this;
    }

    /**
     * 
     * @param context
     * @return
     */
    @Override
    public boolean execute(Context context) {
        Toast.makeText(context, msg, length).show();
        return true;
    }
}
