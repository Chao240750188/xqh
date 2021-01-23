package com.essence.business.xqh.api.realtimemonitor.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/21 0021 19:54
 */
public class WarnBean {

    /**
     * alert : 9999
     * pic : 9999
     * province : 9999
     * city : 9999
     * url : 9999
     * issuecontent : 9999
     * fmeans : 9999
     * signaltype : 9999
     * signallevel : 9999
     * pic2 : 9999
     */

    @SerializedName("alert")
    private String alert;
    @SerializedName("pic")
    private String pic;
    @SerializedName("province")
    private String province;
    @SerializedName("city")
    private String city;
    @SerializedName("url")
    private String url;
    @SerializedName("issuecontent")
    private String issuecontent;
    @SerializedName("fmeans")
    private String fmeans;
    @SerializedName("signaltype")
    private String signaltype;
    @SerializedName("signallevel")
    private String signallevel;
    @SerializedName("pic2")
    private String pic2;

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
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

    public String getIssuecontent() {
        return issuecontent;
    }

    public void setIssuecontent(String issuecontent) {
        this.issuecontent = issuecontent;
    }

    public String getFmeans() {
        return fmeans;
    }

    public void setFmeans(String fmeans) {
        this.fmeans = fmeans;
    }

    public String getSignaltype() {
        return signaltype;
    }

    public void setSignaltype(String signaltype) {
        this.signaltype = signaltype;
    }

    public String getSignallevel() {
        return signallevel;
    }

    public void setSignallevel(String signallevel) {
        this.signallevel = signallevel;
    }

    public String getPic2() {
        return pic2;
    }

    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }
}
