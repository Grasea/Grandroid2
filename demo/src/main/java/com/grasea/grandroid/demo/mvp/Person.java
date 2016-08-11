package com.grasea.grandroid.demo.mvp;

import com.grasea.grandroid.database.Identifiable;
import com.grasea.grandroid.database.Table;

/**
 * Created by Rovers on 2016/7/29.
 */
@Table("Person")
public class Person implements Identifiable {
    protected Integer _id;
    protected String name;
    protected int age;
    protected boolean gender;

    @Override
    public Integer get_id() {
        return _id;
    }

    @Override
    public void set_id(Integer _id) {
        this._id = _id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "[" + name + "," + age + "," + (gender ? "male" : "female") + "]";
    }
}
