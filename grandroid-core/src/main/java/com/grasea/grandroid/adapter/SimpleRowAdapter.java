package com.grasea.grandroid.adapter;

import android.view.View;
import android.view.ViewGroup;

/**
 *
 * @author Rovers
 */
public abstract class SimpleRowAdapter<T> implements RowAdapter<T>, ItemClickable<T> {

    protected UniversalAdapter parentAdapter;

    public void setParentAdapter(UniversalAdapter parentAdapter) {
        this.parentAdapter = parentAdapter;
    }

    protected <T extends View> T findView(View v, String tag, Class<T> c) {
        if (v.getTag() != null && v.getTag().equals(tag)) {
            return (T) v;
        }
        if (v instanceof ViewGroup) {
            View answer = null;
            for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
                answer = findView(((ViewGroup) v).getChildAt(i), tag, c);
                if (answer != null) {
                    return (T) answer;
                }
            }
            return null;
        }
        return null;
    }

    public void onLongPressItem(int index, View view, T item) {
    }

    public void onClickItem(int index, View view, T item) {
    }

    public void notifyDataSetChanged() {
        if (parentAdapter != null) {
            parentAdapter.notifyDataSetChanged();
        }
    }

    public void notifyDataSetInvalidated() {
        if (parentAdapter != null) {
            parentAdapter.notifyDataSetInvalidated();
        }
    }
}
