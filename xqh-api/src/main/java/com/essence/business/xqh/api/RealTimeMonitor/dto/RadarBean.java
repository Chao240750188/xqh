package com.essence.business.xqh.api.realtimemonitor.dto;

import com.google.gson.annotations.SerializedName;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/21 0021 19:54
 */
public class RadarBean {

    /**
     * title : 济南
     * image : /product/2021/01/21/RDCP/SEVP_AOC_RDCP_SLDAS_EBREF_AZ9531_L88_PI_20210121091800000.PNG?v=1611220841945
     * url : /publish/radar/shan-dong/ji-nan.htm
     */

    @SerializedName("title")
    private String title;
    @SerializedName("image")
    private String image;
    @SerializedName("url")
    private String url;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
