/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.actions;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 *
 * @author Rovers
 */
public class StoreAction extends ContextAction {

    protected String appPackage;

    public StoreAction(Context context) {
        super(context);
        this.appPackage = context.getPackageName();
    }

    public StoreAction(Context context, String appPackage) {
        super(context);
        this.appPackage = appPackage;
    }

    public StoreAction(Context context, String actionName, String appPackage) {
        super(context, actionName);
        this.appPackage = appPackage;
    }

    @Override
    public boolean execute(Context context) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("market://details?id=" + appPackage));
        context.startActivity(i);
        return true;
    }
}
