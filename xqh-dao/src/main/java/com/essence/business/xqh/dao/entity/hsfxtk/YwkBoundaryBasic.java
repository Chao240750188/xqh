package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "YWK_BOUNDARY_BASIC")
public class YwkBoundaryBasic {
    @Id
    @Column(name = "STCD")
    private String stcd;
    @Column(name = "MILEAGE")
    private Double mileage;
    @Column(name = "BOUNDARYNM")
    private String boundarynm;
    @Column(name = "BOUNDARY_TYPE")
    private String boundaryType;
    @Column(name = "BOUNDARY_DATA_TYPE")
    private String boundaryDataType;
    @Column(name = "LGTD")
    private Double lgtd;
    @Column(name = "LTTD")
    private Double lttd;
    @Column(name = "BRANCHNM")
    private String branchnm;
    @Column(name = "RVNM")
    private String rvnm;
    @Column(name = "REF_RCS_ID")
    private String rcsId;

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

    public String getRcsId() {
        return rcsId;
    }

    public void setRcsId(String rcsId) {
        this.rcsId = rcsId;
    }
}
