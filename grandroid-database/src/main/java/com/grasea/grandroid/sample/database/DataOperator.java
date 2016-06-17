/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.sample.database;

import android.content.ContentValues;

/**
 *
 * @author Rovers
 */
public interface DataOperator {

    /**
     *
     * @param tableName
     * @param index
     * @return
     */
    boolean delete(String tableName, int index);

    /**
     *
     * @param tableName
     * @param where
     * @return
     */
    boolean delete(String tableName, String where);

    /**
     *
     * @param tableName
     * @return
     */
    boolean drop(String tableName);

    /**
     * 新增欄位可用"ALTER TABLE Photo ADD COLUMN dishKey TEXT;"
     *
     * @param sql
     * @return
     */
    boolean exec(String sql);

    /**
     *
     * @param tableName
     * @param cv
     * @return
     * @throws Exception
     */
    long insert(String tableName, ContentValues cv) throws Exception;

    /**
     *
     * @param tableName
     * @return
     */
    boolean truncate(String tableName);

    /**
     *
     * @param tableName
     * @param index
     * @param updateValues
     * @return
     */
    boolean update(String tableName, int index, ContentValues updateValues);

    /**
     *
     * @param tableName
     * @param whereParamString
     * @param whereValues
     * @param updateValues
     * @return
     */
    boolean update(String tableName, String whereParamString, String[] whereValues, ContentValues updateValues);
    
}
