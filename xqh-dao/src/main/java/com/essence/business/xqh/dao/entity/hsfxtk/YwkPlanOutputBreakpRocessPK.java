package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class YwkPlanOutputBreakpRocessPK implements Serializable {
    private String nPlanid;
    private String breakId;
    private Long absoluteTime;

    @Column(name = "N_PLANID")
    @Id
    public String getnPlanid() {
        return nPlanid;
    }

    public void setnPlanid(String nPlanid) {
        this.nPlanid = nPlanid;
    }

    @Column(name = "BREAK_ID")
    @Id
    public String getBreakId() {
        return breakId;
    }

    public void setBreakId(String breakId) {
        this.breakId = breakId;
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
