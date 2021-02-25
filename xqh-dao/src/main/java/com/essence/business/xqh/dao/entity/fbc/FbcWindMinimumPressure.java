package com.essence.business.xqh.dao.entity.fbc;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "FBC_WIND_MINIMUM_PRESSURE", schema = "XQH", catalog = "")
public class FbcWindMinimumPressure {
    @Id
    @Column(name = "C_ID")
    private String id;
    //方案编码
    @Column(name = "N_PLANID")
    private String nPlanid;
    //流量
    @Column(name = "N_PRESSURE")
    private Double pressure;
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

    public Double getPressure() {
        return pressure;
    }

    public void setPressure(Double pressure) {
        this.pressure = pressure;
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
