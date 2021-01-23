package com.essence.business.xqh.api.fhybdd.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class WrpRvrBsinDto implements Serializable {
    private String rvcd;
    private String rvnm;
    private String alias;
    private String rvtp;
    private String dwwt;
    private String dwwtcd;
    private String hwps;
    private Double hwel;
    private String esps;
    private Double esel;
    private String dtpl;
    private Double rvlen;
    private Double avgg;
    private Double ctar;
    private Double avanram;
    private Double anrnstdv;
    private String rvov;

    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dtupdt;


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

    public Date getDtupdt() {
        return dtupdt;
    }

    public void setDtupdt(Date dtupdt) {
        this.dtupdt = dtupdt;
    }
}
