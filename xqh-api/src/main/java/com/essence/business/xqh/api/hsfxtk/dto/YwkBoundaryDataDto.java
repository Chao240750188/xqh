package com.essence.business.xqh.api.hsfxtk.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class YwkBoundaryDataDto implements Serializable {
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;

    private Double boundaryData;

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Double getBoundaryData() {
        return boundaryData;
    }

    public void setBoundaryData(Double boundaryData) {
        this.boundaryData = boundaryData;
    }
}
