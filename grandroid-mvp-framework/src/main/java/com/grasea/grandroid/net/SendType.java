package com.grasea.grandroid.net;

/**
 * Created by Rovers on 2016/8/10.
 */
public enum SendType {
    FormData("application/form-data"), FormUrlencoded("application/x-www-form-urlencoded"), Json("application/json");

    String contentType;

    SendType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }
}
