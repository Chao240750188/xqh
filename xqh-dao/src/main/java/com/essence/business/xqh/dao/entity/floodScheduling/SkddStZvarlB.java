package com.essence.business.xqh.dao.entity.floodScheduling;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库容曲线表实体类
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/07/20
 */
@Entity
@IdClass(SkddHifZvarlBKey.class)
@Table(name = "SKDD_ST_ZVARL_B",schema = "XQH")
public class SkddStZvarlB {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 水库ID
     */
    @Id
    @Column(name = "RES_CODE")
    private String resCode;

    /**
     * 点序号，库水位和蓄水量对应点在该条库容曲线中的顺序号，从 1 开始，按顺序依次递增。
     */
    @Id
    @Column(name = "PTNO")
    private Integer ptNo;

    /**
     * 库水位
     */
    @Column(name = "RZ")
    private BigDecimal rz;

    /**
     * 蓄水量(库容量)
     */
    @Column(name = "W")
    private BigDecimal w;

    /**
     * 水面面积（平方公里）
     */
    @Column(name = "WSFA")
    private Integer wsfa;

    /**
     * 时间戳
     */
    @Column(name = "MODITIME")
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
