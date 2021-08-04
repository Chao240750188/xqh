package com.essence.business.xqh.api.fhybdd.service;

import com.essence.business.xqh.dao.entity.fhybdd.YwkPlanOutputQ;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninRainfall;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ModelCallHandleDataService {

    /**
     * 保存雨量
     * @param result
     * @return
     */
    CompletableFuture<Integer> saveRainToDb(List<YwkPlaninRainfall> result);

    /**
     * 保存结果
     * @param result
     * @return
     */
    CompletableFuture<Integer> savePlanOut(List<YwkPlanOutputQ> result);

    void handleCsvAndResult(Integer tag ,YwkPlaninfo planinfo);

    CompletableFuture<Integer> callOneMode(String JDPD_MODEL_PATH, String template, String out, String run, String rvcrcrsccd,
                                           YwkPlaninfo planInfo, Map<String, List<Map<String, String>>> waterLevelFlowMap);
}
