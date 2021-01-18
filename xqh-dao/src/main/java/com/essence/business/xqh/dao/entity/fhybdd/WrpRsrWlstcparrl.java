package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "WRP_RSR_WLSTCPARRL", schema = "XQH", catalog = "")
@IdClass(WrpRsrWlstcparrlPK.class)
public class WrpRsrWlstcparrl {
    @Id
    @Column(name = "RSCD")
    private String rscd;
    @Id
    @Column(name = "WL")
    private Double wl;
    @Column(name = "STCP")
    private Double stcp;
    @Column(name = "AR")
    private Double ar;
    @Column(name = "DTUPDT")
    private Timestamp dtupdt;


    public String getRscd() {
        return rscd;
    }

    public void setRscd(String rscd) {
        this.rscd = rscd;
    }


    public Double getWl() {
        return wl;
    }

    public void setWl(Double wl) {
        this.wl = wl;
    }

    public Double getStcp() {
        return stcp;
    }

    public void setStcp(Double stcp) {
        this.stcp = stcp;
    }

    public Double getAr() {
        return ar;
    }

    public void setAr(Double ar) {
        this.ar = ar;
    }

    public Timestamp getDtupdt() {
        return dtupdt;
    }

    public void setDtupdt(Timestamp dtupdt) {
        this.dtupdt = dtupdt;
    }


}
