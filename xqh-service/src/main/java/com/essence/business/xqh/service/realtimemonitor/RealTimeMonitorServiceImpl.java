package com.essence.business.xqh.service.realtimemonitor;

import com.essence.business.xqh.api.realtimemonitor.dto.StationMessageDto;
import com.essence.business.xqh.api.realtimemonitor.service.RealTimeMonitorService;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.dao.fhybdd.WrpRsrBsinDao;
import com.essence.business.xqh.dao.dao.tuoying.TuoyingStStbprpBDao;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.fhybdd.WrpRsrBsin;
import com.essence.business.xqh.dao.entity.tuoying.TuoyingStStbprpB;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/22 0022 10:44
 */
@Service
public class RealTimeMonitorServiceImpl implements RealTimeMonitorService {

    @Autowired
    private StStbprpBDao stStbprpBDao;

    @Autowired
    private WrpRsrBsinDao wrpRsrBsinDao;


    @Override
    public Object getStationMessage() {
        List<StationMessageDto> result = new ArrayList<>();
        List<StStbprpB> stStbprpBS = stStbprpBDao.findAll();
        List<WrpRsrBsin> wrpRsrBsins = wrpRsrBsinDao.findAll();
        for (StStbprpB stStbprpB : stStbprpBS) {
            StationMessageDto dto = new StationMessageDto();
            BeanUtils.copyProperties(stStbprpB,dto);
            result.add(dto);
        }
        for (WrpRsrBsin wrpRsrBsin : wrpRsrBsins) {
            StationMessageDto dto = new StationMessageDto();
            dto.setStcd(wrpRsrBsin.getRscd());
            dto.setStnm(wrpRsrBsin.getRsnm());
            dto.setLgtd(wrpRsrBsin.getLgtd());
            dto.setLttd(wrpRsrBsin.getLttd());
            dto.setSttp("SK");
            result.add(dto);
        }
        return result;
    }
}
