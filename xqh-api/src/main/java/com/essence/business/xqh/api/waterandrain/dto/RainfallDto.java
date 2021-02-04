package com.essence.business.xqh.api.waterandrain.dto;

import java.math.BigDecimal;

/**
 * @author fengpp
 * 2021/2/4 19:10
 */
public class RainfallDto {
    private String stcd;
    private String stnm;
    private BigDecimal lgtd;
    private BigDecimal lttd;
    private BigDecimal rainfall;

    public RainfallDto() {
    }

    public RainfallDto(String stcd, String stnm, BigDecimal lgtd, BigDecimal lttd) {
        this.stcd = stcd;
        this.stnm = stnm;
        this.lgtd = lgtd;
        this.lttd = lttd;
    }

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public String getStnm() {
        return stnm;
    }

    public void setStnm(String stnm) {
        this.stnm = stnm;
    }

    public BigDecimal getLgtd() {
        return lgtd;
    }

    public void setLgtd(BigDecimal lgtd) {
        this.lgtd = lgtd;
    }

    public BigDecimal getLttd() {
        return lttd;
    }

    public void setLttd(BigDecimal lttd) {
        this.lttd = lttd;
    }

    public BigDecimal getRainfall() {
        return rainfall;
    }

    public void setRainfall(BigDecimal rainfall) {
        this.rainfall = rainfall;
    }
}
