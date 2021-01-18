package com.essence.business.xqh.api.rainanalyse.dto;

import java.io.Serializable;
import java.util.List;

public class WaterLevelCompareInfoDto implements Serializable {

    private List<WaterLevelCompareDetailInfoDto> searchTmInfoList;//查询时间段的水位数据

    private List<WaterLevelCompareDetailInfoDto> preSearchTmInfoList;//查询时间段的去年同期水位数据

    public List<WaterLevelCompareDetailInfoDto> getSearchTmInfoList() {
        return searchTmInfoList;
    }

    public void setSearchTmInfoList(List<WaterLevelCompareDetailInfoDto> searchTmInfoList) {
        this.searchTmInfoList = searchTmInfoList;
    }

    public List<WaterLevelCompareDetailInfoDto> getPreSearchTmInfoList() {
        return preSearchTmInfoList;
    }

    public void setPreSearchTmInfoList(List<WaterLevelCompareDetailInfoDto> preSearchTmInfoList) {
        this.preSearchTmInfoList = preSearchTmInfoList;
    }
}
