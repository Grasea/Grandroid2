/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.actions;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

/**
 *
 * @author Rovers
 */
public class URLAction extends ContextAction {

    String url;
    String pack;

    /**
     *
     * @param context
     * @param actionName
     * @param url
     */
    public URLAction(Context context, String actionName, String url) {
        super(context, actionName);
        this.url = url;
    }

    /**
     *
     * @param context
     * @param url
     */
    public URLAction(Context context, String url) {
        super(context);
        this.url = url;
    }

    public String getPackage() {
        return pack;
    }

    public URLAction setPackage(String pack) {
        this.pack = pack;
        return this;
    }

    public URLAction useChrome() {
        this.pack = "com.android.chrome";
        return this;
    }

    /**
     *
     * @param context
     * @return
     */
    @Override
    public boolean execute(Context context) {
        if (url != null && !url.isEmpty()) {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (pack != null) {
                if (pack.equals("com.android.chrome")) {
                    i.setComponent(ComponentName.unflattenFromString("com.android.chrome/com.android.chrome.Main"));
                    i.addCategory("android.intent.category.LAUNCHER");
                } else {
                    i.setPackage(pack);
                }
            }
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                context.startActivity(i);
            } catch (Exception ex) {
                Log.e("grandroid", "fail to open " + url, ex);
                onFailOpenActivity(ex);
            }
        }
        return true;
    }

    public void onFailOpenActivity(Exception ex) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(i);
        } catch (Exception e) {
            Log.e("grandroid", null, e);
        }
    }
}
