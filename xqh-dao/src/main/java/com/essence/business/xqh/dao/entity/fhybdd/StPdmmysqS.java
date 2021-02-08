package com.essence.business.xqh.dao.entity.fhybdd;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fengpp
 * 2021/2/4 20:05
 */
@Entity
@Table(name = "ST_PDMMYSQ_S", schema = "XQH", catalog = "")
public class StPdmmysqS {
    @Id
    @Column(name = "STCD")
    private String stcd;//
    @Column(name = "YR")
    private Integer yr;//年份
    @Column(name = "MNTH")
    private Integer mth;//月份
    @Column(name = "PRDTP")
    private Integer prdtp;//旬月标示：用于区分系列的统计时段是全月或上、中、下旬的代码（1=上旬，2=中旬，3=下旬，4=全月）。
    @Column(name = "ACCP")
    private BigDecimal accp;//累计降雨量
    @Column(name = "MODITIME")
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date modiTime;//时间戳

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public Integer getYr() {
        return yr;
    }

    public void setYr(Integer yr) {
        this.yr = yr;
    }

    public Integer getMth() {
        return mth;
    }

    public void setMth(Integer mth) {
        this.mth = mth;
    }

    public Integer getPrdtp() {
        return prdtp;
    }

    public void setPrdtp(Integer prdtp) {
        this.prdtp = prdtp;
    }

    public BigDecimal getAccp() {
        return accp;
    }

    public void setAccp(BigDecimal accp) {
        this.accp = accp;
    }

    public Date getModiTime() {
        return modiTime;
    }

    public void setModiTime(Date modiTime) {
        this.modiTime = modiTime;
    }
}
