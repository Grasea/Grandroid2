package com.grasea.grandroid.demo.mvp;

import com.grasea.grandroid.demo.BuildConfig;
import com.grasea.grandroid.api.API;
import com.grasea.grandroid.api.Backend;
import com.grasea.grandroid.net.SendMethod;
import com.grasea.grandroid.net.SendType;

/**
 * Created by Rovers on 2016/8/11.
 */
@Backend(BuildConfig.API_URL)
public interface WeatherAPI {
    @API(name = "forecast", path = "/121.5223,25.0270/forecast", method = SendMethod.Get, contentType = SendType.Json)
    public boolean getForecast();

    @API(name = "forecast2", path = "/121.5223,25.0270/forecast", method = SendMethod.Get)
    public boolean getForecastObject();


}
