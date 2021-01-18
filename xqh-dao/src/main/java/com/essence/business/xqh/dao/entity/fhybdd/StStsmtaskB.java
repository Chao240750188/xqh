package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "ST_STSMTASK_B", schema = "XQH", catalog = "")
public class StStsmtaskB {
    //测站编码
    @Id
    @Column(name = "STCD")
    private String stcd;
    //报送段次：测站每日常规报汛的次数。
    @Column(name = "DFRTMS")
    private Double dfrtms;
    //降水量标志
    @Column(name = "PFL")
    private String pfl;
    //蒸发量标志
    @Column(name = "EFL")
    private String efl;
    //水位标志
    @Column(name = "ZFL")
    private String zfl;
    //流量标志
    @Column(name = "QFL")
    private String qfl;
    //蓄水量标志
    @Column(name = "WFL")
    private String wfl;
    //入库流量标志
    @Column(name = "INQFL")
    private String inqfl;
    //闸门启闭标志
    @Column(name = "DAMFL")
    private String damfl;
    //出库流量标志
    @Column(name = "OTQFL")
    private String otqfl;
    //风浪标志
    @Column(name = "WDWVFL")
    private String wdwvfl;
    //泥沙标志
    @Column(name = "SEDFL")
    private String sedfl;
    //冰情标志
    @Column(name = "ICEFL")
    private String icefl;
    //引水量标志
    @Column(name = "PPFL")
    private String ppfl;
    //排水量标志
    @Column(name = "DRNFL")
    private String drnfl;
    //墒情标志
    @Column(name = "SOILFL")
    private String soilfl;
    //地下水标志
    @Column(name = "GRWFL")
    private String grwfl;
    //旬月统计标志
    @Column(name = "STATFL")
    private String statfl;
    //测站联系人
    @Column(name = "OFFICER")
    private String officer;
    //移动电话号码
    @Column(name = "MPHONE")
    private String mphone;
    //固定电话号码
    @Column(name = "SPHONE")
    private String sphone;
    @Column(name = "MODITIME")
    private Timestamp moditime;


    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }


    public Double getDfrtms() {
        return dfrtms;
    }

    public void setDfrtms(Double dfrtms) {
        this.dfrtms = dfrtms;
    }

    public String getPfl() {
        return pfl;
    }

    public void setPfl(String pfl) {
        this.pfl = pfl;
    }

    public String getEfl() {
        return efl;
    }

    public void setEfl(String efl) {
        this.efl = efl;
    }

    public String getZfl() {
        return zfl;
    }

    public void setZfl(String zfl) {
        this.zfl = zfl;
    }

    public String getQfl() {
        return qfl;
    }

    public void setQfl(String qfl) {
        this.qfl = qfl;
    }

    public String getWfl() {
        return wfl;
    }

    public void setWfl(String wfl) {
        this.wfl = wfl;
    }

    public String getInqfl() {
        return inqfl;
    }

    public void setInqfl(String inqfl) {
        this.inqfl = inqfl;
    }

    public String getDamfl() {
        return damfl;
    }

    public void setDamfl(String damfl) {
        this.damfl = damfl;
    }

    public String getOtqfl() {
        return otqfl;
    }

    public void setOtqfl(String otqfl) {
        this.otqfl = otqfl;
    }

    public String getWdwvfl() {
        return wdwvfl;
    }

    public void setWdwvfl(String wdwvfl) {
        this.wdwvfl = wdwvfl;
    }

    public String getSedfl() {
        return sedfl;
    }

    public void setSedfl(String sedfl) {
        this.sedfl = sedfl;
    }

    public String getIcefl() {
        return icefl;
    }

    public void setIcefl(String icefl) {
        this.icefl = icefl;
    }

    public String getPpfl() {
        return ppfl;
    }

    public void setPpfl(String ppfl) {
        this.ppfl = ppfl;
    }

    public String getDrnfl() {
        return drnfl;
    }

    public void setDrnfl(String drnfl) {
        this.drnfl = drnfl;
    }

    public String getSoilfl() {
        return soilfl;
    }

    public void setSoilfl(String soilfl) {
        this.soilfl = soilfl;
    }

    public String getGrwfl() {
        return grwfl;
    }

    public void setGrwfl(String grwfl) {
        this.grwfl = grwfl;
    }

    public String getStatfl() {
        return statfl;
    }

    public void setStatfl(String statfl) {
        this.statfl = statfl;
    }

    public String getOfficer() {
        return officer;
    }

    public void setOfficer(String officer) {
        this.officer = officer;
    }

    public String getMphone() {
        return mphone;
    }

    public void setMphone(String mphone) {
        this.mphone = mphone;
    }

    public String getSphone() {
        return sphone;
    }

    public void setSphone(String sphone) {
        this.sphone = sphone;
    }

    public Timestamp getModitime() {
        return moditime;
    }

    public void setModitime(Timestamp moditime) {
        this.moditime = moditime;
    }


}
