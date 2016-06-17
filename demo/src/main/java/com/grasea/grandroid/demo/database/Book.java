package com.grasea.grandroid.demo.database;

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

import com.grasea.grandroid.sample.database.Identifiable;
import com.grasea.grandroid.sample.database.Table;

/**
 * Created by Alan Ding on 2016/6/16.
 */
@Table("Books")
public class Book implements Identifiable {
    public int _id;
    public String name;
    public int price;
    public boolean saleout;

    @Override
    public Integer get_id() {
        return _id;
    }

    @Override
    public void set_id(Integer id) {
        this._id = id;
    }

    public boolean isSaleout() {
        return saleout;
    }

    public void setSaleout(boolean saleout) {
        this.saleout = saleout;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
