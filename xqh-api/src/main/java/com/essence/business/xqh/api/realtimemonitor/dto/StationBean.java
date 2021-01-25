package com.essence.business.xqh.api.realtimemonitor.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/21 0021 19:54
 */
public class StationBean {

    /**
     * code : 54823
     * province : 山东省
     * city : 济南
     * url : /publish/forecast/ASD/jinan.html
     */

    @SerializedName("code")
    private String code;
    @SerializedName("province")
    private String province;
    @SerializedName("city")
    private String city;
    @SerializedName("url")
    private String url;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
