package com.essence.business.xqh.api.rainfall.service;

import com.essence.business.xqh.api.rainfall.dto.rainmonitoring.*;
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
     * 单个测站信息
     *
     * @param stcd
     * @return
     */
    Map<String, String> getInfo(String stcd);


    /**
     * 实时监测-水情监测-闸坝
     *
     * @return
     */
    List<SluiceDto> getSluiceList();

    /**
     * 实时监视-水情监视-站点查询-站点信息-闸坝
     *
     * @param stcd
     * @return
     */
    SluiceInfoDto getSluiceInfo(String stcd);

    /**
     * 实时监视-水情监视-站点查询-水位流量过程线-闸坝
     *
     * @param dto
     * @return
     */
    SluiceTendencyDto getSluiceTendency(QueryParamDto dto);


    /**
     * 实时监测-水情监测-潮位
     *
     * @return
     */
    List<TideListDto> getTideList();

    /**
     * 实时监视-水情监视-站点查询-站点信息-潮位
     *
     * @param stcd
     * @return
     */
    TideInfoDto getTideInfo(String stcd);

    /**
     * 实时监视-水情监视-站点查询-水位流量过程线-潮位
     *
     * @param dto
     * @return
     */
    TideTendencyDto getTideTendency(QueryParamDto dto);

}
