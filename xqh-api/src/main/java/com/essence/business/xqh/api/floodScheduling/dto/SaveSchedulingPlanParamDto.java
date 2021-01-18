package com.essence.business.xqh.api.floodScheduling.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 保存水库调度信息实体类
 * LiuGt add at 2020-04-09
 */
public class SaveSchedulingPlanParamDto {

    /**
     * 水库ID
     */
    private String resCode;

    /**
     * 起调水位
     */
    private BigDecimal wl;

    /**
     * 计算开始时间
     */
    private String startTime;

    /**
     * 调度规则（1 默认规则 ，2 自定义规则）
     */
    private Integer outType;

    /**
     * 调度规则列表
     */
    private List<ResZvarlViewDto> resZvarlList;

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public BigDecimal getWl() {
        return wl;
    }

    public void setWl(BigDecimal wl) {
        this.wl = wl;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Integer getOutType() {
        return outType;
    }

    public void setOutType(Integer outType) {
        this.outType = outType;
    }

    public List<ResZvarlViewDto> getResZvarlList() {
        return resZvarlList;
    }

    public void setResZvarlList(List<ResZvarlViewDto> resZvarlList) {
        this.resZvarlList = resZvarlList;
    }
}
