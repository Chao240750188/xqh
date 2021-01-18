package com.essence.business.xqh.api.floodForecast.dto;

/**
 * LiuGt add at 2020-03-11
 * 安新江模型，文本文件输入参数-日数据实体类
 */
public class XajModelTxtInputParamDayDto {

    /**
     * 洪水标号,这里使用方案ID代替
     */
    private String floodId;

    /**
     * 日期及时间,格式：1987/8/17 2:00:00
     */
    private String dayDate;

    /**
     * 日降雨量
     */
    private Double dayPrecip;

    /**
     * 日蒸发量
     */
    private Double dayEvapor;

    public String getFloodId() {
        return floodId;
    }

    public void setFloodId(String floodId) {
        this.floodId = floodId;
    }

    public String getDayDate() {
        return dayDate;
    }

    public void setDayDate(String dayDate) {
        this.dayDate = dayDate;
    }

    public Double getDayPrecip() {
        return dayPrecip;
    }

    public void setDayPrecip(Double dayPrecip) {
        this.dayPrecip = dayPrecip;
    }

    public Double getDayEvapor() {
        return dayEvapor;
    }

    public void setDayEvapor(Double dayEvapor) {
        this.dayEvapor = dayEvapor;
    }
}
