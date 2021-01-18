package com.essence.business.xqh.api.floodForecast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * LiuGt add at 2020-03-16
 * 每小时蒸发量的实体类
 */
public class EvaporationTimeDto implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime time;
    /**
     * 蒸发量
     */
    private Double evp;

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Double getEvp() {
        return evp;
    }

    public void setEvp(Double evp) {
        this.evp = evp;
    }
}
