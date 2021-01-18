package com.essence.business.xqh.api.floodForecast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 时段的入库流量和降雨量
 */
public class HourTimeInqAndPDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime tm;

    private double inq; //入库流量

    private double p; //降雨量

    public LocalDateTime getTm() {
        return tm;
    }

    public void setTm(LocalDateTime tm) {
        this.tm = tm;
    }

    public double getInq() {
        return inq;
    }

    public void setInq(double inq) {
        this.inq = inq;
    }

    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
    }
}
