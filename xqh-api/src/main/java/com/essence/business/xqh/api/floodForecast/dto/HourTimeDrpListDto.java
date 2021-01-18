package com.essence.business.xqh.api.floodForecast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 时段降雨（包括平均降雨和累积降雨）列表实体类
 * LiuGt add at 2020-07-09
 */
public class HourTimeDrpListDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime tm;

    private double totalP; //时段累积降雨

    private double avgP; //时段平均降雨量

    public LocalDateTime getTm() {
        return tm;
    }

    public void setTm(LocalDateTime tm) {
        this.tm = tm;
    }

    public double getTotalP() {
        return totalP;
    }

    public void setTotalP(double totalP) {
        this.totalP = totalP;
    }

    public double getAvgP() {
        return avgP;
    }

    public void setAvgP(double avgP) {
        this.avgP = avgP;
    }
}
