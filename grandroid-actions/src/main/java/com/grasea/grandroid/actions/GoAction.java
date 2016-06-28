/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.actions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Rovers
 */
public class GoAction extends ContextAction {

    /**
     *
     */
    protected Bundle bundle;
    /**
     *
     */
    protected int flag = 0;
    /**
     *
     */
    protected boolean isSubTask;
    /**
     *
     */
    protected Class c;
    /**
     *
     */
    protected int requestCode = 0;
    protected boolean noAnimation;
    protected boolean forgetCurrent;
    protected Uri uri;
    protected String intentAction;
    protected int leaveTransition;
    protected int enterTransition;


    /**
     * @param context
     * @param c       target activity
     */
    public GoAction(Context context, Class c) {
        super(context, "undefined");
        this.c = c;
    }

    /**
     * @param context
     * @param actionName
     * @param cp
     */
    public GoAction(Context context, String actionName, String cp) {
        super(context, actionName);
        try {
            c = Class.forName(cp);
        } catch (ClassNotFoundException ex) {
            Log.e(GoAction.class.getName(), null, ex);
        }
    }

    /**
     * @param context
     * @param actionName
     * @param c
     */
    public GoAction(Context context, String actionName, Class c) {
        super(context, actionName);
        this.c = c;
    }

    public GoAction(Activity activity, Class c) {
        super(activity, "undefined");
        this.c = c;
    }

    /**
     * @param bundle
     * @return
     */
    public GoAction setBundle(Bundle bundle) {
        this.bundle = bundle;
        return this;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public GoAction addBundleObject(String key, String value) {
        if (this.bundle == null) {
            bundle = new Bundle();
        }
        bundle.putString(key, value);
        return this;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public GoAction addBundleObject(String key, int value) {
        if (this.bundle == null) {
            bundle = new Bundle();
        }
        bundle.putInt(key, value);
        return this;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public GoAction addBundleObject(String key, boolean value) {
        if (this.bundle == null) {
            bundle = new Bundle();
        }
        bundle.putBoolean(key, value);
        return this;
    }

    /**
     * @param key
     * @param strarr
     * @return
     */
    public GoAction addBundleObject(String key, String[] strarr) {
        if (this.bundle == null) {
            bundle = new Bundle();
        }
        bundle.putStringArray(key, strarr);
        return this;
    }

    /**
     * @param key
     * @param intarr
     * @return
     */
    public GoAction addBundleObject(String key, int[] intarr) {
        if (this.bundle == null) {
            bundle = new Bundle();
        }
        bundle.putIntArray(key, intarr);
        return this;
    }

    /**
     * @param key
     * @param value
     * @return
     */
    public GoAction addBundleObject(String key, double value) {
        if (this.bundle == null) {
            bundle = new Bundle();
        }
        bundle.putDouble(key, value);
        return this;
    }

    public GoAction setTransition(int enterTransition, int leaveTransition) {
        this.leaveTransition = leaveTransition;
        this.enterTransition = enterTransition;
        return this;
    }


    /**
     * @param flag
     * @return
     */
    public GoAction setFlag(int flag) {
        this.flag = flag;
        return this;
    }

    public GoAction addFlag(int flag) {
        this.flag = flag | flag;
        return this;
    }

    /**
     * @return
     */
    public GoAction forgetCurrentFace() {
        forgetCurrent = true;
        return this;
    }

    public GoAction cancelAnimation() {
        noAnimation = true;
        return this;
    }

    /**
     * @return
     */
    public GoAction removeOldFace() {
        return setFlag(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

    /**
     * @return
     */
    public GoAction setSubTask() {
        return setSubTask(0);
    }

    /**
     * @param requestCode
     * @return
     */
    public GoAction setSubTask(int requestCode) {
        isSubTask = true;
        this.requestCode = requestCode;
        return this;
    }

    public GoAction setUri(Uri uri) {
        this.uri = uri;
        return this;
    }

    public GoAction asActionView() {
        this.intentAction = Intent.ACTION_VIEW;
        return this;
    }

    public GoAction setIntentAction(String intentAction) {
        this.intentAction = intentAction;
        return this;
    }

    /**
     * @param context
     * @return
     */
    @Override
    public boolean execute(Context context) {
        if (context != null && c != null) {

            Intent intent = new Intent();
            intent.setClass(context, c);
            if (uri != null) {
                intent.setData(uri);
            }
            if (intentAction != null) {
                intent.setAction(intentAction);
            }
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            if (forgetCurrent) {
                flag = flag | Intent.FLAG_ACTIVITY_NO_HISTORY;
            }
            if (flag > 0) {
                intent.setFlags(flag);
            }
            if (isSubTask && context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, requestCode);
            } else {
                context.startActivity(intent);
            }
            if (noAnimation) {
                ((Activity) context).overridePendingTransition(0, 0);
            } else if (enterTransition != 0 && leaveTransition != 0) {
                ((Activity) context).overridePendingTransition(enterTransition, leaveTransition);
            }

            return true;
        }
        return false;
    }

}
