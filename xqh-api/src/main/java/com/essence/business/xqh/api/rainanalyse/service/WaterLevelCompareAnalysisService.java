package com.essence.business.xqh.api.rainanalyse.service;

import com.essence.business.xqh.api.rainanalyse.dto.WaterLevelAnalysisInfoDto;
import com.essence.business.xqh.api.rainanalyse.dto.WaterLevelCompareInfoDto;
import com.essence.business.xqh.api.rainanalyse.dto.WaterLevelStationInfoDto;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;

import java.util.Date;
import java.util.List;


/**
 * 雨水情测报分析--水情多维分析service
 * @Author huangxiaoli
 * @Description
 * @Date 16:20 2020/9/3
 * @Param
 * @return
 **/
public interface WaterLevelCompareAnalysisService {


    /**
     * 水情分析--查询水位站信息
     * @Author huangxiaoli
     * @Description
     * @Date 10:53 2020/9/12
     * @Param []
     * @return java.util.List<com.essence.tzsyq.rainanalyse.dto.WaterLevelStationInfoDto>
     **/
    public List<WaterLevelStationInfoDto> getWaterLevelStationInfoDto();


    /**
     * 水情分析--根据条件分页查询水位站水位信息
     * @Author huangxiaoli
     * @Description
     * @Date 13:47 2020/9/12
     * @Param [param]
     * @return com.essence.framework.jpa.Paginator<com.essence.tzsyq.rainanalyse.dto.WaterLevelAnalysisInfoDto>
     **/
    public Paginator<WaterLevelAnalysisInfoDto> getWaterLevelAnalysisDataPageInfo(PaginatorParam param);


    /**
     * 水情形式分析--水情对比分析--根据条件分页查询水情信息
     * @Author huangxiaoli
     * @Description
     * @Date 16:20 2020/9/3
     * @Param [param]
     * @return com.essence.framework.jpa.Paginator<com.essence.tzsyq.rainanalyse.dto.WaterLevelAnalysisInfoDto>
     **/
    public Paginator<WaterLevelAnalysisInfoDto> getWaterLevelAnalysisPageInfo(PaginatorParam param);

    /**
     * 水情形式分析--水情对比分析--查询单个测站去年同期水位对比数据
     * @Author huangxiaoli
     * @Description
     * @Date 17:31 2020/9/3
     * @Param [stcd, startTime, endTime]
     * @return com.essence.tzsyq.rainanalyse.dto.WaterLevelCompareInfoDto
     **/
    public WaterLevelCompareInfoDto getWaterLevelCompareInfo(String type, String stcd, Date startTime, Date endTime);





}
