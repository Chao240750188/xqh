package com.essence.business.xqh.api.waterandrain.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fengpp
 * 2021/2/4 20:25
 */
public class RainfallTendencyDto {
    private Date tm;
    private String showTm;
    private BigDecimal rainfall;

    public Date getTm() {
        return tm;
    }

    public void setTm(Date tm) {
        this.tm = tm;
    }

    public String getShowTm() {
        return showTm;
    }

    public void setShowTm(String showTm) {
        this.showTm = showTm;
    }

    public BigDecimal getRainfall() {
        return rainfall;
    }

    public void setRainfall(BigDecimal rainfall) {
        this.rainfall = rainfall;
    }
}
