package com.essence.business.xqh.dao.dao.floodForecast.dto;

/**
 * 慧图水库水情展示实体类
 * LiuGt add at 2020-07-09
 */
public class SqybHtStRsvrRViewDto {

    private String stcd; //测站编码

    private String tm; //时间

    private double rz; //水位

    private double w; //库容量

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public String getTm() {
        return tm;
    }

    public void setTm(String tm) {
        this.tm = tm;
    }

    public double getRz() {
        return rz;
    }

    public void setRz(double rz) {
        this.rz = rz;
    }

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    public SqybHtStRsvrRViewDto() {
    }

    public SqybHtStRsvrRViewDto(String stcd, String tm, double rz, double w) {
        this.stcd = stcd;
        this.tm = tm;
        this.rz = rz;
        this.w = w;
    }
}
