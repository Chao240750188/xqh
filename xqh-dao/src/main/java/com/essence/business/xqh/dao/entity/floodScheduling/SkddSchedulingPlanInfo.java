package com.essence.business.xqh.dao.entity.floodScheduling;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 调度方案信息表实体类
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/03/31
 */
@Entity
@Table(name = "SKDD_SCHEDULING_PLAN_INFO",schema = "XQH")
public class SkddSchedulingPlanInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 方案ID
     */
    @Id
    @Column(name = "PLAN_ID")
    private String planId;

    /**
     * 水库ID
     */
    @Column(name = "RES_CODE")
    private String resCode;

    /**
     * 起调水位
     */
    @Column(name = "WL")
    private BigDecimal wl;

    /**
     * 计算开始时间
     */
    @Column(name = "START_TIME")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 计算结束时间
     */
    @Column(name = "END_TIME")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 方案计算状态信息
     */
    @Column(name = "PLAN_STATUS_INFO")
    private String planStatusInfo;

    /**
     * 方案计算状态（0=未开始计算，1=计算中，2=计算成功，-1=计算失败）
     */
    @Column(name = "PLAN_STATUS")
    private Integer planStatus;

    /**
     * 出库规则（1 默认规则 ，2 自定义规则）
     */
    @Column(name = "OUT_TYPE")
    private Integer outType;

    /**
     * 调度人名称
     */
    @Column(name = "CREATE_USER")
    private String createUser;

    /**
     * 备注
     */
    @Column(name = "NT")
    private String nt;

    /**
     * 是否为成为了历史调度方案 0:否 1:是
     */
    @Column(name = "HISTORY_STATUS")
    private Integer historyStatus;

    /**
     * 时间戳
     */
    @Column(name = "MODI_TIME")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modiTime;

    /**
     * 水库名称
     */
    @Transient
    private String resName;

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public BigDecimal getWl() {
        return wl;
    }

    public void setWl(BigDecimal wl) {
        this.wl = wl;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getPlanStatusInfo() {
        return planStatusInfo;
    }

    public void setPlanStatusInfo(String planStatusInfo) {
        this.planStatusInfo = planStatusInfo;
    }

    public Integer getPlanStatus() {
        return planStatus;
    }

    public void setPlanStatus(Integer planStatus) {
        this.planStatus = planStatus;
    }

    public Integer getOutType() {
        return outType;
    }

    public void setOutType(Integer outType) {
        this.outType = outType;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getNt() {
        return nt;
    }

    public void setNt(String nt) {
        this.nt = nt;
    }

    public Integer getHistoryStatus() {
        return historyStatus;
    }

    public void setHistoryStatus(Integer historyStatus) {
        this.historyStatus = historyStatus;
    }

    public LocalDateTime getModiTime() {
        return modiTime;
    }

    public void setModiTime(LocalDateTime modiTime) {
        this.modiTime = modiTime;
    }

    public String getResName() {
        return resName;
    }

    public void setResName(String resName) {
        this.resName = resName;
    }
}
