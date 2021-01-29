package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "YWK_BREAK_BASIC", schema = "XQH", catalog = "")
public class YwkBreakBasicEntity {
    @Id
    @Column(name = "BREAK_ID")
    private String breakId;
    @Column(name = "BREAK_NO")
    private Long breakNo;
    @Column(name = "BREAK_MILEAGE")
    private Double breakMileage;
    @Column(name = "BREAK_BOTTOM_ELEVATION")
    private Double breakBottomElevation;
    @Column(name = "BREAK_WIDTH")
    private Double breakWidth;
    @Column(name = "START_Z")
    private Double startZ;
    @Column(name = "LGTD")
    private Double lgtd;
    @Column(name = "LTTD")
    private Double lttd;

    public String getBreakId() {
        return breakId;
    }

    public void setBreakId(String breakId) {
        this.breakId = breakId;
    }

    public Long getBreakNo() {
        return breakNo;
    }

    public void setBreakNo(Long breakNo) {
        this.breakNo = breakNo;
    }

    public Double getBreakMileage() {
        return breakMileage;
    }

    public void setBreakMileage(Double breakMileage) {
        this.breakMileage = breakMileage;
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

    public Double getLgtd() {
        return lgtd;
    }

    public void setLgtd(Double lgtd) {
        this.lgtd = lgtd;
    }

    public Double getLttd() {
        return lttd;
    }

    public void setLttd(Double lttd) {
        this.lttd = lttd;
    }
}
