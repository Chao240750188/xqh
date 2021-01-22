package com.essence.business.xqh.api.realtimemonitor.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/21 0021 19:54
 */
public class TempchartBean {

    /**
     * time : 2021/01/14
     * max_temp : 15.9
     * min_temp : 2
     * day_img : 9999
     * day_text : 9999
     * night_img : 9999
     * night_text : 9999
     */

    @SerializedName("time")
    private String time;
    @SerializedName("max_temp")
    private double maxTemp;
    @SerializedName("min_temp")
    private int minTemp;
    @SerializedName("day_img")
    private String dayImg;
    @SerializedName("day_text")
    private String dayText;
    @SerializedName("night_img")
    private String nightImg;
    @SerializedName("night_text")
    private String nightText;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(double maxTemp) {
        this.maxTemp = maxTemp;
    }

    public int getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(int minTemp) {
        this.minTemp = minTemp;
    }

    public String getDayImg() {
        return dayImg;
    }

    public void setDayImg(String dayImg) {
        this.dayImg = dayImg;
    }

    public String getDayText() {
        return dayText;
    }

    public void setDayText(String dayText) {
        this.dayText = dayText;
    }

    public String getNightImg() {
        return nightImg;
    }

    public void setNightImg(String nightImg) {
        this.nightImg = nightImg;
    }

    public String getNightText() {
        return nightText;
    }

    public void setNightText(String nightText) {
        this.nightText = nightText;
    }
}
