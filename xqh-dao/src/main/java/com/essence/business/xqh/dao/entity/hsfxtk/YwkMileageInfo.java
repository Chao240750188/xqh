package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "YWK_MILEAGE_INFO")
public class YwkMileageInfo {

    @Id
    @Column(name = "MILEAGE")
    private Double mileage;

    @Column(name = "LGTD")
    private Double lgtd;

    @Column(name = "LTTD")
    private Double lttd;

    @Column(name = "LEFT_BANK_ELEVATION")
    private Double leftBankElevation;

    @Column(name = "RIGHT_BANK_ELEVATION")
    private Double rightBankElevation;

    @Column(name = "RIVER_BOTTOM_ELEVATION")
    private Double riverBottomElevation;

    public Double getMileage() {
        return mileage;
    }

    public void setMileage(Double mileage) {
        this.mileage = mileage;
    }

    public Double getLgtd() {
        return lgtd;
    }

    public void setLgtd(Double lgtd) {
        this.lgtd = lgtd;
    }

    public Double getLttd() {
        return lttd;
    }

    public void setLttd(Double lttd) {
        this.lttd = lttd;
    }

    public Double getLeftBankElevation() {
        return leftBankElevation;
    }

    public void setLeftBankElevation(Double leftBankElevation) {
        this.leftBankElevation = leftBankElevation;
    }

    public Double getRightBankElevation() {
        return rightBankElevation;
    }

    public void setRightBankElevation(Double rightBankElevation) {
        this.rightBankElevation = rightBankElevation;
    }

    public Double getRiverBottomElevation() {
        return riverBottomElevation;
    }

    public void setRiverBottomElevation(Double riverBottomElevation) {
        this.riverBottomElevation = riverBottomElevation;
    }
}
