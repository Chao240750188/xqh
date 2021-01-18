package com.essence.business.xqh.api.fhybdd.dto;

import java.util.Date;

public class ModelCallBySWDDVo {

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTIme;

    /**
     * 步长
     */
    private int step;

    /**
     * 预见期
     */
    private Date periodEndTime;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTIme() {
        return endTIme;
    }

    public void setEndTIme(Date endTIme) {
        this.endTIme = endTIme;
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
}
