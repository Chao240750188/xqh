package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class YwkPlanOutputGridProcessPK implements Serializable {
    private String nPlanid;
    private Long gridId;
    private Long absoluteTime;

    @Column(name = "N_PLANID")
    @Id
    public String getnPlanid() {
        return nPlanid;
    }

    public void setnPlanid(String nPlanid) {
        this.nPlanid = nPlanid;
    }

    @Column(name = "GRID_ID")
    @Id
    public Long getGridId() {
        return gridId;
    }

    public void setGridId(long gridId) {
        this.gridId = gridId;
    }

    @Column(name = "ABSOLUTE_TIME")
    @Id
    public Long getAbsoluteTime() {
        return absoluteTime;
    }

    public void setAbsoluteTime(long absoluteTime) {
        this.absoluteTime = absoluteTime;
    }


}
