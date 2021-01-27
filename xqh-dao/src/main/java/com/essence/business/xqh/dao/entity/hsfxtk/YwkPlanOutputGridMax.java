package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "YWK_PLAN_OUTPUT_GRID_MAX", schema = "XQH", catalog = "")
public class YwkPlanOutputGridMax {
    @EmbeddedId
    private YwkPlanOutputGridMaxPK idCLass;

    @Column(name = "GRID_SURFACE_ELEVATION")
    private Double gridSurfaceElevation;
    @Column(name = "MAX_Z")
    private Double maxZ;
    @Column(name = "MAX_WATER_DEPTH")
    private Double maxWaterDepth;
    @Column(name = "FLOODD_URATION")
    private Long flooddUration;
    @Column(name = "ARRIVE_TIME")
    private Long arriveTime;
    @Column(name = "MAX_VELOCITY")
    private Double maxVelocity;
    @Column(name = "ABSOLUTE_TIME")
    private Long absoluteTime;
    @Column(name = "RELATIVE_TIME")
    private Date relativeTime;


    public YwkPlanOutputGridMaxPK getIdCLass() {
        return idCLass;
    }

    public void setIdCLass(YwkPlanOutputGridMaxPK idCLass) {
        this.idCLass = idCLass;
    }

    public Double getGridSurfaceElevation() {
        return gridSurfaceElevation;
    }

    public void setGridSurfaceElevation(Double gridSurfaceElevation) {
        this.gridSurfaceElevation = gridSurfaceElevation;
    }

    public Double getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(Double maxZ) {
        this.maxZ = maxZ;
    }

    public Double getMaxWaterDepth() {
        return maxWaterDepth;
    }

    public void setMaxWaterDepth(Double maxWaterDepth) {
        this.maxWaterDepth = maxWaterDepth;
    }

    public Long getFlooddUration() {
        return flooddUration;
    }

    public void setFlooddUration(Long flooddUration) {
        this.flooddUration = flooddUration;
    }

    public Long getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(Long arriveTime) {
        this.arriveTime = arriveTime;
    }

    public Double getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity(Double maxVelocity) {
        this.maxVelocity = maxVelocity;
    }

    public Long getAbsoluteTime() {
        return absoluteTime;
    }

    public void setAbsoluteTime(Long absoluteTime) {
        this.absoluteTime = absoluteTime;
    }

    public Date getRelativeTime() {
        return relativeTime;
    }

    public void setRelativeTime(Date relativeTime) {
        this.relativeTime = relativeTime;
    }
}
