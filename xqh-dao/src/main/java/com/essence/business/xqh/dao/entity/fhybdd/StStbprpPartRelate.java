package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 测站分区关联表
 */
@Entity
@Table(name = "ST_STBPRP_PART_RELATE")
public class StStbprpPartRelate {
    //测站编码
    @Id
    @Column(name = "C_ID")
    private String id;
    //测站编码
    @Column(name = "C_STCD")
    private String stcd;
    //分区编码
    @Column(name = "C_PART")
    private String part;
    //测站名称
    @Column(name = "C_STNM")
    private String stnm;
    //分区名称
    @Column(name = "C_PART_NM")
    private String partNm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getStnm() {
        return stnm;
    }

    public void setStnm(String stnm) {
        this.stnm = stnm;
    }

    public String getPartNm() {
        return partNm;
    }

    public void setPartNm(String partNm) {
        this.partNm = partNm;
    }
}
