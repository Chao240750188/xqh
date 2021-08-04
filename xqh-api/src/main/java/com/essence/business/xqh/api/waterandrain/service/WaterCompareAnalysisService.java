package com.essence.business.xqh.api.waterandrain.service;

import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;

import java.util.List;
import java.util.Map;

/**
 * 水情信息查询-水情对比分析
 *
 * @author fengpp
 * 2021/3/9 14:06
 */
public interface WaterCompareAnalysisService {
    /**
     * 水情信息查询-水情对比分析-左侧树
     * @param dto
     * @return
     */
    List<Map<String, Object>> getStnmList(QueryParamDto dto);

    /**
     * 水情信息查询-水情对比分析-水位
     * @param dto
     * @param flag
     * @return
     */
    Map<String, Object> getWaterLevelTendency(QueryParamDto dto,String flag);

    List<StStbprpB> searchAllRainfallStations();
}