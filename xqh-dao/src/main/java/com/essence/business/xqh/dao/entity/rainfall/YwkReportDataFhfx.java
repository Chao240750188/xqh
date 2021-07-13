package com.essence.business.xqh.dao.entity.rainfall;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "YWK_REPORT_DATA_FHFX")
public class YwkReportDataFhfx {
    @Id
    @Column(name = "C_ID")
    private String cId;
    @Column(name = "C_REPORT_ID")
    private String cReportId;
    @Column(name = "C_STCD")
    private String cStcd;
    @Column(name = "C_NAME")
    private String cName;
    @Column(name = "C_MIN_Z")
    private Double cMinZ;
    @Column(name = "C_MAX_Z")
    private Double cMaxZ;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "C_MAX_Z_TIME")
    private Date cMaxZTime;
    @Column(name = "C_WARN_Z")
    private Double cWarnZ;
    @Column(name = "C_AVG_Z")
    private Double cAvgZ;
    @Column(name = "C_AVG_HISTORY_Z")
    private Double cAvgHistoryZ;
    @Column(name = "C_MAX_Q")
    private Double cMaxQ;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "C_MAX_Q_TIME")
    private Date cMaxQTime;
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

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public Double getcMinZ() {
        return cMinZ;
    }

    public void setcMinZ(Double cMinZ) {
        this.cMinZ = cMinZ;
    }

    public Double getcMaxZ() {
        return cMaxZ;
    }

    public void setcMaxZ(Double cMaxZ) {
        this.cMaxZ = cMaxZ;
    }

    public Date getcMaxZTime() {
        return cMaxZTime;
    }

    public void setcMaxZTime(Date cMaxZTime) {
        this.cMaxZTime = cMaxZTime;
    }

    public Double getcWarnZ() {
        return cWarnZ;
    }

    public void setcWarnZ(Double cWarnZ) {
        this.cWarnZ = cWarnZ;
    }

    public Double getcAvgZ() {
        return cAvgZ;
    }

    public void setcAvgZ(Double cAvgZ) {
        this.cAvgZ = cAvgZ;
    }

    public Double getcAvgHistoryZ() {
        return cAvgHistoryZ;
    }

    public void setcAvgHistoryZ(Double cAvgHistoryZ) {
        this.cAvgHistoryZ = cAvgHistoryZ;
    }

    public Double getcMaxQ() {
        return cMaxQ;
    }

    public void setcMaxQ(Double cMaxQ) {
        this.cMaxQ = cMaxQ;
    }

    public Date getcMaxQTime() {
        return cMaxQTime;
    }

    public void setcMaxQTime(Date cMaxQTime) {
        this.cMaxQTime = cMaxQTime;
    }

    public Date getcCreateTime() {
        return cCreateTime;
    }

    public void setcCreateTime(Date cCreateTime) {
        this.cCreateTime = cCreateTime;
    }
}
