package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "YWK_PLANIN_RAINFALL", schema = "XQH", catalog = "")
public class YwkPlaninRainfall {
    @Id
    @Column(name = "C_ID")
    private String cId;
    @Column(name = "C_STCD")
    private String cStcd;
    @Column(name = "N_DRP")
    private Double nDrp;
    @Column(name = "D_TIME")
    private Date dTime;
    @Column(name = "N_PLANID")
    private String nPlanid;


    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getcStcd() {
        return cStcd;
    }

    public void setcStcd(String cStcd) {
        this.cStcd = cStcd;
    }

    public Double getnDrp() {
        return nDrp;
    }

    public void setnDrp(Double nDrp) {
        this.nDrp = nDrp;
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

    public YwkPlaninRainfall() {
    }

    public YwkPlaninRainfall(String cId, String cStcd, Double nDrp, Date dTime, String nPlanid) {
        this.cId = cId;
        this.cStcd = cStcd;
        this.nDrp = nDrp;
        this.dTime = dTime;
        this.nPlanid = nPlanid;
    }
}
