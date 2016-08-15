package com.grasea.grandroid.api;

import com.grasea.grandroid.net.SendMethod;
import com.grasea.grandroid.net.SendType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Rovers on 2016/7/19.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface API {
    //name
    String name() default "";

    String path() default "";

    SendMethod method() default SendMethod.Post;

    SendType contentType() default SendType.FormUrlencoded;

    String encoding() default "UTF-8";
}
