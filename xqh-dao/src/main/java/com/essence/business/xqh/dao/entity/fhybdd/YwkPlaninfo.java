package com.essence.business.xqh.dao.entity.fhybdd;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "YWK_PLANINFO")
public class YwkPlaninfo {
    @Id
    @Column(name = "N_PLANID")
    private String nPlanid;
    @Column(name = "C_PLANNAME")
    private String cPlanname;
    @Column(name = "C_MIKESCENARIOCODE")
    private String cMikescenariocode;
    @Column(name = "N_CREATEUSER")
    private String nCreateuser;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "N_PLANCURRENTTIME")
    private Date nPlancurrenttime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "D_CACULATESTARTTM")
    private Date dCaculatestarttm;

    @Column(name = "D_CACULATEENDTM")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dCaculateendtm;

    @Column(name = "N_TOTALRAINFALL")
    private Double nTotalrainfall;
    @Column(name = "N_OUTPUTTM")
    private Long nOutputtm;
    @Column(name = "N_PLANTYPE")
    private String nPlantype;
    @Column(name = "N_PLANSTATUS")
    private Long nPlanstatus;
    @Column(name = "N_PLANSTATUSINFO")
    private String nPlanstatusinfo;
    @Column(name = "C_COMMENT")
    private String cComment;
    @Column(name = "N_MODELID")
    private String nModelid;

    @Column(name = "N_SW_MODELID")
    private String nSWModelid;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "D_RAINSTARTTIME")
    private Date dRainstarttime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "D_RAINENDTIME")
    private Date dRainendtime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "D_OPENSOURCESTARTTIME")
    private Date dOpensourcestarttime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "D_OPENSOURCEENDTIME")
    private Date dOpensourceendtime;

    @Column(name = "C_DERIVEPLANTYPE")
    private String cDeriveplantype;
    @Column(name = "N_DERIVEPLANTORDER")
    private Double nDeriveplantorder;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "N_CREATETIME")
    private Date nCreatetime;

    //方案所属系统标识
    @Column(name = "C_PLAN_SYSTEM")
    private String planSystem;

    @Column(name = "C_RIVERID")
   private  String riverId;

    @Column(name = "N_CALIBRATION_STATUS")
    private Long nCalibrationStatus;

    @Column(name = "N_PUBLISH")
    private Long nPublish;

    @Column(name = "N_PUBLISH_TIME")
    public  Date nPublishTime;

    @Column(name = "C_RSCD")
    private String rscd;

    @Transient
    private String rname; //水库名称

    @Transient
    private Long leadTime;//预见期

    @Transient
    private Integer isWarnIng;//告警

    @Transient
    private Double roughness;//糙率

    public Integer getIsWarnIng() {
        return isWarnIng;
    }

    public void setIsWarnIng(Integer isWarnIng) {
        this.isWarnIng = isWarnIng;
    }

    public Date getnPublishTime() {
        return nPublishTime;
    }

    public void setnPublishTime(Date nPublishTime) {
        this.nPublishTime = nPublishTime;
    }

    public Long getnPublish() {
        return nPublish;
    }

    public void setnPublish(Long nPublish) {
        this.nPublish = nPublish;
    }

    public Long getLeadTime() {
        return leadTime;
    }

    public void setLeadTime(Long leadTime) {
        this.leadTime = leadTime;
    }

    public Long getnCalibrationStatus() {
        return nCalibrationStatus;
    }

    public void setnCalibrationStatus(Long nCalibrationStatus) {
        this.nCalibrationStatus = nCalibrationStatus;
    }

    public String getRiverId() {
        return riverId;
    }

    public void setRiverId(String riverId) {
        this.riverId = riverId;
    }

    public String getnSWModelid() {
        return nSWModelid;
    }

    public void setnSWModelid(String nSWModelid) {
        this.nSWModelid = nSWModelid;
    }

    public String getnPlanid() {
        return nPlanid;
    }

    public void setnPlanid(String nPlanid) {
        this.nPlanid = nPlanid;
    }

    public String getcPlanname() {
        return cPlanname;
    }

    public void setcPlanname(String cPlanname) {
        this.cPlanname = cPlanname;
    }

    public String getcMikescenariocode() {
        return cMikescenariocode;
    }

    public void setcMikescenariocode(String cMikescenariocode) {
        this.cMikescenariocode = cMikescenariocode;
    }

    public String getnCreateuser() {
        return nCreateuser;
    }

    public void setnCreateuser(String nCreateuser) {
        this.nCreateuser = nCreateuser;
    }

    public Date getnPlancurrenttime() {
        return nPlancurrenttime;
    }

    public void setnPlancurrenttime(Date nPlancurrenttime) {
        this.nPlancurrenttime = nPlancurrenttime;
    }

    public Date getdCaculatestarttm() {
        return dCaculatestarttm;
    }

    public void setdCaculatestarttm(Date dCaculatestarttm) {
        this.dCaculatestarttm = dCaculatestarttm;
    }

    public Date getdCaculateendtm() {
        return dCaculateendtm;
    }

    public void setdCaculateendtm(Date dCaculateendtm) {
        this.dCaculateendtm = dCaculateendtm;
    }

    public Double getnTotalrainfall() {
        return nTotalrainfall;
    }

    public void setnTotalrainfall(Double nTotalrainfall) {
        this.nTotalrainfall = nTotalrainfall;
    }

    public Long getnOutputtm() {
        return nOutputtm;
    }

    public void setnOutputtm(Long nOutputtm) {
        this.nOutputtm = nOutputtm;
    }

    public String getnPlantype() {
        return nPlantype;
    }

    public void setnPlantype(String nPlantype) {
        this.nPlantype = nPlantype;
    }

    public Long getnPlanstatus() {
        return nPlanstatus;
    }

    public void setnPlanstatus(Long nPlanstatus) {
        this.nPlanstatus = nPlanstatus;
    }

    public String getnPlanstatusinfo() {
        return nPlanstatusinfo;
    }

    public void setnPlanstatusinfo(String nPlanstatusinfo) {
        this.nPlanstatusinfo = nPlanstatusinfo;
    }

    public String getcComment() {
        return cComment;
    }

    public void setcComment(String cComment) {
        this.cComment = cComment;
    }

    public String getnModelid() {
        return nModelid;
    }

    public void setnModelid(String nModelid) {
        this.nModelid = nModelid;
    }

    public Date getdRainstarttime() {
        return dRainstarttime;
    }

    public void setdRainstarttime(Date dRainstarttime) {
        this.dRainstarttime = dRainstarttime;
    }

    public Date getdRainendtime() {
        return dRainendtime;
    }

    public void setdRainendtime(Date dRainendtime) {
        this.dRainendtime = dRainendtime;
    }

    public Date getdOpensourcestarttime() {
        return dOpensourcestarttime;
    }

    public void setdOpensourcestarttime(Date dOpensourcestarttime) {
        this.dOpensourcestarttime = dOpensourcestarttime;
    }

    public Date getdOpensourceendtime() {
        return dOpensourceendtime;
    }

    public void setdOpensourceendtime(Date dOpensourceendtime) {
        this.dOpensourceendtime = dOpensourceendtime;
    }

    public String getcDeriveplantype() {
        return cDeriveplantype;
    }

    public void setcDeriveplantype(String cDeriveplantype) {
        this.cDeriveplantype = cDeriveplantype;
    }

    public Double getnDeriveplantorder() {
        return nDeriveplantorder;
    }

    public void setnDeriveplantorder(Double nDeriveplantorder) {
        this.nDeriveplantorder = nDeriveplantorder;
    }

    public Date getnCreatetime() {
        return nCreatetime;
    }

    public void setnCreatetime(Date nCreatetime) {
        this.nCreatetime = nCreatetime;
    }

    public String getPlanSystem() {
        return planSystem;
    }

    public void setPlanSystem(String planSystem) {
        this.planSystem = planSystem;
    }

    public String getRscd() {
        return rscd;
    }

    public void setRscd(String rscd) {
        this.rscd = rscd;
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname;
    }

    public Double getRoughness() {
        return roughness;
    }

    public void setRoughness(Double roughness) {
        this.roughness = roughness;
    }
}
