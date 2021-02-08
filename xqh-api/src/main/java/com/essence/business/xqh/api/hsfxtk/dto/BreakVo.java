package com.essence.business.xqh.api.hsfxtk.dto;

public class BreakVo {

    private String nPlanid;
    private String breakId;
    private Double breakBottomElevation;
    private Double breakWidth;
    private Double startZ;

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
