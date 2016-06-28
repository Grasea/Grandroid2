/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.actions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 *
 * @author Rovers
 */
public class AlertAction extends ContextAction {

    protected boolean cancelable;
    /**
     *
     */
    protected String title;
    /**
     *
     */
    protected String msg;
    /**
     *
     */
    protected Action actPositive;
    /**
     *
     */
    protected Action actNegative;
    protected Action actCancel;

    /**
     *
     * @param context
     * @param actionName
     */
    public AlertAction(Context context, String actionName) {
        super(context, actionName);
    }

    /**
     *
     * @param context
     */
    public AlertAction(Context context) {
        super(context);
    }

    /**
     *
     * @param title
     * @param msg
     * @param actPositive
     * @param actNegative
     * @return
     */
    public AlertAction setData(String title, String msg, Action actPositive, Action actNegative) {
        if (title != null) {
            this.title = title;
        }
        if (msg != null) {
            this.msg = msg;
        }
        if (actPositive != null) {
            this.actPositive = actPositive;
        }
        if (actNegative != null) {
            this.actNegative = actNegative;
        }
        cancelable = true;
        return this;
    }

    public AlertAction setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        return this;
    }

    public AlertAction setCancelAction(Action actCancel) {
        this.actCancel = actCancel;
        return this;
    }

    /**
     *
     * @param context
     * @return
     */
    @Override
    public boolean execute(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(title).setMessage(msg);
        if (actPositive != null) {
            builder.setPositiveButton(actPositive.getActionName(), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) {
                    actPositive.execute();
                }
            });
        }
        if (actNegative != null) {
            builder.setNegativeButton(actNegative.getActionName(), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface arg0, int arg1) {
                    actNegative.execute();
                }
            });
        }
        builder.setCancelable(cancelable);
        if (cancelable && actCancel != null) {
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

                public void onCancel(DialogInterface dialog) {
                    actCancel.setArgs(dialog).execute();
                }
            });
        }
        builder.show();
        return true;
    }
}
