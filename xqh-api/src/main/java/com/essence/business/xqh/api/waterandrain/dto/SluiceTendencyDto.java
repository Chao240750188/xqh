package com.essence.business.xqh.api.waterandrain.dto;

import com.essence.business.xqh.api.rainfall.dto.rainmonitoring.SluiceTendency;

import java.util.List;

/**
 * @author fengpp
 * 2021/1/25 20:20
 */
public class SluiceTendencyDto {
    private double high;
    private double low;
    private List<SluiceTendency> list;

    public SluiceTendencyDto() {
    }

    public SluiceTendencyDto(double high, double low, List<SluiceTendency> list) {
        this.high = high;
        this.low = low;
        this.list = list;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public List<SluiceTendency> getList() {
        return list;
    }

    public void setList(List<SluiceTendency> list) {
        this.list = list;
    }
}
