package com.grasea.grandroid.demo.mvp;

import java.util.ArrayList;

/**
 * Created by Rovers on 2016/8/11.
 */
public class Forecast {
    String status;
    String lang;
    ForecastResult result;

    static class ForecastResult {
        Hourly hourly;
    }

    static class Hourly {
        String status;
        String description;
        ArrayList<Skycon> skycon;
        ArrayList<Cloudrate> cloudrate;
    }

    static class Skycon {
        String value;
        String datetime;
    }

    static class Cloudrate {
        double value;
        String datetime;
    }
}
