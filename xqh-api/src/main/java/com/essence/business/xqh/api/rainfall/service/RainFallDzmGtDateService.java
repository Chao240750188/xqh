package com.essence.business.xqh.api.rainfall.service;

import com.essence.business.xqh.api.rainfall.dto.dzm.StationRainVgeDto;
import com.essence.business.xqh.api.rainfall.vo.RainDzmReq;

/**
 * @author Stack
 * @version 1.0
 * @date 2020/5/20 0020 18:00
 */
public interface RainFallDzmGtDateService {

    /**
     * 全站的前 时间段 的累计雨量
     *
     * @param req
     * @return
     */
    StationRainVgeDto getAllStationGtDateTotalRainfall(RainDzmReq req);
}
