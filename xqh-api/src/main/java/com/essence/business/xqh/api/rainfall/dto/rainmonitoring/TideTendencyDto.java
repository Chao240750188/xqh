package com.essence.business.xqh.api.rainfall.dto.rainmonitoring;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fengpp
 * 2021/1/25 18:57
 */
public class TideTendencyDto {
    private String showTm;
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date tm;
    private String tdz;
    private String tdptn;
    private BigDecimal warning;
    private BigDecimal wrz;
    private BigDecimal grz;
    private BigDecimal obhtz;
    private BigDecimal hlz;

    public String getShowTm() {
        return showTm;
    }

    public void setShowTm(String showTm) {
        this.showTm = showTm;
    }

    public Date getTm() {
        return tm;
    }

    public void setTm(Date tm) {
        this.tm = tm;
    }

    public String getTdz() {
        return tdz;
    }

    public void setTdz(String tdz) {
        this.tdz = tdz;
    }

    public String getTdptn() {
        return tdptn;
    }

    public void setTdptn(String tdptn) {
        this.tdptn = tdptn;
    }

    public BigDecimal getWarning() {
        return warning;
    }

    public void setWarning(BigDecimal warning) {
        this.warning = warning;
    }

    public BigDecimal getWrz() {
        return wrz;
    }

    public void setWrz(BigDecimal wrz) {
        this.wrz = wrz;
    }

    public BigDecimal getGrz() {
        return grz;
    }

    public void setGrz(BigDecimal grz) {
        this.grz = grz;
    }

    public BigDecimal getObhtz() {
        return obhtz;
    }

    public void setObhtz(BigDecimal obhtz) {
        this.obhtz = obhtz;
    }

    public BigDecimal getHlz() {
        return hlz;
    }

    public void setHlz(BigDecimal hlz) {
        this.hlz = hlz;
    }
}
