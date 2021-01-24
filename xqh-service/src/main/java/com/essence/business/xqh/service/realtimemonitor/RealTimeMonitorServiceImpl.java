package com.essence.business.xqh.service.realtimemonitor;

import com.essence.business.xqh.api.realtimemonitor.dto.RainDataParamDto;
import com.essence.business.xqh.api.realtimemonitor.dto.RainDataResultDto;
import com.essence.business.xqh.api.realtimemonitor.dto.StationMessageDto;
import com.essence.business.xqh.api.realtimemonitor.dto.WaterRegimenMessageDto;
import com.essence.business.xqh.api.realtimemonitor.service.RealTimeMonitorService;
import com.essence.business.xqh.dao.dao.fhybdd.StPptnRDao;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.dao.fhybdd.WrpRsrBsinDao;
import com.essence.business.xqh.dao.dao.realtimemonitor.*;
import com.essence.business.xqh.dao.entity.fhybdd.StPptnR;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.realtimemonitor.TRsvrfsrB;
import com.essence.business.xqh.dao.entity.realtimemonitor.TRvfcchB;
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
import java.util.function.Function;
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

    @Autowired
    private TRiverRODao tRiverRDao;

    @Autowired
    private TRvfcchBDao tRvfcchBDao;

    @Autowired
    private TRsvrRDao tRsvrRDao;

    @Autowired
    private TWasRDao tWasRDao;

    @Autowired
    private TTideRDao tTideRDao;

    @Autowired
    private TRsvrfsrBDao tRsvrfsrBDao;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private Logger logger = LoggerFactory.getLogger(RealTimeMonitorServiceImpl.class);


    @Override
    public Object getStationMessage() {
        List<StationMessageDto> result = new ArrayList<>();
        List<StStbprpB> stStbprpBS = stStbprpBDao.findAll();
        for (StStbprpB stStbprpB : stStbprpBS) {
            StationMessageDto dto = new StationMessageDto();
            BeanUtils.copyProperties(stStbprpB,dto);
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
        //取出时间段相应的数据
        List<StPptnR> stPptnRS = stPptnRDao.getRainFallByTimeAndID(rainDataParamDto.getStcd(), sdf.format(startTime), sdf.format(endTime));
        stopwatch.stop();
        LocalDateTime startT = LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.systemDefault());
        LocalDateTime endT = LocalDateTime.ofInstant(endTime.toInstant(), ZoneId.systemDefault());
        stopwatch.start("2");
        //根据步长返回数据
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

    @Override
    public Object getWaterRegimenMessage() {
        WaterRegimenMessageDto waterRegimenMessageDto = new WaterRegimenMessageDto();
        int waterLevelHistoryHD = 0;
        int waterLevelGuaranteeHD = 0;
        int waterLevelWarningHD = 0;
        int waterLevelHistoryCZ = 0;
        int waterLevelGuaranteeCZ = 0;
        int waterLevelWarningCZ = 0;
        int waterLevelLineSK = 0;
        //测站编码表
        List<StStbprpB> stStbprpBS = stStbprpBDao.findAll();
        //河道站防洪指标表
        List<TRvfcchB> tRvfcchBS = tRvfcchBDao.findAll();
        //库（湖）站汛限水位表 查询主汛期为1的
        List<TRsvrfsrB> tRsvrfsrBS = tRsvrfsrBDao.findByFstp("1");
        //获取河道个数
        List<StStbprpB> collectHD = stStbprpBS.stream().filter(t -> "ZQ".equals(t.getSttp()) || "ZZ".equals(t.getSttp())).collect(Collectors.toList());
        //获取河道站，堰闸站，潮汐站警戒信息
        Map<String, TRvfcchB> collectWarningHD = tRvfcchBS.stream().collect(Collectors.toMap(TRvfcchB::getStcd, Function.identity()));
        //获取水库警戒信息
        Map<String, TRsvrfsrB> collectWarningSK = tRsvrfsrBS.stream().collect(Collectors.toMap(TRsvrfsrB::getStcd, Function.identity()));
        //获取河道水情信息
        List<Map<String, Object>> riverRLastData = tRiverRDao.getRiverRLastData();
        for (Map<String, Object> map : riverRLastData) {
            try {
                String stcd = map.get("stcd").toString();
                //水位
                double z = Double.parseDouble(map.get("z").toString());
                TRvfcchB tRvfcchStandard = collectWarningHD.get(stcd);
                if(tRvfcchStandard==null){
                    continue;
                }
                //警戒水位
                double wrz = Double.parseDouble(tRvfcchStandard.getWrz()==null?"0":tRvfcchStandard.getWrz());
                //保证水位
                double grz = Double.parseDouble(tRvfcchStandard.getGrz()==null?"0":tRvfcchStandard.getGrz());
                //历史最高水位
                double obhtz = Double.parseDouble(tRvfcchStandard.getObhtz()==null?"0":tRvfcchStandard.getObhtz());
                if(obhtz<z){
                    waterLevelHistoryHD++;
                }
                if(wrz<z){
                    waterLevelWarningHD++;
                }
                if(grz<z){
                    waterLevelGuaranteeHD++;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        //获取水库个数
        List<StStbprpB> collectSK = stStbprpBS.stream().filter(t -> "RR".equals(t.getSttp())).collect(Collectors.toList());
        List<Map<String, Object>> rsvrLastData = tRsvrRDao.getRsvrLastData();
        for (Map<String, Object> map : rsvrLastData) {
            try {
                String stcd = map.get("stcd").toString();
                //水位
                double z = Double.parseDouble(map.get("RZ").toString());
                TRsvrfsrB tRsvrfsrB = collectWarningSK.get(stcd);
                if(tRsvrfsrB == null){
                    continue;
                }
                //汛险水位
                double fsltdz = Double.parseDouble(tRsvrfsrB.getFsltdz()==null?"0":tRsvrfsrB.getFsltdz());
                if(fsltdz<z){
                    waterLevelLineSK++;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        //闸坝和潮汐个数
        List<StStbprpB> collectCZ = stStbprpBS.stream().filter(t -> "TT".equals(t.getSttp()) || "DD".equals(t.getSttp())).collect(Collectors.toList());
        List<Map<String, Object>> lastData = tWasRDao.getLastData();
        for (Map<String, Object> map : lastData) {
            try {
                String stcd = map.get("stcd").toString();
                //水位
                double z = Double.parseDouble(map.get("UPZ").toString());
                TRvfcchB tRvfcchStandard = collectWarningHD.get(stcd);
                if(tRvfcchStandard == null){
                    continue;
                }
                //警戒水位
                double wrz = Double.parseDouble(tRvfcchStandard.getWrz()==null?"0":tRvfcchStandard.getWrz());
                //保证水位
                double grz = Double.parseDouble(tRvfcchStandard.getGrz()==null?"0":tRvfcchStandard.getGrz());
                //历史最高水位
                double obhtz = Double.parseDouble(tRvfcchStandard.getObhtz()==null?"0":tRvfcchStandard.getObhtz());
                if(obhtz<z){
                    waterLevelHistoryCZ++;
                }
                if(wrz<z){
                    waterLevelWarningCZ++;
                }
                if(grz<z){
                    waterLevelGuaranteeCZ++;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        //潮汐
        List<Map<String, Object>> lastCData = tTideRDao.getLastData();
        for (Map<String, Object> map : lastCData) {
            try {
                String stcd = map.get("stcd").toString();
                //水位
                double z = Double.parseDouble(map.get("TDZ").toString());
                TRvfcchB tRvfcchStandard = collectWarningHD.get(stcd);
                //警戒水位
                double wrz = Double.parseDouble(tRvfcchStandard.getWrz()==null?"0":tRvfcchStandard.getWrz());
                //保证水位
                double grz = Double.parseDouble(tRvfcchStandard.getGrz()==null?"0":tRvfcchStandard.getGrz());
                //历史最高水位
                double obhtz = Double.parseDouble(tRvfcchStandard.getObhtz()==null?"0":tRvfcchStandard.getObhtz());
                if(obhtz<z){
                    waterLevelHistoryCZ++;
                }
                if(wrz<z){
                    waterLevelWarningCZ++;
                }
                if(grz<z){
                    waterLevelGuaranteeCZ++;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        waterRegimenMessageDto.setCountHD(collectHD.size());
        waterRegimenMessageDto.setWaterLevelHistoryHD(waterLevelHistoryHD);
        waterRegimenMessageDto.setWaterLevelGuaranteeHD(waterLevelGuaranteeHD);
        waterRegimenMessageDto.setWaterLevelWarningHD(waterLevelWarningHD);
        waterRegimenMessageDto.setCountSK(collectSK.size());
        waterRegimenMessageDto.setWaterLevelLineSK(waterLevelLineSK);
        waterRegimenMessageDto.setCountCZ(collectCZ.size());
        waterRegimenMessageDto.setWaterLevelHistoryCZ(waterLevelHistoryCZ);
        waterRegimenMessageDto.setWaterLevelWarningCZ(waterLevelWarningCZ);
        waterRegimenMessageDto.setWaterLevelGuaranteeCZ(waterLevelGuaranteeCZ);

        return waterRegimenMessageDto;
    }

    @Override
    public Object geRiverWayDataOnTime() {
        //测站编码表
        List<StStbprpB> stStbprpBS = stStbprpBDao.findAll();
        //河道站防洪指标表
        List<TRvfcchB> tRvfcchBS = tRvfcchBDao.findAll();
        //获取河道个数
        List<StStbprpB> collectHD = stStbprpBS.stream().filter(t -> "ZQ".equals(t.getSttp()) || "ZZ".equals(t.getSttp())).collect(Collectors.toList());
        List<Map<String, Object>> riverRLastData = tRiverRDao.getRiverRLastData();


        return null;
    }
}
