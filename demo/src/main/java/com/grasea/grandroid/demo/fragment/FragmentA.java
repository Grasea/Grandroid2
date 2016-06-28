package com.grasea.grandroid.demo.fragment;

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

import com.grasea.grandroid.mvp.FragmentContainer;
import com.grasea.grandroid.mvp.GrandroidActivity;
import com.grasea.grandroid.mvp.GrandroidFragment;
import com.grasea.grandroid.mvp.UsingPresenter;

/**
 * Created by Alan Ding on 2016/6/23.
 */
@UsingPresenter(APresenter.class)
public class FragmentA extends GrandroidFragment<APresenter> {
    @Override
    public GrandroidActivity.UISettingEvent getUISetting() {
        return new UISetting();
    }

    public class UISetting implements GrandroidActivity.UISettingEvent {

    }
}
