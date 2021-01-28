package com.essence.business.xqh.api.hsfxtk.dto;

//河道操率dto
public class YwkRiverRoughnessParamDto {
    private String id;
    private String roughnessParamid;
    private Double mileage;
    private Double roughness;
    private Integer isFix;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoughnessParamid() {
        return roughnessParamid;
    }

    public void setRoughnessParamid(String roughnessParamid) {
        this.roughnessParamid = roughnessParamid;
    }

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
