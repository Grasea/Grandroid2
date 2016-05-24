/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 *
 * @param <T>
 * @author Rovers
 */
public class ObjectAdapter<T> extends UniversalAdapter<T> implements ItemClickable<T> {

    /**
     *
     */
    protected List<T> list;
    protected boolean cycle;

    /**
     *
     * @param context
     * @param list
     */
    public ObjectAdapter(Context context, List<T> list) {
        super(context);
        this.list = list;
    }

    /**
     *
     * @param context
     * @param list
     */
    public ObjectAdapter(Context context, List<T> list, boolean cycle) {
        super(context);
        this.list = list;
        this.cycle = cycle;
    }

    public boolean isCycle() {
        return cycle;
    }

    public void setCycle(boolean cycle) {
        this.cycle = cycle;
    }

    public int getCycleIndex() {
        if (!list.isEmpty()) {
            return 3000 - (3000 % list.size());
        } else {
            return -1;
        }
    }

    /**
     *
     * @param list
     */
    public void setList(List<T> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    public List<T> getList() {
        return list;
    }

    /**
     *
     * @return
     */
    public int getCount() {
        if (list == null) {
            return 0;
        } else {
            if (cycle) {
                return Integer.MAX_VALUE;
            } else {
                return list.size();
            }
        }
    }

    /**
     *
     * @param index
     * @return
     */
    public T getItem(int index) {
        return list.get(cycle ? index % list.size() : index);
    }

    /**
     *
     * @param index
     * @return
     */
    public long getItemId(int index) {
        return index;
    }

    /**
     *
     * @param index
     * @param view
     * @param parent
     * @return
     */
    public View getView(int index, View view, ViewGroup parent) {
        try {
            if (view == null) {
                view = createRowView(index, list.get(cycle ? index % list.size() : index));
            }
            fillRowView(index, view, list.get(cycle ? index % list.size() : index));
        } catch (Exception ex) {
            Log.e("grandroid", null, ex);
        }
        return view;
    }

    /**
     *
     * @param index
     * @param item
     * @return
     */
    public View createRowView(int index, T item) {
        if (rowAdapter != null) {
            return rowAdapter.createRowView(context, index, item);
        } else {
            return new TextView(context);
        }
    }

    /**
     *
     * @param index
     * @param cellRenderer
     * @param item
     */
    public void fillRowView(int index, View cellRenderer, T item) throws Exception {
        if (rowAdapter != null) {
            rowAdapter.fillRowView(context, index, cellRenderer, item);
        }
    }

      /**
     *
     * @param index
     * @param view
     * @param item
     */
    public void onClickItem(int index, View view, T item) {
        if (rowAdapter != null && ItemClickable.class.isInstance(rowAdapter)) {
            ((ItemClickable) rowAdapter).onClickItem(index, view, item);
        }
    }

    /**
     *
     * @param index
     * @param view
     * @param item
     */
    public void onLongPressItem(int index, View view, T item) {
        if (rowAdapter != null && ItemClickable.class.isInstance(rowAdapter)) {
            ((ItemClickable) rowAdapter).onLongPressItem(index, view, item);
        }
    }
}
