package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "YWK_PLANIN_FLOOD_BREAK", schema = "XQH", catalog = "")
public class YwkPlaninFloodBreakEntity {
    @Id
    @Column(name = "ID")
    private String id;
    @Column(name = "BREAK_ID")
    private String breakId;
    @Column(name = "BREAK_BOTTOM_ELEVATION")
    private Double breakBottomElevation;
    @Column(name = "BREAK_WIDTH")
    private Double breakWidth;
    @Column(name = "START_Z")
    private Double startZ;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBreakId() {
        return breakId;
    }

    public void setBreakId(String breakId) {
        this.breakId = breakId;
    }

    public Double getBreakBottomElevation() {
        return breakBottomElevation;
    }

    public void setBreakBottomElevation(Double breakBottomElevation) {
        this.breakBottomElevation = breakBottomElevation;
    }

    public Double getBreakWidth() {
        return breakWidth;
    }

    public void setBreakWidth(Double breakWidth) {
        this.breakWidth = breakWidth;
    }

    public Double getStartZ() {
        return startZ;
    }

    public void setStartZ(Double startZ) {
        this.startZ = startZ;
    }
}
