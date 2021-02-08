package com.essence.business.xqh.api.waterandrain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fengpp
 * 2021/2/4 16:16
 */
public class WaterLevelChangeDto {
    private String stcd;
    private String stnm;
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date tm;
    private BigDecimal waterLevel;
    private BigDecimal flow;
    private BigDecimal waterLevelChange;//水位变幅
    private int rwptn;//水势 落 4 涨 5 平 6

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

    public Date getTm() {
        return tm;
    }

    public void setTm(Date tm) {
        this.tm = tm;
    }

    public BigDecimal getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(BigDecimal waterLevel) {
        this.waterLevel = waterLevel;
    }

    public BigDecimal getFlow() {
        return flow;
    }

    public void setFlow(BigDecimal flow) {
        this.flow = flow;
    }

    public BigDecimal getWaterLevelChange() {
        return waterLevelChange;
    }

    public void setWaterLevelChange(BigDecimal waterLevelChange) {
        this.waterLevelChange = waterLevelChange;
    }

    public int getRwptn() {
        return rwptn;
    }

    public void setRwptn(int rwptn) {
        this.rwptn = rwptn;
    }
}
