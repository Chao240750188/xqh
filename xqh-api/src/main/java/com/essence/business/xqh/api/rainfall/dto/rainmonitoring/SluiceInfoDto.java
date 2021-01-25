package com.essence.business.xqh.api.rainfall.dto.rainmonitoring;

/**
 * @author fengpp
 * 2021/1/25 18:20
 */
public class SluiceInfoDto {
    private String stnm;//站名
    private String stcd;//站编码
    private String sttp;//站类
    private String rvnm;//河流名称
    private String admauth;//管理单位
    private String stlc;//站址
    private String esstym;//建站年月
    private String ldkel;//左堤高程
    private String rdkel;//右堤高程
    private String wrz;//警戒水位
    private String grz;//保证水位
    private String wrq;//警戒流量
    private String grq;//保证流量
    private Double lgtd;//经度
    private Double lttd;//纬度

    public String getStnm() {
        return stnm;
    }

    public void setStnm(String stnm) {
        this.stnm = stnm;
    }

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public String getSttp() {
        return sttp;
    }

    public void setSttp(String sttp) {
        this.sttp = sttp;
    }

    public String getRvnm() {
        return rvnm;
    }

    public void setRvnm(String rvnm) {
        this.rvnm = rvnm;
    }

    public String getAdmauth() {
        return admauth;
    }

    public void setAdmauth(String admauth) {
        this.admauth = admauth;
    }

    public String getStlc() {
        return stlc;
    }

    public void setStlc(String stlc) {
        this.stlc = stlc;
    }

    public String getEsstym() {
        return esstym;
    }

    public void setEsstym(String esstym) {
        this.esstym = esstym;
    }

    public String getLdkel() {
        return ldkel;
    }

    public void setLdkel(String ldkel) {
        this.ldkel = ldkel;
    }

    public String getRdkel() {
        return rdkel;
    }

    public void setRdkel(String rdkel) {
        this.rdkel = rdkel;
    }

    public String getWrz() {
        return wrz;
    }

    public void setWrz(String wrz) {
        this.wrz = wrz;
    }

    public String getGrz() {
        return grz;
    }

    public void setGrz(String grz) {
        this.grz = grz;
    }

    public String getWrq() {
        return wrq;
    }

    public void setWrq(String wrq) {
        this.wrq = wrq;
    }

    public String getGrq() {
        return grq;
    }

    public void setGrq(String grq) {
        this.grq = grq;
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
}
