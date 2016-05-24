package com.grasea.ble;

import android.util.Log;

public class Config {
    public static boolean DEBUG = true;
    public static String TAG = "grandroid-ble";
    public static int SCREEN_WIDTH = 1080;

    public static void logd(String message) {
        if (DEBUG) {
            Log.d(Config.TAG, message);
        }
    }

    public static void logd(Boolean message) {
        if (DEBUG) {
            Log.d(Config.TAG, message.toString());
        }
    }

    public static void loge(String message) {
        if (DEBUG) {
            Log.e(Config.TAG, message);
        }
    }

    public static void loge(Throwable th) {
        if (DEBUG) {
            Log.e(Config.TAG, null, th);
        }
    }

    public static void logi(String message) {
        if (DEBUG) {
            Log.i(Config.TAG, message);
        }
    }

    public static void logi(Boolean message) {
        if (DEBUG) {
            Log.i(Config.TAG, message.toString());
        }
    }

}

