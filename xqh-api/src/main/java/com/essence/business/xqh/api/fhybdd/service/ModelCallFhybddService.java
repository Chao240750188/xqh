package com.essence.business.xqh.api.fhybdd.service;

import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;

import java.util.List;
import java.util.Map;

public interface ModelCallFhybddService {

    /**
     * 获取某段时间的降雨量，然后调用水文调度模型
     * @param planId
     */
    List<Map<String,Object>> callMode(String planId);

    /**
     * 方案计划存入到缓存里
     * @param vo
     * @return
     */
    String savePlanWithCache(ModelCallBySWDDVo vo);


    /**
     * 根据方案id获取雨量信息
     * @param planId
     * @return
     */
    List<Map<String, Object>> getRainfalls(String planId);
}
