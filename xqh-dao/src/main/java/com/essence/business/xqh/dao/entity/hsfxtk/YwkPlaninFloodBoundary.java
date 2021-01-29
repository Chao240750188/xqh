package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "YWK_PLANIN_FLOOD_BOUNDARY", schema = "XQH", catalog = "")
public class YwkPlaninFloodBoundary {
    @Id
    @Column(name = "C_ID")
    private String id;
    @Column(name = "N_PLANID")
    private String planId;

    @Column(name = "STCD")
    private String stcd;

    @Column(name = "ABSOLUTE_TIME")
    private Date absoluteTime;

    @Column(name = "RELATIVE_TIME")
    private Long relativeTime;

    @Column(name = "Z")
    private Double z;

    @Column(name = "Q")
    private Double q;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
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

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    public Double getQ() {
        return q;
    }

    public void setQ(Double q) {
        this.q = q;
    }
}
