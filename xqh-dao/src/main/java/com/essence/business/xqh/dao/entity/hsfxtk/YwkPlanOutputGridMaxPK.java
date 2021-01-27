package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;
@Embeddable
public class YwkPlanOutputGridMaxPK implements Serializable {
    @Column(name = "N_PLANID")
    private String nPlanid;

    @Column(name = "GRID_ID")
    private Long gridId;

    public String getnPlanid() {
        return nPlanid;
    }

    public void setnPlanid(String nPlanid) {
        this.nPlanid = nPlanid;
    }

    public Long getGridId() {
        return gridId;
    }

    public void setGridId(Long gridId) {
        this.gridId = gridId;
    }

    public YwkPlanOutputGridMaxPK() {
    }

    public YwkPlanOutputGridMaxPK(String nPlanid, Long gridId) {
        this.nPlanid = nPlanid;
        this.gridId = gridId;
    }
}
