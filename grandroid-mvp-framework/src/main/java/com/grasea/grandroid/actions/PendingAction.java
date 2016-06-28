/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.actions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Only work on grandroid-mvp-framework's {@link GrandroidActivity}.
 * @author Rovers
 */
public abstract class PendingAction extends ContextAction {

    /**
     *
     */
    protected int requestCode;
    protected boolean recreated;
    protected boolean debug;

    /**
     *
     * @param context
     * @param actionName
     * @param requestCode
     */
    public PendingAction(Context context, String actionName, int requestCode) {
        super(context, actionName);
        this.requestCode = requestCode;
        if (Pendable.class.isInstance(context)) {
            ((Pendable) context).registerPendingAction(this);
        }
    }

    /**
     *
     * @param context
     * @param requestCode
     */
    public PendingAction(Context context, int requestCode) {
        this(context, null, requestCode);
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public boolean isRecreated() {
        return recreated;
    }

    public void setRecreated(boolean recreated) {
        this.recreated = recreated;
    }

    public void saveState(Bundle b) {
        if (debug) {
            Log.d("grandroid", "save " + this.getClass().getSimpleName() + " state");
        }
    }

    public void restoreState(Bundle b) {
        if (debug) {
            Log.d("grandroid", "restore " + this.getClass().getSimpleName() + " state, recreated = true");
        }
        recreated = true;
    }

    public PendingAction debug() {
        debug = true;
        return this;
    }

    @Override
    public final boolean execute(Context context) {
        Intent intent = getActionIntent();
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, requestCode);
        }
        return true;
    }

    /**
     *
     * @return
     */
    public abstract Intent getActionIntent();

    /**
     *
     * @param result
     * @param data
     * @return
     */
    public abstract boolean callback(boolean result, Intent data);

    /**
     *
     */
    public void onError() {
        Log.e("grandroid", this.getClass().getSimpleName() + " error.");
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     * @return
     */
    public final boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == this.requestCode) {
            Log.d("grandroid", this.getClass().getSimpleName() + " is callback and handling result...");
            if (!callback(resultCode == Activity.RESULT_OK, data)) {
                onError();
            }
            recreated = false;
            return true;
        }
        return false;
    }
}
