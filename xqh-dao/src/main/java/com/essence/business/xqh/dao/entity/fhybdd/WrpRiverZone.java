package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "WRP_RIVER_ZONE", schema = "XQH", catalog = "")
public class WrpRiverZone {
    @Id
    @Column(name = "C_ID")
    private String cId;
    @Column(name = "RVCD")
    private String rvcd;
    @Column(name = "ZONE_ID")
    private Integer zoneId;
    @Column(name = "ZONE_NAME")
    private String zoneName;


    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getRvcd() {
        return rvcd;
    }

    public void setRvcd(String rvcd) {
        this.rvcd = rvcd;
    }

    public Integer getZoneId() {
        return zoneId;
    }

    public void setZoneId(Integer zoneId) {
        this.zoneId = zoneId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }
}
