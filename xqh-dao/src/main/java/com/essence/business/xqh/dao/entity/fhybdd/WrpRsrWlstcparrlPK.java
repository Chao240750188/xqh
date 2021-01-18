package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

public class WrpRsrWlstcparrlPK implements Serializable {
    private String rscd;
    private Double wl;

    @Column(name = "RSCD")
    @Id
    public String getRscd() {
        return rscd;
    }

    public void setRscd(String rscd) {
        this.rscd = rscd;
    }

    @Column(name = "WL")
    @Id
    public Double getWl() {
        return wl;
    }

    public void setWl(Double wl) {
        this.wl = wl;
    }


}
