package com.essence.business.xqh.api.realtimemonitor.service;

import com.essence.business.xqh.api.realtimemonitor.dto.RainDataParamDto;

import java.text.ParseException;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/22 0022 10:43
 */
public interface RealTimeMonitorService {

    Object getReservoirDataOnTime();

    Object getStationMessage();

    Object getRainDataByStepTimeAndID(RainDataParamDto rainDataParamDto);

    Object getWaterRegimenMessage();

    Object geRiverWayDataOnTime();

    Object geRiverWayDataSingle(String stcd);

    Object getRiverWayWaterLevelByTime(String stcd, String startTime, String endTime) throws ParseException;


    Object getReservoirDataSingle(String stcd);

    Object getReservoirWaterLevelByTime(String stcd, String startTime, String endTime) throws ParseException;

    Object getWaterWayFloodWarningByTime(String startTime, String endTime) throws ParseException;

    Object getWaterWayFloodWarningDetailByTime(String startTime, String endTime) throws ParseException;

    Object getReservoirFloodWarningByTime(String startTime, String endTime) throws ParseException;

    Object getReservoirFloodWarningDetailByTime(String startTime, String endTime) throws ParseException;
}
