package com.essence.business.xqh.api.floodForecast.dto;


import java.util.List;

/**
 * 方案对比结果展示实体类
 * LiuGt add at 2020-07-05
 */
public class PlanContrastResultViewDto {

    /**
     * 方案信息
     */
    private SqybModelPlanInfoDto modelPlanInfo;

    /**
     * 模型计算方案的结果（各时段的入库流量和降雨量）
     */
    List<HourTimeInqAndPDto> modelOutPutRainfallList;

    public SqybModelPlanInfoDto getModelPlanInfo() {
        return modelPlanInfo;
    }

    public void setModelPlanInfo(SqybModelPlanInfoDto modelPlanInfo) {
        this.modelPlanInfo = modelPlanInfo;
    }

    public List<HourTimeInqAndPDto> getModelOutPutRainfallList() {
        return modelOutPutRainfallList;
    }

    public void setModelOutPutRainfallList(List<HourTimeInqAndPDto> modelOutPutRainfallList) {
        this.modelOutPutRainfallList = modelOutPutRainfallList;
    }
}
