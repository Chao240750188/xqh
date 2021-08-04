package com.essence.business.xqh.api.fhybdd.dto;

import java.util.List;

public class ModelPlanInfoManageDto {

    private String planId;

    private List<String> rvcrcrsccds;

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public List<String> getRvcrcrsccds() {
        return rvcrcrsccds;
    }

    public void setRvcrcrsccds(List<String> rvcrcrsccds) {
        this.rvcrcrsccds = rvcrcrsccds;
    }
}
