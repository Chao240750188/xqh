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
    @Column(name = "PLAN_ROUGHNESSID")
    private String planRoughnessId;
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

    public String getPlanRoughnessId() {
        return planRoughnessId;
    }

    public void setPlanRoughnessId(String planRoughnessId) {
        this.planRoughnessId = planRoughnessId;
    }
}
