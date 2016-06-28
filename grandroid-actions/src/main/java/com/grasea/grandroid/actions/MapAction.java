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
public class MapAction extends ViewAction {

    public MapAction(Context context) {
        super(context);
        this.setClass("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
    }

    public MapAction(Context context, String actionName) {
        super(context, actionName);
        this.setClass("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
    }

    public MapAction setLocation(double lat, double lng, int zoom) {
        uri = "geo:" + lat + "," + lng + "?z=" + zoom;
        return this;
    }

    public MapAction setLocation(String address) {
        if (address != null && !address.isEmpty()) {
            uri = "geo:0,0?q=" + address;
        }
        return this;
    }
}
