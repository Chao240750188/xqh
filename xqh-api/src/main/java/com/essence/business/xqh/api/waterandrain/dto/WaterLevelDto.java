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
    private Double lgtd;//经度
    private Double lttd;//纬度
    private String sttp;//站类

    public WaterLevelDto() {
    }

    public WaterLevelDto(String stcd, String stnm, Double lgtd, Double lttd, String sttp) {
        this.stcd = stcd;
        this.stnm = stnm;
        this.lgtd = lgtd;
        this.lttd = lttd;
        this.sttp = sttp;
    }

    public String getSttp() {
        return sttp;
    }

    public void setSttp(String sttp) {
        this.sttp = sttp;
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

    public Double getLgtd() {
        return lgtd;
    }

    public void setLgtd(Double lgtd) {
        this.lgtd = lgtd;
    }

    public Double getLttd() {
        return lttd;
    }

    public void setLttd(Double lttd) {
        this.lttd = lttd;
    }
}
