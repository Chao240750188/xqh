package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

public class StZvarlBPK implements Serializable {
    @Column(name = "STCD")
    @Id
    private String stcd;
    @Column(name = "MSTM")
    @Id
    private Date mstm;
    @Column(name = "PTNO")
    @Id
    private Long ptno;


    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public Date getMstm() {
        return mstm;
    }

    public void setMstm(Date mstm) {
        this.mstm = mstm;
    }

    public Long getPtno() {
        return ptno;
    }

    public void setPtno(Long ptno) {
        this.ptno = ptno;
    }
}
