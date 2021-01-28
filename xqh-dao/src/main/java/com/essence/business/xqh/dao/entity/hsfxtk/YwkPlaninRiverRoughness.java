package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "YWK_PLANIN_RIVER_ROUGHNESS", schema = "XQH", catalog = "")
public class YwkPlaninRiverRoughness {

    @Id
    @Column(name = "ID")
    private String id;
    @Column(name = "ROUGHNESS_PARAMID")
    private String roughnessParamid;
    @Column(name = "MILEAGE")
    private Double mileage;
    @Column(name = "ROUGHNESS")
    private Double roughness;
    @Column(name = "IS_FIX")
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
