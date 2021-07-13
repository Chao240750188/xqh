package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "WRP_RSR_ST_STBPRP")
public class WrpRsrStStbprp {
    @Id
    @Column(name = "C_ID")
    private String cId;
    @Column(name = "RSCD")
    private String rscd;
    @Column(name = "STCD")
    private String stcd;


    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getRscd() {
        return rscd;
    }

    public void setRscd(String rscd) {
        this.rscd = rscd;
    }

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }


}
