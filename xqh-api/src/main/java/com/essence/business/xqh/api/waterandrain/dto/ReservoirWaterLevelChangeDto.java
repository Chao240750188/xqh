package com.essence.business.xqh.api.waterandrain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fengpp
 * 2021/1/30 19:32
 */
public class ReservoirWaterLevelChangeDto {
    private String stcd;
    private String stnm;
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date tm;
    private String showTm;
    private BigDecimal rz;//库水位
    private BigDecimal inq;//入库流量
    private BigDecimal w;//蓄水量
    private BigDecimal newChange;//最新变化
    private int rwptn;//水势

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

    public String getShowTm() {
        return showTm;
    }

    public void setShowTm(String showTm) {
        this.showTm = showTm;
    }

    public BigDecimal getRz() {
        return rz;
    }

    public void setRz(BigDecimal rz) {
        this.rz = rz;
    }

    public BigDecimal getInq() {
        return inq;
    }

    public void setInq(BigDecimal inq) {
        this.inq = inq;
    }

    public BigDecimal getW() {
        return w;
    }

    public void setW(BigDecimal w) {
        this.w = w;
    }

    public BigDecimal getNewChange() {
        return newChange;
    }

    public void setNewChange(BigDecimal newChange) {
        this.newChange = newChange;
    }

    public int getRwptn() {
        return rwptn;
    }

    public void setRwptn(int rwptn) {
        this.rwptn = rwptn;
    }
}
