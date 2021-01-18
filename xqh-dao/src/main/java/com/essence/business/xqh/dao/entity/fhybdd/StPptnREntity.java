package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "ST_PPTN_R", schema = "XQH", catalog = "")
public class StPptnREntity {
    @Column(name = "ID")
    private String id;
    //测站编码
    @Column(name = "STCD")
    private String stcd;
    //时间
    @Column(name = "TM")
    private Timestamp tm;
    //时段降水量
    @Column(name = "DRP")
    private Double drp;
    //时段长
    @Column(name = "INTV")
    private Double intv;
    //降水历时
    @Column(name = "PDR")
    private Double pdr;
    //日降水量
    @Column(name = "DYP")
    private Double dyp;
    //天气状况
    @Column(name = "WTH")
    private String wth;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public Timestamp getTm() {
        return tm;
    }

    public void setTm(Timestamp tm) {
        this.tm = tm;
    }

    public Double getDrp() {
        return drp;
    }

    public void setDrp(Double drp) {
        this.drp = drp;
    }

    public Double getIntv() {
        return intv;
    }

    public void setIntv(Double intv) {
        this.intv = intv;
    }

    public Double getPdr() {
        return pdr;
    }

    public void setPdr(Double pdr) {
        this.pdr = pdr;
    }

    public Double getDyp() {
        return dyp;
    }

    public void setDyp(Double dyp) {
        this.dyp = dyp;
    }

    public String getWth() {
        return wth;
    }

    public void setWth(String wth) {
        this.wth = wth;
    }
}
