package com.essence.business.xqh.api.hsfxtk.dto;

import java.io.Serializable;

public class YwkBoundaryBasicDto implements Serializable {
    private String stcd;
    private Double mileage;
    private String boundarynm;
    private String boundaryType;
    private String boundaryDataType;
    private Double lgtd;
    private Double lttd;
    private String branchnm;
    private String rvnm;

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public Double getMileage() {
        return mileage;
    }

    public void setMileage(Double mileage) {
        this.mileage = mileage;
    }

    public String getBoundarynm() {
        return boundarynm;
    }

    public void setBoundarynm(String boundarynm) {
        this.boundarynm = boundarynm;
    }

    public String getBoundaryType() {
        return boundaryType;
    }

    public void setBoundaryType(String boundaryType) {
        this.boundaryType = boundaryType;
    }

    public String getBoundaryDataType() {
        return boundaryDataType;
    }

    public void setBoundaryDataType(String boundaryDataType) {
        this.boundaryDataType = boundaryDataType;
    }

    public Double getLgtd() {
        return lgtd;
    }

    public void setLgtd(Double lgtd) {
        this.lgtd = lgtd;
    }

    public Double getLttd() {
        return lttd;
    }

    public void setLttd(Double lttd) {
        this.lttd = lttd;
    }

    public String getBranchnm() {
        return branchnm;
    }

    public void setBranchnm(String branchnm) {
        this.branchnm = branchnm;
    }

    public String getRvnm() {
        return rvnm;
    }

    public void setRvnm(String rvnm) {
        this.rvnm = rvnm;
    }
}
