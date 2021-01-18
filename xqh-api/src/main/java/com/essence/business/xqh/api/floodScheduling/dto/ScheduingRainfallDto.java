package com.essence.business.xqh.api.floodScheduling.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 调度过程中的降雨数据实体类
 * LiuGt add at 2020-07-10
 */
public class ScheduingRainfallDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tm;

    private double drp;

    public LocalDateTime getTm() {
        return tm;
    }

    public void setTm(LocalDateTime tm) {
        this.tm = tm;
    }

    public double getDrp() {
        return drp;
    }

    public void setDrp(double drp) {
        this.drp = drp;
    }
}
