package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "YWK_FLOOD_CHANNEL_FLOW")
public class YwkFloodChannelFlow {
    @Id
    @Column(name = "C_ID")
    private String cId;
    @Column(name = "FLOOD_CHANNEL_ID")
    private String floodChannelId;
    @Column(name = "Q")
    private Double q;
    @Column(name = "RELATIVE_TIME")
    private Long relativeTime;
    @Column(name = "ABSOLUTE_TIME")
    private Timestamp absoluteTime;

    public String getFloodChannelId() {
        return floodChannelId;
    }

    public void setFloodChannelId(String floodChannelId) {
        this.floodChannelId = floodChannelId;
    }

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public Double getQ() {
        return q;
    }

    public void setQ(Double q) {
        this.q = q;
    }

    public Long getRelativeTime() {
        return relativeTime;
    }

    public void setRelativeTime(Long relativeTime) {
        this.relativeTime = relativeTime;
    }

    public Timestamp getAbsoluteTime() {
        return absoluteTime;
    }

    public void setAbsoluteTime(Timestamp absoluteTime) {
        this.absoluteTime = absoluteTime;
    }
}
