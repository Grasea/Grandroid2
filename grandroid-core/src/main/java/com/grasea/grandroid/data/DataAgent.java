/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

/**
 * 記錄、載回app資料的物件，實作上採用Android的SharedPreference機制(類似Java的Properties)
 * 除了Service以外，Activity應繼承Face、呼叫getData()函數來取得實體
 *
 * @author Rovers
 */
public class DataAgent {

    //protected static ConcurrentHashMap data;
    /**
     *
     */
    protected SharedPreferences settings;

    /**
     *
     * @param context
     */
    public DataAgent(Context context) {
        settings = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     *
     * @param settings
     */
    public DataAgent(SharedPreferences settings) {
        this.settings = settings;
    }

    /**
     * 將一組key-value放進SharedPreference
     *
     * @param key
     * @param value
     * @return 回傳value本身
     */
    public String putPreference(String key, String value) {
        settings.edit().putString(key, value).commit();
        return value;
    }

    public int putPreference(String key, int value) {
        settings.edit().putInt(key, value).commit();
        return value;
    }

    public float putPreference(String key, float value) {
        settings.edit().putFloat(key, value).commit();
        return value;
    }

    public long putPreference(String key, long value) {
        settings.edit().putLong(key, value).commit();
        return value;
    }

    public boolean putPreference(String key, boolean value) {
        settings.edit().putBoolean(key, value).commit();
        return value;
    }

    /**
     *
     * @return
     */
    public SharedPreferences getPreferences() {
        return settings;
    }

    /**
     *
     * @return
     */
    public Editor getEditor() {
        return settings.edit();
    }

    /**
     * 從SharedPreference裡取出對應至該key的值
     *
     * @param key
     * @return 對應至該key的值，若不存在則回傳空字串(非null)
     */
    public String getPreference(String key) {
        return settings.getString(key, "");
    }

    public String getPreference(String key, String defaultValue) {
        return settings.getString(key, defaultValue);
    }

    /**
     * 從SharedPreference裡取出對應至該key的值
     *
     * @param key
     * @return 對應至該key的值，若不存在則回傳0(非null)
     */
    public int getInt(String key) {
        return settings.getInt(key, 0);
    }

    public int getInt(String key, int defaultValue) {
        return settings.getInt(key, defaultValue);
    }

    /**
     * 從SharedPreference裡取出對應至該key的值
     *
     * @param key
     * @return 對應至該key的值，若不存在則回傳0(非null)
     */
    public long getLong(String key) {
        return settings.getLong(key, 0l);
    }

    public long getLong(String key, long defaultValue) {
        return settings.getLong(key, defaultValue);
    }

    /**
     * 從SharedPreference裡取出對應至該key的值
     *
     * @param key
     * @return 對應至該key的值，若不存在則回傳0.0f(非null)
     */
    public float getFloat(String key) {
        return settings.getFloat(key, 0f);
    }

    public float getFloat(String key, float defaultValue) {
        return settings.getFloat(key, defaultValue);
    }

    /**
     * 從SharedPreference裡取出對應至該key的值
     *
     * @param key
     * @return 對應至該key的值，若不存在則回傳false(非null)
     */
    public boolean getBoolean(String key) {
        return settings.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return settings.getBoolean(key, defaultValue);
    }
}
