package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "WRP_RCS_SWYB")
public class WrpRcsSwyb {
    /**
     * 断面id
     */
    @Id
    @Column(name = "RVCRCRSCCD")
    private String rvcrcrsccd;
    @Column(name = "RVCRCRSCNM")
    private String rvcrcrscnm;
    @Column(name = "RVCD")
    private String rvcd;
    @Column(name = "RVNM")
    private String rvnm;


    public String getRvcrcrsccd() {
        return rvcrcrsccd;
    }

    public void setRvcrcrsccd(String rvcrcrsccd) {
        this.rvcrcrsccd = rvcrcrsccd;
    }


    public String getRvcrcrscnm() {
        return rvcrcrscnm;
    }

    public void setRvcrcrscnm(String rvcrcrscnm) {
        this.rvcrcrscnm = rvcrcrscnm;
    }


    public String getRvcd() {
        return rvcd;
    }

    public void setRvcd(String rvcd) {
        this.rvcd = rvcd;
    }


    public String getRvnm() {
        return rvnm;
    }

    public void setRvnm(String rvnm) {
        this.rvnm = rvnm;
    }


}
