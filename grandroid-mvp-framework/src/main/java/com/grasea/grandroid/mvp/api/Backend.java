package com.grasea.grandroid.mvp.api;

import com.grasea.grandroid.mvp.model.DefaultValue;
import com.grasea.grandroid.mvp.model.Storage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Rovers on 2016/7/19.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Backend {
    String value() default "";
}
