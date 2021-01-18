package com.essence.business.xqh.dao.entity.floodForecast;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 蒸发量-输入模型参数实体
 * @author LiuGt
 *
 * 2020年03月12日 下午4:37:16
 */
@Entity
@Table(name = "SQYB_MODEL_INPUT_EVAPORATION",schema = "XQH")
public class SqybModelInputEvaporation implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID")
    private String id; //输入蒸发量条件id

    @Column(name = "PLAN_ID")
    private String  planId; //方案id

    @Column(name = "STCD")
    private String  stcd; //测站id

    @Column(name = "E")
    private Double  e; //蒸发量

    @Column(name = "TM")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime tm; //蒸发时段

    @Column(name = "MODI_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime  modiTime; //修改时间

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public Double getE() {
        return e;
    }

    public void setE(Double e) {
        this.e = e;
    }

    public LocalDateTime getTm() {
        return tm;
    }

    public void setTm(LocalDateTime tm) {
        this.tm = tm;
    }

    public LocalDateTime getModiTime() {
        return modiTime;
    }

    public void setModiTime(LocalDateTime modiTime) {
        this.modiTime = modiTime;
    }
}
