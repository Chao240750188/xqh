package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "WRP_RSR_HYCH", schema = "XQH", catalog = "")
public class WrpRsrHychEntity {
    @Id
    @Column(name = "RSCD")
    private String rscd;
    @Column(name = "CNCTAR")
    private Double cnctar;
    @Column(name = "RVCRLEN")
    private Double rvcrlen;
    @Column(name = "RVCRGR")
    private Double rvcrgr;
    @Column(name = "AVANPRAM")
    private Double avanpram;
    @Column(name = "AVANRNAM")
    private Double avanrnam;
    @Column(name = "AVANSDAM")
    private Double avansdam;
    @Column(name = "MYAMINTP")
    private Double myamintp;
    @Column(name = "DSRCIN")
    private Long dsrcin;
    @Column(name = "DSPKFL")
    private Double dspkfl;
    @Column(name = "DSFLVLONDY")
    private Double dsflvlondy;
    @Column(name = "DSFLVLTHDY")
    private Double dsflvlthdy;
    @Column(name = "CHRCIN")
    private Long chrcin;
    @Column(name = "CHPKFL")
    private Double chpkfl;
    @Column(name = "CHFLDR")
    private Long chfldr;
    @Column(name = "CHFLVL")
    private Double chflvl;
    @Column(name = "RSRRGTPFM")
    private String rsrrgtpfm;
    @Column(name = "CHFLLV")
    private Double chfllv;
    @Column(name = "DSFLLV")
    private Double dsfllv;
    @Column(name = "FLCNHGWL")
    private Double flcnhgwl;
    @Column(name = "MJFLSSCNWL")
    private Double mjflsscnwl;
    @Column(name = "NRSTLV")
    private Double nrstlv;
    @Column(name = "DDWL")
    private Double ddwl;
    @Column(name = "TTSTCP")
    private Double ttstcp;
    @Column(name = "FLSTCP")
    private Double flstcp;
    @Column(name = "FLCNSTCP")
    private Double flcnstcp;
    @Column(name = "ACSTCP")
    private Double acstcp;
    @Column(name = "DDSTCP")
    private Double ddstcp;
    @Column(name = "DTUPDT")
    private Timestamp dtupdt;


    public String getRscd() {
        return rscd;
    }

    public void setRscd(String rscd) {
        this.rscd = rscd;
    }

    public Double getCnctar() {
        return cnctar;
    }

    public void setCnctar(Double cnctar) {
        this.cnctar = cnctar;
    }

    public Double getRvcrlen() {
        return rvcrlen;
    }

    public void setRvcrlen(Double rvcrlen) {
        this.rvcrlen = rvcrlen;
    }

    public Double getRvcrgr() {
        return rvcrgr;
    }

    public void setRvcrgr(Double rvcrgr) {
        this.rvcrgr = rvcrgr;
    }

    public Double getAvanpram() {
        return avanpram;
    }

    public void setAvanpram(Double avanpram) {
        this.avanpram = avanpram;
    }

    public Double getAvanrnam() {
        return avanrnam;
    }

    public void setAvanrnam(Double avanrnam) {
        this.avanrnam = avanrnam;
    }

    public Double getAvansdam() {
        return avansdam;
    }

    public void setAvansdam(Double avansdam) {
        this.avansdam = avansdam;
    }

    public Double getMyamintp() {
        return myamintp;
    }

    public void setMyamintp(Double myamintp) {
        this.myamintp = myamintp;
    }



    public Double getDspkfl() {
        return dspkfl;
    }

    public void setDspkfl(Double dspkfl) {
        this.dspkfl = dspkfl;
    }

    public Double getDsflvlondy() {
        return dsflvlondy;
    }

    public void setDsflvlondy(Double dsflvlondy) {
        this.dsflvlondy = dsflvlondy;
    }

    public Double getDsflvlthdy() {
        return dsflvlthdy;
    }

    public void setDsflvlthdy(Double dsflvlthdy) {
        this.dsflvlthdy = dsflvlthdy;
    }



    public Double getChpkfl() {
        return chpkfl;
    }

    public void setChpkfl(Double chpkfl) {
        this.chpkfl = chpkfl;
    }



    public Double getChflvl() {
        return chflvl;
    }

    public void setChflvl(Double chflvl) {
        this.chflvl = chflvl;
    }

    public String getRsrrgtpfm() {
        return rsrrgtpfm;
    }

    public void setRsrrgtpfm(String rsrrgtpfm) {
        this.rsrrgtpfm = rsrrgtpfm;
    }

    public Double getChfllv() {
        return chfllv;
    }

    public void setChfllv(Double chfllv) {
        this.chfllv = chfllv;
    }

    public Double getDsfllv() {
        return dsfllv;
    }

    public void setDsfllv(Double dsfllv) {
        this.dsfllv = dsfllv;
    }

    public Double getFlcnhgwl() {
        return flcnhgwl;
    }

    public void setFlcnhgwl(Double flcnhgwl) {
        this.flcnhgwl = flcnhgwl;
    }

    public Double getMjflsscnwl() {
        return mjflsscnwl;
    }

    public void setMjflsscnwl(Double mjflsscnwl) {
        this.mjflsscnwl = mjflsscnwl;
    }

    public Double getNrstlv() {
        return nrstlv;
    }

    public void setNrstlv(Double nrstlv) {
        this.nrstlv = nrstlv;
    }

    public Double getDdwl() {
        return ddwl;
    }

    public void setDdwl(Double ddwl) {
        this.ddwl = ddwl;
    }

    public Double getTtstcp() {
        return ttstcp;
    }

    public void setTtstcp(Double ttstcp) {
        this.ttstcp = ttstcp;
    }

    public Double getFlstcp() {
        return flstcp;
    }

    public void setFlstcp(Double flstcp) {
        this.flstcp = flstcp;
    }

    public Double getFlcnstcp() {
        return flcnstcp;
    }

    public void setFlcnstcp(Double flcnstcp) {
        this.flcnstcp = flcnstcp;
    }

    public Double getAcstcp() {
        return acstcp;
    }

    public void setAcstcp(Double acstcp) {
        this.acstcp = acstcp;
    }

    public Double getDdstcp() {
        return ddstcp;
    }

    public void setDdstcp(Double ddstcp) {
        this.ddstcp = ddstcp;
    }

    public Timestamp getDtupdt() {
        return dtupdt;
    }

    public void setDtupdt(Timestamp dtupdt) {
        this.dtupdt = dtupdt;
    }

    public void setDsrcin(Long dsrcin) {
        this.dsrcin = dsrcin;
    }

    public Long getChrcin() {
        return chrcin;
    }

    public void setChrcin(Long chrcin) {
        this.chrcin = chrcin;
    }

    public Long getChfldr() {
        return chfldr;
    }

    public void setChfldr(Long chfldr) {
        this.chfldr = chfldr;
    }
}
