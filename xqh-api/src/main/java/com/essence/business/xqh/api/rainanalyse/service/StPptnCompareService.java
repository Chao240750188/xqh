package com.essence.business.xqh.api.rainanalyse.service;


import com.essence.business.xqh.api.rainanalyse.vo.RainCompareAnalyseReq;

import java.util.Map;

/**
 * 年雨量(StPptnYearRainfall)表服务接口
 *
 * @author xzc
 * @since 2020-07-04 14:06:32
 */
public interface StPptnCompareService {

    Map<String, Double> findSummaryAnalyse(RainCompareAnalyseReq req, String format);
}