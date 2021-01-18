package com.essence.business.xqh.api.floodScheduling.dto;

/**
 * 调度方案管理分页请求参数实体
 * LiuGt add at 2020-03-30
 */
public class SchedulingPlanDocPageListParamDto {

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
}
