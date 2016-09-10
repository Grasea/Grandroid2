package com.grasea.grandroid.demo.fragment.normal;

/**
 * Copyright (C) 2016 Alan Ding
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grasea.grandroid.demo.R;
import com.grasea.grandroid.mvp.GrandroidActivity;
import com.grasea.grandroid.mvp.GrandroidFragment;
import com.grasea.grandroid.mvp.UsingPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alan Ding on 2016/9/10.
 */
@UsingPresenter(DemoFragmentPresenter.class)
public class FragmentDemo extends GrandroidFragment<DemoFragmentPresenter> {
    @BindView(R.id.tv_hello)
    TextView tvHello;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_layout_demo, null);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public GrandroidActivity.UISettingEvent getUISetting() {
        DemoEvent demoEvent = new DemoEvent();
        demoEvent.title = "FragmentDemo";
        return demoEvent;
    }
}
