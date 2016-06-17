package com.grasea.grandroid.sample.database;

import android.database.Cursor;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

/**
 *
 * @author Rovers
 */
public enum TypeMapping {

    /**
     *
     */
    BOOLEAN,
    /**
     *
     */
    INT,
    /**
     *
     */
    DOUBLE,
    /**
     *
     */
    FLOAT,
    /**
     *
     */
    LONG,
    /**
     *
     */
    STRING,
    /**
     *
     */
    DATETIME,
    /**
     *
     */
    JSONOBJECT,
    /**
     *
     */
    JSONARRAY;

    /**
     *
     * @param typeClass
     * @return
     */
    public static TypeMapping getType(Class typeClass) {
        if (typeClass == int.class || typeClass == Integer.class) {
            return INT;
        } else if (typeClass == double.class || typeClass == Double.class) {
            return DOUBLE;
        } else if (typeClass == float.class || typeClass == Float.class) {
            return FLOAT;
        } else if (typeClass == long.class || typeClass == Long.class) {
            return LONG;
        } else if (typeClass == boolean.class || typeClass == Boolean.class) {
            return BOOLEAN;
        } else if (typeClass == Date.class) {
            return DATETIME;
        } else if (typeClass == JSONObject.class) {
            return JSONOBJECT;
        } else if (typeClass == JSONArray.class) {
            return JSONARRAY;
        } else {
            return STRING;
        }
    }

    /**
     *
     * @param cursor
     * @param fieldName
     * @return
     */
    public Object getResultSetValue(Cursor cursor, String fieldName) {
        int index = cursor.getColumnIndex(fieldName);
        return index >= 0 ? getResultSetValue(cursor, index) : null;
    }

    /**
     *
     * @param value
     * @return
     */
    public Object envalue(Object value) {
        if (value.getClass() == Date.class) {
            return ((Date) value).getTime();
        }
        if (value.getClass() == boolean.class || value.getClass() == Boolean.class) {
            return (Boolean) value ? 1 : 0;
        }
        return value;
    }

    /**
     *
     * @param cursor
     * @param index
     * @return
     */
    public Object getResultSetValue(Cursor cursor, int index) {
        try {
            switch (this) {
                case BOOLEAN:
                    return cursor.getInt(index) == 1;
                case INT:
                    return cursor.getInt(index);
                case DOUBLE:
                    return cursor.getDouble(index);
                case FLOAT:
                    return cursor.getFloat(index);
                case LONG:
                    return cursor.getLong(index);
                case STRING:
                    return cursor.getString(index);
                case DATETIME:
                    return new Date(cursor.getLong(index));
                case JSONOBJECT:
                    String json = cursor.getString(index);
                    if (json == null) {
                        return null;
                    } else {
                        return new JSONObject(json);
                    }
                case JSONARRAY:
                    String jsonarr = cursor.getString(index);
                    if (jsonarr == null) {
                        return null;
                    } else {
                        return new JSONArray(jsonarr);
                    }
            }
        } catch (Exception ex) {
            Log.e("grandroid", null, ex);
        }
        return null;
    }

    /**
     *
     * @return
     */
    public String getSqlType() {
        switch (this) {
            case BOOLEAN:
                return "integer";
            case INT:
                return "integer";
            case DOUBLE:
                return "real";
            case FLOAT:
                return "real";
            case LONG:
                return "integer";
            case STRING:
                return "text";
            case DATETIME:
                return "integer";
            default:
                return "text";
        }
    }
}
