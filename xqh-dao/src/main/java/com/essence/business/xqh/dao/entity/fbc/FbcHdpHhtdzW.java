package com.essence.business.xqh.dao.entity.fbc;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "FBC_HDP_HHTDZ_W", schema = "XQH", catalog = "")
public class FbcHdpHhtdzW {
    @Id
    @Column(name = "C_ID")
    private String id;
    //方案编码
    @Column(name = "N_PLANID")
    private String nPlanid;
    //潮位
    @Column(name = "TDZ")
    private Double tdz;
    //相对时间
    @Column(name = "ABSOLUTE_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date absoluteTime;

    //绝对时间
    @Column(name = "RELATIVE_TIME")
    private Long relativeTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getnPlanid() {
        return nPlanid;
    }

    public void setnPlanid(String nPlanid) {
        this.nPlanid = nPlanid;
    }

    public Double getTdz() {
        return tdz;
    }

    public void setTdz(Double tdz) {
        this.tdz = tdz;
    }

    public Date getAbsoluteTime() {
        return absoluteTime;
    }

    public void setAbsoluteTime(Date absoluteTime) {
        this.absoluteTime = absoluteTime;
    }

    public Long getRelativeTime() {
        return relativeTime;
    }

    public void setRelativeTime(Long relativeTime) {
        this.relativeTime = relativeTime;
    }
}
