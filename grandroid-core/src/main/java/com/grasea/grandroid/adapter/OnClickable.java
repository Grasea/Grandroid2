package com.grasea.grandroid.adapter;

/**
 * Created by Alan Ding on 2016/1/19.
 */
public interface OnClickable<T extends Object> {
    public void onItemClick(int index, T item);
}
