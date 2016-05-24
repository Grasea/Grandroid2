package com.grasea.ble.annotations;

import com.grasea.ble.Config;

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Created by Alan Ding on 2016/5/18.
 */
public class NameBinder implements Parsable {

    private static NameBinder ourInstance = new NameBinder();

    public static NameBinder getInstance() {
        return ourInstance;
    }

    private HashMap<String, String> nameMap;

    private NameBinder() {
        nameMap = new HashMap<>();
    }

    public static void bind(Object object) {
        if (ourInstance == null) {
            ourInstance = new NameBinder();
        }
        try {
            ourInstance.startBindClass(object);
        } catch (Exception e) {
            Config.loge(e);
        }
    }

    private void doUnbind() {
    }

    public static void unbind() {
        ourInstance = null;
    }

    @Override
    public void startBindClass(Object object) throws Exception {
        if (!nameMap.isEmpty()) {
            nameMap.clear();
        }
        Field[] declaredFields = object.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            AliasName annotation = field.getAnnotation(AliasName.class);
            if (annotation != null) {
                String name = annotation.name();
                if (!field.getType().equals(String.class)) {
                    throw new Exception("UUID's Name must to be 'String' type.");
                }
                field.setAccessible(true);
                nameMap.put((String) field.get(object), name);
            }
        }
    }

    public String getName(String uuid) {
        return nameMap.get(uuid);
    }
}
