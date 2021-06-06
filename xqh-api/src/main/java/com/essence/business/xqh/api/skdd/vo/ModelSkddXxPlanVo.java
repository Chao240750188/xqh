package com.essence.business.xqh.api.skdd.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class ModelSkddXxPlanVo {

    /**
     * 方案名称
     */
    private String planName;

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
     * 集水区模型
     */
    private String catchmentAreaModelId;

    /**
     * 河段模型
     */
    private String reachId;

    /**
     * 步长
     */
    private int step;

    /**
     * 0是分钟，1是小时
     */
    private int timeType = 0;

    /**
     * 河流id
     */
    private String rvcd;

    /**
     * 模型调度水库对应水文站的编码
     */
    private String rscd;

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
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

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getTimeType() {
        return timeType;
    }

    public void setTimeType(int timeType) {
        this.timeType = timeType;
    }

    public String getRvcd() {
        return rvcd;
    }

    public void setRvcd(String rvcd) {
        this.rvcd = rvcd;
    }

    public String getRscd() {
        return rscd;
    }

    public void setRscd(String rscd) {
        this.rscd = rscd;
    }

}
