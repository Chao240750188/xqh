package com.essence.business.xqh.api.realtimemonitor.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/21 0021 19:54
 */
public class WeatherBean {

    /**
     * temperature : 4.1
     * temperatureDiff : 1
     * airpressure : 1000
     * humidity : 71
     * rain : 0
     * rcomfort : 38
     * icomfort : -3
     * info : 晴
     * img : 0
     * feelst : 1.7
     */

    @SerializedName("temperature")
    private double temperature;
    @SerializedName("temperatureDiff")
    private int temperatureDiff;
    @SerializedName("airpressure")
    private int airpressure;
    @SerializedName("humidity")
    private int humidity;
    @SerializedName("rain")
    private int rain;
    @SerializedName("rcomfort")
    private int rcomfort;
    @SerializedName("icomfort")
    private int icomfort;
    @SerializedName("info")
    private String info;
    @SerializedName("img")
    private String img;
    @SerializedName("feelst")
    private double feelst;

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getTemperatureDiff() {
        return temperatureDiff;
    }

    public void setTemperatureDiff(int temperatureDiff) {
        this.temperatureDiff = temperatureDiff;
    }

    public int getAirpressure() {
        return airpressure;
    }

    public void setAirpressure(int airpressure) {
        this.airpressure = airpressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getRain() {
        return rain;
    }

    public void setRain(int rain) {
        this.rain = rain;
    }

    public int getRcomfort() {
        return rcomfort;
    }

    public void setRcomfort(int rcomfort) {
        this.rcomfort = rcomfort;
    }

    public int getIcomfort() {
        return icomfort;
    }

    public void setIcomfort(int icomfort) {
        this.icomfort = icomfort;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public double getFeelst() {
        return feelst;
    }

    public void setFeelst(double feelst) {
        this.feelst = feelst;
    }
}
