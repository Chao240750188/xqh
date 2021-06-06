package com.essence.business.xqh.api.rainfall.service;

import com.essence.business.xqh.api.rainfall.dto.YTRainTimeDto;
import com.essence.business.xqh.dao.entity.rainfall.YwkTypicalRainTime;

import java.util.List;

public interface RainFallEventsService {

    Boolean searchRainFallEventsIsExits(String cRainName);

    String addRainFallEvents(YwkTypicalRainTime ywkTypicalRainTime);

    List<YwkTypicalRainTime> getRainFallEventsByName(String cRainName);

    void deleteRainFallEvents(String cId);

    void updateRainFallEvents(String cId, YwkTypicalRainTime ywkTypicalRainTime);

    List<YTRainTimeDto> getAllRainFallEvents();
}
