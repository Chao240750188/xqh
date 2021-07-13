package com.essence.business.xqh.dao.entity.fhybdd;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "YWK_WATER_REPORT_DATA")
public class YwkWaterReportData {
    //简报或公报雨情数据
    @Id
    @Column(name = "C_ID")
    private String id;

    //报告id
    @Column(name = "C_REPORT_ID")
    private String reportId;

    //站编码
    @Column(name = "C_RSCD")
    private String rscd;

    //站名称
    @Column(name = "C_RSNM")
    private String rsnm;

    //最低水位时间
    @Column(name = "D_MINTIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date minTime;

    //最高水位时间
    @Column(name = "D_MAXTIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date maxTime;

    //最低水位
    @Column(name = "N_MINZ")
    private Double minz;

    //最高水位
    @Column(name = "N_MAXZ")
    private Double maxz;

    //最高水位
    @Column(name = "N_WRZ")
    private Double wrz;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getRscd() {
        return rscd;
    }

    public void setRscd(String rscd) {
        this.rscd = rscd;
    }

    public String getRsnm() {
        return rsnm;
    }

    public void setRsnm(String rsnm) {
        this.rsnm = rsnm;
    }

    public Date getMinTime() {
        return minTime;
    }

    public void setMinTime(Date minTime) {
        this.minTime = minTime;
    }

    public Date getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(Date maxTime) {
        this.maxTime = maxTime;
    }

    public Double getMinz() {
        return minz;
    }

    public void setMinz(Double minz) {
        this.minz = minz;
    }

    public Double getMaxz() {
        return maxz;
    }

    public void setMaxz(Double maxz) {
        this.maxz = maxz;
    }

    public Double getWrz() {
        return wrz;
    }

    public void setWrz(Double wrz) {
        this.wrz = wrz;
    }
}
