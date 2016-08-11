package com.grasea.grandroid.mvp.api;

import com.android.volley.Request;
import com.grasea.grandroid.mvp.model.DefaultValue;
import com.grasea.grandroid.mvp.model.Storage;
import com.grasea.grandroid.net.SendMethod;
import com.grasea.grandroid.net.SendType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Rovers on 2016/7/19.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface API {
    String value() default "";

    //String path() default "";

    SendMethod method() default SendMethod.Post;

    SendType contentType() default SendType.FormUrlencoded;

    String encoding() default "UTF-8";
}
