package com.grasea.grandroid.adapter;

import java.util.List;

/**
 * Created by Alan Ding on 2016/5/27.
 */
public interface MultipleSelector {

    void toggleSelection(int position);

    void clearSelections();

    int getSelectedItemCount();

    List<Integer> getSelectedItems();
}
