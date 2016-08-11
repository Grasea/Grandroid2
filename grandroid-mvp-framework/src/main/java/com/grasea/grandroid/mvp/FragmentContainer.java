package com.grasea.grandroid.mvp;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Rovers on 2016/5/7.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FragmentContainer {
    int id() default -1;
}
