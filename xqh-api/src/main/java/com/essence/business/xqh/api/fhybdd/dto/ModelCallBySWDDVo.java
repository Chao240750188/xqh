package com.essence.business.xqh.api.fhybdd.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ModelCallBySWDDVo {

    /**
     * 方案名称
     */
    private String cPlanname;
    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 步长
     */
    private int step;

    /**
     * 预见期
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date periodEndTime;

    /**
     * 模型id  1是SCS模型，2是DWX
     */
    private String modelId;

    /**
     * 河流id
     */
    private String rvcd;

    //集水区模型id
    private String catchmentAreaModelId;
    //河段模型id
    private String reachId;

    public String getCatchmentAreaModelId() {
        return catchmentAreaModelId;
    }

    public void setCatchmentAreaModelId(String catchmentAreaModelId) {
        this.catchmentAreaModelId = catchmentAreaModelId;
    }

    public String getReachId() {
        return reachId;
    }

    public void setReachId(String reachId) {
        this.reachId = reachId;
    }

    public String getcPlanname() {
        return cPlanname;
    }

    public void setcPlanname(String cPlanname) {
        this.cPlanname = cPlanname;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Date getPeriodEndTime() {
        return periodEndTime;
    }

    public void setPeriodEndTime(Date periodEndTime) {
        this.periodEndTime = periodEndTime;
    }

    public String getRvcd() {
        return rvcd;
    }

    public void setRvcd(String rvcd) {
        this.rvcd = rvcd;
    }
}
