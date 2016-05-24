/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.net;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * HTTP連線物件，用來擷取網頁的回傳結果，可用在取得JSON字串或網頁HTML的時候，支援POST。
 *
 * @author Rovers
 */
public class Mon {

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
    protected boolean loginConnection = false;
    /**
     *
     */
    protected int method;

    protected String contentType;

    /**
     *
     */
    public boolean isHttps;
    /**
     *
     */
    public static final int POST = 0;
    /**
     *
     */
    public static final int GET = 1;
    /**
     *
     */
    public static final int PUT = 2;
    /**
     *
     */
    public static final int DELETE = 3;

    public static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";

    public static final String CONTENT_TYPE_JSON = "application/json";

    /**
     * @param uri 欲擷取資料的URL
     */
    public Mon(String uri) {
        this(uri, false);
    }

    /**
     * @param uri 欲擷取資料的URL
     */
    public Mon(String uri, boolean keepCookie) {
        this.uri = uri;
        isHttps = uri.startsWith("https");
        this.encoding = "UTF-8";
        param = new HashMap<String, String>();
        headerParams = new HashMap<String, String>();
        method = 0;//default use POST
        this.keepingCookie = keepCookie;
        contentType = CONTENT_TYPE_FORM_URLENCODED;
    }

    /**
     * @return
     */
    protected Mon getLoginMon() {
        return null;
    }

    /**
     * @return
     */
    public Mon asLoginConnection() {
        loginConnection = true;
        return this;
    }

    /**
     * @param result
     * @return
     */
    protected boolean handleLogin(String result) {
        return false;
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
    public Mon setKeepingCookie(boolean keepingCookie) {
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
    public Mon encode(String encoding) {
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
    public Mon put(String key, String value) {
        param.put(key, value);
        return this;
    }

    public Mon putHeader(String key, String value) {
        if (headerParams == null) {
            headerParams = new HashMap<String, String>();
        }
        headerParams.put(key, value);
        return this;
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
        if (contentType.equals(CONTENT_TYPE_JSON)){
            JSONObject jo=new JSONObject();
            for (String key : param.keySet()) {
                try {
                    jo.put(key,param.get(key));
                } catch (JSONException e) {
                    Log.e("grandroid", null, e);
                }
            }
            sb.append(jo.toString());
        }else {
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

    /**
     * 開始連線傳輸，並將結果JSON文字包成JSONObject回傳
     *
     * @return server端回傳的JSON文字所包裝成的JSONObject
     * @throws JSONException
     */
    public JSONObject sendAndWrap() throws Exception {
        String string = null;
        try {
            string = sendWithError();
//            if (string != null && !string.startsWith("{")) {
//                Log.e("grandriod", "server response: " + (string.length() > 50 ? string.substring(0, 50) : string));
//            }
            return new JSONObject(string);
        } catch (Exception e) {
            if (string != null) {
                Log.e("grandroid", "server response: " + string);
            }
            Log.e("grandroid", null, e);
            //Log.e("grandroid", "mon url = " + uri);
            //Log.e("grandroid", "mon params = " + getParameters());
            //Log.e("grandroid", "mon result = " + string);
            throw e;
        }
    }

    /**
     * 開始連線傳輸，並將結果JSON文字包成JSONArray回傳
     *
     * @return server端回傳的JSON文字所包裝成的JSONArray
     * @throws JSONException
     */
    public JSONArray sendAndWrapArray() throws Exception {
        String string = null;
        try {
            string = sendWithError();
//            if (string != null && !string.startsWith("[")) {
//                Log.e("grandriod", "server response: " + (string.length() > 50 ? string.substring(0, 50) : string));
//            }
            return new JSONArray(string);
        } catch (Exception e) {
            if (string != null) {
                Log.e("grandroid", "server response: " + string);
            }
            Log.e("grandroid", null, e);
            throw e;
        }
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public int getMethod() {
        return method;
    }

    public Mon asHttps() {
        this.isHttps = true;
        return this;
    }

    /**
     * @return
     */
    public Mon asPost() {
        method = 0;
        return this;
    }

    /**
     * @return
     */
    public Mon asGet() {
        method = 1;
        return this;
    }

    /**
     * @return
     */
    public Mon asPut() {
        method = 2;
        return this;
    }

    /**
     * @return
     */
    public Mon asDelete() {
        method = 3;
        return this;
    }

    public Mon contentJSON(){
        contentType=CONTENT_TYPE_JSON;
        return this;
    }

    /**
     * 開始連線傳輸
     *
     * @return server端回應的字串傳回
     */
    public String send() {
        try {
            return sendWithError();
        } catch (Exception ex) {
            Log.e("grandroid", null, ex);
            return "{msg:\"" + ex.toString() + "\"}";
        }
    }

    public String sendWithError() throws Exception {
        String requestString = getParameters();
//            for (String key : param.keySet()) {
//                String encodedValue = key + "=" + URLEncoder.encode(param.get(key).replaceAll("\\\\/", "/"), "UTF-8");
//                requestString += requestString.length() == 0 ? encodedValue : "&" + encodedValue;
//            }
        //Log.d("grandroid", "real url = " + (uri + (method == 1 ? "?" + requestString : "")));
        URL url = new URL(uri + (method == 1 ? (uri.contains("?") ? requestString : "?" + requestString) : ""));
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        Log.d("grandroid", "keepingCookie=" + isKeepingCookie() + ", cookie=" + getCookie());
        if (isKeepingCookie()) {
            if (getCookie() == null) {
                Mon logMon = getLoginMon();
                if (logMon != null) {
                    Log.d("grandroid", "login...");
                    if (!handleLogin(logMon.asLoginConnection().send())) {
                        throw new Exception("mon auto-login fial");
                    }
                }
            }
        }
        ////设置连接属性
        if (method % 2 == 0) {
            httpConn.setDoOutput(true);//使用 URL 连接进行输出
        }
        httpConn.setDoInput(true);//使用 URL 连接进行输入
        httpConn.setUseCaches(false);//忽略缓存
        httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
//        httpConn.setInstanceFollowRedirects(true);  //you still need to handle redirect manully.
//        HttpURLConnection.setFollowRedirects(true);
        switch (method) {
            case 0:
                httpConn.setRequestMethod("POST");//设置URL请求方法
                break;
            case 1:
                httpConn.setRequestMethod("GET");//设置URL请求方法
                break;
            case 2:
                httpConn.setRequestMethod("PUT");//设置URL请求方法
                break;
            case 3:
                httpConn.setRequestMethod("DELETE");//设置URL请求方法
                break;
        }
        if (method % 2 == 0) {

            byte[] requestStringBytes = requestString.getBytes("UTF-8");
            //httpConn.setRequestProperty("Content-length", "" + requestStringBytes.length);
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpConn.setRequestProperty("Charset", "UTF-8");
            if (keepingCookie && getCookie() != null) {
                Log.d("grandroid", "send Cookie = " + getCookie());
                httpConn.setRequestProperty("Cookie", getCookie());
            }
            if (headerParams != null && !headerParams.isEmpty()) {
                for (String key : headerParams.keySet()) {
                    httpConn.setRequestProperty(key, headerParams.get(key));
                }
            }
            httpConn.connect();
            //
            //建立输出流，并写入数据
            OutputStream outputStream = httpConn.getOutputStream();
            outputStream.write(requestStringBytes);
            outputStream.close();
        } else {
            if (keepingCookie && getCookie() != null) {
                //Log.d("grandroid", "Cookie = " + cookie);
                httpConn.setRequestProperty("Cookie", getCookie());
            }
            if (headerParams != null && !headerParams.isEmpty()) {
                for (String key : headerParams.keySet()) {
                    httpConn.setRequestProperty(key, headerParams.get(key));
                }
            }
            httpConn.connect();
        }
        //获得响应状态
        int responseCode = httpConn.getResponseCode();
        if (isVerifiedResponseCode(responseCode)) {//连接成功
            if (loginConnection || isKeepingCookie()) {
                extractCookie(httpConn);
            }

            //当正确响应时处理数据
            StringBuilder sb = new StringBuilder();
            String readLine;
            BufferedReader responseReader;
            if (httpConn.getHeaderField("Content-Type") != null && httpConn.getHeaderField("Content-Type").contains("charset")) {
                encoding = httpConn.getHeaderField("Content-Type").substring(httpConn.getHeaderField("Content-Type").indexOf("charset=") + 8);
                if (encoding.contains(";")) {
                    encoding = encoding.substring(0, encoding.indexOf(";"));
                }
            }
            //处理响应流，必须与服务器响应流输出的编码一致
            try {
                responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), encoding));
            } catch (Exception e) {
                responseReader = new BufferedReader(new InputStreamReader(httpConn.getErrorStream(), encoding));
            }
            while ((readLine = responseReader.readLine()) != null) {
                sb.append(readLine).append("\n");
            }
            responseReader.close();
            return sb.toString().trim();
        } else {
            return "{\"msg\":\"Mon connect fail\",\"code\":" + responseCode + "}";
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
