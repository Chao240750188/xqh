package com.essence.business.xqh.api.rainfall.service;

import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;

import java.util.List;
import java.util.Map;

/**
 * @author fengpp
 * 2021/1/21 18:23
 */
public interface RainMonitoringService {

    /**
     * 实施监测-雨情监测-雨情概况
     *
     * @param dto
     * @return
     */
    Map<String, Object> rainSummary(QueryParamDto dto);

    /**
     * 降雨分布列表
     *
     * @param dto
     * @return
     */
    List<Map<String, Object>> getRainDistributionList(QueryParamDto dto);

    /**
     * 测站信息
     * @param stcd
     * @return
     */
    Map<String, String> getInfo(String stcd);
}
