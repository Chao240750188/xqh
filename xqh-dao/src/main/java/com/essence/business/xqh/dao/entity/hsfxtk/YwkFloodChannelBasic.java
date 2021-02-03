package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "YWK_FLOOD_CHANNEL_BASIC", schema = "XQH", catalog = "")
public class YwkFloodChannelBasic {
    @Id
    @Column(name = "FLOOD_CHANNEL_ID")
    private String floodChannelId;
    @Column(name = "BREAK_ID")
    private String breakId;
    @Column(name = "OUTFLOW_AND_INFLOW_TYPE")
    private String outflowAndInflowType;
    @Column(name = "MILEAGE")
    private Double mileage;

    public String getBreakId() {
        return breakId;
    }

    public void setBreakId(String breakId) {
        this.breakId = breakId;
    }

    public String getFloodChannelId() {
        return floodChannelId;
    }

    public void setFloodChannelId(String floodChannelId) {
        this.floodChannelId = floodChannelId;
    }

    public String getOutflowAndInflowType() {
        return outflowAndInflowType;
    }

    public void setOutflowAndInflowType(String outflowAndInflowType) {
        this.outflowAndInflowType = outflowAndInflowType;
    }

    public Double getMileage() {
        return mileage;
    }

    public void setMileage(Double mileage) {
        this.mileage = mileage;
    }


}
