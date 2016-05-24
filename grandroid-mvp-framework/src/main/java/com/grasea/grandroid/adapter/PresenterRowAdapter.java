package com.grasea.grandroid.adapter;

import com.grasea.grandroid.adapter.SimpleRowAdapter;
import com.grasea.grandroid.mvp.GrandroidPresenter;

/**
 * Created by USER on 2016/5/14.
 */
public abstract class PresenterRowAdapter<T, S extends GrandroidPresenter> extends SimpleRowAdapter<T> {
    protected S presenter;

    public PresenterRowAdapter(S presenter) {
        this.presenter = presenter;
    }

    public S getPresenter() {
        return presenter;
    }
}
