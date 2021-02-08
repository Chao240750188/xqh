package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "YWK_MODEL_BOUNDARY_BASIC_RL", schema = "XQH", catalog = "")
public class YwkModelBoundaryBasicRl {
    @Id
    @Column(name = "RLID")
    private String rlid;
    @Column(name = "IDMODEL_ID")
    private String idmodelId;
    @Column(name = "STCD")
    private String stcd;

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

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }
}
