package com.essence.business.xqh.dao.entity.rainfall;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "YWK_REPORT_DATA_FHFX_INFO", schema = "XQH", catalog = "")
public class YwkReportDataFhfxInfo {
    @Id
    @Column(name = "C_ID")
    private String cId;
    @Column(name = "C_REPORT_ID")
    private String cReportId;
    @Column(name = "C_ZQ_INFO")
    private String cZqInfo;
    @Column(name = "C_WATER_QUANTITY_INFO")
    private String cWaterQuantityInfo;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "C_CREATE_TIME")
    private Date cCreateTime;
    @Column(name = "C_TYPE")
    private String cType;


    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getcReportId() {
        return cReportId;
    }

    public void setcReportId(String cReportId) {
        this.cReportId = cReportId;
    }

    public String getcZqInfo() {
        return cZqInfo;
    }

    public void setcZqInfo(String cZqInfo) {
        this.cZqInfo = cZqInfo;
    }

    public String getcWaterQuantityInfo() {
        return cWaterQuantityInfo;
    }

    public void setcWaterQuantityInfo(String cWaterQuantityInfo) {
        this.cWaterQuantityInfo = cWaterQuantityInfo;
    }

    public Date getcCreateTime() {
        return cCreateTime;
    }

    public void setcCreateTime(Date cCreateTime) {
        this.cCreateTime = cCreateTime;
    }

    public String getcType() {
        return cType;
    }

    public void setcType(String cType) {
        this.cType = cType;
    }
}
