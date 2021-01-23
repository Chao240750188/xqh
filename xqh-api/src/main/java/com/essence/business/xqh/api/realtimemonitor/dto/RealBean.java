package com.essence.business.xqh.api.realtimemonitor.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/21 0021 19:54
 */
public class RealBean {

    /**
     * station : {"code":"54823","province":"山东省","city":"济南","url":"/publish/forecast/ASD/jinan.html"}
     * publish_time : 2021-01-21 18:40
     * weather : {"temperature":4.1,"temperatureDiff":1,"airpressure":1000,"humidity":71,"rain":0,"rcomfort":38,"icomfort":-3,"info":"晴","img":"0","feelst":1.7}
     * wind : {"direct":"东北风","power":"微风","speed":""}
     * warn : {"alert":"9999","pic":"9999","province":"9999","city":"9999","url":"9999","issuecontent":"9999","fmeans":"9999","signaltype":"9999","signallevel":"9999","pic2":"9999"}
     */

    @SerializedName("station")
    private StationBean station;
    @SerializedName("publish_time")
    private String publishTime;
    @SerializedName("weather")
    private WeatherBean weather;
    @SerializedName("wind")
    private WindBean wind;
    @SerializedName("warn")
    private WarnBean warn;

    public StationBean getStation() {
        return station;
    }

    public void setStation(StationBean station) {
        this.station = station;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public WeatherBean getWeather() {
        return weather;
    }

    public void setWeather(WeatherBean weather) {
        this.weather = weather;
    }

    public WindBean getWind() {
        return wind;
    }

    public void setWind(WindBean wind) {
        this.wind = wind;
    }

    public WarnBean getWarn() {
        return warn;
    }

    public void setWarn(WarnBean warn) {
        this.warn = warn;
    }
}
