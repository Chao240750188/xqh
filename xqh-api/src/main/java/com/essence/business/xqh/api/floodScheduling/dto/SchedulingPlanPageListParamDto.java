package com.essence.business.xqh.api.floodScheduling.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 调度方案分页请求参数实体
 * LiuGt add at 2020-03-31
 */
public class SchedulingPlanPageListParamDto {

    /**
     * 当前页码
     */
    private int currentPage;

    /**
     * 每页条数
     */
    private int pageSize;

    /**
     * 水库ID
     */
    private String resCode;

    /**
     * 历史状态
     */
    private Integer historyStatus;

    /**
     * 计算开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private String startTime;

    /**
     * 计算结束时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private String endTime;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public Integer getHistoryStatus() {
        return historyStatus;
    }

    public void setHistoryStatus(Integer historyStatus) {
        this.historyStatus = historyStatus;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
