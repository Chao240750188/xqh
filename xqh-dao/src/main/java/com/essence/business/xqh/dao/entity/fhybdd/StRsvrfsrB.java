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
@Table(name = "ST_RSVRFSR_B", schema = "XQH", catalog = "")
public class StRsvrfsrB {
    //测站编码
    @Id
    @Column(name = "STCD")
    private String stcd;

    //对应汛限水位开始启用的日期 4 位月日mmdd
    @Column(name = "BGMD")
    private String bgmd;

    //结束月日
    @Column(name = "EDMD")
    private String edmd;

    //汛限水位
    @Column(name = "FSLTDZ")
    private Double fsltdz;

    //汛限库容
    @Column(name = "FSLTDW")
    private Double fsltdw;

    //汛期类别
    @Column(name = "FSTP")
    private String fstp;

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

    public String getBgmd() {
        return bgmd;
    }

    public void setBgmd(String bgmd) {
        this.bgmd = bgmd;
    }

    public String getEdmd() {
        return edmd;
    }

    public void setEdmd(String edmd) {
        this.edmd = edmd;
    }

    public Double getFsltdz() {
        return fsltdz;
    }

    public void setFsltdz(Double fsltdz) {
        this.fsltdz = fsltdz;
    }

    public Double getFsltdw() {
        return fsltdw;
    }

    public void setFsltdw(Double fsltdw) {
        this.fsltdw = fsltdw;
    }

    public String getFstp() {
        return fstp;
    }

    public void setFstp(String fstp) {
        this.fstp = fstp;
    }

    public Date getModiTime() {
        return modiTime;
    }

    public void setModiTime(Date modiTime) {
        this.modiTime = modiTime;
    }
}
