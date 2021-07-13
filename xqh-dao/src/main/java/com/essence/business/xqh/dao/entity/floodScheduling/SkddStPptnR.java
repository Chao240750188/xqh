package com.essence.business.xqh.dao.entity.floodScheduling;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 实时降雨表实体类
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/06/28
 */
@Entity
@IdClass(SkddStPptnRKey.class)
@Table(name = "SKDD_ST_PPTN_R")
public class SkddStPptnR implements Serializable {

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
     * 点序号，库水位和蓄水量对应点在该条库容曲线中的顺序号，从 1 开始，按顺序依次递增。
     */
    @Id
    @Column(name = "TM")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tm;

    /**
     * 时段降水量(单位：mm)
     */
    @Column(name = "DRP")
    private Double drp;

    /**
     * 时段长(单位：h)
     */
    @Column(name = "INTV")
    private BigDecimal intv;

    /**
     * 降水历时
     */
    @Column(name = "PDR")
    private BigDecimal pdr;

    /**
     * 日降水量(单位：mm)
     */
    @Column(name = "DYP")
    private BigDecimal dyp;

    /**
     * 天气状况
     */
    @Column(name = "WTH")
    private String wth;

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

    public Double getDrp() {
        return drp;
    }

    public void setDrp(Double drp) {
        this.drp = drp;
    }

    public BigDecimal getIntv() {
        return intv;
    }

    public void setIntv(BigDecimal intv) {
        this.intv = intv;
    }

    public BigDecimal getPdr() {
        return pdr;
    }

    public void setPdr(BigDecimal pdr) {
        this.pdr = pdr;
    }

    public BigDecimal getDyp() {
        return dyp;
    }

    public void setDyp(BigDecimal dyp) {
        this.dyp = dyp;
    }

    public String getWth() {
        return wth;
    }

    public void setWth(String wth) {
        this.wth = wth;
    }
}
