package com.essence.business.xqh.api.rainfall.dto.rainmonitoring;

import java.math.BigDecimal;

/**
 * @author fengpp
 * 2021/1/27 19:27
 */
public class ReservoirListDto {
    private String stnm;
    private BigDecimal rz;//库水位
    private BigDecimal inq;//入库流量
    private BigDecimal otq;//出库流量
    private BigDecimal w;//蓄水量
    private BigDecimal floodWaterLevel;//距汛限水位
    private BigDecimal floodW;//距汛限蓄水量
    private BigDecimal normalWaterLevel;//距正常水位
    private BigDecimal normalW;//距正常蓄水量
    private BigDecimal ddz;//死水位
    private BigDecimal fsltdz;//汛限水位
    private BigDecimal normz;//正常高水位
    private Integer rwptn;//水势
    private String hnnm;//水系
    private String stcd;

    public String getStnm() {
        return stnm;
    }

    public void setStnm(String stnm) {
        this.stnm = stnm;
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

    public BigDecimal getOtq() {
        return otq;
    }

    public void setOtq(BigDecimal otq) {
        this.otq = otq;
    }

    public BigDecimal getW() {
        return w;
    }

    public void setW(BigDecimal w) {
        this.w = w;
    }

    public BigDecimal getFloodWaterLevel() {
        return floodWaterLevel;
    }

    public void setFloodWaterLevel(BigDecimal floodWaterLevel) {
        this.floodWaterLevel = floodWaterLevel;
    }

    public BigDecimal getFloodW() {
        return floodW;
    }

    public void setFloodW(BigDecimal floodW) {
        this.floodW = floodW;
    }

    public BigDecimal getNormalWaterLevel() {
        return normalWaterLevel;
    }

    public void setNormalWaterLevel(BigDecimal normalWaterLevel) {
        this.normalWaterLevel = normalWaterLevel;
    }

    public BigDecimal getNormalW() {
        return normalW;
    }

    public void setNormalW(BigDecimal normalW) {
        this.normalW = normalW;
    }

    public BigDecimal getDdz() {
        return ddz;
    }

    public void setDdz(BigDecimal ddz) {
        this.ddz = ddz;
    }

    public BigDecimal getFsltdz() {
        return fsltdz;
    }

    public void setFsltdz(BigDecimal fsltdz) {
        this.fsltdz = fsltdz;
    }

    public BigDecimal getNormz() {
        return normz;
    }

    public void setNormz(BigDecimal normz) {
        this.normz = normz;
    }

    public Integer getRwptn() {
        return rwptn;
    }

    public void setRwptn(Integer rwptn) {
        this.rwptn = rwptn;
    }

    public String getHnnm() {
        return hnnm;
    }

    public void setHnnm(String hnnm) {
        this.hnnm = hnnm;
    }

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }
}
