package com.essence.business.xqh.api.floodScheduling.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;

/**
 * 调度结果数据统计
 * LiuGt add at 2020-04-08
 */
public class SchedulingResultStatisticsDto {

    /**
     * 起调水位
     */
    private BigDecimal wl;

    /**
     * 最高水位
     */
    private BigDecimal maxRz;

    /**
     * 最高水位蓄水量
     */
    private BigDecimal maxRzW;

    /**
     * 入库洪峰
     */
    private BigDecimal maxInq;

    /**
     * 入库洪峰峰现时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private String maxInqTime;

    /**
     * 入库水量
     */
    private BigDecimal totalInq;

    /**
     * 出库洪峰
     */
    private BigDecimal maxOtq;

    /**
     * 出库洪峰峰现时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private String maxOtqTime;

    /**
     * 出库水量
     */
    private BigDecimal totalOtq;

    public BigDecimal getWl() {
        return wl;
    }

    public void setWl(BigDecimal wl) {
        this.wl = wl;
    }

    public BigDecimal getMaxRz() {
        return maxRz;
    }

    public void setMaxRz(BigDecimal maxRz) {
        this.maxRz = maxRz;
    }

    public BigDecimal getMaxRzW() {
        return maxRzW;
    }

    public void setMaxRzW(BigDecimal maxRzW) {
        this.maxRzW = maxRzW;
    }

    public BigDecimal getMaxInq() {
        return maxInq;
    }

    public void setMaxInq(BigDecimal maxInq) {
        this.maxInq = maxInq;
    }

    public String getMaxInqTime() {
        return maxInqTime;
    }

    public void setMaxInqTime(String maxInqTime) {
        this.maxInqTime = maxInqTime;
    }

    public BigDecimal getTotalInq() {
        return totalInq;
    }

    public void setTotalInq(BigDecimal totalInq) {
        this.totalInq = totalInq;
    }

    public BigDecimal getMaxOtq() {
        return maxOtq;
    }

    public void setMaxOtq(BigDecimal maxOtq) {
        this.maxOtq = maxOtq;
    }

    public String getMaxOtqTime() {
        return maxOtqTime;
    }

    public void setMaxOtqTime(String maxOtqTime) {
        this.maxOtqTime = maxOtqTime;
    }

    public BigDecimal getTotalOtq() {
        return totalOtq;
    }

    public void setTotalOtq(BigDecimal totalOtq) {
        this.totalOtq = totalOtq;
    }
}
