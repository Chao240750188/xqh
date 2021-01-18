package com.essence.business.xqh.api.floodForecast.dto;


import java.util.List;

/**
 * 时段水库水位展示实体类
 * LiuGt add at 2020-07-13
 */
public class HourTimeWaterLevelViewDto {

    /**
     * 水库信息
     */
    private SqybResDto resInfo;

    /**
     * 时段水位列表
     */
    private List<HourTimeWaterLevelListDto> waterLevelListDtoList;

    public SqybResDto getResInfo() {
        return resInfo;
    }

    public void setResInfo(SqybResDto resInfo) {
        this.resInfo = resInfo;
    }

    public List<HourTimeWaterLevelListDto> getWaterLevelListDtoList() {
        return waterLevelListDtoList;
    }

    public void setWaterLevelListDtoList(List<HourTimeWaterLevelListDto> waterLevelListDtoList) {
        this.waterLevelListDtoList = waterLevelListDtoList;
    }
}
