package com.essence.business.xqh.dao.entity.floodForecast;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * LiuGt add at 2020-03-16
 * 蒸发量小时数据表实体类
 */
@Entity
@IdClass(SqybHriEvHrKey.class)
@Table(name = "SQYB_HRI_EV_HR")
public class SqybHriEvHr implements Serializable {

    private static final long serialVersionUID = 39;

    /***/
    @Id
    @Column(name = "STCD")
    private String stcd;

    /***/
    @Id
    @Column(name = "TM")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime tm;

    /**
     * 蒸发量
     */
    @Column(name = "DRE")
    private Double dre;

    /**
     * 蒸发器类型
     */
    @Column(name = "EPTP")
    private String eptp;

    /**
     * 日蒸发量
     */
    @Column(name = "DYE")
    private Double dye;

    /**
     * 天气状况
     */
    @Column(name = "WTH")
    private String wth;

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public LocalDateTime getTm() {
        return tm;
    }

    public void setTm(LocalDateTime tm) {
        this.tm = tm;
    }

    public Double getDre() {
        return dre;
    }

    public void setDre(Double dre) {
        this.dre = dre;
    }

    public String getEptp() {
        return eptp;
    }

    public void setEptp(String eptp) {
        this.eptp = eptp;
    }

    public Double getDye() {
        return dye;
    }

    public void setDye(Double dye) {
        this.dye = dye;
    }

    public String getWth() {
        return wth;
    }

    public void setWth(String wth) {
        this.wth = wth;
    }
}
