package com.essence.business.xqh.dao.dao.rainfall.dto;

import java.util.Date;

public class THdmisTotalRainfallDto {
    //降雨级别
    private Integer level;
    //经度
    private Double lgtd;
    //维度
    private Double lttd;
    //降雨量
    private Double drp;
    //测站编码
    private String stcd;
    //测站名称
    private String stnm;
    // 时间
    private Date tm;

    private String tempTm;

    public String getTempTm() {
        return tempTm;
    }

    public void setTempTm(String tempTm) {
        this.tempTm = tempTm;
    }

    public Date getTm() {
        return tm;
    }

    public void setTm(Date tm) {
        this.tm = tm;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Double getLgtd() {
        return lgtd;
    }

    public void setLgtd(Double lgtd) {
        this.lgtd = lgtd;
    }

    public Double getLttd() {
        return lttd;
    }

    public void setLttd(Double lttd) {
        this.lttd = lttd;
    }

    public Double getDrp() {
        return drp;
    }

    public void setDrp(Double drp) {
        this.drp = drp;
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

    public THdmisTotalRainfallDto(Double drp, String stcd, Date tm) {
        this.drp = drp;
        this.stcd = stcd;
        this.tm = tm;
    }
    public THdmisTotalRainfallDto(){

    }
}
