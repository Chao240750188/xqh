package com.essence.business.xqh.dao.entity.tuoying;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/***
* 水库水情表
 * @Author huangxiaoli
 * @Description
 * @Date 16:34 2020/12/31
 * @Param
 * @return
 **/
@Entity
@IdClass(TuoyingStRsvrRKey1.class)
@Table(name = "ST_RSVR_R")
public class TuoyingStRsvrR1 implements Serializable {
    /***/
    @Id
    @Column(name = "STCD")
    private String stcd;//测站编码

    @Id
    @Column(name = "TM")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date tm;//时间

    @Column(name = "RZ")
    private Double rz;//库上水位(单位：m)

    @Column(name = "INQ")
    private Double inq;//入库流量(单位：m3/s)

    @Column(name = "W")
    private Double w;//蓄水量(百万立方米)

    @Column(name = "BLRZ")
    private Double blrz;//库下水位(单位：m)

    @Column(name = "OTQ")
    private Double otq;//出库流量(单位：m3/s)

    @Column(name = "RWCHRCD")
    private String rwchrcd;//库水特征码

    @Column(name = "RWPTN")
    private String rwptn;//库水水势

    @Column(name = "INQDR")
    private Double inqdr;//入流时段长

    @Column(name = "MSQMT")
    private String msqmt;//测流方法

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

    public Double getRz() {
        return rz;
    }

    public void setRz(Double rz) {
        this.rz = rz;
    }

    public Double getInq() {
        return inq;
    }

    public void setInq(Double inq) {
        this.inq = inq;
    }

    public Double getW() {
        return w;
    }

    public void setW(Double w) {
        this.w = w;
    }

    public Double getBlrz() {
        return blrz;
    }

    public void setBlrz(Double blrz) {
        this.blrz = blrz;
    }

    public Double getOtq() {
        return otq;
    }

    public void setOtq(Double otq) {
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

    public Double getInqdr() {
        return inqdr;
    }

    public void setInqdr(Double inqdr) {
        this.inqdr = inqdr;
    }

    public String getMsqmt() {
        return msqmt;
    }

    public void setMsqmt(String msqmt) {
        this.msqmt = msqmt;
    }
}
