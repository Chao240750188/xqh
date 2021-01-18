package com.essence.business.xqh.dao.dao.rainfall.dto;

import java.io.Serializable;

public class FxEventTypeDetailInfoDto implements Serializable {
    private String stcd;//测站编码
    private String stnm;//站点名称
    private Double lgtd;//经度
    private Double lttd;//纬度
    private String stlc;//站址

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

    public String getStlc() {
        return stlc;
    }

    public void setStlc(String stlc) {
        this.stlc = stlc;
    }

    public FxEventTypeDetailInfoDto() {
    }

    public FxEventTypeDetailInfoDto(String stcd, String stnm, Double lgtd, Double lttd, String stlc) {
        this.stcd = stcd;
        this.stnm = stnm;
        this.lgtd = lgtd;
        this.lttd = lttd;
        this.stlc = stlc;
    }
}
