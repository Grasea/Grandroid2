/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.data;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Rovers
 */
public class StateKeeper {

    /**
     *
     */
    protected ConcurrentHashMap subject;
    protected SharedPreferences settings;
    protected HashSet<String> temporaryKeys;

    public StateKeeper(Context context) {
        subject = new ConcurrentHashMap();
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        temporaryKeys = new HashSet<String>();
    }

    /**
     * 記錄下view的值 (目前只支援TextView及EditText)
     *
     * @param <T>
     * @param obj 設定過Tag屬性的View
     * @return
     */
    public <T extends View> T keep(T obj) {
        return keep(obj, true);
    }

    /**
     * 記錄下view的值 (目前只支援TextView及EditText)
     *
     * @param activity
     * @param viewID
     * @return
     */
    public View keep(Activity activity, int viewID) {
        return keep(activity, viewID, true);
    }

    /**
     * 記錄下view的值 (目前只支援TextView及EditText)
     *
     * @param activity
     * @param viewID Resource ID
     * @param fillView 是否要載入先前的資料
     * @return
     */
    public View keep(Activity activity, int viewID, boolean fillView) {
        View obj = activity.findViewById(viewID);
        if (obj != null) {
            keep(obj, fillView);
        }
        return obj;
    }

    /**
     * 記錄下view的值 (目前只支援TextView及EditText)
     *
     * @param <T>
     * @param obj view物件
     * @param fillView 是否要載入先前的資料
     * @return
     */
    public <T extends View> T keep(T obj, boolean fillView) {
        if (obj.getTag() == null) {
            String tempKey = String.valueOf((int) Math.random() * 99999);
            temporaryKeys.add(tempKey);
            obj.setTag(tempKey);
        }
        if (fillView) {
            load(obj);
        }
        subject.put(obj.getTag(), obj);
        return obj;
    }

    /**
     * 儲存目前Activity裡所有宣告過keep的view值
     */
    public void digest() {
        if (subject.size() > 0) {
            for (Object obj : subject.values()) {
                if (obj instanceof View) {
                    save((View) obj);
                }
            }
            settings.edit().commit();
        }
    }

    public void removeTemporaryKeys() {
        if (temporaryKeys.size() > 0) {
            Editor ed = settings.edit();
            for (String key : temporaryKeys) {
                ed.remove(key);
            }
            ed.commit();
        }
    }

    /**
     * 儲存view的值，需設定過tag
     *
     * @param obj view物件
     */
    protected void save(View obj) {
        Editor editor = settings.edit();
        if (obj instanceof TextView) {
            editor.putString(obj.getTag().toString(), ((TextView) obj).getText().toString());
        } else if (obj instanceof EditText) {
            editor.putString(obj.getTag().toString(), ((EditText) obj).getText().toString()).commit();
        } else if (obj instanceof ListView) {
            editor.putInt(obj.getTag().toString(), ((ListView) obj).getFirstVisiblePosition()).commit();
        } else if (obj instanceof Gallery) {
            editor.putInt(obj.getTag().toString(), ((Gallery) obj).getFirstVisiblePosition()).commit();
        } else if (obj instanceof GridView) {
            editor.putInt(obj.getTag().toString(), ((GridView) obj).getFirstVisiblePosition()).commit();
        }
        editor.commit();
    }

    /**
     * 載入該view前次的值，需設定過tag
     *
     * @param obj view物件
     * @return 回傳該載入的值，若沒有資料則為空字串(不是null)
     */
    protected void load(View obj) {
        String tag = obj.getTag().toString();
        if (settings.contains(tag)) {
            if (obj instanceof TextView) {
                ((TextView) obj).setText(settings.getString(tag, ""));
            } else if (obj instanceof EditText) {
                ((EditText) obj).setText(settings.getString(tag, ""));
            } else if (obj instanceof ListView) {
                ((ListView) obj).setSelectionFromTop(settings.getInt(tag, 0), 0);
            } else if (obj instanceof Gallery) {
                ((Gallery) obj).setSelection(settings.getInt(tag, 0));
            } else if (obj instanceof GridView) {
                ((GridView) obj).setSelection(settings.getInt(tag, 0));
            }
        }
    }
}
