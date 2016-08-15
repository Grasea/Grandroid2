package com.grasea.grandroid.adapter;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Alan Ding on 2016/1/19.
 */
public interface OnClickable<T extends Object, VH> {
    public void onItemClick(VH holder, int index, T item);
}
