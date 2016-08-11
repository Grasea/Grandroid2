package com.grasea.grandroid.demo.mvp;

import com.grasea.grandroid.mvp.api.API;
import com.grasea.grandroid.mvp.api.Backend;
import com.grasea.grandroid.net.SendMethod;

import org.json.JSONObject;

/**
 * Created by Rovers on 2016/8/11.
 */
@Backend("http://api.caiyunapp.com/v2/KtK4V+sX75AisJEd")
public interface WeatherAPI {
    @API(value = "/121.5223,25.0270/forecast", method = SendMethod.Get)
    public boolean getForecast();

    @API(value = "/121.5223,25.0270/forecast", method = SendMethod.Get)
    public boolean getForecastObject();
}
