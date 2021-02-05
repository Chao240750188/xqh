package com.essence.business.xqh.api.waterandrain.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fengpp
 * 2021/2/5 15:12
 */
public class RainfallTimeTendencyDto {
    private Date tm;
    private String showTm;
    private BigDecimal rainfall;
    private Integer step;

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

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }
}
