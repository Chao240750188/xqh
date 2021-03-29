package com.essence.business.xqh.service.rainfall;

import com.essence.business.xqh.api.rainfall.dto.dzm.StationRainVgeDto;
import com.essence.business.xqh.api.rainfall.service.RainFallDzmBetweenService;
import com.essence.business.xqh.api.rainfall.vo.RainDzmReq;
import com.essence.business.xqh.common.util.gis.RainFallLevelTz;
import com.essence.business.xqh.dao.dao.fhybdd.StPptnRDao;
import com.essence.business.xqh.dao.dao.rainfall.dto.THdmisTotalRainfallDto;
import com.essence.framework.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName RainFallDzmServiceImpl
 * @Description TODO
 * @Author zhichao.xing
 * @Date 2020/7/2 11:47
 * @Version 1.0
 **/
@Service
public class RainFallDzmBetweenServiceImpl extends AbstractRainFallDzmService implements RainFallDzmBetweenService {
    /*@Autowired
    StPptnRaintimedataDao stPptnRaintimedataDao;*/

    @Autowired
    private StPptnRDao pptnRDao;

    @Override
    protected List<THdmisTotalRainfallDto> getDbRainfall(RainDzmReq req) {
//        List<Map<String, Object>> dbRainList = pptnRDao.queryByTmBetween(req.getStartTime(), req.getEndTime());
        Date startTime = req.getStartTime();
        Date endTime = req.getEndTime();
        String s1 = DateUtil.dateToStringNormal(startTime);
        String s2 = DateUtil.dateToStringNormal(endTime);
        List<Map<String, Object>> dbRainList2 = pptnRDao.queryByTmBetween(s1, s2);
        List<THdmisTotalRainfallDto> dbRainList=new ArrayList<>();
        dbRainList2.forEach(item->{
            Object drp = item.get("DRP");
            Object tm = item.get("TM");
            Object stcd = item.get("STCD");
            THdmisTotalRainfallDto dto=new THdmisTotalRainfallDto();
            dto.setDrp(Double.valueOf(drp.toString()));
            String string = tm.toString();
            Date dateWithFormat = DateUtil.getDateWithFormat(string, "yyyy-MM-dd HH:mm:ss");
            dto.setTm(dateWithFormat);
            dto.setStcd(stcd.toString());
            dbRainList.add(dto);
        });
        dbRainList.forEach(entity -> entity.setLevel(RainFallLevelTz.getRainFallLevel(entity.getDrp(), RainFallLevelTz.getLevel(req.getHours()))));
        return dbRainList;
    }

    @Override
    public StationRainVgeDto getAllStationBetweenTotalRainfall(RainDzmReq req) {
        return super.getAllStationTotalRainfall(req);
    }

}
