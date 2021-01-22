package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "WRP_RCS_BSIN", schema = "XQH", catalog = "")
public class WrpRcsBsin {
    @Id
    @Column(name = "RVCRCRSCCD")
    private String rvcrcrsccd;
    @Column(name = "RVCRCRSCNM")
    private String rvcrcrscnm;
    @Column(name = "MNUN")
    private String mnun;
    @Column(name = "CMUN")
    private String cmun;
    @Column(name = "ADDVCD")
    private String addvcd;
    @Column(name = "VLTW")
    private String vltw;
    @Column(name = "CTCD")
    private String ctcd;
    @Column(name = "LGTD")
    private Long lgtd;
    @Column(name = "LTTD")
    private Long lttd;
    @Column(name = "CRSCWD")
    private Long crscwd;
    @Column(name = "RVCHWD")
    private Long rvchwd;
    @Column(name = "RVMNCHEL")
    private Long rvmnchel;
    @Column(name = "DTPL")
    private String dtpl;
    @Column(name = "RVCRCRSCOV")
    private String rvcrcrscov;
    @Column(name = "DTUPDT")
    private Timestamp dtupdt;


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

    public String getMnun() {
        return mnun;
    }

    public void setMnun(String mnun) {
        this.mnun = mnun;
    }

    public String getCmun() {
        return cmun;
    }

    public void setCmun(String cmun) {
        this.cmun = cmun;
    }

    public String getAddvcd() {
        return addvcd;
    }

    public void setAddvcd(String addvcd) {
        this.addvcd = addvcd;
    }

    public String getVltw() {
        return vltw;
    }

    public void setVltw(String vltw) {
        this.vltw = vltw;
    }

    public String getCtcd() {
        return ctcd;
    }

    public void setCtcd(String ctcd) {
        this.ctcd = ctcd;
    }

    public Long getLgtd() {
        return lgtd;
    }

    public void setLgtd(Long lgtd) {
        this.lgtd = lgtd;
    }

    public Long getLttd() {
        return lttd;
    }

    public void setLttd(Long lttd) {
        this.lttd = lttd;
    }

    public Long getCrscwd() {
        return crscwd;
    }

    public void setCrscwd(Long crscwd) {
        this.crscwd = crscwd;
    }

    public Long getRvchwd() {
        return rvchwd;
    }

    public void setRvchwd(Long rvchwd) {
        this.rvchwd = rvchwd;
    }

    public Long getRvmnchel() {
        return rvmnchel;
    }

    public void setRvmnchel(Long rvmnchel) {
        this.rvmnchel = rvmnchel;
    }

    public String getDtpl() {
        return dtpl;
    }

    public void setDtpl(String dtpl) {
        this.dtpl = dtpl;
    }

    public String getRvcrcrscov() {
        return rvcrcrscov;
    }

    public void setRvcrcrscov(String rvcrcrscov) {
        this.rvcrcrscov = rvcrcrscov;
    }

    public Timestamp getDtupdt() {
        return dtupdt;
    }

    public void setDtupdt(Timestamp dtupdt) {
        this.dtupdt = dtupdt;
    }


}
