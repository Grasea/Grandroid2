package com.grasea.grandroid.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Rovers on 2016/7/19.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Backend {
    String value() default "";

    boolean debug() default false;

    //Connection Timeout, 0 means using OKHttp Client default value
    int timeout() default 0;

    //0 means using OKHttp Client default value
    int readTimeout() default 0;

    //0 means using OKHttp Client default value
    int writeTimeout() default 0;
}
