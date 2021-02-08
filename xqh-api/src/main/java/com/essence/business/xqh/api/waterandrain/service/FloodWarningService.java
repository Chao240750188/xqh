package com.essence.business.xqh.api.waterandrain.service;

import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.api.realtimemonitor.dto.FloodWarningDto;
import com.essence.business.xqh.api.realtimemonitor.dto.WaterWayFloodWarningCountDto;

/**
 * 洪水告警service
 * @author fengpp
 * 2021/2/2 11:14
 */
public interface FloodWarningService {

    /**
     * 水雨情查询-洪水告警-闸坝
     *
     * @param dto
     * @return
     */
    FloodWarningDto getSluiceFloodWarning(QueryParamDto dto);

    /**
     * 水雨情查询-洪水告警-潮汐
     *
     * @param dto
     * @return
     */
    FloodWarningDto getTideFloodWarning(QueryParamDto dto);

    /**
     * 水雨情查询-洪水告警-模态框-闸坝
     *
     * @param dto
     * @return
     */
    WaterWayFloodWarningCountDto getSluiceFloodWarningList(QueryParamDto dto);

    /**
     * 水雨情查询-洪水告警-模态框-潮汐
     *
     * @param dto
     * @return
     */
    WaterWayFloodWarningCountDto getTideFloodWarningList(QueryParamDto dto);
}
