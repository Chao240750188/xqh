package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "YWK_PLAN_OUTPUT_BREAKP_ROCESS")
@IdClass(YwkPlanOutputBreakpRocessPK.class)
public class YwkPlanOutputBreakpRocess {
    @Id
    @Column(name = "N_PLANID")
    private String nPlanid;
    @Id
    @Column(name = "BREAK_ID")
    private String breakId;
    @Id
    @Column(name = "ABSOLUTE_TIME")
    private Long absoluteTime;
    @Column(name = "RELATIVE_TIME")
    private Timestamp relativeTime;
    @Column(name = "Q")
    private Double q;

    public String getnPlanid() {
        return nPlanid;
    }

    public void setnPlanid(String nPlanid) {
        this.nPlanid = nPlanid;
    }

    public String getBreakId() {
        return breakId;
    }

    public void setBreakId(String breakId) {
        this.breakId = breakId;
    }

    public Long getAbsoluteTime() {
        return absoluteTime;
    }

    public void setAbsoluteTime(Long absoluteTime) {
        this.absoluteTime = absoluteTime;
    }

    public Timestamp getRelativeTime() {
        return relativeTime;
    }

    public void setRelativeTime(Timestamp relativeTime) {
        this.relativeTime = relativeTime;
    }

    public Double getQ() {
        return q;
    }

    public void setQ(Double q) {
        this.q = q;
    }
}
