/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.actions;

import android.content.Context;

/**
 *
 * @author Rovers
 */
public class YoutubeAction extends ViewAction {

    public YoutubeAction(Context context) {
        super(context);
    }

    public YoutubeAction(Context context, String actionName) {
        super(context, actionName);
    }

    public YoutubeAction setURL(String url) {
        uri = url;
        return this;
    }
}
