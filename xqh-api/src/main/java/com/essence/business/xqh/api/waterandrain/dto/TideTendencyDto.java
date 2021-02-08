package com.essence.business.xqh.api.waterandrain.dto;

import com.essence.business.xqh.api.rainfall.dto.rainmonitoring.TideTendency;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author fengpp
 * 2021/1/25 18:57
 */
public class TideTendencyDto {

    private double high;
    private double low;
    private BigDecimal maxWaterLevel;//最高水位
    private BigDecimal minWaterLevel;//最低水位
    private String maxWaterLevelTm;//最高水位时间
    private String minWaterLevelTm;//最低水位时间
    private List<TideTendency> list;

    public TideTendencyDto() {
    }

    public TideTendencyDto(double high, double low, BigDecimal maxWaterLevel, BigDecimal minWaterLevel, String maxWaterLevelTm, String minWaterLevelTm, List<TideTendency> list) {
        this.high = high;
        this.low = low;
        this.maxWaterLevel = maxWaterLevel;
        this.minWaterLevel = minWaterLevel;
        this.maxWaterLevelTm = maxWaterLevelTm;
        this.minWaterLevelTm = minWaterLevelTm;
        this.list = list;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
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

    public String getMaxWaterLevelTm() {
        return maxWaterLevelTm;
    }

    public void setMaxWaterLevelTm(String maxWaterLevelTm) {
        this.maxWaterLevelTm = maxWaterLevelTm;
    }

    public String getMinWaterLevelTm() {
        return minWaterLevelTm;
    }

    public void setMinWaterLevelTm(String minWaterLevelTm) {
        this.minWaterLevelTm = minWaterLevelTm;
    }

    public List<TideTendency> getList() {
        return list;
    }

    public void setList(List<TideTendency> list) {
        this.list = list;
    }
}
