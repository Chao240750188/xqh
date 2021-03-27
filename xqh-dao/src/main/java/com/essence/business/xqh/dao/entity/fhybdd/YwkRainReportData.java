package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "YWK_RAIN_REPORT_DATA", schema = "XQH", catalog = "")
public class YwkRainReportData {
    //简报或公报雨情数据
    @Id
    @Column(name = "C_ID")
    private String id;

    //报告id
    @Column(name = "C_REPORT_ID")
    private String reportId;

    //分区名称
    @Column(name = "C_PART_NAME")
    private String partName;

    //雨量站编码
    @Column(name = "C_STCD")
    private String stcd;

    //雨量站名称
    @Column(name = "C_STNM")
    private String stnm;

    //雨量站名称
    @Column(name = "N_DRP")
    private Double drp;

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

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public String getStnm() {
        return stnm;
    }

    public void setStnm(String stnm) {
        this.stnm = stnm;
    }

    public Double getDrp() {
        return drp;
    }

    public void setDrp(Double drp) {
        this.drp = drp;
    }
}
