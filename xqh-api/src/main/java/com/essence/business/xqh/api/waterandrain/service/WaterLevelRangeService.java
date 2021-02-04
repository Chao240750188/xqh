package com.essence.business.xqh.api.waterandrain.service;

import com.essence.business.xqh.api.waterandrain.dto.ReservoirWaterLevelChangeDto;
import com.essence.business.xqh.api.waterandrain.dto.WaterLevelChangeDto;
import com.essence.business.xqh.api.waterandrain.dto.WaterLevelMaxRangeDto;
import com.essence.business.xqh.api.waterandrain.dto.WaterLevelRangeDto;
import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;

import java.util.List;

/**
 * 水位变幅service
 * @author fengpp
 * 2021/2/2 11:00
 */
public interface WaterLevelRangeService {
    /**
     * 水位变幅-水位变幅
     * @param dto
     * @return
     */
    WaterLevelRangeDto getWaterLevelRange(QueryParamDto dto);

    /**
     * 水位变幅-最大变幅
     * @param dto
     * @return
     */
    WaterLevelMaxRangeDto getWaterLevelMaxRange(QueryParamDto dto);

    /**
     * 水位变幅-闸坝、河道、潮汐-模态框
     * @param dto
     * @return
     */
    List<WaterLevelChangeDto> getWaterLevelChange(QueryParamDto dto);

    /**
     * 水位变幅-水库-模态框
     * @param dto
     * @return
     */
    List<ReservoirWaterLevelChangeDto> getReservoirWaterLevelChange(QueryParamDto dto);
}
