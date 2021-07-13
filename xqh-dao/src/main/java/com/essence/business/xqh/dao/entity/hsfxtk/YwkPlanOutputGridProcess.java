package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "YWK_PLAN_OUTPUT_GRID_PROCESS")
public class YwkPlanOutputGridProcess {
    @EmbeddedId
    YwkPlanOutputGridProcessPK pk;

    @Column(name = "GRID_DEPTH")
    private Double gridDepth;

    @Column(name = "ABSOLUTE_TIME")
    private Timestamp absoluteTime;

    public YwkPlanOutputGridProcess() {
        this.pk = new YwkPlanOutputGridProcessPK();
    }

    public YwkPlanOutputGridProcessPK getPk() {
        return pk;
    }

    public void setPk(YwkPlanOutputGridProcessPK pk) {
        this.pk = pk;
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
}
