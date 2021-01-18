package com.essence.business.xqh.api.floodScheduling.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 出库规则表实体类
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/04/07
 */
public class SkddHifZvarlBDto implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 水库ID
     */
    private String resCode;

    /**
     * 点序号，库水位和蓄水量对应点在该条库容曲线中的顺序号，从 1 开始，按顺序依次递增。
     */
    private Integer ptNo;

    /**
     * 库水位
     */
    private BigDecimal rz;

    /**
     * 蓄水量(库容量)
     */
    private BigDecimal w;

    /**
     * 溢洪道出库流量
     */
    private BigDecimal spdsq;

    /**
     * 输水洞出库流量
     */
    private BigDecimal shq;

    /**
     * 水面面积（平方公里）
     */
    private Integer wsfa;

    /**
     * 时间戳
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modiTime;

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

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

    public BigDecimal getW() {
        return w;
    }

    public void setW(BigDecimal w) {
        this.w = w;
    }

    public BigDecimal getSpdsq() {
        return spdsq;
    }

    public void setSpdsq(BigDecimal spdsq) {
        this.spdsq = spdsq;
    }

    public BigDecimal getShq() {
        return shq;
    }

    public void setShq(BigDecimal shq) {
        this.shq = shq;
    }

    public Integer getWsfa() {
        return wsfa;
    }

    public void setWsfa(Integer wsfa) {
        this.wsfa = wsfa;
    }

    public LocalDateTime getModiTime() {
        return modiTime;
    }

    public void setModiTime(LocalDateTime modiTime) {
        this.modiTime = modiTime;
    }
}
