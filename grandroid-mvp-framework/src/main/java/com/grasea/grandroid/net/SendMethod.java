package com.grasea.grandroid.net;

import com.android.volley.Request;

/**
 * Created by Rovers on 2016/8/10.
 */
public enum SendMethod {
    Get(Request.Method.GET), Post(Request.Method.POST), Put(Request.Method.PUT), Delete(Request.Method.DELETE);

    int volleyMethod;

    SendMethod(int volleyMethod) {
        this.volleyMethod = volleyMethod;
    }

    public int getVolleyMethod() {
        return volleyMethod;
    }
}
