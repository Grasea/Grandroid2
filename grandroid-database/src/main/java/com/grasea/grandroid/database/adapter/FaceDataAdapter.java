/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.database.adapter;

import android.content.Context;

import java.util.ArrayList;

import com.grasea.grandroid.adapter.ObjectAdapter;
import com.grasea.grandroid.database.GenericHelper;
import com.grasea.grandroid.database.Identifiable;

/**
 *
 * @param <T>
 * @author Rovers
 */
public class FaceDataAdapter<T extends Identifiable> extends ObjectAdapter<T> {

    /**
     *
     */
    protected GenericHelper<T> helper;
    /**
     *
     */
    protected String where;
    /**
     *
     */
    protected boolean available = false;

    /**
     *
     * @param context
     * @param helper
     */
    public FaceDataAdapter(Context context, GenericHelper<T> helper) {
        this(context, helper, false);
    }

    /**
     *
     * @param context
     * @param helper
     */
    public FaceDataAdapter(Context context, GenericHelper<T> helper, boolean cycle) {
        this(context, helper, null, false, cycle);
    }

    public FaceDataAdapter(Context context, GenericHelper<T> helper, String where, boolean ignoreFirstRefresh) {
        this(context, helper, where, ignoreFirstRefresh, false);
    }

    /**
     *
     * @param context
     * @param helper
     * @param where
     * @param ignoreFirstRefresh
     */
    public FaceDataAdapter(Context context, GenericHelper<T> helper, String where, boolean ignoreFirstRefresh, boolean cycle) {
        super(context, new ArrayList<T>(), cycle);
        this.where = where;
        this.helper = helper;
        available = !ignoreFirstRefresh;
        setList(onDataLoaded(helper.select(where)));
    }

    /**
     *
     */
    public void refresh() {
        if (available) {
            if (where != null && where.length() > 0) {
                list = onDataLoaded(helper.select(where));
            } else {
                list = onDataLoaded(helper.select());
            }
            this.notifyDataSetInvalidated();
        } else {
            available = true;
        }
    }

    /**
     *
     * @param where
     */
    public void requery(String where) {
        this.where = where;
        refresh();
    }

    /**
     *
     * @return
     */
    public GenericHelper<T> getHelper() {
        return helper;
    }

    protected ArrayList<T> onDataLoaded(ArrayList<T> list) {
        return list;
    }
}
