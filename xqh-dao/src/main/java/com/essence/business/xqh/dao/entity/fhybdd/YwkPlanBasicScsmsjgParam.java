package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "YWK_PLAN_BASIC_SCSMSJG_PARAM")
public class YwkPlanBasicScsmsjgParam {
    @Id
    @Column(name = "C_ID")
    private String cId;
    @Column(name = "MSJG_K")
    private Double msjgK;
    @Column(name = "MSJG_X")
    private Double msjgX;
    @Column(name = "SCS_CN")
    private Long scsCn;
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

    public Double getMsjgK() {
        return msjgK;
    }

    public void setMsjgK(Double msjgK) {
        this.msjgK = msjgK;
    }

    public Double getMsjgX() {
        return msjgX;
    }

    public void setMsjgX(Double msjgX) {
        this.msjgX = msjgX;
    }

    public Long getScsCn() {
        return scsCn;
    }

    public void setScsCn(Long scsCn) {
        this.scsCn = scsCn;
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
