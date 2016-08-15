package com.grasea.grandroid.api;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import graneric.ProxyObject;
import graneric.annotation.Anno;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rovers on 2016/5/7.
 */
public class RemoteProxy extends ProxyObject<RemoteProxy> {
    private static Context context;

    static {
        bindAnnotationHandler(RemoteProxy.class);
    }

    private String baseUrl;
    private Object callback;
    protected ConcurrentHashMap<String, Method> callbackMap;
    protected Retrofit retrofit;
    protected Object retrofitService;

    public static void init(Context context) {
        RemoteProxy.context = context;
    }

    public static <T> T reflect(Class<T> interfaceClass, Object callback) {
        return reflect(interfaceClass, new RemoteProxy(interfaceClass, callback));
    }

    protected RemoteProxy(Class subjectInterface, Object callback) {
        super(subjectInterface);
        this.baseUrl = ((Backend) getAnnotation(Backend.class)).value();
        this.callback = callback;
        callbackMap = new ConcurrentHashMap<>();
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitService = retrofit.create(subjectInterface);
        ArrayList<Method> methods = Anno.scanMethodForAnnotation(callback.getClass(), Callback.class);
        for (Method m : methods) {
            callbackMap.put(m.getAnnotation(Callback.class).value(), m);
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    protected Method getCallbackMethod(String methodName) {
        if (callbackMap.containsKey(methodName)) {
            return callbackMap.get(methodName);
        } else {
            return null;
        }
    }

    public Object invoke(Object proxy, final Method m, Object[] args) throws Throwable {
        retrofit2.Call call = (Call) m.invoke(retrofitService, args);
        final Method callbackMethod = getCallbackMethod(m.getName());
        if (callbackMethod != null) {
            call.enqueue(new retrofit2.Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        callbackMethod.invoke(callback, context, response.body());
                    } catch (IllegalAccessException e) {
                        Log.e("grandroid", null, e);
                    } catch (InvocationTargetException e) {
                        Log.e("grandroid", null, e);
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {

                }
            });
        }
        return call;
    }
}
