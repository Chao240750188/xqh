package com.essence.business.xqh.api.floodForecast.dto;
import java.util.List;

/**
 * LiuGt add at 2020-03-18
 * 方案结果展示实体类
 */
public class PlanResultViewDto {

    /**
     * 方案信息
     */
    private SqybModelPlanInfoDto modelPlanInfo;

    /**
     * 平均误差
     */
    private Double avgErrors;

    /**
     * 各时段入库流量
     */
    private List<PlanResultListDto> planResultListDtoList;

    public SqybModelPlanInfoDto getModelPlanInfo() {
        return modelPlanInfo;
    }

    public void setModelPlanInfo(SqybModelPlanInfoDto modelPlanInfo) {
        this.modelPlanInfo = modelPlanInfo;
    }

    public Double getAvgErrors() {
        return avgErrors;
    }

    public void setAvgErrors(Double avgErrors) {
        this.avgErrors = avgErrors;
    }

    public List<PlanResultListDto> getPlanResultListDtoList() {
        return planResultListDtoList;
    }

    public void setPlanResultListDtoList(List<PlanResultListDto> planResultListDtoList) {
        this.planResultListDtoList = planResultListDtoList;
    }
}
