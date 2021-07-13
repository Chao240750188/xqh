package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "YWK_RIVER_ROUGHNESS_BASIC")
public class YwkRiverRoughnessBasic {
    @Id
    @Column(name = "RIVER_ROUGHNESSID")
    private String riverRoughnessid;
    @Column(name = "MILEAGE")
    private Double mileage;
    @Column(name = "ROUGHNESS")
    private Double roughness;
    @Column(name = "TYPE")
    private String type;


    public String getRiverRoughnessid() {
        return riverRoughnessid;
    }

    public void setRiverRoughnessid(String riverRoughnessid) {
        this.riverRoughnessid = riverRoughnessid;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


}
