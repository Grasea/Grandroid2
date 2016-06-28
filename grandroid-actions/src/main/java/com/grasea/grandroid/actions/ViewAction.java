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
public class ViewAction extends ContextAction {

    protected String uri;
    protected String className;
    protected String packageName;
    protected Class c;

    public ViewAction(Context context) {
        super(context);
    }

    public ViewAction(Context context, String actionName) {
        super(context, actionName);
    }

    public ViewAction setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public ViewAction setClass(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
        return this;
    }

    public ViewAction setClass(Class c) {
        this.c = c;
        return this;
    }

    @Override
    public boolean execute(Context context) {
        if (uri != null && !uri.isEmpty()) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                if (c != null) {
                    intent.setClass(context, c);
                }
                if (packageName != null && className != null) {
                    intent.setClassName(packageName, className);
                }
                //intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                context.startActivity(intent);
            } catch (Exception ex) {
                onFailOpenActivity(ex);
            }
        }
        return true;
    }

    public void onFailOpenActivity(Exception ex) {
    }
}
