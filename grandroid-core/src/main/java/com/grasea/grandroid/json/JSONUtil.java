/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.database.json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.TreeSet;

/**
 *
 * @author Rovers
 */
public class JSONUtil {

    /**
     *
     * @param arr
     * @param index
     * @return
     * @throws JSONException
     */
    public static JSONArray removeArrayItem(JSONArray arr, int index) throws JSONException {
        JSONArray newArr = new JSONArray();
        for (int i = 0; i < arr.length(); i++) {
            if (index != i) {
                newArr.put(arr.get(i));
            }
        }
        return newArr;
    }

    public static JSONArray sortByStringAttribute(JSONArray arr, final String attrName, final boolean asc) {
        return sort(arr, new Comparator<JSONObject>() {

            public int compare(JSONObject arg0, JSONObject arg1) {
                return (asc ? 1 : -1) * arg0.optString(attrName).compareTo(arg1.optString(attrName));
            }
        });
    }

    public static JSONArray sort(JSONArray arr, Comparator<JSONObject> comparator) {
        JSONArray f = new JSONArray();
        TreeSet<JSONObject> ts = new TreeSet<JSONObject>(comparator);
        for (int i = 0; i < arr.length(); i++) {
            try {
                ts.add(arr.getJSONObject(i));
            } catch (JSONException ex) {
                Log.e("grandroid", null, ex);
            }
        }
        for (JSONObject jo : ts) {
            f.put(jo);
        }
        return f;
    }
}
