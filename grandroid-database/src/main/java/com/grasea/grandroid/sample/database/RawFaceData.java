/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.sample.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;

/**
 *
 * @author Rovers
 */
public class RawFaceData extends SQLiteOpenHelper implements DataOperator {

    private final Object locks;
    /**
     *
     */
    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    /**
     *
     */
    protected SQLiteDatabase db4read;
    protected SQLiteDatabase db4write;
    /**
     *
     */
    protected Context context;
    protected String dbName;
    protected boolean transactionMode;
    protected boolean transacting;
    protected boolean neverClose;

    /**
     *
     * @param context
     * @param dbName
     */
    public RawFaceData(Context context, String dbName, boolean transactionMode) {
        super(context, dbName, null, 1);
        this.dbName = dbName;
        db4read = null;
        this.context = context;
        this.transactionMode = transactionMode;
        locks = new Object();
    }

    public void neverClose() {
        neverClose = true;
    }

    /**
     *
     * @param arg0
     */
    @Override
    public void onCreate(SQLiteDatabase arg0) {
    }

    /**
     *
     * @param arg0
     * @param arg1
     * @param arg2
     */
    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
    }

    /**
     *
     * @return
     */
    public Context getContext() {
        return context;
    }

    public String getDatabaseName() {
        return dbName;
    }

//    protected void setContentValues(ContentValues cv, String keyValues) {
//        String[] keyValuePair = keyValues.split(",");
//        for (int i = 0; i < keyValuePair.length; i++) {
//            String[] keyValue = keyValuePair[i].split("=");
//            if (keyValue.length == 2) {
//                cv.put(keyValue[0].trim(), keyValue[1].trim());
//            }
//        }
//    }
    /**
     *
     * @param tableName
     * @param fieldPart
     * @return
     */
    public boolean createTable(String tableName, String fieldPart) {
        db4write.execSQL("create TABLE IF NOT EXISTS " + tableName + " (_id INTEGER primary key autoincrement, " + fieldPart + ")");
        return true;
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
     * @param col
     * @param type
     * @param defaultValue
     * @return
     */
    public boolean addColumn(String tableName, String col, TypeMapping type, String defaultValue) {
        try {//DEFAULT 'baz'
            defaultValue = defaultValue == null ? "" : " DEFAULT " + defaultValue;
            return exec("ALTER TABLE " + tableName + " ADD COLUMN " + col + " " + type.getSqlType() + defaultValue + ";");
        } catch (Exception ex) {
            Log.e("grandroid", "fail to add column " + col + " to " + tableName);
            return false;
        }
    }

    /**
     *
     * @param tableName
     * @param cv
     * @return
     * @throws Exception
     */
    @Override
    public long insert(String tableName, ContentValues cv) throws Exception {
        long result = 0L;
        synchronized (locks) {
            result = db4write.insert(tableName, null, cv);
        }
        return result;
    }

    /**
     *
     * @param tableName
     * @param index
     * @param updateValues
     * @return
     */
    @Override
    public boolean update(String tableName, int index, ContentValues updateValues) {
        boolean result = false;
        synchronized (locks) {
            result = update(tableName, "_id=?", new String[]{Integer.toString(index)}, updateValues);
        }
        return result;
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
        boolean result = false;
        synchronized (locks) {
            result = db4write.update(tableName, updateValues, whereParamString, whereValues) > 0;
        }
        return result;
    }

    /**
     * 新增欄位可用"ALTER TABLE Photo ADD COLUMN dishKey TEXT;"
     *
     * @param sql
     * @return
     */
    @Override
    public boolean exec(String sql) {
        try {
            synchronized (locks) {
                db4write.execSQL(sql);
            }
            return true;
        } catch (Exception ex) {
            Log.e("grandroid", null, ex);
            return false;
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
        String where = "_id=?";
        synchronized (locks) {
            db4write.delete(tableName, where, new String[]{Integer.toString(index)});
        }
        return true;
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
            db4write.execSQL("Delete from " + tableName + " " + where);
        }
        return true;
    }

    /**
     *
     * @param tableName
     * @return
     */
    @Override
    public boolean truncate(String tableName) {
        try {
            synchronized (locks) {
                db4write.execSQL("Delete from " + tableName);
            }
            return true;
        } catch (Exception ex) {
            Log.e("grandroid", null, ex);
            return false;
        }
        //db4write.delete(tableName, null, null);
    }

    /**
     *
     * @param tableName
     * @return
     */
    @Override
    public boolean drop(String tableName) {
        try {
            synchronized (locks) {
                db4write.execSQL("drop table if exists " + tableName);
            }
            return true;
        } catch (Exception ex) {
            Log.e("grandroid", null, ex);
            return false;
        }
    }

    /**
     *
     * @param tableName
     * @return
     */
    public Cursor select(String tableName) {
        synchronized (locks) {
            prepareDatabase();
            Cursor cursor = db4read.query(tableName, null, null, null, null, null, null);
            return cursor;
        }
    }

    /**
     *
     * @param tableName
     * @param where
     * @return
     */
    public Cursor select(String tableName, String where) {
        synchronized (locks) {
            prepareDatabase();
            Cursor cursor = db4read.rawQuery("Select * from " + tableName + " " + where, null);
            return cursor;
        }
    }

    /**
     *
     * @param tableName
     * @param where
     * @param start
     * @param count
     * @return
     */
    public Cursor select(String tableName, String where, int start, int count) {
        synchronized (locks) {
            prepareDatabase();
            Cursor cursor = db4read.rawQuery("Select * from " + tableName + " " + where + " limit " + start + "," + count, null);
            return cursor;
        }
    }

    /**
     *
     * @param tableName
     * @param id
     * @return
     */
    public Cursor selectSingle(String tableName, int id) {
        synchronized (locks) {
            prepareDatabase();
            String where = "_id=?";
            Cursor cursor = db4read.query(tableName, null, where, new String[]{Integer.toString(id)}, null, null, null);
            return cursor;
        }
    }

    /**
     *
     * @param tableName
     * @param where
     * @return
     */
    public Cursor selectSingle(String tableName, String where) {
        synchronized (locks) {
            prepareDatabase();
            Cursor cursor = db4read.rawQuery("Select * from " + tableName + " " + where + "  LIMIT 1", null);
            return cursor;
        }
    }

    /**
     *
     * @param sql
     * @return
     */
    public Cursor query(String sql) {
        synchronized (locks) {
            prepareDatabase();
            Cursor cursor = db4read.rawQuery(sql, null);
            return cursor;
        }
    }

    public boolean isEditing() {
        return db4write != null;
    }

    /**
     *
     */
    public void startEdit() {
        if (db4write != null) {
            if (db4write.isOpen()) {
                if (transactionMode && !transacting) {
                    transacting = true;
                    db4write.beginTransaction();
                }
                return;
            } else {
                db4write.close();
                db4write.releaseReference();
                db4write = null;
            }
        }
        db4write = this.getWritableDatabase();
        if (transactionMode) {
            transacting = true;
            db4write.beginTransaction();
        }
    }

    void endTransaction() {
        if (transactionMode && transacting) {
            db4write.setTransactionSuccessful();
            db4write.endTransaction();
        }
        transacting = false;
    }

    public void endEdit() {
        if (db4write != null) {
            endTransaction();
            if (!neverClose) {
                db4write.close();
                db4write.releaseReference();
                db4write = null;
            }
        }
    }

    /**
     *
     */
    public void prepareDatabase() {
        if (db4read != null) {
            if (db4read.isOpen()) {
                return;
            } else {
                db4read.releaseReference();
                db4read = null;
            }
        }
        db4read = this.getReadableDatabase();
    }

    /**
     *
     * @param tableName
     * @param where
     * @param visitor
     */
    public void acceptVisitor(String tableName, String where, CursorVisitor visitor) {
        prepareDatabase();
        Cursor cursor = db4read.rawQuery("Select * from " + tableName + " " + where, null);
        boolean keepRunning = true;
        try {
            while (keepRunning && cursor.moveToNext()) {
                keepRunning = visitor.handleRow(cursor);
            }
            cursor.close();
            visitor.afterVisit();
        } catch (Exception ex) {
            visitor.onError(ex);
        }

    }

    /**
     *
     * @throws Throwable
     */
    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

    public void commitEdit() {
        if (db4write != null && transactionMode) {
            if (transacting) {
                db4write.setTransactionSuccessful();
                db4write.endTransaction();
            }
            db4write.beginTransaction();
            transacting = true;
        }
    }

    public boolean isTransactionMode() {
        return transactionMode;
    }

    public boolean isTransacting() {
        return transacting;
    }
}
