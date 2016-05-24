package com.grasea.grandroid.app;

import android.app.Application;

import com.grasea.grandroid.mvp.GrandroidPresenter;
import com.grasea.grandroid.net.Molley;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by USER on 2016/5/7.
 */
public class GrandroidApplication extends Application {
    protected ConcurrentHashMap<Class, GrandroidPresenter> presenterMap;

    @Override
    public void onCreate() {
        super.onCreate();
        presenterMap = new ConcurrentHashMap<>();
        Molley.init(getApplicationContext());
    }

    public void putPresenter(GrandroidPresenter presenter) {
        presenterMap.put(presenter.getClass(), presenter);
    }

    public GrandroidPresenter getPresenter(Class c) {
        return presenterMap.get(c);
    }
}
