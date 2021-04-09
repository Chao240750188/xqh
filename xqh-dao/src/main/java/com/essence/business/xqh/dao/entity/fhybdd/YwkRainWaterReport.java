package com.essence.business.xqh.dao.entity.fhybdd;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "YWK_RAIN_WATER_REPORT", schema = "XQH", catalog = "")
public class YwkRainWaterReport {
    //简报或公报或防洪报表
    @Id
    @Column(name = "C_ID")
    private String id;

    //年份
    @Column(name = "N_YEAR")
    private Integer year;

    //第几期
    @Column(name = "N_SERIAL_NUMBER")
    private Integer serialNumber;

    //报告名称
    @Column(name = "C_REPORT_NAME")
    private String reportName;

    //创建时间
    @Column(name = "D_CREATE_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    //报告数据结束时间
    @Column(name = "D_REPORT_START_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reportStartTime;

    //报告数据结束时间
    @Column(name = "D_REPORT_END_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reportEndTime;

    //报告类型（0简报，1公报，2防洪形式分析报告）
    @Column(name = "C_REPORT_TYPE")
    private String reportType;

    //雨情描述
    @Column(name = "C_DESCRIBE_RAIN_INFO")
    private String describeRainInfo;

    //水情描述
    @Column(name = "C_DESCRIBE_WATER_INFO")
    private String describeWaterInfo;

    /**
     * 报告签发
     */
    @Column(name = "C_SIGN")
    private String sign;
    /**
     * 报告核定
     */
    @Column(name = "C_VERIFICATION")
    private String verification;
    /**
     * 报告核稿
     */
    @Column(name = "C_ENGAGEMENT")
    private String engagement;
    /**
     * 报告拟稿
     */
    @Column(name = "C_DARFT")
    private String darft;

    /**
     * 报告状态（0草稿，1历史报告）
     */
    @Column(name = "C_REPORT_STATUS")
    private String reportStatus;

    /**
     * 分区编码
     */
    @Column(name = "C_PART_ID")
    private String cPartId;

    public String getcPartId() {
        return cPartId;
    }

    public void setcPartId(String cPartId) {
        this.cPartId = cPartId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getReportStartTime() {
        return reportStartTime;
    }

    public void setReportStartTime(Date reportStartTime) {
        this.reportStartTime = reportStartTime;
    }

    public Date getReportEndTime() {
        return reportEndTime;
    }

    public void setReportEndTime(Date reportEndTime) {
        this.reportEndTime = reportEndTime;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getDescribeRainInfo() {
        return describeRainInfo;
    }

    public void setDescribeRainInfo(String describeRainInfo) {
        this.describeRainInfo = describeRainInfo;
    }

    public String getDescribeWaterInfo() {
        return describeWaterInfo;
    }

    public void setDescribeWaterInfo(String describeWaterInfo) {
        this.describeWaterInfo = describeWaterInfo;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getVerification() {
        return verification;
    }

    public void setVerification(String verification) {
        this.verification = verification;
    }

    public String getEngagement() {
        return engagement;
    }

    public void setEngagement(String engagement) {
        this.engagement = engagement;
    }

    public String getDarft() {
        return darft;
    }

    public void setDarft(String darft) {
        this.darft = darft;
    }

    public String getReportStatus() {
        return reportStatus;
    }

    public void setReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }
}
