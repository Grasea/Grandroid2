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
public class ShowSMSAction extends SendSMSAction {

    public ShowSMSAction(Context context) {
        super(context);
    }

    public ShowSMSAction(Context context, String actionName) {
        super(context, actionName);
    }

    public ShowSMSAction(Context context, String tel, String message) {
        super(context, tel, message);
    }

    public ShowSMSAction(Context context, String actionName, String tel, String message) {
        super(context, actionName, tel, message);
    }

    @Override
    public boolean execute(Context context) {
        if (tel == null) {
            tel = "";
        }
        if (message == null) {
            message = "";
        }
        Uri uri = Uri.parse("smsto:" + tel);
        Intent it = new Intent(Intent.ACTION_VIEW, uri);
        it.putExtra("sms_body", message);
        it.putExtra("address", tel);
        it.setType("vnd.android-dir/mms-sms");
        context.startActivity(it);
        return true;
    }
}
