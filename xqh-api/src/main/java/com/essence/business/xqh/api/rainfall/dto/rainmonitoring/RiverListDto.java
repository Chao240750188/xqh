package com.essence.business.xqh.api.rainfall.dto.rainmonitoring;

import java.math.BigDecimal;

/**
 * @author fengpp
 * 2021/1/27 16:12
 */
public class RiverListDto {
    private String stcd;
    private String stnm;
    private BigDecimal waterLevel;//水位
    private BigDecimal waterLevelChange;//水位变幅
    private BigDecimal flow;//流量
    private BigDecimal flowChange;//流量变幅

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public String getStnm() {
        return stnm;
    }

    public void setStnm(String stnm) {
        this.stnm = stnm;
    }

    public BigDecimal getWaterLevel() {
        return waterLevel;
    }

    public void setWaterLevel(BigDecimal waterLevel) {
        this.waterLevel = waterLevel;
    }

    public BigDecimal getWaterLevelChange() {
        return waterLevelChange;
    }

    public void setWaterLevelChange(BigDecimal waterLevelChange) {
        this.waterLevelChange = waterLevelChange;
    }

    public BigDecimal getFlow() {
        return flow;
    }

    public void setFlow(BigDecimal flow) {
        this.flow = flow;
    }

    public BigDecimal getFlowChange() {
        return flowChange;
    }

    public void setFlowChange(BigDecimal flowChange) {
        this.flowChange = flowChange;
    }
}
