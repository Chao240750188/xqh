package com.essence.business.xqh.api.floodForecast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * LiuGt add at 2020-03-19
 * 修改方案计算小时蒸发量 dto
 * @author LiuGt
 *
 */
public class PlanEvaporationDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 方案id
     */
    private String planId;

    /**
     * 测站id
     */
    private String stcd;
    /**
     * 时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime time;
    /**
     * 蒸发量
     */
    private Double dre;

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public Double getDre() {
        return dre;
    }

    public void setDre(Double dre) {
        this.dre = dre;
    }
}
