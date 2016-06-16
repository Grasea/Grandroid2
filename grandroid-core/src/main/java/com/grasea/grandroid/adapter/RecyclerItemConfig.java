package com.grasea.grandroid.adapter;
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

import android.support.v7.widget.RecyclerView;

/**
 * Created by Alan Ding on 2016/5/27.
 */

public class RecyclerItemConfig<VH extends RecyclerView.ViewHolder> {
    public int itemIds;
    public Class<VH> vhClass;

    public RecyclerItemConfig() {
        boolean hasItemLayout = this.getClass().isAnnotationPresent(ItemConfig.class);
        if (!hasItemLayout) {
            throw new NullPointerException("Didn't find any ItemLayout annotation.");
        }
        ItemConfig annotation = this.getClass().getAnnotation(ItemConfig.class);
        itemIds = annotation.id();
        vhClass = annotation.viewHolder();
    }
}
