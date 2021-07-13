package com.essence.business.xqh.dao.entity.fhybdd;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "ST_PPTN_R")
//@Table(name = "ST_PPTN_R")
public class StPptnR {
    @Id
    @Column(name = "ID")
    private String id;
    //测站编码
    @Column(name = "STCD")
    private String stcd;
    //时间
    @Column(name = "TM")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date tm;
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

    public Date getTm() {
        return tm;
    }

    public void setTm(Date tm) {
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
