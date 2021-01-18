package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "YWK_PLAN_OUTPUT_Q", schema = "XQH", catalog = "")
public class YwkPlanOutputQ {
    @Id
    @Column(name = "IDC_ID")
    private String idcId;
    @Column(name = "N_PLANID")
    private String nPlanid;
    @Column(name = "D_TIME")
    private Timestamp dTime;
    @Column(name = "N_Q")
    private Double nQ;


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

    public Timestamp getdTime() {
        return dTime;
    }

    public void setdTime(Timestamp dTime) {
        this.dTime = dTime;
    }


    public Double getnQ() {
        return nQ;
    }

    public void setnQ(Double nQ) {
        this.nQ = nQ;
    }
}
