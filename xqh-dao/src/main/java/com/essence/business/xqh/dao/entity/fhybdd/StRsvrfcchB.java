package com.essence.business.xqh.dao.entity.fhybdd;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 水文(水库)特征值等信息。
 */
@Entity
@Table(name = "ST_RSVRFCCH_B")
public class StRsvrfcchB {
    //测站编码
    @Id
    @Column(name = "STCD")
    private String stcd;

    //水库类型
    @Column(name = "RSVRTP")
    private String rsvrtp;

    //坝顶高程
    @Column(name = "DAMEL")
    private Double damel;

    //校核洪水位
    @Column(name = "CKFLZ")
    private Double ckflz;

    //设计洪水位
    @Column(name = "DSFLZ")
    private Double dsflz;

    //正常水位
    @Column(name = "NORMZ")
    private Double normz;

    //死水位
    @Column(name = "DDZ")
    private Double ddz;

    //兴利水位
    @Column(name = "ACTZ")
    private Double actz;

    //总库容
    @Column(name = "TTCP")
    private Double ttcp;

    //防洪库容
    @Column(name = "FLDCP")
    private Double fldcp;

    //兴利库容
    @Column(name = "ACTCP")
    private Double actcp;

    //死库容
    @Column(name = "DDCP")
    private Double ddcp;

    //历史最高库水位
    @Column(name = "HHRZ")
    private Double hhrz;

    //历史最大蓄水量
    @Column(name = "HMXW")
    private Double hmxw;

    //历史最高库水位（蓄水量）时间
    @Column(name = "HHRZTM")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date hhrztm;

    //历史最大入流
    @Column(name = "HMXINQ")
    private Double hmxinq;

    //历史最大入流时段长
    @Column(name = "RSTDR")
    private Double rstdr;

    //历史最大入流出现时间
    @Column(name = "HMXINQTM")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date hmxinqtm;

    //历史最大出流
    @Column(name = "HMXOTQ")
    private Double hmxotq;

    //历史最大出流出现时间
    @Column(name = "HMXOTQTM")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date hmxotqtm;

    //历史最低库水位
    @Column(name = "HLRZ")
    private Double hlrz;

    //历史最低库水位出现时间
    @Column(name = "HLRZTM")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date hlrztm;

    //历史最小日均入流
    @Column(name = "HMNINQ")
    private Double hmninq;

    //历史最小日均入流出现时间
    @Column(name = "HMNINQTM")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date hmninqtm;

    //低水位告警值
    @Column(name = "LAZ")
    private Double laz;

    //启动预报流量标准
    @Column(name = "SFQ")
    private Double sfq;

    //编辑时间
    @Column(name = "MODITIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modiTime;

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public String getRsvrtp() {
        return rsvrtp;
    }

    public void setRsvrtp(String rsvrtp) {
        this.rsvrtp = rsvrtp;
    }

    public Double getDamel() {
        return damel;
    }

    public void setDamel(Double damel) {
        this.damel = damel;
    }

    public Double getCkflz() {
        return ckflz;
    }

    public void setCkflz(Double ckflz) {
        this.ckflz = ckflz;
    }

    public Double getDsflz() {
        return dsflz;
    }

    public void setDsflz(Double dsflz) {
        this.dsflz = dsflz;
    }

    public Double getNormz() {
        return normz;
    }

    public void setNormz(Double normz) {
        this.normz = normz;
    }

    public Double getDdz() {
        return ddz;
    }

    public void setDdz(Double ddz) {
        this.ddz = ddz;
    }

    public Double getActz() {
        return actz;
    }

    public void setActz(Double actz) {
        this.actz = actz;
    }

    public Double getTtcp() {
        return ttcp;
    }

    public void setTtcp(Double ttcp) {
        this.ttcp = ttcp;
    }

    public Double getFldcp() {
        return fldcp;
    }

    public void setFldcp(Double fldcp) {
        this.fldcp = fldcp;
    }

    public Double getActcp() {
        return actcp;
    }

    public void setActcp(Double actcp) {
        this.actcp = actcp;
    }

    public Double getDdcp() {
        return ddcp;
    }

    public void setDdcp(Double ddcp) {
        this.ddcp = ddcp;
    }

    public Double getHhrz() {
        return hhrz;
    }

    public void setHhrz(Double hhrz) {
        this.hhrz = hhrz;
    }

    public Double getHmxw() {
        return hmxw;
    }

    public void setHmxw(Double hmxw) {
        this.hmxw = hmxw;
    }

    public Date getHhrztm() {
        return hhrztm;
    }

    public void setHhrztm(Date hhrztm) {
        this.hhrztm = hhrztm;
    }

    public Double getHmxinq() {
        return hmxinq;
    }

    public void setHmxinq(Double hmxinq) {
        this.hmxinq = hmxinq;
    }

    public Double getRstdr() {
        return rstdr;
    }

    public void setRstdr(Double rstdr) {
        this.rstdr = rstdr;
    }

    public Date getHmxinqtm() {
        return hmxinqtm;
    }

    public void setHmxinqtm(Date hmxinqtm) {
        this.hmxinqtm = hmxinqtm;
    }

    public Double getHmxotq() {
        return hmxotq;
    }

    public void setHmxotq(Double hmxotq) {
        this.hmxotq = hmxotq;
    }

    public Date getHmxotqtm() {
        return hmxotqtm;
    }

    public void setHmxotqtm(Date hmxotqtm) {
        this.hmxotqtm = hmxotqtm;
    }

    public Double getHlrz() {
        return hlrz;
    }

    public void setHlrz(Double hlrz) {
        this.hlrz = hlrz;
    }

    public Date getHlrztm() {
        return hlrztm;
    }

    public void setHlrztm(Date hlrztm) {
        this.hlrztm = hlrztm;
    }

    public Double getHmninq() {
        return hmninq;
    }

    public void setHmninq(Double hmninq) {
        this.hmninq = hmninq;
    }

    public Date getHmninqtm() {
        return hmninqtm;
    }

    public void setHmninqtm(Date hmninqtm) {
        this.hmninqtm = hmninqtm;
    }

    public Double getLaz() {
        return laz;
    }

    public void setLaz(Double laz) {
        this.laz = laz;
    }

    public Double getSfq() {
        return sfq;
    }

    public void setSfq(Double sfq) {
        this.sfq = sfq;
    }

    public Date getModiTime() {
        return modiTime;
    }

    public void setModiTime(Date modiTime) {
        this.modiTime = modiTime;
    }
}
