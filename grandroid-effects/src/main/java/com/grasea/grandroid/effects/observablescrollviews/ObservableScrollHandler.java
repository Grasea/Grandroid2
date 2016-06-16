package com.grasea.grandroid.effects.observablescrollviews;
/*
 * Copyright (C) 2016 Alan Ding
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.github.ksoichiro.android.observablescrollview.TouchInterceptionFrameLayout;

/**
 * Created by Alan Ding on 2016/5/31.
 */
public interface ObservableScrollHandler {
    void setTouchInterceptionFrameLayout(TouchInterceptionFrameLayout interceptionFrameLayout);

    TouchInterceptionFrameLayout getTouchInterceptionFrameLayout();

    void setScrollView(ObservableScrollView scrollableView);

    Scrollable getScrollView();

    void setCanScrollValue(int canScrollValue);

    int getCanScrollValue();

    void scrollTo(float position);
}
