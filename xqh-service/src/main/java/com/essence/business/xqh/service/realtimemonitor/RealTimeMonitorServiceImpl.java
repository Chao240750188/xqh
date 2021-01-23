package com.essence.business.xqh.service.realtimemonitor;

import com.essence.business.xqh.api.realtimemonitor.dto.RainDataParamDto;
import com.essence.business.xqh.api.realtimemonitor.dto.RainDataResultDto;
import com.essence.business.xqh.api.realtimemonitor.dto.StationMessageDto;
import com.essence.business.xqh.api.realtimemonitor.service.RealTimeMonitorService;
import com.essence.business.xqh.dao.dao.fhybdd.StPptnRDao;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.dao.fhybdd.WrpRsrBsinDao;
import com.essence.business.xqh.dao.dao.tuoying.TuoyingStStbprpBDao;
import com.essence.business.xqh.dao.entity.fhybdd.StPptnR;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.fhybdd.WrpRsrBsin;
import com.essence.business.xqh.dao.entity.tuoying.TuoyingStStbprpB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
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

    @Autowired
    private StPptnRDao stPptnRDao;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Logger logger = LoggerFactory.getLogger(RealTimeMonitorServiceImpl.class);


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

    @Override
    public Object getRainDataByStepTimeAndID(RainDataParamDto rainDataParamDto) {
        StopWatch stopwatch=new StopWatch("kaishi");
        List<RainDataResultDto> list = new ArrayList<>();
        Date startTime = rainDataParamDto.getStartTime();
        Date endTime = rainDataParamDto.getEndTime();
        Integer step = rainDataParamDto.getStep();
        stopwatch.start("1");
        List<StPptnR> stPptnRS = stPptnRDao.getRainFallByTimeAndID(rainDataParamDto.getStcd(), sdf.format(startTime), sdf.format(endTime));
        stopwatch.stop();
        LocalDateTime startT = LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.systemDefault());
        LocalDateTime endT = LocalDateTime.ofInstant(endTime.toInstant(), ZoneId.systemDefault());
        stopwatch.start("2");
        while (startT.isBefore(endT)){
            Date time = Date.from(startT.atZone(ZoneId.systemDefault()).toInstant());
            Double collect = stPptnRS.stream().filter(t -> t.getTm().after(time)).filter(t -> t.getTm()
                    .before(Date.from(endT.atZone(ZoneId.systemDefault()).toInstant()))).collect(Collectors.summingDouble(StPptnR::getDrp));
            RainDataResultDto dataResultDto = new RainDataResultDto();
            dataResultDto.setRainfall(collect);
            dataResultDto.setStep(step);
            dataResultDto.setTime(time);
            startT = startT.plusHours(step);
            list.add(dataResultDto);
        }
        stopwatch.stop();
        logger.info(stopwatch.prettyPrint());
        return list;
    }
}
