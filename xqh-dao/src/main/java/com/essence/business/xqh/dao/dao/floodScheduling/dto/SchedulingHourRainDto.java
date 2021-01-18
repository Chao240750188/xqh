package com.essence.business.xqh.dao.dao.floodScheduling.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 水库调度时时段降雨数据实体类
 * LiuGt add at 2020-06-29
 */
public class SchedulingHourRainDto {

    /**
     * 时间（格式）：2020-06-29 18:00:00
     */
    Date time;

    /**
     * 雨量值
     */
    BigDecimal rainfall;

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
}
