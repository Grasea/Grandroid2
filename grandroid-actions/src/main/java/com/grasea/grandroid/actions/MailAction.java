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
public class MailAction extends ContextAction {

    protected String receiver;
    protected String subject;
    protected String content;

    public MailAction(Context context) {
        super(context);
        receiver = "";
        subject = "";
        content = "";
    }

    public MailAction(Context context, String actionName) {
        super(context, actionName);
        receiver = "";
        subject = "";
        content = "";
    }

    public MailAction setReceiver(String receiver) {
        this.receiver = receiver;
        return this;
    }

    public MailAction setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public MailAction setContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public boolean execute(Context context) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", receiver == null ? "" : receiver, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject == null ? "" : subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, content == null ? "" : content);
        context.startActivity(Intent.createChooser(emailIntent, "Send Email"));
        return true;
    }
}
