package com.essence.business.xqh.api.skdd.vo;

public class ModelSkddXxInputVo {

    /**
     * 方案ID
     */
    private String planId;

    /**
     * 初始起调水位
     */
    private Double numberZ;

    /**
     * 初始下泄流量
     */
    private Double numberQ;

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public Double getNumberZ() {
        return numberZ;
    }

    public void setNumberZ(Double numberZ) {
        this.numberZ = numberZ;
    }

    public Double getNumberQ() {
        return numberQ;
    }

    public void setNumberQ(Double numberQ) {
        this.numberQ = numberQ;
    }
}
