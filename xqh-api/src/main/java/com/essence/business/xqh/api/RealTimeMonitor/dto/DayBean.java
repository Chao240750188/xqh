package com.essence.business.xqh.api.realtimemonitor.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/21 0021 19:54
 */
public class DayBean {

    /**
     * weather : {"info":"9999","img":"9999","temperature":"9999"}
     * wind : {"direct":"9999","power":"9999"}
     */

    @SerializedName("weather")
    private WeatherBeanX weather;
    @SerializedName("wind")
    private WindBeanX wind;

    public WeatherBeanX getWeather() {
        return weather;
    }

    public void setWeather(WeatherBeanX weather) {
        this.weather = weather;
    }

    public WindBeanX getWind() {
        return wind;
    }

    public void setWind(WindBeanX wind) {
        this.wind = wind;
    }
}
