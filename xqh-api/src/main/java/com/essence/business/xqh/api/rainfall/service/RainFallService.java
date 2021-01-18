package com.essence.business.xqh.api.rainfall.service;

import com.essence.business.xqh.api.rainfall.dto.RainFallDto;
import com.essence.business.xqh.api.rainfall.dto.StationWaterDto;
import com.essence.business.xqh.api.rainfall.dto.WalterLevelDto;
import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;

import java.util.List;

public interface RainFallService {


    List<RainFallDto> getRainFallAllByTime(QueryParamDto dto);

    RainFallDto getStationRainFallByTime(QueryParamDto dto);

    List<WalterLevelDto> getWaterLevelByTime(QueryParamDto dto);

    StationWaterDto getWaterLevelByStationAndTime(QueryParamDto dto);
}
