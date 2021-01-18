package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "YWK_PLANINFO", schema = "XQH", catalog = "")
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
    @Column(name = "N_PLANCURRENTTIME")
    private Date nPlancurrenttime;
    @Column(name = "D_CACULATESTARTTM")
    private Date dCaculatestarttm;
    @Column(name = "D_CACULATEENDTM")
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
    @Column(name = "D_RAINSTARTTIME")
    private Date dRainstarttime;
    @Column(name = "D_RAINENDTIME")
    private Date dRainendtime;
    @Column(name = "D_OPENSOURCESTARTTIME")
    private Date dOpensourcestarttime;
    @Column(name = "D_OPENSOURCEENDTIME")
    private Date dOpensourceendtime;
    @Column(name = "C_DERIVEPLANTYPE")
    private String cDeriveplantype;
    @Column(name = "N_DERIVEPLANTORDER")
    private Double nDeriveplantorder;
    @Column(name = "N_CREATETIME")
    private Timestamp nCreatetime;

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

    public Timestamp getnCreatetime() {
        return nCreatetime;
    }

    public void setnCreatetime(Timestamp nCreatetime) {
        this.nCreatetime = nCreatetime;
    }
}
