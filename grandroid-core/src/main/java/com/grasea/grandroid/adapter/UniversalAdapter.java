/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 *
 * @author Rovers
 */
public abstract class UniversalAdapter<T> extends BaseAdapter {

    protected Context context;
    protected RowAdapter<T> rowAdapter;

    public UniversalAdapter(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public RowAdapter<T> getRowAdapter() {
        return rowAdapter;
    }

    public void setRowAdapter(RowAdapter<T> rowAdapter) {
        this.rowAdapter = rowAdapter;
        if (rowAdapter instanceof SimpleRowAdapter) {
            ((SimpleRowAdapter) rowAdapter).setParentAdapter(this);
        }
    }

    public abstract int getCount();

    public abstract Object getItem(int index);

    public abstract View getView(int index, View cellRenderer, ViewGroup parent);

    public abstract View createRowView(int index, T item);

    public abstract void fillRowView(int index, View cellRenderer, T item) throws Exception;

    /**
     *
     * @param <T>
     * @param v
     * @param tag
     * @param c
     * @return
     */
    protected <T extends View> T findView(View v, String tag, Class<T> c) {
        return findViewByTag(v, tag, c);
    }

    public static <T extends View> T findViewByTag(View v, String tag, Class<T> c) {
        if (v.getTag() != null && v.getTag().equals(tag)) {
            return (T) v;
        }
        if (v instanceof ViewGroup) {
            View answer = null;
            for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
                answer = findViewByTag(((ViewGroup) v).getChildAt(i), tag, c);
                if (answer != null) {
                    return (T) answer;
                }
            }
            return null;
        }
        return null;
    }
}
