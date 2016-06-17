/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.sample.database;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

/**
 *
 * @author Rovers
 */
public class FaceData extends RawFaceData implements DataOperator {

    private final Object locks=new Object();

    /**
     *
     * @param context
     * @param dbName
     */
    public FaceData(Context context, String dbName) {
        super(context, dbName, false);
    }

    /**
     *
     * @param tableName
     * @param fieldPart
     * @return
     */
    public boolean createTable(String tableName, String fieldPart) {
        synchronized (locks) {
            startEdit();
            try {
                return super.createTable(tableName, fieldPart);
            } catch (Exception e) {
                Log.e("grandroid", "fieldPare = " + fieldPart);
                Log.e("grandroid", null, e);
                return false;
            } finally {
                endEdit();
            }
        }
    }

    /**
     *
     * @param tableName
     * @param col
     * @param type
     * @return
     */
    public boolean addColumn(String tableName, String col, TypeMapping type) {
        return addColumn(tableName, col, type, null);
    }

    /**
     *
     * @param tableName
     * @param cv
     * @return
     * @throws Exception
     */
    @Override
    public long insert(String tableName, ContentValues cv) {
        synchronized (locks) {
            startEdit();
            try {
                return super.insert(tableName, cv);
            } catch (Exception ex) {
                Log.e("grandroid", null, ex);
                return -1;
            } finally {
                endEdit();
            }
        }
    }

    /**
     *
     * @param tableName
     * @param whereParamString
     * @param whereValues
     * @param updateValues
     * @return
     */
    @Override
    public boolean update(String tableName, String whereParamString, String[] whereValues, ContentValues updateValues) {
        synchronized (locks) {
            startEdit();
            try {
                return super.update(tableName, whereParamString, whereValues, updateValues);
            } catch (Exception e) {
                Log.e("grandroid", null, e);
                return false;
            } finally {
                endEdit();
            }
        }
    }

    /**
     * 新增欄位可用"ALTER TABLE Photo ADD COLUMN dishKey TEXT;"
     *
     * @param sql
     * @return
     */
    @Override
    public boolean exec(String sql) {
        synchronized (locks) {
            startEdit();
            try {
                return super.exec(sql);
            } catch (Exception e) {
                Log.e("grandroid", null, e);
                return false;
            } finally {
                endEdit();
            }
        }
    }

    /**
     *
     * @param tableName
     * @param index
     * @return
     */
    @Override
    public boolean delete(String tableName, int index) {
        synchronized (locks) {
            startEdit();
            try {
                return super.delete(tableName, index);
            } catch (Exception e) {
                Log.e("grandroid", null, e);
                return false;
            } finally {
                endEdit();
            }
        }
    }

    /**
     *
     * @param tableName
     * @param where
     * @return
     */
    @Override
    public boolean delete(String tableName, String where) {
        synchronized (locks) {
            startEdit();
            try {
                return super.delete(tableName, where);
            } catch (Exception e) {
                Log.e("grandroid", null, e);
                return false;
            } finally {
                endEdit();
            }
        }
    }

    /**
     *
     * @param tableName
     * @return
     */
    @Override
    public boolean truncate(String tableName) {
        synchronized (locks) {
            startEdit();
            try {
                return super.truncate(tableName);
            } catch (Exception e) {
                Log.e("grandroid", null, e);
                return false;
            } finally {
                endEdit();
            }
        }
    }

    /**
     *
     * @param tableName
     * @return
     */
    @Override
    public boolean drop(String tableName) {
        synchronized (locks) {
            startEdit();
            try {
                return super.drop(tableName);
            } catch (Exception e) {
                Log.e("grandroid", null, e);
                return false;
            } finally {
                endEdit();
            }
        }
    }

    @Override
    public boolean isEditing() {
        return true;
    }
}
