package com.grasea.grandroid.mvp.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.grasea.database.json.JSONConverter;
import com.grasea.grandroid.database.FaceData;
import com.grasea.grandroid.database.GenericHelper;
import com.grasea.grandroid.database.Identifiable;
import com.grasea.grandroid.mvp.model.Get;
import com.grasea.grandroid.mvp.model.Put;
import com.grasea.grandroid.mvp.model.Query;
import com.grasea.grandroid.mvp.model.Save;
import com.grasea.grandroid.net.Molley;
import com.grasea.grandroid.net.ResultHandler;

import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import graneric.ObjectTypeHandler;
import graneric.ProxyObject;
import graneric.annotation.Anno;

/**
 * Created by Rovers on 2016/5/7.
 */
public class RemoteProxy extends ProxyObject<RemoteProxy> {
    private static Context context;

    static {
        bindAnnotationHandler(RemoteProxy.class);
    }

    private String baseUrl;
    private Class callbackClass;
    protected ConcurrentHashMap<String, Method> callbackMap;

    public static void init(Context context) {
        RemoteProxy.context = context;
    }

    public static <T> T reflect(Class<T> interfaceClass, Class callbackClass) {
        return reflect(interfaceClass, new RemoteProxy(interfaceClass, callbackClass));
    }

    protected RemoteProxy(Class subjectInterface, Class callbackClass) {
        super(subjectInterface);
        this.baseUrl = ((Backend) getAnnotation(Backend.class)).value();
        this.callbackClass = callbackClass;
        callbackMap = new ConcurrentHashMap<>();
        ArrayList<Method> methods = Anno.scanMethodForAnnotation(callbackClass, Callback.class);
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

    @API()
    protected static boolean api(final RemoteProxy proxy, final Annotation ann, final Method m, Object[] args) throws Exception {
        new Molley(proxy.getBaseUrl() + m.getAnnotation(API.class).value()).setMethod(m.getAnnotation(API.class).method()).send(new ResultHandler<JSONObject>() {
            @Override
            public void onAPIResult(JSONObject content) {
                Method callback = proxy.getCallbackMethod(m.getName());
                if (callback != null) {
                    try {
                        if (callback.getParameterTypes()[1].equals(JSONObject.class)) {
                            callback.invoke(null, context, content);
                        } else {
                            callback.invoke(null, context, JSONConverter.toObject(content, callback.getParameterTypes()[1]));
                        }
                    } catch (IllegalAccessException e) {
                        Log.e("grandroid", null, e);
                    } catch (InvocationTargetException e) {
                        Log.e("grandroid", null, e);
                    }
                }
            }

            @Override
            public void onAPIError(Throwable t) {
                Log.e("grandroid", null, t);
            }
        });
        return true;
    }
}
