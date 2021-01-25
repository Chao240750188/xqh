package com.essence.business.xqh.api.rainfall.dto.rainmonitoring;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fengpp
 * 2021/1/25 18:34
 */
public class SluiceTendencyDto {
    private String showTm;
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date tm;
    private String tgtq;
    private String upz;
    private String dwz;
    private String supwptn;
    private BigDecimal wrz;
    private BigDecimal warning;

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

    public String getTgtq() {
        return tgtq;
    }

    public void setTgtq(String tgtq) {
        this.tgtq = tgtq;
    }

    public String getUpz() {
        return upz;
    }

    public void setUpz(String upz) {
        this.upz = upz;
    }

    public String getDwz() {
        return dwz;
    }

    public void setDwz(String dwz) {
        this.dwz = dwz;
    }

    public String getSupwptn() {
        return supwptn;
    }

    public void setSupwptn(String supwptn) {
        this.supwptn = supwptn;
    }

    public BigDecimal getWrz() {
        return wrz;
    }

    public void setWrz(BigDecimal wrz) {
        this.wrz = wrz;
    }

    public BigDecimal getWarning() {
        return warning;
    }

    public void setWarning(BigDecimal warning) {
        this.warning = warning;
    }
}
