package com.essence.business.xqh.api.realtimemonitor.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/21 0021 19:54
 */
public class AirBean {

    /**
     * forecasttime : 2021-01-21 18:00
     * aqi : 218
     * aq : 5
     * text : 重度污染
     * aqiCode : 99202;99204;99205;99206;99207;99208;99209
     */

    @SerializedName("forecasttime")
    private String forecasttime;
    @SerializedName("aqi")
    private int aqi;
    @SerializedName("aq")
    private int aq;
    @SerializedName("text")
    private String text;
    @SerializedName("aqiCode")
    private String aqiCode;

    public String getForecasttime() {
        return forecasttime;
    }

    public void setForecasttime(String forecasttime) {
        this.forecasttime = forecasttime;
    }

    public int getAqi() {
        return aqi;
    }

    public void setAqi(int aqi) {
        this.aqi = aqi;
    }

    public int getAq() {
        return aq;
    }

    public void setAq(int aq) {
        this.aq = aq;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAqiCode() {
        return aqiCode;
    }

    public void setAqiCode(String aqiCode) {
        this.aqiCode = aqiCode;
    }
}
