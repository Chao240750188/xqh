package com.essence.business.xqh.api.realtimemonitor.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/21 0021 19:54
 */
public class MonthBean {

    /**
     * month : 1
     * maxTemp : 4.1
     * minTemp : -4.1
     * precipitation : 5.8
     */

    @SerializedName("month")
    private int month;
    @SerializedName("maxTemp")
    private double maxTemp;
    @SerializedName("minTemp")
    private double minTemp;
    @SerializedName("precipitation")
    private double precipitation;

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public double getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(double minTemp) {
        this.minTemp = minTemp;
    }

    public double getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(double precipitation) {
        this.precipitation = precipitation;
    }
}
