package com.essence.business.xqh.api.hsfxtk.dto;

/**
 * 河流河道
 */
public class YwkRoughnessVo {

    private Double mileage; //里程（单位：m），保留2位小数

    private Double roughness;//糙率（无量纲，取值范围：0.015-0.04），保留3位小数

    private Integer isFix;  //是否固定值（0=否，1=是），默认值为1



    public Double getMileage() {
        return mileage;
    }

    public void setMileage(Double mileage) {
        this.mileage = mileage;
    }

    public Double getRoughness() {
        return roughness;
    }

    public void setRoughness(Double roughness) {
        this.roughness = roughness;
    }

    public Integer getIsFix() {
        return isFix;
    }

    public void setIsFix(Integer isFix) {
        this.isFix = isFix;
    }

}
