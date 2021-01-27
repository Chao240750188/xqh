package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;
@Embeddable
public class YwkPlanOutputGridProcessPK implements Serializable {
    @Column(name = "N_PLANID")
    private String nPlanid;
    @Column(name = "GRID_ID")
    private Long gridId;
    @Column(name = "RELATIVE_TIME")
    private Long relativeTime;


    public String getnPlanid() {
        return nPlanid;
    }

    public void setnPlanid(String nPlanid) {
        this.nPlanid = nPlanid;
    }

    public Long getGridId() {
        return gridId;
    }

    public void setGridId(long gridId) {
        this.gridId = gridId;
    }

    public void setGridId(Long gridId) {
        this.gridId = gridId;
    }

    public Long getRelativeTime() {
        return relativeTime;
    }

    public void setRelativeTime(Long relativeTime) {
        this.relativeTime = relativeTime;
    }
}
