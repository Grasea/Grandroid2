/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.grasea.grandroid.adapter;

import android.view.View;

/**
 *
 * @param <T> 
 * @author Rovers
 */
public interface ItemClickable<T> {
    /**
     * 
     * @param index
     * @param view
     * @param item
     */
    public void onClickItem(int index, View view, T item);
    /**
     * 
     * @param index
     * @param view
     * @param item
     */
    public void onLongPressItem(int index, View view, T item);
}
