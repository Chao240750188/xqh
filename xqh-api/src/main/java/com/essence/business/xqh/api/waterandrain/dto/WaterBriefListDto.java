package com.essence.business.xqh.api.waterandrain.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fengpp
 * 2021/1/29 20:01
 */
public class WaterBriefListDto {
    private String rvnm;//河名
    private String stnm;//站名
    private BigDecimal monthLowWaterLevel;//月最低水位
    private BigDecimal maxWaterLevel;//月最高水位-实况
    private Date maxWaterLevelTm;//月最高水位-出现日期
    private BigDecimal maxWaterLevelYearMax;//月最高水位-历年最高
    private Integer maxWaterLevelYear;//月最高水位-出现年份
    private BigDecimal maxWaterLevelAvg;//月最高水位-历年均值

    private BigDecimal avgWaterLevel;//月平均水位-实况
    private BigDecimal avgWaterLevelYearAvg;//月平均水位-历年均值
    private BigDecimal avgWaterLevelAbsolute;//月平均水位-绝对距平

    private BigDecimal maxFlow;//月最大流量-实况
    private Date maxFlowTm;//月最大流量-时间
    private BigDecimal maxFlowYearMax;//月最大流量-历年最高
    private Integer maxFlowYear;//月最大流量-出现年份
    private BigDecimal maxFlowAvg;//月最大流量-历年均值

    private BigDecimal avgFlow;//月平均流量-实况
    private BigDecimal avgFlowYear;//月平均流量-历年均值
    private BigDecimal avgFlowAbsolute;//月平均流量-绝对距平

    public String getRvnm() {
        return rvnm;
    }

    public void setRvnm(String rvnm) {
        this.rvnm = rvnm;
    }

    public String getStnm() {
        return stnm;
    }

    public void setStnm(String stnm) {
        this.stnm = stnm;
    }

    public BigDecimal getMonthLowWaterLevel() {
        return monthLowWaterLevel;
    }

    public void setMonthLowWaterLevel(BigDecimal monthLowWaterLevel) {
        this.monthLowWaterLevel = monthLowWaterLevel;
    }

    public BigDecimal getMaxWaterLevel() {
        return maxWaterLevel;
    }

    public void setMaxWaterLevel(BigDecimal maxWaterLevel) {
        this.maxWaterLevel = maxWaterLevel;
    }

    public Date getMaxWaterLevelTm() {
        return maxWaterLevelTm;
    }

    public void setMaxWaterLevelTm(Date maxWaterLevelTm) {
        this.maxWaterLevelTm = maxWaterLevelTm;
    }

    public BigDecimal getMaxWaterLevelYearMax() {
        return maxWaterLevelYearMax;
    }

    public void setMaxWaterLevelYearMax(BigDecimal maxWaterLevelYearMax) {
        this.maxWaterLevelYearMax = maxWaterLevelYearMax;
    }

    public Integer getMaxWaterLevelYear() {
        return maxWaterLevelYear;
    }

    public void setMaxWaterLevelYear(Integer maxWaterLevelYear) {
        this.maxWaterLevelYear = maxWaterLevelYear;
    }

    public BigDecimal getMaxWaterLevelAvg() {
        return maxWaterLevelAvg;
    }

    public void setMaxWaterLevelAvg(BigDecimal maxWaterLevelAvg) {
        this.maxWaterLevelAvg = maxWaterLevelAvg;
    }

    public BigDecimal getAvgWaterLevel() {
        return avgWaterLevel;
    }

    public void setAvgWaterLevel(BigDecimal avgWaterLevel) {
        this.avgWaterLevel = avgWaterLevel;
    }

    public BigDecimal getAvgWaterLevelYearAvg() {
        return avgWaterLevelYearAvg;
    }

    public void setAvgWaterLevelYearAvg(BigDecimal avgWaterLevelYearAvg) {
        this.avgWaterLevelYearAvg = avgWaterLevelYearAvg;
    }

    public BigDecimal getAvgWaterLevelAbsolute() {
        return avgWaterLevelAbsolute;
    }

    public void setAvgWaterLevelAbsolute(BigDecimal avgWaterLevelAbsolute) {
        this.avgWaterLevelAbsolute = avgWaterLevelAbsolute;
    }

    public BigDecimal getMaxFlow() {
        return maxFlow;
    }

    public void setMaxFlow(BigDecimal maxFlow) {
        this.maxFlow = maxFlow;
    }

    public Date getMaxFlowTm() {
        return maxFlowTm;
    }

    public void setMaxFlowTm(Date maxFlowTm) {
        this.maxFlowTm = maxFlowTm;
    }

    public BigDecimal getMaxFlowYearMax() {
        return maxFlowYearMax;
    }

    public void setMaxFlowYearMax(BigDecimal maxFlowYearMax) {
        this.maxFlowYearMax = maxFlowYearMax;
    }

    public Integer getMaxFlowYear() {
        return maxFlowYear;
    }

    public void setMaxFlowYear(Integer maxFlowYear) {
        this.maxFlowYear = maxFlowYear;
    }

    public BigDecimal getMaxFlowAvg() {
        return maxFlowAvg;
    }

    public void setMaxFlowAvg(BigDecimal maxFlowAvg) {
        this.maxFlowAvg = maxFlowAvg;
    }

    public BigDecimal getAvgFlow() {
        return avgFlow;
    }

    public void setAvgFlow(BigDecimal avgFlow) {
        this.avgFlow = avgFlow;
    }

    public BigDecimal getAvgFlowYear() {
        return avgFlowYear;
    }

    public void setAvgFlowYear(BigDecimal avgFlowYear) {
        this.avgFlowYear = avgFlowYear;
    }

    public BigDecimal getAvgFlowAbsolute() {
        return avgFlowAbsolute;
    }

    public void setAvgFlowAbsolute(BigDecimal avgFlowAbsolute) {
        this.avgFlowAbsolute = avgFlowAbsolute;
    }
}
