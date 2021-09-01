package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "YWK_PLAN_BASIC_XGGX_PARAM")
public class YwkPlanBasicXggxParam {
    @Id
    @Column(name = "C_ID")
    private String cId;
    @Column(name = "ZONE_ID")
    private String zoneId;
    @Column(name = "XGGX_A")
    private Double xggxA;
    @Column(name = "XGGX_B")
    private Double xggxB;
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

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public Double getXggxA() {
        return xggxA;
    }

    public void setXggxA(Double xggxA) {
        this.xggxA = xggxA;
    }

    public Double getXggxB() {
        return xggxB;
    }

    public void setXggxB(Double xggxB) {
        this.xggxB = xggxB;
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
