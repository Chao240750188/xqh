package com.essence.business.xqh.api.waterandrain.service;

import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.api.waterandrain.dto.DayRainfallDto;
import com.essence.business.xqh.api.waterandrain.dto.MonthRainfallDto;

import java.util.*;

/**
 * @author fengpp
 * 2021/2/4 18:17
 */
public interface RainfallSearchService {
    /**
     * 一段时间内雨量
     *
     * @param dto
     * @return
     */
    DayRainfallDto getDayRainfall(QueryParamDto dto);

    /**
     * 旬雨量表
     * @param year
     * @param mth
     * @param prdtp
     * @return
     */
    MonthRainfallDto getMonthRainfall(Integer year, Integer mth, Integer prdtp);


    /**
     * 日雨量-单个站点雨量过程线
     *
     * @param dto
     * @return
     */
    Map<String, Object> getDayRainfallTendency(QueryParamDto dto);

    /**
     * 时段雨量-单个站点雨量过程线
     *
     * @param dto
     * @return
     */
    Map<String, Object> getTimeRainfallTendency(QueryParamDto dto);

    /**
     * 旬雨量-单个站点雨量过程线
     *
     * @param dto
     * @return
     */
    Map<String, Object> getTenDaysRainfallTendency(QueryParamDto dto);

    /**
     * 月雨量-单个站点雨量过程线
     *
     * @param dto
     * @return
     */
    Map<String, Object> getMonthRainfallTendency(QueryParamDto dto);
}
