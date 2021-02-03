package com.essence.business.xqh.api.waterandrain.dto;

import com.essence.business.xqh.api.waterandrain.dto.WaterLevelDto;

import java.util.List;

/**
 * @author fengpp
 * 2021/1/30 19:23
 */
public class WaterLevelMaxRangeDto {
    private List<WaterLevelDto> greaterThanOne;//大于1
    private List<WaterLevelDto> greaterThanZeroPointFive;//0.5~1
    private List<WaterLevelDto> greaterThanZero;//0~0.5
    private List<WaterLevelDto> noChange;//无变化

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
}
