package com.essence.business.xqh.api.waterandrain.dto;

import java.math.BigDecimal;

/**
 * @author fengpp
 * 2021/1/25 18:47
 */
public class TideListDto {

    private String stcd;
    private String stnm;
    private BigDecimal lgtd;
    private BigDecimal lttd;
    private BigDecimal tdz;
    private BigDecimal airp;
    private String color;

    //超历史最高水位
    private Integer isThanWaterLevelHistory;
    //超警戒水位
    private Integer isThanWaterLevelWarning;
    //超保证水位
    private Integer isThanWaterLevelGuarantee;

    public TideListDto(String stcd, String stnm, BigDecimal lgtd, BigDecimal lttd, BigDecimal tdz, BigDecimal airp) {
    }

    public TideListDto(String stcd, String stnm, BigDecimal lgtd, BigDecimal lttd, BigDecimal tdz, BigDecimal airp, String color) {
        this.stcd = stcd;
        this.stnm = stnm;
        this.lgtd = lgtd;
        this.lttd = lttd;
        this.tdz = tdz;
        this.airp = airp;
        this.color = color;
        this.isThanWaterLevelHistory = 0;
        this.isThanWaterLevelWarning = 0;
        this.isThanWaterLevelGuarantee = 0;
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

    public BigDecimal getTdz() {
        return tdz;
    }

    public void setTdz(BigDecimal tdz) {
        this.tdz = tdz;
    }

    public BigDecimal getAirp() {
        return airp;
    }

    public void setAirp(BigDecimal airp) {
        this.airp = airp;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public Integer getIsThanWaterLevelHistory() {
        return isThanWaterLevelHistory;
    }

    public void setIsThanWaterLevelHistory(Integer isThanWaterLevelHistory) {
        this.isThanWaterLevelHistory = isThanWaterLevelHistory;
    }

    public Integer getIsThanWaterLevelWarning() {
        return isThanWaterLevelWarning;
    }

    public void setIsThanWaterLevelWarning(Integer isThanWaterLevelWarning) {
        this.isThanWaterLevelWarning = isThanWaterLevelWarning;
    }

    public Integer getIsThanWaterLevelGuarantee() {
        return isThanWaterLevelGuarantee;
    }

    public void setIsThanWaterLevelGuarantee(Integer isThanWaterLevelGuarantee) {
        this.isThanWaterLevelGuarantee = isThanWaterLevelGuarantee;
    }
}
