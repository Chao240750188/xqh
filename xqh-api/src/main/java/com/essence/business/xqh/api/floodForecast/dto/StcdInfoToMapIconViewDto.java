package com.essence.business.xqh.api.floodForecast.dto;

import java.util.List;

public class StcdInfoToMapIconViewDto {

    private String sttpName;

    private List<StcdInfoToMapIconListDto> stcdList;

    public String getSttpName() {
        return sttpName;
    }

    public void setSttpName(String sttpName) {
        this.sttpName = sttpName;
    }

    public List<StcdInfoToMapIconListDto> getStcdList() {
        return stcdList;
    }

    public void setStcdList(List<StcdInfoToMapIconListDto> stcdList) {
        this.stcdList = stcdList;
    }
}
