/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.actions;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

/**
 * @author Rovers 在AndroidManifest.xml要加上下面設定，才會顯示
 *         <activity android:name="grandroid.activity.DialogActivity" android:theme =
 *         "@style/Transparent"/>
 */
public class NotifyAction extends ContextAction {

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
    protected int icon;
    /**
     *
     */
    protected int group;
    /**
     *
     */
    protected Class target;
    /**
     *
     */
    protected Bundle extra;
    /**
     *
     */
    protected int flag;
    protected int targetFlag;
    /**
     *
     */
    protected boolean autoCancel;
    /**
     *
     */
    protected boolean virbation;

    protected boolean actSound = false;
    protected Uri soundUri;

    /**
     *
     */
    protected Bitmap largeIcon;

    /**
     *
     */
    protected int color;

    /**
     * 在AndroidManifest.xml要加上下面設定，才會顯示
     * <activity android:name="grandroid.activity.DialogActivity" android:theme
     * = "@style/Transparent"/>
     *
     * @param context
     * @param actionName
     */
    public NotifyAction(Context context, String actionName) {
        super(context, actionName);
        title = "未設定";
        icon = R.drawable.ic_dialog_info;
        group = 0;
        target = null;
        flag = PendingIntent.FLAG_UPDATE_CURRENT;
        //targetFlag = Intent.FLAG_ACTIVITY_NEW_TASK;
        autoCancel = true;
    }

    /**
     * 在AndroidManifest.xml要加上下面設定，才會顯示
     * <activity android:name="grandroid.activity.DialogActivity" android:theme
     * = "@style/Transparent"/>
     *
     * @param context
     */
    public NotifyAction(Context context) {
        this(context, "");
    }

    /**
     * @param title
     * @param msg
     * @return
     */
    public NotifyAction setContent(String title, String msg) {
        this.title = title;
        this.msg = msg;
        return this;
    }

    /**
     * @param group
     * @return
     */
    public NotifyAction setGroup(int group) {
        this.group = group;
        return this;
    }

    /**
     * @param icon
     * @return
     */
    public NotifyAction setIcon(int icon) {
        this.icon = icon;
        return this;
    }

    /**
     * @param target
     * @param extra
     * @return
     */
    public NotifyAction setTarget(Class target, Bundle extra) {
        this.target = target;
        this.extra = extra;
        return this;
    }


    /**
     * @param flag
     * @return
     */
    public NotifyAction setFlag(int flag) {
        this.flag = flag;
        return this;
    }

    public NotifyAction setTargetFlag(int targetFlag) {
        this.targetFlag = targetFlag;
        return this;
    }

    /**
     * @param autoCancel
     * @return
     */
    public NotifyAction setAutoCancel(boolean autoCancel) {
        this.autoCancel = autoCancel;
        return this;
    }

    /**
     * @return
     */
    public boolean isVirbation() {
        return virbation;
    }

    /**
     * @param virbation
     * @return
     */
    public NotifyAction setVirbation(boolean virbation) {
        this.virbation = virbation;
        return this;
    }

    public boolean isActSound() {
        return actSound;
    }

    public NotifyAction setActSound(boolean actSound) {
        this.actSound = actSound;
        return this;
    }

    public Uri getSoundUri() {
        return soundUri;
    }

    public NotifyAction setSoundUri(Uri soundUri) {
        this.soundUri = soundUri;
        this.actSound = true;
        return this;
    }

    public NotifyAction setLargeIcon(Bitmap largeIcon) {
        this.largeIcon = largeIcon;
        return this;
    }

    public Bitmap getLargeIcon() {
        return largeIcon;
    }

    public NotifyAction setColor(int color) {
        this.color = color;
        return this;
    }

    /**
     * @param context
     * @return
     */
    @Override
    public boolean execute(Context context) {
        CharSequence contentTitle = title;
        CharSequence contentText = msg;

        Intent notificationIntent = null;
        if (target != null) {
            notificationIntent = new Intent(context, target);
            if (targetFlag != 0) {
                notificationIntent.setFlags(targetFlag);
            }

            notificationIntent.setAction(Intent.ACTION_MAIN);
            notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

            Log.d("grandroid", "notification put bundle:" + extra.toString());
            notificationIntent.putExtras(extra);
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(context)
                .setSmallIcon(icon)
                .setContentTitle(contentTitle)
                .setContentText(contentText);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(autoCancel);
        builder.setLights(Color.BLUE, 500, 500);
        if (virbation) {
//            no.defaults |= Notification.DEFAULT_VIBRATE;
            long[] vibrate = {0, 100, 200, 300};
            builder.setVibrate(vibrate);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (color != 0) {
                builder.setColor(color);
            }
        }
//        builder.setStyle(new NotificationCompat.InboxStyle());
//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (actSound) {
            if (soundUri == null) {
                builder.setDefaults(Notification.DEFAULT_SOUND);
//                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//                if (alarmSound == null) {
//                    alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
//                    if (alarmSound == null) {
//                        alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                    }
//                }
//                soundUri = alarmSound;
            } else {
                builder.setSound(soundUri);
            }
        }
        if (largeIcon != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(largeIcon));
            } else {
                builder.setLargeIcon(largeIcon);
            }
        }

// Add as notification  
        manager.notify(group, builder.build());
        return true;

//        Notification no = new Notification(icon, msg, System.currentTimeMillis());
//        if (autoCancel) {
//            no.flags = Notification.FLAG_AUTO_CANCEL;;
//        }
//        CharSequence contentTitle = title;
//        CharSequence contentText = msg;
//        Intent notificationIntent = null;
//        if (target != null) {
//            notificationIntent = new Intent(context, target);
//            notificationIntent.setFlags(targetFlag);
//            if (target == DialogActivity.class) {
//                if (extra == null) {
//                    extra = new Bundle();
//                }
//                extra.putString("TITLE", title);
//                extra.putString("CONTENT", msg);
//            }
//            notificationIntent.putExtras(extra);
//        }
//        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, flag);
//
//        no.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
//        if (virbation) {
//            no.defaults |= Notification.DEFAULT_VIBRATE;
//            long[] vibrate = {0, 100, 200, 300};
//            no.vibrate = vibrate;
//        }
//        manager.notify(group, no);
//        return true;
    }
}
