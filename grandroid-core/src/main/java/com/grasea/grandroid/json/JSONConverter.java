/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.database.json;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Rovers
 */
public class JSONConverter {

    /**
     *
     */
    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");

    public static <T> T toObject(JSONObject jo, T obj) {
        String processField = "";
        try {
            Field[] farr = obj.getClass().getDeclaredFields();
            for (int i = 0; i < farr.length; i++) {
                farr[i].setAccessible(true);
                processField = farr[i].getName();
                if (farr[i].getName().equals("mongoID")) {
                    if (jo.has("_id")) {
                        farr[i].set(obj, jo.getJSONObject("_id").getString("$oid"));
                    }
                } else if (!farr[i].getName().equals("_id")) {
                    if (jo.has(farr[i].getName()) && !jo.isNull(farr[i].getName())) {
                        if (farr[i].getType().equals(Date.class)) {
                            farr[i].set(obj, sdf.parse(jo.getString(farr[i].getName())));
                        } else if (farr[i].getType().equals(String.class)) {
                            farr[i].set(obj, jo.get(farr[i].getName()).toString());
                        } else if (farr[i].getType().equals(ArrayList.class)) {
                            ParameterizedType stringListType = (ParameterizedType) farr[i].getGenericType();
                            Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
                            ArrayList list = toList(jo.getJSONArray(farr[i].getName()), stringListClass);
                            farr[i].set(obj, list);
                        } else if (farr[i].getType().equals(JSONObject.class)) {
                            farr[i].set(obj, jo.get(farr[i].getName()));
                        } else if (farr[i].getType().equals(JSONArray.class)) {
                            farr[i].set(obj, jo.get(farr[i].getName()));
                        } else {
                            Object value = jo.get(farr[i].getName());
                            if (value instanceof JSONObject) {
                                farr[i].set(obj, toObject((JSONObject) value, farr[i].getType()));
                            } else {
                                try {
                                    if (value.getClass() == Integer.class) {
                                        if (farr[i].getType() == Double.class) {
                                            farr[i].set(obj, ((Integer) value).doubleValue());
                                        } else if (farr[i].getType() == Float.class) {
                                            farr[i].set(obj, ((Integer) value).floatValue());
                                        }else{
                                            farr[i].set(obj, value);
                                        }
                                    } else {
                                        farr[i].set(obj, value);
                                    }
                                } catch (Exception ex) {
                                    Log.d("grandroid", "can't set field " + farr[i].getName() + " with value " + value + " of type " + value.getClass().getSimpleName());
                                    throw ex;
                                }
                            }
                        }
                    }
                }
            }
            return obj;
        } catch (Exception ex) {
            Log.e("grandroid", "Field:" + processField, ex);
        }
        return null;
    }

    /**
     *
     * @param <T>
     * @param jo
     * @param c
     * @return
     */
    public static <T> T toObject(JSONObject jo, Class<T> c) {
        try {
            T obj = c.newInstance();
            return toObject(jo, obj);
        } catch (Exception ex) {
            Log.e("grandroid", null, ex);
            return null;
        }
    }

    public static ArrayList toList(JSONArray arr, Class stringListClass) {
        ArrayList list = new ArrayList();//(ArrayList) farr[i].getType().newInstance()
        try {
            //ParameterizedType stringListType = (ParameterizedType) farr[i].getGenericType();
            //Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
            //JSONArray arr = jo.getJSONArray(farr[i].getName());
            for (int j = 0; j < arr.length(); j++) {
                if (stringListClass == Boolean.class) {
                    list.add(j, arr.getBoolean(j));
                } else if (stringListClass == Integer.class) {
                    list.add(j, arr.getInt(j));
                } else if (stringListClass == Double.class) {
                    list.add(j, arr.getDouble(j));
                } else if (stringListClass == String.class) {
                    list.add(j, arr.getString(j));
                } else if (stringListClass == Object.class) {
                    list.add(j, arr.get(j));
                } else if (stringListClass == ArrayList.class) {
                    list.add(j, toList(arr.getJSONArray(j), Object.class));
                } else if (stringListClass == Date.class) {
                    list.add(j, sdf.parse(arr.getString(j)));
                } else {
                    list.add(j, toObject(arr.getJSONObject(j), stringListClass));
                }
            }
        } catch (Exception ex) {
            Log.e("grandroid", stringListClass.getSimpleName() + " array parse fail:" + arr.toString(), ex);
        }
        return list;
    }

    /**
     *
     * @param <T>
     * @param obj
     * @return
     */
    public static <T> JSONObject fromObject(T obj) {
        try {
            Class<T> c = (Class<T>) obj.getClass();
            JSONObject jo = new JSONObject();
            Field[] farr = c.getDeclaredFields();
            for (int i = 0; i < farr.length; i++) {
                farr[i].setAccessible(true);
                Object value = farr[i].get(obj);
                if (value != null) {
                    if (farr[i].getName().equals("mongoID")) {
                        if (farr[i].get(obj) != null) {
                            jo.put("_id", new JSONObject().put("$oid", value));
                        }
                    } else if (!farr[i].getName().equals("_id")) {
                        if (farr[i].getType().equals(Date.class)) {
                            jo.put(farr[i].getName(), sdf.format((Date) value));
                        } else if (value instanceof String) {
                            if (((String) value).startsWith("[")) {
                                jo.put(farr[i].getName(), new JSONArray((String) value));
                            } else if (((String) farr[i].get(obj)).startsWith("{")) {
                                jo.put(farr[i].getName(), new JSONObject((String) value));
                            } else {
                                jo.put(farr[i].getName(), value);
                            }
                        } else if (farr[i].getType().isPrimitive() || farr[i].getType().equals(Date.class) || farr[i].getType().equals(Integer.class) || farr[i].getType().equals(Long.class) || farr[i].getType().equals(Boolean.class) || farr[i].getType().equals(Float.class) || farr[i].getType().equals(Double.class) || farr[i].getType().equals(JSONObject.class) || farr[i].getType().equals(JSONArray.class)) {
                            jo.put(farr[i].getName(), value);
                        } else {
                            if (farr[i].getType().equals(ArrayList.class)) {
                                ArrayList list = (ArrayList) value;
                                ParameterizedType stringListType = (ParameterizedType) farr[i].getGenericType();
                                Class<?> stringListClass = (Class<?>) stringListType.getActualTypeArguments()[0];
                                JSONArray array = fromList(list, stringListClass);
                                jo.put(farr[i].getName(), array);
                            } else {
                                jo.put(farr[i].getName(), fromObject(value));
                            }
                        }
                    }
                }
            }
            return jo;
        } catch (Exception ex) {
            Log.e("grandroid", null, ex);
        }
        return null;
    }

    public static <T> JSONArray fromList(ArrayList<T> list, Class<T> c) {
        JSONArray array = new JSONArray();
        for (T item : list) {
            if (c == ArrayList.class) {
                array.put(fromList((ArrayList) item, ArrayList.class));
            } else if (c == Date.class) {
                array.put(sdf.format(item));
            } else if (c == Integer.class || c == Double.class || c == String.class || c == Boolean.class) {
                array.put(item);
            } else {
                array.put(fromObject(item));
            }
        }
        return array;
    }

    /**
     *
     * @param date
     * @return
     */
    public String formatDate(Date date) {
        return sdf.format(date);
    }

    /**
     *
     * @return
     */
    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
    }
}
