package com.essence.business.xqh.api.rainfall.dto.rainmonitoring;

import java.math.BigDecimal;

/**
 * @author fengpp
 * 2021/1/26 16:48
 */
public class FloodWarningDto {
    private String stcd;
    private String stnm;
    private BigDecimal lgtd;
    private BigDecimal lttd;
    private BigDecimal wrz;//警戒水位
    private BigDecimal grz;//保证水位
    private BigDecimal obhtz;//最高水位
    private BigDecimal upz;//闸上水位

    public FloodWarningDto() {
    }

    public FloodWarningDto(String stcd, String stnm, BigDecimal lgtd, BigDecimal lttd, BigDecimal wrz, BigDecimal grz, BigDecimal obhtz) {
        this.stcd = stcd;
        this.stnm = stnm;
        this.lgtd = lgtd;
        this.lttd = lttd;
        this.wrz = wrz;
        this.grz = grz;
        this.obhtz = obhtz;
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

    public BigDecimal getWrz() {
        return wrz;
    }

    public void setWrz(BigDecimal wrz) {
        this.wrz = wrz;
    }

    public BigDecimal getGrz() {
        return grz;
    }

    public void setGrz(BigDecimal grz) {
        this.grz = grz;
    }

    public BigDecimal getObhtz() {
        return obhtz;
    }

    public void setObhtz(BigDecimal obhtz) {
        this.obhtz = obhtz;
    }

    public BigDecimal getUpz() {
        return upz;
    }

    public void setUpz(BigDecimal upz) {
        this.upz = upz;
    }
}
