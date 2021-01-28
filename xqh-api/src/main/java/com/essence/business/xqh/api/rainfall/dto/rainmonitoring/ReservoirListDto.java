package com.essence.business.xqh.api.rainfall.dto.rainmonitoring;

import java.math.BigDecimal;

/**
 * @author fengpp
 * 2021/1/27 19:27
 */
public class ReservoirListDto {
    private String stnm;
    private String rz;//库水位
    private String inq;//入库流量
    private String otq;//出库流量
    private String w;//蓄水量
    private String floodWaterLevel;//距汛限水位
    private String floodW;//距汛限蓄水量
    private String normalWaterLevel;//距正常水位
    private String normalW;//距正常蓄水量
    private String ddz;//死水位
    private String fsltdz;//汛限水位
    private String normz;//正常高水位
    private Integer rwptn;//水势  落 4 涨 5 平 6
    private String hnnm;//水系
    private String stcd;

    public String getStnm() {
        return stnm;
    }

    public void setStnm(String stnm) {
        this.stnm = stnm;
    }

    public String getRz() {
        return rz;
    }

    public void setRz(String rz) {
        this.rz = rz;
    }

    public String getInq() {
        return inq;
    }

    public void setInq(String inq) {
        this.inq = inq;
    }

    public String getOtq() {
        return otq;
    }

    public void setOtq(String otq) {
        this.otq = otq;
    }

    public String getW() {
        return w;
    }

    public void setW(String w) {
        this.w = w;
    }

    public String getFloodWaterLevel() {
        return floodWaterLevel;
    }

    public void setFloodWaterLevel(String floodWaterLevel) {
        this.floodWaterLevel = floodWaterLevel;
    }

    public String getFloodW() {
        return floodW;
    }

    public void setFloodW(String floodW) {
        this.floodW = floodW;
    }

    public String getNormalWaterLevel() {
        return normalWaterLevel;
    }

    public void setNormalWaterLevel(String normalWaterLevel) {
        this.normalWaterLevel = normalWaterLevel;
    }

    public String getNormalW() {
        return normalW;
    }

    public void setNormalW(String normalW) {
        this.normalW = normalW;
    }

    public String getDdz() {
        return ddz;
    }

    public void setDdz(String ddz) {
        this.ddz = ddz;
    }

    public String getFsltdz() {
        return fsltdz;
    }

    public void setFsltdz(String fsltdz) {
        this.fsltdz = fsltdz;
    }

    public String getNormz() {
        return normz;
    }

    public void setNormz(String normz) {
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
