package com.essence.business.xqh.api.realtimemonitor.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/21 0021 19:54
 */
public class DetailBean {

    /**
     * date : 2021-01-21
     * pt : 2021-01-21 20:00
     * day : {"weather":{"info":"9999","img":"9999","temperature":"9999"},"wind":{"direct":"9999","power":"9999"}}
     * night : {"weather":{"info":"多云","img":"1","temperature":"0"},"wind":{"direct":"东北风","power":"微风"}}
     */

    @SerializedName("date")
    private String date;
    @SerializedName("pt")
    private String pt;
    @SerializedName("day")
    private DayBean day;
    @SerializedName("night")
    private NightBean night;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPt() {
        return pt;
    }

    public void setPt(String pt) {
        this.pt = pt;
    }

    public DayBean getDay() {
        return day;
    }

    public void setDay(DayBean day) {
        this.day = day;
    }

    public NightBean getNight() {
        return night;
    }

    public void setNight(NightBean night) {
        this.night = night;
    }
}
