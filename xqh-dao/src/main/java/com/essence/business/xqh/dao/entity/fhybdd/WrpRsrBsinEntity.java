package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "WRP_RSR_BSIN", schema = "XQH", catalog = "")
public class WrpRsrBsinEntity {
    @Id
    @Column(name = "RSCD")
    private String rscd;
    @Column(name = "RSNM")
    private String rsnm;
    @Column(name = "ALIAS")
    private String alias;
    @Column(name = "PRST")
    private String prst;
    @Column(name = "PRSC")
    private String prsc;
    @Column(name = "PRGR")
    private String prgr;
    @Column(name = "MNBLGR")
    private String mnblgr;
    @Column(name = "MNUN")
    private String mnun;
    @Column(name = "CMUN")
    private String cmun;
    @Column(name = "BLSYS")
    private String blsys;
    @Column(name = "ADDVCD")
    private String addvcd;
    @Column(name = "VLTW")
    private String vltw;
    @Column(name = "CTCD")
    private String ctcd;
    @Column(name = "RVCD")
    private String rvcd;
    @Column(name = "LGTD")
    private Double lgtd;
    @Column(name = "LTTD")
    private Double lttd;
    @Column(name = "EQMTPKACLT")
    private String eqmtpkaclt;
    @Column(name = "BSSSIN")
    private Integer bsssin;
    @Column(name = "FREQIN")
    private Integer freqin;
    @Column(name = "DTPL")
    private String dtpl;
    @Column(name = "CPYR")
    private String cpyr;
    @Column(name = "ISRG")
    private String isrg;
    @Column(name = "RSOV")
    private String rsov;
    @Column(name = "DTUPDT")
    private Timestamp dtupdt;


    public String getRscd() {
        return rscd;
    }

    public void setRscd(String rscd) {
        this.rscd = rscd;
    }

    public String getRsnm() {
        return rsnm;
    }

    public void setRsnm(String rsnm) {
        this.rsnm = rsnm;
    }


    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPrst() {
        return prst;
    }

    public void setPrst(String prst) {
        this.prst = prst;
    }

    public String getPrsc() {
        return prsc;
    }

    public void setPrsc(String prsc) {
        this.prsc = prsc;
    }

    public String getPrgr() {
        return prgr;
    }

    public void setPrgr(String prgr) {
        this.prgr = prgr;
    }

    public String getMnblgr() {
        return mnblgr;
    }

    public void setMnblgr(String mnblgr) {
        this.mnblgr = mnblgr;
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

    public String getBlsys() {
        return blsys;
    }

    public void setBlsys(String blsys) {
        this.blsys = blsys;
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

    public String getRvcd() {
        return rvcd;
    }

    public void setRvcd(String rvcd) {
        this.rvcd = rvcd;
    }

    public Double getLgtd() {
        return lgtd;
    }

    public void setLgtd(Double lgtd) {
        this.lgtd = lgtd;
    }

    public Double getLttd() {
        return lttd;
    }

    public void setLttd(Double lttd) {
        this.lttd = lttd;
    }

    public Integer getBsssin() {
        return bsssin;
    }

    public void setBsssin(Integer bsssin) {
        this.bsssin = bsssin;
    }

    public Integer getFreqin() {
        return freqin;
    }

    public void setFreqin(Integer freqin) {
        this.freqin = freqin;
    }

    public String getEqmtpkaclt() {
        return eqmtpkaclt;
    }

    public void setEqmtpkaclt(String eqmtpkaclt) {
        this.eqmtpkaclt = eqmtpkaclt;
    }



    public String getDtpl() {
        return dtpl;
    }

    public void setDtpl(String dtpl) {
        this.dtpl = dtpl;
    }

    public String getCpyr() {
        return cpyr;
    }

    public void setCpyr(String cpyr) {
        this.cpyr = cpyr;
    }

    public String getIsrg() {
        return isrg;
    }

    public void setIsrg(String isrg) {
        this.isrg = isrg;
    }

    public String getRsov() {
        return rsov;
    }

    public void setRsov(String rsov) {
        this.rsov = rsov;
    }

    public Timestamp getDtupdt() {
        return dtupdt;
    }

    public void setDtupdt(Timestamp dtupdt) {
        this.dtupdt = dtupdt;
    }


}
