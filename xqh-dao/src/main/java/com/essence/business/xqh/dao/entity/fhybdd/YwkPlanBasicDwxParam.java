package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "YWK_PLAN_BASIC_DWX_PARAM")
public class YwkPlanBasicDwxParam {
    @Id
    @Column(name = "C_ID")
    private String cId;
    @Column(name = "UNIT_ONE")
    private Double unitOne;
    @Column(name = "UNIT_TWO")
    private Double unitTwo;
    @Column(name = "UNIT_THREE")
    private Double unitThree;
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

    public Double getUnitOne() {
        return unitOne;
    }

    public void setUnitOne(Double unitOne) {
        this.unitOne = unitOne;
    }

    public Double getUnitTwo() {
        return unitTwo;
    }

    public void setUnitTwo(Double unitTwo) {
        this.unitTwo = unitTwo;
    }

    public Double getUnitThree() {
        return unitThree;
    }

    public void setUnitThree(Double unitThree) {
        this.unitThree = unitThree;
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
