package com.essence.business.xqh.service.rainfall;

import com.essence.business.xqh.api.rainfall.dto.YTRainTimeDto;
import com.essence.business.xqh.api.rainfall.service.RainFallEventsService;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.dao.dao.rainfall.YwkTypicalRainTimeDao;
import com.essence.business.xqh.dao.entity.rainfall.YwkTypicalRainTime;
import com.essence.euauth.common.util.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.*;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("all")
public class RainFallEventsServiceImpl implements RainFallEventsService {

    @Autowired
    YwkTypicalRainTimeDao ywkTypicalRainTimeDao;

    @Override
    public Boolean searchRainFallEventsIsExits(String cRainName) {
        List<YwkTypicalRainTime> all = ywkTypicalRainTimeDao.findByCRainName(cRainName);
        return !CollectionUtils.isEmpty(all) ? true:false;
    }

    @Override
    public String addRainFallEvents(YwkTypicalRainTime ywkTypicalRainTime) {
        List<YwkTypicalRainTime> all = ywkTypicalRainTimeDao.findByCRainName(ywkTypicalRainTime.getcRainName());
        if(!CollectionUtils.isEmpty(all)){
            return  "RainNameIsExist";
        }
        ywkTypicalRainTime.setcId(UuidUtil.get32UUIDStr());
        ywkTypicalRainTime.setdUpdateTime(DateUtil.getCurrentTime());
        YwkTypicalRainTime save = ywkTypicalRainTimeDao.save(ywkTypicalRainTime);
        return save.getcId();
    }

    @Override
    public List<YwkTypicalRainTime> getRainFallEventsByName(String cRainName) {
//        Date dateWithFormat = DateUtil.getDateWithFormat(dEndTime, "yyyy-MM-dd HH:mm:ss");
//        List<YwkTypicalRainTime> all = ywkTypicalRainTimeDao.findByCRainNameLikeOrderByDEndTimeDesc(cRainName);
        List<YwkTypicalRainTime> all = ywkTypicalRainTimeDao.findByCRainNameContains(cRainName);
        List<YwkTypicalRainTime> collect = all.stream().sorted(Comparator.comparing(YwkTypicalRainTime::getdEndTime).reversed()).collect(Collectors.toList());
        return collect;
    }

    @Override
    public void deleteRainFallEvents(String cId) {
        ywkTypicalRainTimeDao.deleteById(cId);
    }

    @Override
    public void updateRainFallEvents(String cId, YwkTypicalRainTime ywkTypicalRainTime) {
        YwkTypicalRainTime searchById = ywkTypicalRainTimeDao.findById(cId).get();
        if(ywkTypicalRainTime.getcRainName() != null) searchById.setcRainName(ywkTypicalRainTime.getcRainName());
        if(ywkTypicalRainTime.getdStartTime() != null) searchById.setdStartTime(ywkTypicalRainTime.getdStartTime());
        if(ywkTypicalRainTime.getdEndTime() != null) searchById.setdEndTime(ywkTypicalRainTime.getdEndTime());
        searchById.setdUpdateTime(DateUtil.getCurrentTime());
        ywkTypicalRainTimeDao.save(searchById);
    }

    @Override
    public List<YTRainTimeDto> getAllRainFallEvents() {
        List<YwkTypicalRainTime> all = ywkTypicalRainTimeDao.findAll();
        List<YTRainTimeDto> allDto = new ArrayList<>();
        for (YwkTypicalRainTime ywkTypicalRainTime : all) {
            YTRainTimeDto dto = new YTRainTimeDto();
            dto.setcRainName(ywkTypicalRainTime.getcRainName());
            dto.setdStartTime(ywkTypicalRainTime.getdStartTime());
            dto.setdEndTime(ywkTypicalRainTime.getdEndTime());
            allDto.add(dto);
        }
        return allDto;
    }

}
