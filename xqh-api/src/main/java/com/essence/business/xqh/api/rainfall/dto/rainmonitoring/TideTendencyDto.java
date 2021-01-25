package com.essence.business.xqh.api.rainfall.dto.rainmonitoring;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author fengpp
 * 2021/1/25 18:57
 */
public class TideTendencyDto {

    private double high;
    private double low;
    private BigDecimal obhtz;//最高水位
    private BigDecimal hlz;//最低水位
    private String maxTm;//最高水位时间
    private String minTm;//最低水位时间
    private List<TideTendency> list;

    public TideTendencyDto() {
    }

    public TideTendencyDto(double high, double low, BigDecimal obhtz, BigDecimal hlz, String maxTm, String minTm, List<TideTendency> list) {
        this.high = high;
        this.low = low;
        this.obhtz = obhtz;
        this.hlz = hlz;
        this.maxTm = maxTm;
        this.minTm = minTm;
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

    public BigDecimal getObhtz() {
        return obhtz;
    }

    public void setObhtz(BigDecimal obhtz) {
        this.obhtz = obhtz;
    }

    public BigDecimal getHlz() {
        return hlz;
    }

    public void setHlz(BigDecimal hlz) {
        this.hlz = hlz;
    }

    public String getMaxTm() {
        return maxTm;
    }

    public void setMaxTm(String maxTm) {
        this.maxTm = maxTm;
    }

    public String getMinTm() {
        return minTm;
    }

    public void setMinTm(String minTm) {
        this.minTm = minTm;
    }

    public List<TideTendency> getList() {
        return list;
    }

    public void setList(List<TideTendency> list) {
        this.list = list;
    }
}
