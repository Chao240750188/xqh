package com.essence.business.xqh.dao.entity.fhybdd;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "YWK_PLAN_OUTPUT_Q", schema = "XQH", catalog = "")
public class YwkPlanOutputQ {
    @Id
    @Column(name = "IDC_ID")
    private String idcId;
    @Column(name = "N_PLANID")
    private String nPlanid;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "D_TIME")
    private Date dTime;
    @Column(name = "N_Q")
    private Double nQ;
    @Column(name = "RVCRCRSCCD")
    private String rvcrcrsccd;


    public String getIdcId() {
        return idcId;
    }

    public void setIdcId(String idcId) {
        this.idcId = idcId;
    }

    public String getnPlanid() {
        return nPlanid;
    }

    public void setnPlanid(String nPlanid) {
        this.nPlanid = nPlanid;
    }

    public Date getdTime() {
        return dTime;
    }

    public void setdTime(Date dTime) {
        this.dTime = dTime;
    }

    public Double getnQ() {
        return nQ;
    }

    public void setnQ(Double nQ) {
        this.nQ = nQ;
    }

    public String getRvcrcrsccd() {
        return rvcrcrsccd;
    }

    public void setRvcrcrsccd(String rvcrcrsccd) {
        this.rvcrcrsccd = rvcrcrsccd;
    }

    public YwkPlanOutputQ() {
    }

    public YwkPlanOutputQ(String idcId, String nPlanid, Date dTime, Double nQ, String rvcrcrsccd) {
        this.idcId = idcId;
        this.nPlanid = nPlanid;
        this.dTime = dTime;
        this.nQ = nQ;
        this.rvcrcrsccd = rvcrcrsccd;
    }
}
