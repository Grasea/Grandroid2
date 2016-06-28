/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.actions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

/**
 * @author Rovers
 */
public class ShareAction extends ContextAction {

    protected String title;
    protected String content;
    protected String caption;
    protected String assignedPackage;
    protected boolean isSubTask;
    protected int requestCode = 0;

    public ShareAction(Context context) {
        super(context);
        content = "";
        caption = "分享至";
    }

    public ShareAction(Context context, String actionName) {
        super(context, actionName);
        content = "";
        caption = "分享至";
    }

    public ShareAction setTitle(String title) {
        this.title = title;
        return this;
    }

    public ShareAction setContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * @return
     */
    public ShareAction setSubTask() {
        return setSubTask(0);
    }

    /**
     * @param requestCode
     * @return
     */
    public ShareAction setSubTask(int requestCode) {
        isSubTask = true;
        this.requestCode = requestCode;
        return this;
    }

    public ShareAction setDialogCaption(String caption) {
        this.caption = caption;
        return this;
    }

    public ShareAction setPackage(String assignedPackage) {
        this.assignedPackage = assignedPackage;
        return this;
    }

    @Override
    public boolean execute(Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

//                        
        //intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        if (title != null) {
            intent.putExtra(Intent.EXTRA_TITLE, title);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (assignedPackage == null) {
            if (isSubTask && context instanceof Activity) {
                ((Activity) context).startActivityForResult(intent, requestCode);
            } else {
                context.startActivity(Intent.createChooser(intent, caption));
            }
        } else {
            try {
                context.getPackageManager().getApplicationInfo(assignedPackage, 0);

                intent.setPackage(assignedPackage);//"com.sina.weibo"
                if (isSubTask && context instanceof Activity) {
                    intent.setPackage(assignedPackage);//"com.sina.weibo"
                    ((Activity) context).startActivityForResult(intent, requestCode);
                } else {
                    context.startActivity(intent);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

        }
        return true;
    }
}
