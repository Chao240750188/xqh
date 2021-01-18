package com.essence.business.xqh.api.floodForecast.dto;

/**
 * LiuGt add at 2020-03-12
 * 安新江模型，文本文件输入参数-时段数据实体类
 */
public class XajModelTxtInputParamHourDto {
    /**
     * 洪水标号,这里使用方案ID代替
     */
    private String floodId;

    /**
     * 时间(时段),格式：2:00 or 10:00
     */
    private String floodDate;

    /**
     * 降雨量/h
     */
    private Double floodPrecip;

    /**
     * 蒸发量/h
     */
    private Double floodEvapor;

    public String getFloodId() {
        return floodId;
    }

    public void setFloodId(String floodId) {
        this.floodId = floodId;
    }

    public String getFloodDate() {
        return floodDate;
    }

    public void setFloodDate(String floodDate) {
        this.floodDate = floodDate;
    }

    public Double getFloodPrecip() {
        return floodPrecip;
    }

    public void setFloodPrecip(Double floodPrecip) {
        this.floodPrecip = floodPrecip;
    }

    public Double getFloodEvapor() {
        return floodEvapor;
    }

    public void setFloodEvapor(Double floodEvapor) {
        this.floodEvapor = floodEvapor;
    }
}
