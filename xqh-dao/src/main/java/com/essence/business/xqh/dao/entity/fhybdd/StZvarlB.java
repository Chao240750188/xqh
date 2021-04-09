package com.essence.business.xqh.dao.entity.fhybdd;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "ST_ZVARL_B", schema = "XQH", catalog = "")
@IdClass(StZvarlBPK.class)
public class StZvarlB {
    @Id
    @Column(name = "STCD")
    private String stcd;
    @Id
    @Column(name = "MSTM")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date mstm;
    @Id
    @Column(name = "PTNO")
    private Long ptno;
    @Column(name = "RZ")
    private BigDecimal rz;
    @Column(name = "W")
    private BigDecimal w;
    @Column(name = "WSFA")
    private BigDecimal wsfa;

    @Column(name = "MODITIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date moditime;


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

    public BigDecimal getRz() {
        return rz;
    }

    public void setRz(BigDecimal rz) {
        this.rz = rz;
    }

    public BigDecimal getW() {
        return w;
    }

    public void setW(BigDecimal w) {
        this.w = w;
    }

    public BigDecimal getWsfa() {
        return wsfa;
    }

    public void setWsfa(BigDecimal wsfa) {
        this.wsfa = wsfa;
    }

    public Date getModitime() {
        return moditime;
    }

    public void setModitime(Date moditime) {
        this.moditime = moditime;
    }
}
