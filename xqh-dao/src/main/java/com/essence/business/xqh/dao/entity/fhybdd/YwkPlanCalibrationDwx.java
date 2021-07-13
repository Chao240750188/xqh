package com.essence.business.xqh.dao.entity.fhybdd;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "YWK_PLAN_CALIBRATION_DWX")
public class YwkPlanCalibrationDwx {
    @Id
    @Column(name = "C_ID")
    private String cId;
    @Column(name = "N_PLANID")
    private String nPlanid;
    @Column(name = "UNIT_ONE")
    private Double unitOne;
    @Column(name = "UNIT_TWO")
    private Double unitTwo;
    @Column(name = "UNIT_THREE")
    private Double unitThree;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
