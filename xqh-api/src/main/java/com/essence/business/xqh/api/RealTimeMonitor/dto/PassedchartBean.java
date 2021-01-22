package com.essence.business.xqh.api.realtimemonitor.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/21 0021 19:54
 */
public class PassedchartBean {

    /**
     * rain1h : 0
     * rain24h : 9999
     * rain12h : 9999
     * rain6h : 9999
     * temperature : 4.8
     * tempDiff :
     * humidity : 69
     * pressure : 1000
     * windDirection : 1
     * windSpeed : 1.5
     * time : 2021-01-21 18:00
     */

    @SerializedName("rain1h")
    private int rain1h;
    @SerializedName("rain24h")
    private int rain24h;
    @SerializedName("rain12h")
    private int rain12h;
    @SerializedName("rain6h")
    private int rain6h;
    @SerializedName("temperature")
    private double temperature;
    @SerializedName("tempDiff")
    private String tempDiff;
    @SerializedName("humidity")
    private int humidity;
    @SerializedName("pressure")
    private int pressure;
    @SerializedName("windDirection")
    private int windDirection;
    @SerializedName("windSpeed")
    private double windSpeed;
    @SerializedName("time")
    private String time;

    public int getRain1h() {
        return rain1h;
    }

    public void setRain1h(int rain1h) {
        this.rain1h = rain1h;
    }

    public int getRain24h() {
        return rain24h;
    }

    public void setRain24h(int rain24h) {
        this.rain24h = rain24h;
    }

    public int getRain12h() {
        return rain12h;
    }

    public void setRain12h(int rain12h) {
        this.rain12h = rain12h;
    }

    public int getRain6h() {
        return rain6h;
    }

    public void setRain6h(int rain6h) {
        this.rain6h = rain6h;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getTempDiff() {
        return tempDiff;
    }

    public void setTempDiff(String tempDiff) {
        this.tempDiff = tempDiff;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(int windDirection) {
        this.windDirection = windDirection;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
