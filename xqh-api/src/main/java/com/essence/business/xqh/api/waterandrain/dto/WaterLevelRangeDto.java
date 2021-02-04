package com.essence.business.xqh.api.waterandrain.dto;

import java.util.List;

/**
 * @author fengpp
 * 2021/1/30 19:25
 */
public class WaterLevelRangeDto {
    private List<WaterLevelDto> greaterThanOne;//大于1
    private List<WaterLevelDto> greaterThanZeroPointFive;//0.5~1
    private List<WaterLevelDto> greaterThanZero;//0~0.5
    private List<WaterLevelDto> noChange;//无变化
    private List<WaterLevelDto> lessThanZero;//-0.5~0
    private List<WaterLevelDto> lessThanMinusZeroPointFive;//-1~-0.5
    private List<WaterLevelDto> lessThanMinusOne;//小于-1

    public List<WaterLevelDto> getGreaterThanOne() {
        return greaterThanOne;
    }

    public void setGreaterThanOne(List<WaterLevelDto> greaterThanOne) {
        this.greaterThanOne = greaterThanOne;
    }

    public List<WaterLevelDto> getGreaterThanZeroPointFive() {
        return greaterThanZeroPointFive;
    }

    public void setGreaterThanZeroPointFive(List<WaterLevelDto> greaterThanZeroPointFive) {
        this.greaterThanZeroPointFive = greaterThanZeroPointFive;
    }

    public List<WaterLevelDto> getGreaterThanZero() {
        return greaterThanZero;
    }

    public void setGreaterThanZero(List<WaterLevelDto> greaterThanZero) {
        this.greaterThanZero = greaterThanZero;
    }

    public List<WaterLevelDto> getNoChange() {
        return noChange;
    }

    public void setNoChange(List<WaterLevelDto> noChange) {
        this.noChange = noChange;
    }

    public List<WaterLevelDto> getLessThanZero() {
        return lessThanZero;
    }

    public void setLessThanZero(List<WaterLevelDto> lessThanZero) {
        this.lessThanZero = lessThanZero;
    }

    public List<WaterLevelDto> getLessThanMinusZeroPointFive() {
        return lessThanMinusZeroPointFive;
    }

    public void setLessThanMinusZeroPointFive(List<WaterLevelDto> lessThanMinusZeroPointFive) {
        this.lessThanMinusZeroPointFive = lessThanMinusZeroPointFive;
    }

    public List<WaterLevelDto> getLessThanMinusOne() {
        return lessThanMinusOne;
    }

    public void setLessThanMinusOne(List<WaterLevelDto> lessThanMinusOne) {
        this.lessThanMinusOne = lessThanMinusOne;
    }
}
