package com.essence.business.xqh.api.rainfall.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class YTRainTimeDto {

    private String cRainName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dStartTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dEndTime;

    public String getcRainName() {
        return cRainName;
    }

    public void setcRainName(String cRainName) {
        this.cRainName = cRainName;
    }

    public Date getdStartTime() {
        return dStartTime;
    }

    public void setdStartTime(Date dStartTime) {
        this.dStartTime = dStartTime;
    }

    public Date getdEndTime() {
        return dEndTime;
    }

    public void setdEndTime(Date dEndTime) {
        this.dEndTime = dEndTime;
    }
}
