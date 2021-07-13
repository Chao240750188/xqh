package com.essence.business.xqh.dao.entity.fbc;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.Objects;

/**
 * 风暴潮-最短距离
 */
@Entity
@Table(name = "FBC_SHORTEST_DISTANCE")
public class FbcShortestDistance {
    @Id
    @Column(name = "C_ID")
    private String cId;
    @Column(name = "N_PLANID")
    private String nPlanid;
    @Column(name = "SHORTEST_DISTANCE")
    private Double shortestDistance;
    @Column(name = "RELATIVE_TIME")
    private Long relativeTime;
    @Column(name = "ABSOLUTE_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date absoluteTime;

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getnPlanid() {
        return nPlanid;
    }

    public void setnPlanid(String nPlanid) {
        this.nPlanid = nPlanid;
    }

    public Double getShortestDistance() {
        return shortestDistance;
    }

    public void setShortestDistance(Double shortestDistance) {
        this.shortestDistance = shortestDistance;
    }

    public Long getRelativeTime() {
        return relativeTime;
    }

    public void setRelativeTime(Long relativeTime) {
        this.relativeTime = relativeTime;
    }

    public Date getAbsoluteTime() {
        return absoluteTime;
    }

    public void setAbsoluteTime(Date absoluteTime) {
        this.absoluteTime = absoluteTime;
    }
}
