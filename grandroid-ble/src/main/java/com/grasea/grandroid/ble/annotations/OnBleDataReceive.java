package com.grasea.grandroid.ble.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Alan Ding on 2016/5/16.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OnBleDataReceive {
    String serviceUUID() default "";

    String characteristicUUID() default "";

    String[] characteristicUUIDs() default {};

}
