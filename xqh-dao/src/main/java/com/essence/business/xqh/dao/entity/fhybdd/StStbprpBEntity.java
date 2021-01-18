package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "ST_STBPRP_B", schema = "XQH", catalog = "")
public class StStbprpBEntity {
    //测站编码
    @Id
    @Column(name = "STCD")
    private String stcd;
    //测站名字
    @Column(name = "STNM")
    private String stnm;
    //河流名称
    @Column(name = "RVNM")
    private String rvnm;
    //水系名称
    @Column(name = "HNNM")
    private String hnnm;
    //流域名称
    @Column(name = "BSNM")
    private String bsnm;
    //精度
    @Column(name = "LGTD")
    private Double lgtd;
    //维度
    @Column(name = "LTTD")
    private Double lttd;
    //站址
    @Column(name = "STLC")
    private String stlc;
    //行政区区划吗
    @Column(name = "ADDVCD")
    private String addvcd;
    //基面名称
    @Column(name = "DTMNM")
    private String dtmnm;
    //基面高程
    @Column(name = "DTMEL")
    private Double dtmel;
    //基面修正值
    @Column(name = "DTPR")
    private Double dtpr;
    //站类
    @Column(name = "STTP")
    private String sttp;
    //报汛等级
    @Column(name = "FRGRD")
    private String frgrd;
    //建站年月
    @Column(name = "ESSTYM")
    private String esstym;
    //始报年月
    @Column(name = "BGFRYM")
    private String bgfrym;
    //隶属行业单位
    @Column(name = "ATCUNIT")
    private String atcunit;
    //信息管理单位
    @Column(name = "ADMAUTH")
    private String admauth;
    //交换管理单位
    @Column(name = "LOCALITY")
    private String locality;
    //测站岸别
    @Column(name = "STBK")
    private String stbk;
    //测站方位
    @Column(name = "STAZT")
    private String stazt;
    //至河口距离
    @Column(name = "DSTRVM")
    private Double dstrvm;
    //集水面积
    @Column(name = "DRNA")
    private Double drna;
    //拼音码
    @Column(name = "PHCD")
    private String phcd;
    //启用标志
    @Column(name = "USFL")
    private String usfl;
    //备注
    @Column(name = "COMMENTS")
    private String comments;
    @Column(name = "MODITIME")
    private Timestamp moditime;


    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public String getStnm() {
        return stnm;
    }

    public void setStnm(String stnm) {
        this.stnm = stnm;
    }

    public String getRvnm() {
        return rvnm;
    }

    public void setRvnm(String rvnm) {
        this.rvnm = rvnm;
    }

    public String getHnnm() {
        return hnnm;
    }

    public void setHnnm(String hnnm) {
        this.hnnm = hnnm;
    }

    public String getBsnm() {
        return bsnm;
    }

    public void setBsnm(String bsnm) {
        this.bsnm = bsnm;
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

    public String getStlc() {
        return stlc;
    }

    public void setStlc(String stlc) {
        this.stlc = stlc;
    }

    public String getAddvcd() {
        return addvcd;
    }

    public void setAddvcd(String addvcd) {
        this.addvcd = addvcd;
    }

    public String getDtmnm() {
        return dtmnm;
    }

    public void setDtmnm(String dtmnm) {
        this.dtmnm = dtmnm;
    }

    public Double getDtmel() {
        return dtmel;
    }

    public void setDtmel(Double dtmel) {
        this.dtmel = dtmel;
    }

    public Double getDtpr() {
        return dtpr;
    }

    public void setDtpr(Double dtpr) {
        this.dtpr = dtpr;
    }

    public String getSttp() {
        return sttp;
    }

    public void setSttp(String sttp) {
        this.sttp = sttp;
    }

    public String getFrgrd() {
        return frgrd;
    }

    public void setFrgrd(String frgrd) {
        this.frgrd = frgrd;
    }

    public String getEsstym() {
        return esstym;
    }

    public void setEsstym(String esstym) {
        this.esstym = esstym;
    }

    public String getBgfrym() {
        return bgfrym;
    }

    public void setBgfrym(String bgfrym) {
        this.bgfrym = bgfrym;
    }

    public String getAtcunit() {
        return atcunit;
    }

    public void setAtcunit(String atcunit) {
        this.atcunit = atcunit;
    }

    public String getAdmauth() {
        return admauth;
    }

    public void setAdmauth(String admauth) {
        this.admauth = admauth;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getStbk() {
        return stbk;
    }

    public void setStbk(String stbk) {
        this.stbk = stbk;
    }

    public String getStazt() {
        return stazt;
    }

    public void setStazt(String stazt) {
        this.stazt = stazt;
    }

    public Double getDstrvm() {
        return dstrvm;
    }

    public void setDstrvm(Double dstrvm) {
        this.dstrvm = dstrvm;
    }

    public Double getDrna() {
        return drna;
    }

    public void setDrna(Double drna) {
        this.drna = drna;
    }

    public String getPhcd() {
        return phcd;
    }

    public void setPhcd(String phcd) {
        this.phcd = phcd;
    }

    public String getUsfl() {
        return usfl;
    }

    public void setUsfl(String usfl) {
        this.usfl = usfl;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Timestamp getModitime() {
        return moditime;
    }

    public void setModitime(Timestamp moditime) {
        this.moditime = moditime;
    }
}
