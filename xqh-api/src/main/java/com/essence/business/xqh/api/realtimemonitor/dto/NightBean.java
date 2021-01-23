package com.essence.business.xqh.api.realtimemonitor.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/21 0021 19:54
 */
public class NightBean {

    /**
     * weather : {"info":"多云","img":"1","temperature":"0"}
     * wind : {"direct":"东北风","power":"微风"}
     */

    @SerializedName("weather")
    private WeatherBeanXX weather;
    @SerializedName("wind")
    private WindBeanXX wind;

    public WeatherBeanXX getWeather() {
        return weather;
    }

    public void setWeather(WeatherBeanXX weather) {
        this.weather = weather;
    }

    public WindBeanXX getWind() {
        return wind;
    }

    public void setWind(WindBeanXX wind) {
        this.wind = wind;
    }
}
