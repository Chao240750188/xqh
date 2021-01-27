package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "YWK_PLAN_OUTPUT_GRID_PROCESS", schema = "XQH", catalog = "")
@IdClass(YwkPlanOutputGridProcessPK.class)
public class YwkPlanOutputGridProcess {
    @Id
    @Column(name = "N_PLANID")
    private String nPlanid;
    @Id
    @Column(name = "GRID_ID")
    private Long gridId;
    @Column(name = "GRID_DEPTH")
    private Double gridDepth;
    @Id
    @Column(name = "ABSOLUTE_TIME")
    private Timestamp absoluteTime;
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

    public void setGridId(Long gridId) {
        this.gridId = gridId;
    }

    public Double getGridDepth() {
        return gridDepth;
    }

    public void setGridDepth(Double gridDepth) {
        this.gridDepth = gridDepth;
    }

    public Timestamp getAbsoluteTime() {
        return absoluteTime;
    }

    public void setAbsoluteTime(Timestamp absoluteTime) {
        this.absoluteTime = absoluteTime;
    }

    public Long getRelativeTime() {
        return relativeTime;
    }

    public void setRelativeTime(Long relativeTime) {
        this.relativeTime = relativeTime;
    }
}
