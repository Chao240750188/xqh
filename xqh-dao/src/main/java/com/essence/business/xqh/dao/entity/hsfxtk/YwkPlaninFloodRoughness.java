package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "YWK_PLANIN_FLOOD_ROUGHNESS", schema = "XQH", catalog = "")
public class YwkPlaninFloodRoughness {
    @Id
    @Column(name = "PLAN_ROUGHNESSID")
    private String planRoughnessid;
    @Column(name = "ROUGHNESS_PARAMID")
    private String roughnessParamid;
    @Column(name = "ROUGHNESS_PARAMNM")
    private String roughnessParamnm;
    @Column(name = "GRID_SYNTHESIZE_ROUGHNESS")
    private Double gridSynthesizeRoughness;


    public String getPlanRoughnessid() {
        return planRoughnessid;
    }

    public void setPlanRoughnessid(String planRoughnessid) {
        this.planRoughnessid = planRoughnessid;
    }

    public String getRoughnessParamid() {
        return roughnessParamid;
    }

    public void setRoughnessParamid(String roughnessParamid) {
        this.roughnessParamid = roughnessParamid;
    }

    public String getRoughnessParamnm() {
        return roughnessParamnm;
    }

    public void setRoughnessParamnm(String roughnessParamnm) {
        this.roughnessParamnm = roughnessParamnm;
    }

    public Double getGridSynthesizeRoughness() {
        return gridSynthesizeRoughness;
    }

    public void setGridSynthesizeRoughness(Double gridSynthesizeRoughness) {
        this.gridSynthesizeRoughness = gridSynthesizeRoughness;
    }
}
