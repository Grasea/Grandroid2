package com.grasea.grandroid.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.grasea.grandroid.app.GrandroidApplication;

/**
 * Created by USER on 2016/5/7.
 */
public abstract class GrandroidActivity<P extends GrandroidPresenter> extends AppCompatActivity {
    protected GrandroidPresenter presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Class presenterClass = this.getClass().getAnnotation(UsingPresenter.class).value();
        if (getApplication() instanceof GrandroidApplication) {
            GrandroidApplication app = (GrandroidApplication) getApplication();
            boolean singleton = this.getClass().getAnnotation(UsingPresenter.class).singleton();
            presenter = singleton ? app.getPresenter(presenterClass) : null;
            if (presenter == null) {
                try {
                    presenter = (GrandroidPresenter) presenterClass.newInstance();
                    if (singleton) {
                        app.putPresenter(presenter);
                    }
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                presenter = (GrandroidPresenter) presenterClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        presenter.setContract(this);
    }


    public P getPresenter() {
        return (P) presenter;
    }
}
