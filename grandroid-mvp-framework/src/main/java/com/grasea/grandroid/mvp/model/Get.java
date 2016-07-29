package com.grasea.grandroid.mvp.model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by USER on 2016/7/19.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Get {
    String value() default "";

    DefaultValue defaultValue() default DefaultValue.EMPTY_STRING;
}
