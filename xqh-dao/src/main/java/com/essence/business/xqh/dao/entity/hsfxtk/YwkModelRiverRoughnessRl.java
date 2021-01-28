package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "YWK_MODEL_RIVER_ROUGHNESS_RL", schema = "XQH", catalog = "")
public class YwkModelRiverRoughnessRl {
    @Id
    @Column(name = "RLID")
    private String rlid;
    @Column(name = "IDMODEL_ID")
    private String idmodelId;
    @Column(name = "RIVER_ROUGHNESSID")
    private String riverRoughnessid;

    public String getRlid() {
        return rlid;
    }

    public void setRlid(String rlid) {
        this.rlid = rlid;
    }

    public String getIdmodelId() {
        return idmodelId;
    }

    public void setIdmodelId(String idmodelId) {
        this.idmodelId = idmodelId;
    }

    public String getRiverRoughnessid() {
        return riverRoughnessid;
    }

    public void setRiverRoughnessid(String riverRoughnessid) {
        this.riverRoughnessid = riverRoughnessid;
    }
}
