package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "YWK_PLAN_CALIBRATION_ZONE", schema = "XQH", catalog = "")
public class YwkPlanCalibrationZone {
    @Id
    @Column(name = "C_ID")
    private String cId;
    @Column(name = "ZONE_ID")
    private String zoneId;
    @Column(name = "MSJG_K")
    private Double msjgK;
    @Column(name = "MSJG_X")
    private Double msjgX;
    @Column(name = "SCS_CN")
    private Long scsCn;
    @Column(name = "N_PLANID")
    private String nPlanid;

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

    public String getnPlanid() {
        return nPlanid;
    }

    public void setnPlanid(String nPlanid) {
        this.nPlanid = nPlanid;
    }
}
