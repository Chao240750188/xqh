package com.essence.business.xqh.api.rainfall.dto.rainmonitoring;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * @author fengpp
 * 2021/1/26 18:57
 */
public class FloodWarningListDto {
    private String stcd;
    private String stnm;
    private BigDecimal hourWaterLevel;//8时水位
    private BigDecimal hourFlow;//8时流量
    private BigDecimal maxWaterLevel;//最高水位
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date maxWaterLevelTm;//最高水位时间
    private BigDecimal maxFlow;//最大流量
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date maxFlowTm;//最大流量时间
    private BigDecimal wrz;//警戒水位
    private BigDecimal beyondWrz;//超警戒水位
    private BigDecimal grz;//保证水位
    private BigDecimal beyondGrz;//超保证水位
    private BigDecimal history;//历史最高
    private BigDecimal beyondHistory;//超历史最高
    private String rvnm;//河流
    private String hnnm;//水系

    public FloodWarningListDto() {
    }

    public FloodWarningListDto(String stcd, Map<String, Object> map) {
        String stnm = map.get("STNM") == null ? "" : map.get("STNM").toString();
        String rvnm = map.get("RVNM").toString() == null ? "" : map.get("RVNM").toString();
        String hnnm = map.get("HNNM").toString() == null ? "" : map.get("HNNM").toString();
        BigDecimal wrz = new BigDecimal(map.get("WRZ") == null ? "0" : map.get("WRZ").toString());
        BigDecimal grz = new BigDecimal(map.get("GRZ") == null ? "0" : map.get("GRZ").toString());
        BigDecimal obhtz = new BigDecimal(map.get("OBHTZ") == null ? "0" : map.get("OBHTZ").toString());
        this.stcd = stcd;
        this.stnm = stnm;
        this.wrz = wrz;
        this.grz = grz;
        this.history = obhtz;
        this.rvnm = rvnm;
        this.hnnm = hnnm;
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

    public BigDecimal getHourWaterLevel() {
        return hourWaterLevel;
    }

    public void setHourWaterLevel(BigDecimal hourWaterLevel) {
        this.hourWaterLevel = hourWaterLevel;
    }

    public BigDecimal getHourFlow() {
        return hourFlow;
    }

    public void setHourFlow(BigDecimal hourFlow) {
        this.hourFlow = hourFlow;
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

    public BigDecimal getWrz() {
        return wrz;
    }

    public void setWrz(BigDecimal wrz) {
        this.wrz = wrz;
    }

    public BigDecimal getBeyondWrz() {
        if (maxWaterLevel != null) {
            if (wrz != null) {
                this.beyondWrz = maxWaterLevel.subtract(wrz);
            } else {
                this.beyondWrz = maxWaterLevel;
            }
        } else if (wrz != null) {
            this.beyondWrz = new BigDecimal(0).subtract(maxWaterLevel);
        }
        return beyondWrz;
    }

    public void setBeyondWrz(BigDecimal beyondWrz) {
        this.beyondWrz = beyondWrz;
    }

    public BigDecimal getGrz() {
        return grz;
    }

    public void setGrz(BigDecimal grz) {
        this.grz = grz;
    }

    public BigDecimal getBeyondGrz() {
        if (maxWaterLevel != null) {
            if (grz != null) {
                this.beyondGrz = maxWaterLevel.subtract(grz);
            } else {
                this.beyondGrz = maxWaterLevel;
            }
        } else if (grz != null) {
            this.beyondGrz = new BigDecimal(0).subtract(maxWaterLevel);
        }
        return beyondGrz;
    }

    public void setBeyondGrz(BigDecimal beyondGrz) {
        this.beyondGrz = beyondGrz;
    }

    public BigDecimal getHistory() {
        return history;
    }

    public void setHistory(BigDecimal history) {
        this.history = history;
    }

    public BigDecimal getBeyondHistory() {
        if (maxWaterLevel != null) {
            if (history != null) {
                this.beyondHistory = maxWaterLevel.subtract(history);
            } else {
                this.beyondHistory = maxWaterLevel;
            }
        } else if (history != null) {
            this.beyondHistory = new BigDecimal(0).subtract(maxWaterLevel);
        }

        return beyondHistory;
    }

    public void setBeyondHistory(BigDecimal beyondHistory) {
        this.beyondHistory = beyondHistory;
    }

    public String getRvnm() {
        return rvnm;
    }

    public void setRvnm(String rvnm) {
        this.rvnm = rvnm;
    }

    public String getHnnm() {
        return hnnm;
    }

    public void setHnnm(String hnnm) {
        this.hnnm = hnnm;
    }
}
