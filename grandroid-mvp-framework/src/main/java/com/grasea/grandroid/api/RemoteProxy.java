package com.grasea.grandroid.api;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import graneric.ProxyObject;
import graneric.annotation.Anno;
import okhttp3.CertificatePinner;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Rovers on 2016/5/7.
 */
public class RemoteProxy extends ProxyObject<RemoteProxy> {
    public interface Initializer {
        void onOkHttpClientInitilize(OkHttpClient.Builder builder);

        void onRetrofitInitilize(Retrofit.Builder builder);

    }

    static {
        bindAnnotationHandler(RemoteProxy.class);
    }

    private String baseUrl;
    private Object callback;
    private boolean debug;

    protected ConcurrentHashMap<String, Method> callbackMap;
    protected ConcurrentHashMap<String, Method> requestFailMap;
    protected Retrofit retrofit;
    protected Object retrofitService;
    protected static ArrayList<Converter.Factory> factoryArrayList = new ArrayList<>();
    protected static Initializer initializer;

    public static <T> T reflect(Class<T> interfaceClass, Object callback) {
        return reflect(interfaceClass, new RemoteProxy(interfaceClass, callback));
    }


    protected RemoteProxy(Class subjectInterface, Object callback) {
        super(subjectInterface);
        Backend backend = (Backend) getAnnotation(Backend.class);
        this.baseUrl = backend.value();
        this.callback = callback;
        debug = backend.debug();
        callbackMap = new ConcurrentHashMap<>();
        requestFailMap = new ConcurrentHashMap<>();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(baseUrl);
        if (RemoteProxy.factoryArrayList != null) {
            for (Converter.Factory factory : RemoteProxy.factoryArrayList) {
                builder.addConverterFactory(factory);
            }
        }
        builder.addConverterFactory(GsonConverterFactory.create());
        if (backend.timeout() > 0 || backend.readTimeout() > 0 || backend.writeTimeout() > 0 || debug) {
            OkHttpClient.Builder okbuilder = new OkHttpClient.Builder();
            if (debug) {
                HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                okbuilder.addInterceptor(httpLoggingInterceptor);
            }
            if (backend.timeout() > 0) {
                okbuilder.connectTimeout(backend.timeout(), TimeUnit.SECONDS);
            }
            if (backend.readTimeout() > 0) {
                okbuilder.readTimeout(backend.readTimeout(), TimeUnit.SECONDS);
            }
            if (backend.writeTimeout() > 0) {
                okbuilder.writeTimeout(backend.writeTimeout(), TimeUnit.SECONDS);
            }
            if (initializer != null) {
                initializer.onOkHttpClientInitilize(okbuilder);
            }

            builder.client(okbuilder.build());
        }
        if (initializer != null) {
            initializer.onRetrofitInitilize(builder);
        }
        retrofit = builder.build();
        retrofitService = retrofit.create(subjectInterface);
        ArrayList<Method> methods = Anno.scanMethodForAnnotation(callback.getClass(), Callback.class);
        for (Method m : methods) {
            callbackMap.put(m.getAnnotation(Callback.class).value(), m);
        }
        methods = Anno.scanMethodForAnnotation(callback.getClass(), RequestFail.class);
        for (Method m : methods) {
            requestFailMap.put(m.getAnnotation(RequestFail.class).value(), m);
        }
    }

    public static void setInitializer(Initializer initializer) {
        RemoteProxy.initializer = initializer;
    }

    public static void addConverterFactory(Converter.Factory factory) {
        RemoteProxy.factoryArrayList.add(factory);
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

    protected Method getRequestFailMethod(String methodName) {
        if (requestFailMap.containsKey(methodName)) {
            return requestFailMap.get(methodName);
        } else {
            if (requestFailMap.containsKey("")) {
                return requestFailMap.get("");
            } else
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
                                     callbackMethod.invoke(callback, response.body());
                                 } catch (Exception e) {
                                     if (e instanceof InvocationTargetException) {
                                         Log.e("grandroid", null, ((InvocationTargetException) e).getTargetException());
                                     } else {
                                         Log.e("grandroid", null, e);
                                     }
                                 }
                             }

                             @Override
                             public void onFailure(Call call, Throwable t) {
                                 Method requestFailMethod = getRequestFailMethod(m.getName());
                                 if (requestFailMethod != null) {
                                     try {
                                         requestFailMethod.invoke(callback, m.getName(), t);
                                     } catch (IllegalAccessException e) {
                                         Log.e("grandroid", null, e);
                                     } catch (InvocationTargetException e) {
                                         Log.e("grandroid", null, e);
                                     }
                                 }
                             }
                         }

            );
        }
        return call;
    }
}
