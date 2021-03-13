package com.essence.business.xqh.dao.entity.fhybdd;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "YWK_PLAN_CALIBRATION_FLOW", schema = "XQH", catalog = "")
public class YwkPlanCalibrationFlow {
    @Id
    @Column(name = "C_ID")
    private String cId;
    @Column(name = "CALIBRATION_ID")
    private String calibrationId;
    @Column(name = "UP_FLOW")
    private Double upFlow;
    @Column(name = "DOWN_FLOW")
    private Double downFlow;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "CREATE_TIME")
    private Date createTime;

    public String getcId() {
        return cId;
    }

    public void setcId(String cId) {
        this.cId = cId;
    }

    public String getCalibrationId() {
        return calibrationId;
    }

    public void setCalibrationId(String calibrationId) {
        this.calibrationId = calibrationId;
    }

    public Double getUpFlow() {
        return upFlow;
    }

    public void setUpFlow(Double upFlow) {
        this.upFlow = upFlow;
    }

    public Double getDownFlow() {
        return downFlow;
    }

    public void setDownFlow(Double downFlow) {
        this.downFlow = downFlow;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
