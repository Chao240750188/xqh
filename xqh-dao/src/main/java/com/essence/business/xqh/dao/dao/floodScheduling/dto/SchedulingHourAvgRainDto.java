package com.essence.business.xqh.dao.dao.floodScheduling.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 水库调度过程各时段总降雨和平均降雨量数据实体类
 * LiuGt add at 2020-07-14
 */
public class SchedulingHourAvgRainDto {

    /**
     * 时间（格式）：2020-06-29 18:00:00
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    Date time;

    /**
     * 时段总降雨量值
     */
    BigDecimal rainfall;

    /**
     * 时段测站数量
     */
    Integer stcdCount;

    /**
     * 时段平均降雨量值
     */
    BigDecimal avgRainfall;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public BigDecimal getRainfall() {
        return rainfall;
    }

    public void setRainfall(BigDecimal rainfall) {
        this.rainfall = rainfall;
    }

    public Integer getStcdCount() {
        return stcdCount;
    }

    public void setStcdCount(Integer stcdCount) {
        this.stcdCount = stcdCount;
    }

    public BigDecimal getAvgRainfall() {
        return avgRainfall;
    }

    public void setAvgRainfall(BigDecimal avgRainfall) {
        this.avgRainfall = avgRainfall;
    }
}
