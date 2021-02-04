package com.essence.business.xqh.api.waterandrain.dto;

import java.math.BigDecimal;

/**
 * @author fengpp
 * 2021/1/30 19:20
 */
public class WaterLevelDto {
    private String stcd;
    private String stnm;
    private BigDecimal maxWaterLevel;//最高水位
    private BigDecimal minWaterLevel;//最低水位
    private double lgtd;//经度
    private double lttd;//纬度

    public WaterLevelDto() {
    }

    public WaterLevelDto(String stcd, String stnm, double lgtd, double lttd) {
        this.stcd = stcd;
        this.stnm = stnm;
        this.lgtd = lgtd;
        this.lttd = lttd;
    }

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public String getStnm() {
        return stnm;
    }

    public void setStnm(String stnm) {
        this.stnm = stnm;
    }

    public BigDecimal getMaxWaterLevel() {
        return maxWaterLevel;
    }

    public void setMaxWaterLevel(BigDecimal maxWaterLevel) {
        this.maxWaterLevel = maxWaterLevel;
    }

    public BigDecimal getMinWaterLevel() {
        return minWaterLevel;
    }

    public void setMinWaterLevel(BigDecimal minWaterLevel) {
        this.minWaterLevel = minWaterLevel;
    }

    public double getLgtd() {
        return lgtd;
    }

    public void setLgtd(double lgtd) {
        this.lgtd = lgtd;
    }

    public double getLttd() {
        return lttd;
    }

    public void setLttd(double lttd) {
        this.lttd = lttd;
    }
}
