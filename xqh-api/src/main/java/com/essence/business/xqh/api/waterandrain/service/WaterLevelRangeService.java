package com.essence.business.xqh.api.waterandrain.service;

import com.essence.business.xqh.api.waterandrain.dto.*;
import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;

import java.util.List;

/**
 * 水位变幅service
 *
 * @author fengpp
 * 2021/2/2 11:00
 */
public interface WaterLevelRangeService {
    /**
     * 水位变幅-水位变幅
     *
     * @param dto
     * @return
     */
    WaterLevelRangeDto getWaterLevelRange(QueryParamDto dto);

    /**
     * 水位变幅-最大变幅
     *
     * @param dto
     * @return
     */
    WaterLevelMaxRangeDto getWaterLevelMaxRange(QueryParamDto dto);

    /**
     * 最大变幅-水库-模态框
     *
     * @param dto
     * @return
     */
    List<WaterLevelMaxChangeDto> getWaterLevelMaxChange(QueryParamDto dto);

    /**
     * 水位变幅-水库-模态框
     *
     * @param dto
     * @return
     */
    List<ReservoirWaterLevelChangeDto> getReservoirWaterLevelChange(QueryParamDto dto);


    /**
     * 水位变幅-闸坝，潮汐，河道-模态框-最新
     *
     * @param dto
     * @return
     */
    List<WaterLevelChangeDto> getWaterLevelChange(QueryParamDto dto);
}
