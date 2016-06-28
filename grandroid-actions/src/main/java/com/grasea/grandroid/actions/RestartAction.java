/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.actions;

import android.content.Context;
import android.content.Intent;

/**
 * first activity should add attribute " android:clearTaskOnLaunch"
 *
 * @author Rovers
 */
public class RestartAction extends ContextAction {

    public RestartAction(Context context, String actionName) {
        super(context, actionName);
    }

    public RestartAction(Context context) {
        super(context);
    }

    @Override
    public boolean execute(Context context) {
        Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//        ComponentName componentInfo = taskInfo.get(0).topActivity;
//        am.restartPackage(componentInfo.getPackageName());
        return true;
    }
}
