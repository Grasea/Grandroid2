/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @param <T>
 * @author Rovers
 */
public class GenericHelper<T extends Identifiable> extends DataHelper<T> {

    /**
     *
     */
    protected static CopyOnWriteArrayList<String> verifiedTables = new CopyOnWriteArrayList<String>();
    /**
     *
     */
    protected Class<T> classData;
    /**
     *
     */
    protected TypeMapping[] types;
    /**
     *
     */
    protected Field[] fields;
    /**
     *
     */
    protected int indexID;

    /**
     *
     * @param fd
     * @param objClass
     */
    public GenericHelper(RawFaceData fd, Class<T> objClass) {
        this(fd, objClass, true);
    }

    /**
     *
     * @param fd
     * @param objClass
     * @param createTable
     */
    public GenericHelper(RawFaceData fd, Class<T> objClass, boolean createTable) {
        super(fd, objClass, false);
        Log.d("grandroid", "DataHelper initialized");
        classData = objClass;
        Field[] farr = objClass.getDeclaredFields();
        types = new TypeMapping[farr.length];
        fields = new Field[farr.length];
        for (int idx = 0; idx < farr.length; idx++) {
            if (farr[idx].getName().equals("_id")) {
                indexID = idx;
            }
            fields[idx] = farr[idx];
            fields[idx].setAccessible(true);
            types[idx] = TypeMapping.getType(farr[idx].getType());
        }
        if (createTable) {
            this.create();
        }
        if (!verifiedTables.contains(tableName)) {
            ContentValues cv = new ContentValues();
            for (int idx = 0; idx < farr.length; idx++) {
                cv.put(fields[idx].getName(), "");
            }
            Log.d("grandroid", "start to fix table " + this.getTableName());
            fixTable(cv);
            verifiedTables.add(tableName);
        }
        for (int i = 0; i < farr.length; i++) {
            if (i != indexID) {
                if (farr[i].isAnnotationPresent(Index.class)) {
                    fd.exec("CREATE INDEX IF NOT EXISTS " + tableName + "_" + farr[i].getName() + "_idx ON " + tableName + "(" + farr[i].getName() + ");");
                }
            }
        }
    }

    /**
     *
     * @param column
     * @return
     */
    @Override
    public TypeMapping getColumnType(String column) {
        for (int idx = 0; idx < fields.length; idx++) {
            if (column.equalsIgnoreCase(fields[idx].getName())) {
                return types[idx];
            }
        }
        return TypeMapping.STRING;
    }

    /**
     *
     * @return
     */
    @Override
    public String getCreationString() {
        String str = "";
        for (int i = 0; i < fields.length; i++) {
            if (i != indexID) {
                str += str.length() == 0 ? "[" + fields[i].getName() + "] " + types[i].getSqlType() : ", [" + fields[i].getName() + "] " + types[i].getSqlType();
            }
        }
        return str;
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public Integer getID(T obj) {
        return obj.get_id();
    }

    /**
     *
     * @param obj
     * @return
     */
    @Override
    public ContentValues getKeyValues(T obj) throws Exception {
        if (obj == null) {
            Log.e("grandroid", "obj is null, can't retrieve key and values");
            throw new Exception("obj is null, can't retrieve key and values");
        }
        ContentValues kvs = new ContentValues();
        for (int i = 0; i < fields.length; i++) {
            if (i != indexID) {
                try {
                    Object value = fields[i].get(obj);
                    if (value != null) {
                        //str += str.length() == 0 ? fields[i].getName() + "=" + types[i].envalue(value) : ", " + fields[i].getName() + "=" + types[i].envalue(value);
                        kvs.put("[" + fields[i].getName() + "]", types[i].envalue(value).toString());
                    } else {
                        Log.w("grandroid", "getter " + fields[i].getName() + " has error (value is null)");
                    }
                } catch (Exception ex) {
                    Log.e("grandroid", null, ex);
                }
            }
        }
        //Log.i(this.getClass().getName(), "obj : " + str);
        return kvs;
    }

    /**
     *
     * @param cursor
     * @return
     */
    @Override
    public T getObject(Cursor cursor) {
        try {
            T obj = classData.newInstance();
            for (int i = 0; i < fields.length; i++) {
                try {
                    fields[i].set(obj, types[i].getResultSetValue(cursor, fields[i].getName()));
                } catch (Exception ex) {
                    Log.e(this.getClass().getName(), "%%%%%%%%%%%%%%" + fields[i] + "%%%%%%%%%%%%%");
                    Log.e(this.getClass().getName(), null, ex);
                }
            }
            return obj;
        } catch (Exception ex) {
            Log.e("grandroid", null, ex);
            return null;
        }
    }

    /**
     *
     * @param <S>
     * @param c
     * @return
     */
    public <S extends Identifiable> GenericHelper<S> createHelper(Class<S> c) {
        return new GenericHelper<S>(fd, c);
    }

    /**
     *
     * @param <S>
     * @param c
     * @param createTable
     * @return
     */
    public <S extends Identifiable> GenericHelper<S> createHelper(Class<S> c, boolean createTable) {
        return new GenericHelper<S>(fd, c, createTable);
    }
}
