package com.essence.business.xqh.api.waterandrain.dto;

import com.essence.business.xqh.common.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fengpp
 * 2021/1/30 19:29
 */
public class WaterLevelChangeDto {
    private String stcd;
    private String stnm;
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date maxWaterLevelTm;
    private String maxWaterLevelShowTm;
    private BigDecimal maxWaterLevel;//最高水位
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date minWaterLevelTm;
    private String minWaterLevelShowTm;
    private BigDecimal minWaterLevel;//最低水位
    private BigDecimal maxChange;//最新变幅

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

    public Date getMaxWaterLevelTm() {
        return maxWaterLevelTm;
    }

    public void setMaxWaterLevelTm(Date maxWaterLevelTm) {
        this.maxWaterLevelTm = maxWaterLevelTm;
    }

    public String getMaxWaterLevelShowTm() {
        String value = null;
        if (maxWaterLevelTm != null) {
            value = DateUtil.dateToStringNormal3(maxWaterLevelTm);
        }
        this.maxWaterLevelShowTm = value;
        return maxWaterLevelShowTm;
    }

    public void setMaxWaterLevelShowTm(String maxWaterLevelShowTm) {
        this.maxWaterLevelShowTm = maxWaterLevelShowTm;
    }

    public BigDecimal getMaxWaterLevel() {
        return maxWaterLevel;
    }

    public void setMaxWaterLevel(BigDecimal maxWaterLevel) {
        this.maxWaterLevel = maxWaterLevel;
    }

    public Date getMinWaterLevelTm() {
        return minWaterLevelTm;
    }

    public void setMinWaterLevelTm(Date minWaterLevelTm) {
        this.minWaterLevelTm = minWaterLevelTm;
    }

    public String getMinWaterLevelShowTm() {
        String value = null;
        if (minWaterLevelTm != null) {
            value = DateUtil.dateToStringNormal3(minWaterLevelTm);
        }
        this.minWaterLevelShowTm = value;
        return minWaterLevelShowTm;
    }

    public void setMinWaterLevelShowTm(String minWaterLevelShowTm) {
        this.minWaterLevelShowTm = minWaterLevelShowTm;
    }

    public BigDecimal getMinWaterLevel() {
        return minWaterLevel;
    }

    public void setMinWaterLevel(BigDecimal minWaterLevel) {
        this.minWaterLevel = minWaterLevel;
    }

    public BigDecimal getMaxChange() {
        return maxChange;
    }

    public void setMaxChange(BigDecimal maxChange) {
        this.maxChange = maxChange;
    }
}
