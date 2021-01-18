package com.essence.business.xqh.api.floodScheduling.dto;

import java.math.BigDecimal;

/**
 * 水库默认出库规则实体类
 * LiuGt add at 2020-04-08
 */
public class ResZvarlViewDto {

    /**
     * 点序号，库水位和蓄水量对应点在该条库容曲线中的顺序号，从 1 开始，按顺序依次递增。
     */
    private Integer ptNo;

    /**
     * 库水位
     */
    private BigDecimal rz;

    /**
     * 出库流量（立方米/秒）
     */
    private BigDecimal otq;

    public Integer getPtNo() {
        return ptNo;
    }

    public void setPtNo(Integer ptNo) {
        this.ptNo = ptNo;
    }

    public BigDecimal getRz() {
        return rz;
    }

    public void setRz(BigDecimal rz) {
        this.rz = rz;
    }

    public BigDecimal getOtq() {
        return otq;
    }

    public void setOtq(BigDecimal otq) {
        this.otq = otq;
    }
}
