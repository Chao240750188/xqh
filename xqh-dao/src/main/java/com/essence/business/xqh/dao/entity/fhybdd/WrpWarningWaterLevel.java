package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "WRP_WARNING_WATER_LEVEL", schema = "XQH", catalog = "")
public class WrpWarningWaterLevel {

    @Id
    @Column(name = "C_ID")
    private String cId;
    @Column(name = "RCS_ID")
    private String rcsId;
    @Column(name = "WARNING_WATER_LEVEL")
    private Double warningWaterLevel;

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getRcsId() {
        return rcsId;
    }

    public void setRcsId(String rcsId) {
        this.rcsId = rcsId;
    }

    public Double getWarningWaterLevel() {
        return warningWaterLevel;
    }

    public void setWarningWaterLevel(Double warningWaterLevel) {
        this.warningWaterLevel = warningWaterLevel;
    }
}
