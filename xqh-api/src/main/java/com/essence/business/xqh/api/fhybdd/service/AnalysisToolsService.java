package com.essence.business.xqh.api.fhybdd.service;

import java.util.List;
import java.util.Map;

public interface AnalysisToolsService {

    /**
     *获取水文站的涨差分析
     * @param map
     * @return
     */
    List<Map<String, Object>> getAnalysisOfPriceDifference(Map map);


    /**
     * 获取分流比计算
     * @param map
     * @return
     */
    Map<String, Object>getSplitRatioCalculation(Map map);

    /**
     * 获取静库容反推入库
     * @param map
     * @return
     */
    Map<String, Object> getJkrftrkInformation(Map map) throws Exception;

    /**
     * 获取水库列表数据成功
     * @return
     */
    Object getReservoirList();

    /**
     * 获取分段静库容反推入库
     * @param list
     * @return
     */
    Object getJkrftrkInformationWithSection(List<Map> list )throws Exception;


    /**
     * 获取分段库容反推时间序列成功
     * @param map
     * @return
     */
    Object beforeGetJkrftrkInformationWithSection(Map map) throws Exception;


    /**
     * 获取分洪区列表
     * @return
     */
    List<Map<String, Object>> getFloodList();
}
