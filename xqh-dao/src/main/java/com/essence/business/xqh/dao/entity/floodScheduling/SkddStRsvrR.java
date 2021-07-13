package com.essence.business.xqh.dao.entity.floodScheduling;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 水库水情表
 * LiuGt add at 2020-07-22
 */
@Entity
@IdClass(SkddStRsvrRKey.class)
@Table(name = "SKDD_ST_RSVR_R")
public class SkddStRsvrR implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 测站ID
     */
    @Id
    @Column(name = "STCD")
    private String stcd;

    /**
     * 时间
     */
    @Id
    @Column(name = "TM")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tm;

    /**
     * 库上水位(单位：m)
     */
    @Column(name = "RZ")
    private BigDecimal rz;

    /**
     * 入库流量(单位：m3/s)
     */
    @Column(name = "INQ")
    private BigDecimal inq;

    /**
     * 蓄水量(百万立方米)
     */
    @Column(name = "W")
    private BigDecimal w;

    /**
     * 库下水位(单位：m)
     */
    @Column(name = "BLRZ")
    private BigDecimal blrz;

    /**
     * 出库流量(单位：m3/s)
     */
    @Column(name = "OTQ")
    private BigDecimal otq;

    /**
     * 库水特征码
     */
    @Column(name = "RWCHRCD")
    private String rwchrcd;

    /**
     * 库水水势
     */
    @Column(name = "RWPTN")
    private String rwptn;

    /**
     * 入流时段长
     */
    @Column(name = "INQDR")
    private BigDecimal inqdr;

    /**
     * 测流方法
     */
    @Column(name = "MSQMT")
    private String msqmt;

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public LocalDateTime getTm() {
        return tm;
    }

    public void setTm(LocalDateTime tm) {
        this.tm = tm;
    }

    public BigDecimal getRz() {
        return rz;
    }

    public void setRz(BigDecimal rz) {
        this.rz = rz;
    }

    public BigDecimal getInq() {
        return inq;
    }

    public void setInq(BigDecimal inq) {
        this.inq = inq;
    }

    public BigDecimal getW() {
        return w;
    }

    public void setW(BigDecimal w) {
        this.w = w;
    }

    public BigDecimal getBlrz() {
        return blrz;
    }

    public void setBlrz(BigDecimal blrz) {
        this.blrz = blrz;
    }

    public BigDecimal getOtq() {
        return otq;
    }

    public void setOtq(BigDecimal otq) {
        this.otq = otq;
    }

    public String getRwchrcd() {
        return rwchrcd;
    }

    public void setRwchrcd(String rwchrcd) {
        this.rwchrcd = rwchrcd;
    }

    public String getRwptn() {
        return rwptn;
    }

    public void setRwptn(String rwptn) {
        this.rwptn = rwptn;
    }

    public BigDecimal getInqdr() {
        return inqdr;
    }

    public void setInqdr(BigDecimal inqdr) {
        this.inqdr = inqdr;
    }

    public String getMsqmt() {
        return msqmt;
    }

    public void setMsqmt(String msqmt) {
        this.msqmt = msqmt;
    }
}
