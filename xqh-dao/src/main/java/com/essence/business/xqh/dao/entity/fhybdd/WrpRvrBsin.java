package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "WRP_RVR_BSIN", schema = "XQH", catalog = "")
public class WrpRvrBsin {
    @Id
    @Column(name = "RVCD")
    private String rvcd;
    @Column(name = "RVNM")
    private String rvnm;
    @Column(name = "ALIAS")
    private String alias;
    @Column(name = "RVTP")
    private String rvtp;
    @Column(name = "DWWT")
    private String dwwt;
    @Column(name = "DWWTCD")
    private String dwwtcd;
    @Column(name = "HWPS")
    private String hwps;
    @Column(name = "HWEL")
    private Double hwel;
    @Column(name = "ESPS")
    private String esps;
    @Column(name = "ESEL")
    private Double esel;
    @Column(name = "DTPL")
    private String dtpl;
    @Column(name = "RVLEN")
    private Double rvlen;
    @Column(name = "AVGG")
    private Double avgg;
    @Column(name = "AVANRAM")
    private Double ctar;
    @Column(name = "ANRNSTDV")
    private Double avanram;
    @Column(name = "CTAR")
    private Double anrnstdv;
    @Column(name = "RVOV")
    private String rvov;
    @Column(name = "DTUPDT")
    private Timestamp dtupdt;


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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getRvtp() {
        return rvtp;
    }

    public void setRvtp(String rvtp) {
        this.rvtp = rvtp;
    }

    public String getDwwt() {
        return dwwt;
    }

    public void setDwwt(String dwwt) {
        this.dwwt = dwwt;
    }

    public String getDwwtcd() {
        return dwwtcd;
    }

    public void setDwwtcd(String dwwtcd) {
        this.dwwtcd = dwwtcd;
    }

    public String getHwps() {
        return hwps;
    }

    public void setHwps(String hwps) {
        this.hwps = hwps;
    }

    public Double getHwel() {
        return hwel;
    }

    public void setHwel(Double hwel) {
        this.hwel = hwel;
    }

    public String getEsps() {
        return esps;
    }

    public void setEsps(String esps) {
        this.esps = esps;
    }

    public Double getEsel() {
        return esel;
    }

    public void setEsel(Double esel) {
        this.esel = esel;
    }

    public String getDtpl() {
        return dtpl;
    }

    public void setDtpl(String dtpl) {
        this.dtpl = dtpl;
    }

    public Double getRvlen() {
        return rvlen;
    }

    public void setRvlen(Double rvlen) {
        this.rvlen = rvlen;
    }

    public Double getAvgg() {
        return avgg;
    }

    public void setAvgg(Double avgg) {
        this.avgg = avgg;
    }

    public Double getCtar() {
        return ctar;
    }

    public void setCtar(Double ctar) {
        this.ctar = ctar;
    }

    public Double getAvanram() {
        return avanram;
    }

    public void setAvanram(Double avanram) {
        this.avanram = avanram;
    }

    public Double getAnrnstdv() {
        return anrnstdv;
    }

    public void setAnrnstdv(Double anrnstdv) {
        this.anrnstdv = anrnstdv;
    }

    public String getRvov() {
        return rvov;
    }

    public void setRvov(String rvov) {
        this.rvov = rvov;
    }

    public Timestamp getDtupdt() {
        return dtupdt;
    }

    public void setDtupdt(Timestamp dtupdt) {
        this.dtupdt = dtupdt;
    }
}
