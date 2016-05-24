/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.adapter;

import android.content.Context;
import android.view.View;

/**
 *
 * @author Rovers
 */
public interface RowAdapter<T> {

    public View createRowView(Context context, int index, T item);

    public void fillRowView(Context context, int index, View cellRenderer, T item) throws Exception;
}
