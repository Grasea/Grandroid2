/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.net;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * HTTP連線物件，用來擷取網頁的回傳結果，可用在取得JSON字串或網頁HTML的時候，支援POST。
 *
 * @author Rovers
 */
public class Molley {

    protected static RequestQueue requestQueue;

    /**
     *
     */
    protected String uri;
    /**
     *
     */
    protected HashMap<String, String> param;
    /**
     *
     */
    protected HashMap<String, String> headerParams;
    /**
     *
     */
    protected String encoding;
    /**
     *
     */
    public static String cookie = null;
    /**
     *
     */
    public boolean keepingCookie = false;
    /**
     *
     */
    protected SendMethod method;

    protected SendType contentType;

    /**
     *
     */
    public boolean isHttps;

    private static int timeout = DefaultRetryPolicy.DEFAULT_TIMEOUT_MS;

    public static void init(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    /**
     * @param uri 欲擷取資料的URL
     */
    public Molley(String uri) {
        this(uri, false);
    }

    /**
     * @param uri 欲擷取資料的URL
     */
    public Molley(String uri, boolean keepCookie) {
        this.uri = uri;
        isHttps = uri.startsWith("https");
        this.encoding = "UTF-8";
        param = new HashMap<String, String>();
        headerParams = new HashMap<String, String>();
        method = SendMethod.Post;//default use POST
        this.keepingCookie = keepCookie;
        contentType = SendType.FormUrlencoded;
    }

    public static void setTimeout(int timeout) {
        Molley.timeout = timeout;
    }

    /**
     * @return
     */
    public boolean isKeepingCookie() {
        return keepingCookie;
    }

    /**
     * @param keepingCookie
     */
    public Molley setKeepingCookie(boolean keepingCookie) {
        this.keepingCookie = keepingCookie;
        return this;
    }

    /**
     * 伺服端服務網址
     *
     * @return
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param encoding
     * @return
     */
    public Molley encode(String encoding) {
        this.encoding = encoding;
        return this;
    }

    /**
     * 新增一組傳輸參數
     *
     * @param key   參數的名字
     * @param value 參數值
     * @return Mon物件本身，方便串接
     */
    public Molley put(String key, String value) {
        param.put(key, value);
        return this;
    }

    public Molley putHeader(String key, String value) {
        if (headerParams == null) {
            headerParams = new HashMap<String, String>();
        }
        headerParams.put(key, value);
        return this;
    }

    public HashMap<String, String> getHeaderParams() {
        return headerParams;
    }

    /**
     * 清除變數
     */
    public void clear() {
        headerParams.clear();
        param.clear();
    }

    /**
     * 取得參數內容 (實作方式為TreeMap.toString())
     *
     * @return
     */
    public String getOrderedParameters() {
        TreeMap<String, String> tm = new TreeMap<String, String>();
        for (String key : param.keySet()) {
            tm.put(key, param.get(key));
        }
        return tm.toString();
    }

    public void setParameters(String params) {
        String[] pairs = params.split("&");
        for (int i = 0; i < pairs.length; i++) {
            if (pairs[i].contains("=")) {
                String[] kv = pairs[i].split("=");
                if (kv.length == 2) {
                    put(kv[0], kv[1]);
                } else {
                    put(kv[0], "");
                }
            } else {
                //Log.e("grandroid", params + " cant extract '" + pairs[i] + "'");
            }
        }
    }

    public String getParameters() {
        return getParameters(true);
    }

    public String getParameters(boolean encode) {
        StringBuilder sb = new StringBuilder();
        if (contentType.equals(SendType.Json)) {
            JSONObject jo = new JSONObject();
            for (String key : param.keySet()) {
                try {
                    jo.put(key, param.get(key));
                } catch (JSONException e) {
                    Log.e("grandroid", null, e);
                }
            }
            sb.append(jo.toString());
        } else {
            for (String key : param.keySet()) {
                if (sb.length() != 0) {
                    sb.append("&");
                }
                try {
                    sb.append(key).append("=").append(encode ? URLEncoder.encode(param.get(key).replaceAll("\\\\/", "/"), "UTF-8") : param.get(key));
                } catch (UnsupportedEncodingException ex) {
                    Log.e("grandroid", null, ex);
                }
            }
        }
        return sb.toString();
    }

    public Molley setMethod(SendMethod method) {
        this.method = method;
        return this;
    }

    public SendMethod getMethod() {
        return method;
    }

    public Molley asHttps() {
        this.isHttps = true;
        return this;
    }

    /**
     * @return
     */
    public Molley asPost() {
        method = SendMethod.Post;
        return this;
    }

    /**
     * @return
     */
    public Molley asGet() {
        method = SendMethod.Get;
        return this;
    }

    /**
     * @return
     */
    public Molley asPut() {
        method = SendMethod.Put;
        return this;
    }

    /**
     * @return
     */
    public Molley asDelete() {
        method = SendMethod.Delete;
        return this;
    }

    public Molley contentJSON() {
        contentType = SendType.Json;
        return this;
    }

    public static RequestQueue getRequestQueue() {
        return requestQueue;
    }

    /**
     * 開始連線傳輸
     *
     * @return server端回應的字串傳回
     */
    public <T> void send(final ResultHandler<T> resultHandler) {
        try {
            StringRequest request = new StringRequest(method.getVolleyMethod(), uri, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Class<T> parameterClass = (Class<T>) ((ParameterizedType) resultHandler.getClass().getGenericInterfaces()[0])
                            .getActualTypeArguments()[0];
                    if (parameterClass == String.class) {
                        resultHandler.onAPIResult((T) response);
                    } else if (parameterClass == JSONObject.class) {
                        try {
                            resultHandler.onAPIResult((T) new JSONObject(response));
                        } catch (JSONException e) {
                            resultHandler.onAPIError(e);
                        }
                    } else if (parameterClass == JSONArray.class) {
                        try {
                            resultHandler.onAPIResult((T) new JSONArray(response));
                        } catch (JSONException e) {
                            resultHandler.onAPIError(e);
                        }
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    resultHandler.onAPIError(error);
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    return getHeaderParams();
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    return param;
                }

                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    return super.parseNetworkResponse(response);
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    return getParameters().getBytes();
                }

                @Override
                public String getBodyContentType() {
                    return contentType + "; charset=" + encoding;
                }
            };
            request.setRetryPolicy(new DefaultRetryPolicy(timeout,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
// Adding request to request queue
            requestQueue.add(request);
        } catch (Exception ex) {
            resultHandler.onAPIError(ex);
        }
    }


    protected boolean isVerifiedResponseCode(int responseCode) {
        return responseCode >= HttpURLConnection.HTTP_OK && responseCode < 400;
    }

    /**
     * @return
     */
    public static String getCookie() {
        return cookie;
    }

    /**
     * @param connection
     */
    protected void extractCookie(HttpURLConnection connection) {
        String cookieVal = null;
//        cookie = "";
        String tempCookie = "";
        String key = null;
        for (int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++) {
            if (key.equalsIgnoreCase("set-cookie")) {
                cookieVal = connection.getHeaderField(i);
                Log.d("grandroid", "cookie: " + cookieVal);
                cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));
                tempCookie = tempCookie + cookieVal + ";";
            }
        }
        if (tempCookie.length() != 0) {
            cookie = tempCookie;
        }
        Log.d("grandroid", "get cookie: " + cookie);
    }
}
