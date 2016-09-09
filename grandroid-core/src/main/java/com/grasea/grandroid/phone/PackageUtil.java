/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.phone;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

/**
 *
 * @author Rovers
 */
public class PackageUtil {

    public static boolean isIntentAvailable(Context context, String action) {
        Intent actionIntent = new Intent(action);
        PackageManager pm = context.getPackageManager();
        return pm.queryIntentActivities(actionIntent, 0).size() > 0;
    }

    public static boolean isPackageInstalled(Context context, String pkg) {
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(pkg, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void startOrDownload(Context context, String action, String pkg) {
        if (isIntentAvailable(context, action)) {
            context.startActivity(new Intent(action));
        } else {
            download(context, pkg);
        }
    }

    public static void download(Context context, String pkg) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + pkg)));
    }
}
