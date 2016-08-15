package com.grasea.grandroid.demo.mvp;

import android.accounts.Account;

import com.grasea.grandroid.mvp.model.DefaultValue;
import com.grasea.grandroid.mvp.model.Get;
import com.grasea.grandroid.mvp.model.Put;
import com.grasea.grandroid.mvp.model.Query;
import com.grasea.grandroid.mvp.model.Save;
import com.grasea.grandroid.mvp.model.Storage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rovers on 2016/7/19.
 */
public interface UserModel {

    @Put("name")
    public boolean saveName(String name);

    @Get(value = "name", defaultValue = DefaultValue.EMPTY_STRING)
    public String getName();

    @Put("age")
    public boolean saveAge(int age);

    @Get(value = "age", defaultValue = DefaultValue.ZERO)
    public int getAge();

    @Put("gender")
    public boolean saveGender(boolean male);

    @Get(value = "gender", defaultValue = DefaultValue.TRUE)
    public boolean getGender();

    @Save(Person.class)
    public boolean saveUserData(Person account);

    @Query(Person.class)
    public ArrayList<Person> getAllPerson();

    @Query(Person.class)
    public Person getPerson(String where);

    @Put(value = "person_list", storage = Storage.Memory)
    public boolean putPersonList(ArrayList<Person> list);

    @Get(value = "person_list", storage = Storage.Memory, defaultValue = DefaultValue.NULL)
    public ArrayList<Person> getPersonList();
}
