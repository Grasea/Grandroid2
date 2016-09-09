/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.phone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;

/**
 *
 * @author Rovers
 */
public class SMSHelper {

    /**
     * 
     */
    public static final String SMS_REC = "android.provider.Telephony.SMS_RECEIVED";

    /**
     * 
     * @param context
     * @param intent
     * @return
     */
    public String retrieveSMS(Context context, Intent intent) {
        StringBuilder sb = new StringBuilder();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Object[] myOBJpdus = (Object[]) bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[myOBJpdus.length];
            for (int i = 0; i < messages.length; i++) {
                messages[i] = SmsMessage.createFromPdu((byte[]) myOBJpdus[i]);
            }
            for (SmsMessage currentMessage : messages) {
                sb.append(currentMessage.getDisplayMessageBody());
            }
        }
        return sb.toString();
//        Logger.getLogger(SMSHelper.class.getName()).log(Level.INFO, sb.toString());
//        Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG).show();
//        Intent i = new Intent(context, Arcface.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(i);

    }
}
