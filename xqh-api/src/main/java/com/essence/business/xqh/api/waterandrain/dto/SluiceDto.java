package com.essence.business.xqh.api.waterandrain.dto;

import java.math.BigDecimal;

/**
 * @author fengpp
 * 2021/1/24 17:13
 */
public class SluiceDto {
    private String stcd;
    private String stnm;
    private String rvnm;
    private BigDecimal lgtd;
    private BigDecimal lttd;
    private BigDecimal upz;//闸上水位
    private BigDecimal dwz;//闸下水位
    private BigDecimal tgtq;//过闸流量
    private BigDecimal wrz;//警戒水位
    private String color;//展示颜色

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

    public String getRvnm() {
        return rvnm;
    }

    public void setRvnm(String rvnm) {
        this.rvnm = rvnm;
    }

    public BigDecimal getLgtd() {
        return lgtd;
    }

    public void setLgtd(BigDecimal lgtd) {
        this.lgtd = lgtd;
    }

    public BigDecimal getLttd() {
        return lttd;
    }

    public void setLttd(BigDecimal lttd) {
        this.lttd = lttd;
    }

    public BigDecimal getUpz() {
        return upz;
    }

    public void setUpz(BigDecimal upz) {
        this.upz = upz;
    }

    public BigDecimal getDwz() {
        return dwz;
    }

    public void setDwz(BigDecimal dwz) {
        this.dwz = dwz;
    }

    public BigDecimal getTgtq() {
        return tgtq;
    }

    public void setTgtq(BigDecimal tgtq) {
        this.tgtq = tgtq;
    }

    public BigDecimal getWrz() {
        return wrz;
    }

    public void setWrz(BigDecimal wrz) {
        this.wrz = wrz;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
