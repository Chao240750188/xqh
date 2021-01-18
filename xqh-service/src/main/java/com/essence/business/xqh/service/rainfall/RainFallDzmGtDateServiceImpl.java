package com.essence.business.xqh.service.rainfall;

import com.essence.business.xqh.api.rainfall.dto.dzm.StationRainVgeDto;
import com.essence.business.xqh.api.rainfall.service.RainFallDzmGtDateService;
import com.essence.business.xqh.api.rainfall.vo.RainDzmReq;
import com.essence.business.xqh.common.util.gis.RainFallLevelTz;
import com.essence.business.xqh.dao.dao.rainfall.StPptnRDao;
import com.essence.business.xqh.dao.dao.rainfall.dto.THdmisTotalRainfallDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName RainFallDzmServiceImpl
 * @Description TODO
 * @Author zhichao.xing
 * @Date 2020/7/2 11:47
 * @Version 1.0
 **/
@Service
public class RainFallDzmGtDateServiceImpl extends AbstractRainFallDzmService implements RainFallDzmGtDateService {

   /* @Autowired
    StPptnRaintimedataDao stPptnRaintimedataDao;*/

    @Autowired
    private StPptnRDao pptnRDao;


    @Override
    protected List<THdmisTotalRainfallDto> getDbRainfall(RainDzmReq req) {
        // 修改为实时表查询
        List<THdmisTotalRainfallDto> dbRainList = pptnRDao.queryByTmAfter(req.getStartTime());
        dbRainList.forEach(entity -> entity.setLevel(RainFallLevelTz.getRainFallLevel(entity.getDrp(), RainFallLevelTz.getLevel(req.getHours()))));
        return dbRainList;
    }

    @Override
    public StationRainVgeDto getAllStationGtDateTotalRainfall(RainDzmReq req) {
        return getAllStationTotalRainfall(req);
    }
}
