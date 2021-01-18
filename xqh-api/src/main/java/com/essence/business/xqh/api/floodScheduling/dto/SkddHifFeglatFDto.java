package com.essence.business.xqh.api.floodScheduling.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 调度预报成果表实体类
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/04/01
 */
public class SkddHifFeglatFDto implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */

    private String id;

    /**
     * 方案ID
     */

    private String planId;

    /**
     * 水库ID
     */

    private String resCode;

    /**
     * 预报单位
     */

    private String unitName;

    /**
     * 依据时间
     */

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fymdh;

    /**
     * 发布时间
     */

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime iymdh;

    /**
     * 发生时间
     */

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime ymdh;

    /**
     * 预报水位
     */

    private BigDecimal z;

    /**
     * 预报蓄水量（万立方米）
     */

    private BigDecimal w;

    /**
     * 入库流量（立方米/秒）
     */

    private BigDecimal inq;

    /**
     * 预报出流（立方米/秒）
     */

    private BigDecimal otq;

    /**
     * 调度用户
     */

    private String createUser;

    /**
     * 备注
     */
    private String nt;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modiTime;

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

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public LocalDateTime getFymdh() {
        return fymdh;
    }

    public void setFymdh(LocalDateTime fymdh) {
        this.fymdh = fymdh;
    }

    public LocalDateTime getIymdh() {
        return iymdh;
    }

    public void setIymdh(LocalDateTime iymdh) {
        this.iymdh = iymdh;
    }

    public LocalDateTime getYmdh() {
        return ymdh;
    }

    public void setYmdh(LocalDateTime ymdh) {
        this.ymdh = ymdh;
    }

    public BigDecimal getZ() {
        return z;
    }

    public void setZ(BigDecimal z) {
        this.z = z;
    }

    public BigDecimal getW() {
        return w;
    }

    public void setW(BigDecimal w) {
        this.w = w;
    }

    public BigDecimal getInq() {
        return inq;
    }

    public void setInq(BigDecimal inq) {
        this.inq = inq;
    }

    public BigDecimal getOtq() {
        return otq;
    }

    public void setOtq(BigDecimal otq) {
        this.otq = otq;
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

    public LocalDateTime getModiTime() {
        return modiTime;
    }

    public void setModiTime(LocalDateTime modiTime) {
        this.modiTime = modiTime;
    }
}
