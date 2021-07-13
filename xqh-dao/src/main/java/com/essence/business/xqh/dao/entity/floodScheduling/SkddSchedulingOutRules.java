package com.essence.business.xqh.dao.entity.floodScheduling;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 调度方案调度规则数据表实体类
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/04/09
 */
@Entity
@Table(name = "SKDD_SCHEDULING_OUT_RULES")
public class SkddSchedulingOutRules implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    @Column(name = "ID")
    private String id;

    /**
     * 方案ID
     */
    @Column(name = "PLAN_ID")
    private String planId;

    /**
     * 库水位
     */
    @Column(name = "RZ")
    private BigDecimal rz;

    /**
     * 点序号，库水位和蓄水量对应点在该条库容曲线中的顺序号，从 1 开始，按顺序依次递增。
     */
    @Column(name = "PTNO")
    private Integer ptNo;

    /**
     * 出库流量
     */
    @Column(name = "OTQ")
    private BigDecimal otq;

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

    public BigDecimal getRz() {
        return rz;
    }

    public void setRz(BigDecimal rz) {
        this.rz = rz;
    }

    public Integer getPtNo() {
        return ptNo;
    }

    public void setPtNo(Integer ptNo) {
        this.ptNo = ptNo;
    }

    public BigDecimal getOtq() {
        return otq;
    }

    public void setOtq(BigDecimal otq) {
        this.otq = otq;
    }
}
