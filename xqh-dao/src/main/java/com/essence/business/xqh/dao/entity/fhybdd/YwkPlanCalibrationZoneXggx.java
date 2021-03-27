package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "YWK_PLAN_CALIBRATION_ZONE_XGGX", schema = "XQH", catalog = "")
public class YwkPlanCalibrationZoneXggx {
    @Id
    @Column(name = "C_ID")
    private String cId;
    @Column(name = "N_PLANID")
    private String nPlanid;
    @Column(name = "ZONE_ID")
    private String zoneId;
    @Column(name = "XGGX_A")
    private Double xggxA;
    @Column(name = "XGGX_B")
    private Double xggxB;
    @Column(name = "CREATE_TIME")
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
