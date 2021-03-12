package com.essence.business.xqh.dao.entity.fhybdd;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "YWK_PLAN_CALIBRATION_XAJ_BASIC", schema = "XQH", catalog = "")
public class YwkPlanCalibrationXaJBasic {
    @Id
    @Column(name = "C_ID")
    private String cId;
    @Column(name = "N_PLANID")
    private String nPlanid;

    @Column(name = "XAJ_K")
    private Double xajK;
    @Column(name = "XAJ_B")
    private Double xajB;
    @Column(name = "XAJ_C")
    private Double xajC;
    @Column(name = "XAJ_WUM")
    private Double xajWum;
    @Column(name = "XAJ_WLM")
    private Double xajWlm;
    @Column(name = "XAJ_WDM")
    private Double xajWdm;
    @Column(name = "XAJ_WU0")
    private Double xajWu0;
    @Column(name = "XAJ_WL0")
    private Double xajWl0;
    @Column(name = "XAJ_WD0")
    private Double xajWd0;

    @Column(name = "CREATE_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getnPlanid() {
        return nPlanid;
    }

    public void setnPlanid(String nPlanid) {
        this.nPlanid = nPlanid;
    }



    public Double getXajK() {
        return xajK;
    }

    public void setXajK(Double xajK) {
        this.xajK = xajK;
    }

    public Double getXajB() {
        return xajB;
    }

    public void setXajB(Double xajB) {
        this.xajB = xajB;
    }

    public Double getXajC() {
        return xajC;
    }

    public void setXajC(Double xajC) {
        this.xajC = xajC;
    }

    public Double getXajWum() {
        return xajWum;
    }

    public void setXajWum(Double xajWum) {
        this.xajWum = xajWum;
    }

    public Double getXajWlm() {
        return xajWlm;
    }

    public void setXajWlm(Double xajWlm) {
        this.xajWlm = xajWlm;
    }

    public Double getXajWdm() {
        return xajWdm;
    }

    public void setXajWdm(Double xajWdm) {
        this.xajWdm = xajWdm;
    }

    public Double getXajWu0() {
        return xajWu0;
    }

    public void setXajWu0(Double xajWu0) {
        this.xajWu0 = xajWu0;
    }

    public Double getXajWl0() {
        return xajWl0;
    }

    public void setXajWl0(Double xajWl0) {
        this.xajWl0 = xajWl0;
    }

    public Double getXajWd0() {
        return xajWd0;
    }

    public void setXajWd0(Double xajWd0) {
        this.xajWd0 = xajWd0;
    }



    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
