package com.essence.business.xqh.api.floodScheduling.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 水库水情表
 * LiuGt add at 2020-07-22
 */
public class SkddStRsvrRDto implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 测站ID
     */
    private String stcd;

    /**
     * 时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime tm;

    /**
     * 库上水位(单位：m)
     */

    private BigDecimal rz;

    /**
     * 入库流量(单位：m3/s)
     */

    private BigDecimal inq;

    /**
     * 蓄水量(百万立方米)
     */

    private BigDecimal w;

    /**
     * 库下水位(单位：m)
     */

    private BigDecimal blrz;

    /**
     * 出库流量(单位：m3/s)
     */

    private BigDecimal otq;

    /**
     * 库水特征码
     */

    private String rwchrcd;

    /**
     * 库水水势
     */

    private String rwptn;

    /**
     * 入流时段长
     */

    private BigDecimal inqdr;

    /**
     * 测流方法
     */

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
