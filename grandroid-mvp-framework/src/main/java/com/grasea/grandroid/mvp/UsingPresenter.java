package com.grasea.grandroid.mvp;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by USER on 2016/5/7.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface UsingPresenter {
    Class value();

    boolean singleton() default true;
}
