package com.essence.business.xqh.api.floodForecast.dto;

/**
 * LiuGt add at 2020-03-18
 * 方案结果展示实体类
 */
public class PlanResultListDto {

    /**
     * 日期及时段（yyyy-MM-dd HH:mm:ss）
     */
    private String tm;

    /**
     * 实测流量
     */
    private Double realQ;

    /**
     * 预测流量
     */
    private Double estimateQ;

    /**
     * 误差
     */
    private Double errorRate;

    public String getTm() {
        return tm;
    }

    public void setTm(String tm) {
        this.tm = tm;
    }

    public Double getRealQ() {
        return realQ;
    }

    public void setRealQ(Double realQ) {
        this.realQ = realQ;
    }

    public Double getEstimateQ() {
        return estimateQ;
    }

    public void setEstimateQ(Double estimateQ) {
        this.estimateQ = estimateQ;
    }

    public Double getErrorRate() {
        return errorRate;
    }

    public void setErrorRate(Double errorRate) {
        this.errorRate = errorRate;
    }
}
