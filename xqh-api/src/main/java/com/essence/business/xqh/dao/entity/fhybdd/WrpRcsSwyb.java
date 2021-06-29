package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "WRP_RCS_SWYB", schema = "XQH", catalog = "")
public class WrpRcsSwyb {
    private String rvcrcrsccd;
    private String rvcrcrscnm;
    private String rvcd;
    private String rvnm;

    @Id
    @Column(name = "RVCRCRSCCD")
    public String getRvcrcrsccd() {
        return rvcrcrsccd;
    }

    public void setRvcrcrsccd(String rvcrcrsccd) {
        this.rvcrcrsccd = rvcrcrsccd;
    }

    @Basic
    @Column(name = "RVCRCRSCNM")
    public String getRvcrcrscnm() {
        return rvcrcrscnm;
    }

    public void setRvcrcrscnm(String rvcrcrscnm) {
        this.rvcrcrscnm = rvcrcrscnm;
    }

    @Basic
    @Column(name = "RVCD")
    public String getRvcd() {
        return rvcd;
    }

    public void setRvcd(String rvcd) {
        this.rvcd = rvcd;
    }

    @Basic
    @Column(name = "RVNM")
    public String getRvnm() {
        return rvnm;
    }

    public void setRvnm(String rvnm) {
        this.rvnm = rvnm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WrpRcsSwyb that = (WrpRcsSwyb) o;
        return Objects.equals(rvcrcrsccd, that.rvcrcrsccd) &&
                Objects.equals(rvcrcrscnm, that.rvcrcrscnm) &&
                Objects.equals(rvcd, that.rvcd) &&
                Objects.equals(rvnm, that.rvnm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rvcrcrsccd, rvcrcrscnm, rvcd, rvnm);
    }
}
