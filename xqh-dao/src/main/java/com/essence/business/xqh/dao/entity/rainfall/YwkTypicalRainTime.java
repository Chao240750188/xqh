package com.essence.business.xqh.dao.entity.rainfall;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "YWK_TYPICAL_RAIN_TIME", schema = "XQH", catalog = "")
public class YwkTypicalRainTime{

    @Id
    @Column(name = "C_ID")
    private String cId;

    @Column(name = "C_RAIN_NAME")
    private String cRainName;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "D_START_TIME")
    private Date dStartTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "D_END_TIME")
    private Date dEndTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "D_UPDATE_TIME")
    private Date dUpdateTime;


    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getcRainName() {
        return cRainName;
    }

    public void setcRainName(String cRainName) {
        this.cRainName = cRainName;
    }

    public Date getdStartTime() {
        return dStartTime;
    }

    public void setdStartTime(Date dStartTime) {
        this.dStartTime = dStartTime;
    }

    public Date getdEndTime() {
        return dEndTime;
    }

    public void setdEndTime(Date dEndTime) {
        this.dEndTime = dEndTime;
    }

    public Date getdUpdateTime() {
        return dUpdateTime;
    }

    public void setdUpdateTime(Date dUpdateTime) {
        this.dUpdateTime = dUpdateTime;
    }
}
