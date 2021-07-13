package com.essence.business.xqh.dao.entity.rainfall;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "YWK_REPORT_DATA_FHFX_SK")
public class YwkReportDataFhfxSk {
    @Id
    @Column(name = "C_ID")
    private String cId;
    @Column(name = "C_REPORT_ID")
    private String cReportId;
    @Column(name = "C_STCD")
    private String cStcd;
    @Column(name = "C_STNM")
    private String cStnm;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "C_START_TIME")
    private Date cStartTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "C_END_TIME")
    private Date cEndTime;
    @Column(name = "C_AVG_HISTORY_Z")
    private Double cAvgHistoryZ;
    @Column(name = "C_WATER_HISTORY_QUANTITY")
    private Double cWaterHistoryQuanTity;
    @Column(name = "C_START_Z")
    private Double cStartZ;
    @Column(name = "C_START_QUANTITY")
    private Double cStartQuanTity;
    @Column(name = "C_END_Z")
    private Double cEndZ;
    @Column(name = "C_END_QUANTITY")
    private Double cEndQuanTity;
    @Column(name = "C_DIFF_QUANTITY")
    private Double cDiffQuantity;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "C_CREATE_TIME")
    private Date cCreateTime;


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

    public String getcStcd() {
        return cStcd;
    }

    public void setcStcd(String cStcd) {
        this.cStcd = cStcd;
    }

    public String getcStnm() {
        return cStnm;
    }

    public void setcStnm(String cStnm) {
        this.cStnm = cStnm;
    }

    public Date getcStartTime() {
        return cStartTime;
    }

    public void setcStartTime(Date cStartTime) {
        this.cStartTime = cStartTime;
    }

    public Date getcEndTime() {
        return cEndTime;
    }

    public void setcEndTime(Date cEndTime) {
        this.cEndTime = cEndTime;
    }

    public Double getcAvgHistoryZ() {
        return cAvgHistoryZ;
    }

    public void setcAvgHistoryZ(Double cAvgHistoryZ) {
        this.cAvgHistoryZ = cAvgHistoryZ;
    }

    public Double getcWaterHistoryQuanTity() {
        return cWaterHistoryQuanTity;
    }

    public void setcWaterHistoryQuanTity(Double cWaterHistoryQuanTity) {
        this.cWaterHistoryQuanTity = cWaterHistoryQuanTity;
    }

    public Double getcStartZ() {
        return cStartZ;
    }

    public void setcStartZ(Double cStartZ) {
        this.cStartZ = cStartZ;
    }

    public Double getcStartQuanTity() {
        return cStartQuanTity;
    }

    public void setcStartQuanTity(Double cStartQuanTity) {
        this.cStartQuanTity = cStartQuanTity;
    }

    public Double getcEndZ() {
        return cEndZ;
    }

    public void setcEndZ(Double cEndZ) {
        this.cEndZ = cEndZ;
    }

    public Double getcEndQuanTity() {
        return cEndQuanTity;
    }

    public void setcEndQuanTity(Double cEndQuanTity) {
        this.cEndQuanTity = cEndQuanTity;
    }

    public Double getcDiffQuantity() {
        return cDiffQuantity;
    }

    public void setcDiffQuantity(Double cDiffQuantity) {
        this.cDiffQuantity = cDiffQuantity;
    }

    public Date getcCreateTime() {
        return cCreateTime;
    }

    public void setcCreateTime(Date cCreateTime) {
        this.cCreateTime = cCreateTime;
    }
}
