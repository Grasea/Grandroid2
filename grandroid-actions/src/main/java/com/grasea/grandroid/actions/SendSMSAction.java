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
public class SendSMSAction extends ContextAction {

    /**
     *
     */
    protected String tel;
    protected String message;

    public SendSMSAction(Context context) {
        super(context);
    }

    public SendSMSAction(Context context, String actionName) {
        super(context, actionName);
    }

    public SendSMSAction(Context context, String tel, String message) {
        super(context);
        this.tel = tel;
        this.message = message;
    }

    public SendSMSAction(Context context, String actionName, String tel, String message) {
        super(context, actionName);
        this.tel = tel;
        this.message = message;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
        Intent it = new Intent(Intent.ACTION_SENDTO, uri);
        it.putExtra("sms_body", message);
        context.startActivity(it);
        return true;
    }
}
