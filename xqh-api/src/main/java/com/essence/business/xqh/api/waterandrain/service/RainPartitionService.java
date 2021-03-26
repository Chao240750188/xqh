package com.essence.business.xqh.api.waterandrain.service;

import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.api.rainfall.vo.RainPartitionDto;
import com.essence.business.xqh.api.waterandrain.dto.*;

import java.util.List;
import java.util.Map;

/**
 * 雨水情查询-雨量信息查询分区业务层
 */
public interface RainPartitionService {

    /**
     * 查询分区雨量按类型和时间
     * @param reqDto
     * @return
     */
    Object getPartRain(RainPartitionDto reqDto);
}
