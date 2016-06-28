package com.grasea.grandroid.mvp;

/**
 * Copyright (C) 2016 Alan Ding
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grasea.grandroid.app.GrandroidApplication;
import com.grasea.grandroid.mvpframework.R;

import java.lang.reflect.Field;

import de.greenrobot.event.EventBus;

/**
 * Created by Alan Ding on 2016/6/20.
 */
public abstract class GrandroidFragment<P extends GrandroidPresenter> extends Fragment {
    protected GrandroidPresenter presenter;

    /**
     * Use GrandroidFragment must to be call super.onCreateView().
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Class presenterClass = this.getClass().getAnnotation(UsingPresenter.class).value();
        if (getActivity().getApplication() instanceof GrandroidApplication) {
            GrandroidApplication app = (GrandroidApplication) getActivity().getApplication();
            boolean singleton = this.getClass().getAnnotation(UsingPresenter.class).singleton();
            presenter = singleton ? app.getPresenter(presenterClass) : null;
            if (presenter == null) {
                try {
                    presenter = (GrandroidPresenter) presenterClass.newInstance();
                    if (singleton) {
                        app.putPresenter(presenter);
                    }
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                presenter = (GrandroidPresenter) presenterClass.newInstance();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        presenter.setContract(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    public P getPresenter() {
        return (P) presenter;
    }

    public int[] getFragmentTransitions() {
        return new int[]{R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_out_right, R.anim.slide_in_right};
    }

    public Boolean onBackPress() {
        return true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(getUISetting());
    }

    /**
     *
     */
    public void finish() {
        KeyEvent backEvtDown = new KeyEvent(KeyEvent.ACTION_DOWN,
                KeyEvent.KEYCODE_BACK);
        KeyEvent backEvtUp = new KeyEvent(KeyEvent.ACTION_UP,
                KeyEvent.KEYCODE_BACK);
        getActivity().dispatchKeyEvent(backEvtDown);
        getActivity().dispatchKeyEvent(backEvtUp);
    }

    public abstract GrandroidActivity.UISettingEvent getUISetting();
}
