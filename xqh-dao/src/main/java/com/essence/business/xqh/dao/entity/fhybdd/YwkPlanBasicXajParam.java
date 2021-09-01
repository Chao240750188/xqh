package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "YWK_PLAN_BASIC_XAJ_PARAM")
public class YwkPlanBasicXajParam {
    @Id
    @Column(name = "C_ID")
    private String cId;
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
    @Column(name = "XAJ_EP")
    private Double xajEp;
    @Column(name = "ZONE_ID")
    private String zoneId;
    @Column(name = "CREATE_TIME",insertable = false,updatable = false)
    private Timestamp createTime;
    @Column(name = "UPDATE_TIME",insertable = false,updatable = false)
    private Timestamp updateTime;

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
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

    public Double getXajEp() {
        return xajEp;
    }

    public void setXajEp(Double xajEp) {
        this.xajEp = xajEp;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
}
