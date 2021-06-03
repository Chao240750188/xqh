package com.essence.business.xqh.service.realtimemonitor;

import com.essence.business.xqh.api.realtimemonitor.dto.*;
import com.essence.business.xqh.api.realtimemonitor.service.RealTimeMonitorService;
import com.essence.business.xqh.dao.dao.fhybdd.StPptnRDao;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.dao.fhybdd.WrpRsrBsinDao;
import com.essence.business.xqh.dao.dao.realtimemonitor.*;
import com.essence.business.xqh.dao.entity.fhybdd.StPptnR;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.realtimemonitor.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
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

    @Autowired
    private TRsvrfcchBDao tRsvrfcchBDao;

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
        //获取水库数据最新一条记录
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
        List<RiverWayDataDto> results = new ArrayList<>();
        //测站编码表
        List<StStbprpB> stStbprpBS = stStbprpBDao.findAll();
        //河道站防洪指标表
        List<TRvfcchB> tRvfcchBS = tRvfcchBDao.findAll();
        //获取河道站个数
        List<StStbprpB> collectHD = stStbprpBS.stream().filter(t -> "ZQ".equals(t.getSttp()) || "ZZ".equals(t.getSttp())).collect(Collectors.toList());
        //获取河道水情信息
        List<Map<String, Object>> riverRLastData = tRiverRDao.getRiverRLastData();
        Map<String, Map<String, Object>> riverWayDataMap = riverRLastData.stream().collect(Collectors.toMap(t -> t.get("stcd").toString(), Function.identity()));
        //获取河道站，堰闸站，潮汐站警戒信息
        Map<String, TRvfcchB> collectWarningHD = tRvfcchBS.stream().collect(Collectors.toMap(TRvfcchB::getStcd, Function.identity()));

        for (StStbprpB stStbprpB : collectHD) {
            try {
                RiverWayDataDto dataDto = new RiverWayDataDto();
                BeanUtils.copyProperties(stStbprpB,dataDto);
                String stcd = stStbprpB.getStcd();
                Map<String, Object> map = riverWayDataMap.get(stcd);
                if(map!=null && map.get("z") != null){
                    dataDto.setWaterLevel(Double.parseDouble(map.get("z").toString()));
                }
                if(map!=null && map.get("q") != null){
                    dataDto.setFlow(Double.parseDouble(map.get("q").toString()));
                }
                TRvfcchB tRvfcchStandard = collectWarningHD.get(stcd);

                if(tRvfcchStandard != null){
                    if (tRvfcchStandard.getWrz() != null){
                        dataDto.setWarningWaterLevel(Double.parseDouble(tRvfcchStandard.getWrz()));
                    }
                    //警戒水位
                    double wrz = Double.parseDouble(tRvfcchStandard.getWrz()==null?"0":tRvfcchStandard.getWrz());
                    //保证水位
                    double grz = Double.parseDouble(tRvfcchStandard.getGrz()==null?"0":tRvfcchStandard.getGrz());
                    //历史最高水位
                    double obhtz = Double.parseDouble(tRvfcchStandard.getObhtz()==null?"0":tRvfcchStandard.getObhtz());

                    if(obhtz < dataDto.getWaterLevel()){
                        dataDto.setIsThanWaterLevelHistory(1);
                    }
                    if(wrz <  dataDto.getWaterLevel()){
                        dataDto.setIsThanWaterLevelWarning(1);
                    }
                    if(grz <  dataDto.getWaterLevel()){
                        dataDto.setIsThanWaterLevelGuarantee(1);
                    }

                }
                results.add(dataDto);
            } catch (BeansException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    @Override
    public Object geRiverWayDataSingle(String stcd) {
        RiverWayDataSingleDto result = new RiverWayDataSingleDto();
        StStbprpB byStcd = stStbprpBDao.findByStcd(stcd);
        //河道站防洪指标表
        TRvfcchB tRvfcchB = tRvfcchBDao.findByStcd(stcd);
        BeanUtils.copyProperties(byStcd,result);
        if(tRvfcchB != null){
            BeanUtils.copyProperties(tRvfcchB,result);
        }
        return result;
    }


    @Override
    public Object getRiverWayWaterLevelByTime(String stcd, String startTime, String endTime) throws ParseException {
        RiverWayDataTimeDto result = new RiverWayDataTimeDto();
        DecimalFormat format = new DecimalFormat("0.00");
        double low = 0;
        double high = 0;
        Date startDate = sdf.parse(startTime);
        Date endDate = sdf.parse(endTime);
        List<TRiverR> riverRS = tRiverRDao.findByStcdAndTmBetweenOrderByTmDesc(stcd, startDate, endDate);
        List<RiverWayDataDetailDto> dataDetailDtos = new ArrayList<>();
        //河道站防洪指标表
        TRvfcchB tRvfcchB = tRvfcchBDao.findByStcd(stcd);
        for (TRiverR riverR : riverRS) {
            RiverWayDataDetailDto dto = new RiverWayDataDetailDto();
            BeanUtils.copyProperties(tRvfcchB,dto);
            if(riverR.getQ()!=null){
                double flow = Double.parseDouble(riverR.getQ());
                dto.setFlow(Double.parseDouble(format.format(flow)));
            }
            if(riverR.getZ()!=null){
                double waterLevel = Double.parseDouble(riverR.getZ());
                dto.setWaterLevel(Double.parseDouble(format.format(waterLevel)));
            }
            //获取警戒水位
            String wrz = tRvfcchB.getWrz();
            //距警戒
            if(wrz!=null && riverR.getZ()!=null){
                dto.setDistance(Double.parseDouble(riverR.getZ())-Double.parseDouble(wrz));
            }
            dto.setTm(riverR.getTm());
            dto.setWptn(riverR.getWptn());
            dataDetailDtos.add(dto);
        }
        //根据水位判断水势
//        for (int i = riverRS.size()-1; i > 0 ; i--) {
//            TRiverR tRiverRLast = riverRS.get(i);
//            TRiverR tRiverRFirst = riverRS.get(i - 1);
//            RiverWayDataDetailDto dto = new RiverWayDataDetailDto();
//            BeanUtils.copyProperties(tRvfcchB,dto);
//            if(tRiverRLast.getQ()!=null){
//                double flow = Double.parseDouble(tRiverRLast.getQ());
//                dto.setFlow(flow);
//            }
//            if(tRiverRLast.getZ()!=null){
//                double waterLevel = Double.parseDouble(tRiverRLast.getZ());
//                dto.setWaterLevel(waterLevel);
//            }
//            //获取警戒水位
//            String wrz = tRvfcchB.getWrz();
//            //距警戒
//            if(wrz!=null && tRiverRLast.getZ()!=null){
//                dto.setDistance(Double.parseDouble(tRiverRLast.getZ())-Double.parseDouble(wrz));
//            }
//            dto.setTm(tRiverRLast.getTm());
//            if(tRiverRLast.getZ()!= null && tRiverRFirst.getZ() != null){
//                double v1 = Double.parseDouble(tRiverRLast.getZ());
//                double v2 = Double.parseDouble(tRiverRFirst.getZ());
//                if(v1 == v2){
//                    dto.setSituation(0);
//                }else if(v1 > v2){
//                    dto.setSituation(1);
//                }else {
//                    dto.setSituation(-1);
//                }
//            }
//            dataDetailDtos.add(dto);
//        }
        //获取最大流量和最小流量
        List<TRiverR> collectQ = riverRS.stream().filter(t -> t.getQ() != null).sorted(new Comparator<TRiverR>() {
            @Override
            public int compare(TRiverR o1, TRiverR o2) {
                double v1 = Double.parseDouble(o1.getQ());
                double v2 = Double.parseDouble(o2.getQ());
                return Double.compare(v1, v2);
            }
        }).collect(Collectors.toList());
        //获取最大水位和最小水位
        List<TRiverR> collectZ = riverRS.stream().filter(t -> t.getZ() != null).sorted(new Comparator<TRiverR>() {
            @Override
            public int compare(TRiverR o1, TRiverR o2) {
                double v1 = Double.parseDouble(o1.getZ());
                double v2 = Double.parseDouble(o2.getZ());
                return Double.compare(v1, v2);
            }
        }).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(collectQ)){
            double max = Double.parseDouble(format.format(Double.parseDouble(collectQ.get(collectQ.size() - 1).getQ())));
            double min = Double.parseDouble(format.format(Double.parseDouble(collectQ.get(0).getQ())));
            result.setMaxFlow(max);
            result.setMinFlow(min);
            result.setMaxFlowTm(collectQ.get(collectQ.size()-1).getTm());
            result.setMinFlowTm(collectQ.get(0).getTm());
        }
        if(!CollectionUtils.isEmpty(collectZ)){
            double max =  Double.parseDouble(format.format(Double.parseDouble(collectZ.get(collectZ.size() - 1).getZ())));
            double min =  Double.parseDouble(format.format(Double.parseDouble(collectZ.get(0).getZ())));
            result.setMaxWaterLevel(max);
            result.setMinWaterLevel(min);
            result.setMaxWaterLevelTm(collectZ.get(collectZ.size()-1).getTm());
            result.setMinWaterLevelTm(collectZ.get(0).getTm());
            high = max ;
            low = min ;
        }
        result.setHigh(Math.ceil(high));
        result.setLow(Math.floor(low));
        result.setRiverWayDataDetailDtos(dataDetailDtos);
        result.setStcd(stcd);
        return result;
    }

    @Override
    public Object getReservoirDataOnTime() {
        List<ReservoirDataDto> results = new ArrayList<>();
        //获取水库水文站个数
        List<StStbprpB> stStbprpBS = stStbprpBDao.findBySttp("RR");
        //库（湖）站汛限水位表 查询主汛期为1的
        List<TRsvrfsrB> tRsvrfsrBS = tRsvrfsrBDao.findByFstp("1");
        //获取水库警戒信息
        Map<String, TRsvrfsrB> collectWarningSK = tRsvrfsrBS.stream().collect(Collectors.toMap(TRsvrfsrB::getStcd, Function.identity()));
        List<Map<String, Object>> rsvrLastData = tRsvrRDao.getRsvrLastData();
        Map<String, Map<String, Object>> rsvrLastDataMap = rsvrLastData.stream().collect(Collectors.toMap(t -> t.get("stcd").toString(), Function.identity()));

        for (StStbprpB stStbprpB : stStbprpBS) {
            try {
                ReservoirDataDto dataDto = new ReservoirDataDto();
                BeanUtils.copyProperties(stStbprpB,dataDto);
                dataDto.setIsThanWaterLevelLine(0);
                String stcd = stStbprpB.getStcd();
                Map<String, Object> map = rsvrLastDataMap.get(stcd);
                if(map!=null && map.get("RZ") != null){
                    dataDto.setWaterLevel(Double.parseDouble(map.get("RZ").toString()));
                }
                TRsvrfsrB tRsvrfsrB = collectWarningSK.get(stcd);
                if(tRsvrfsrB != null ){
                    if ( tRsvrfsrB.getFsltdz() != null) {
                        dataDto.setWaterLevelLine(Double.parseDouble(tRsvrfsrB.getFsltdz()));
                    }
                    //汛险水位
                    double fsltdz = Double.parseDouble(tRsvrfsrB.getFsltdz()==null?"0":tRsvrfsrB.getFsltdz());
                    if(fsltdz < dataDto.getWaterLevel()){
                        dataDto.setIsThanWaterLevelLine(1);
                    }
                }
                results.add(dataDto);
            } catch (BeansException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

    @Override
    public Object getReservoirDataSingle(String stcd) {
        ReservoirDataSingleDto result = new ReservoirDataSingleDto();
        StStbprpB byStcd = stStbprpBDao.findByStcd(stcd);
        //库（湖）站防洪指标表
        TRsvrfcchB tRvfcchB = tRsvrfcchBDao.findByStcd(stcd);
        BeanUtils.copyProperties(byStcd,result);
        if(tRvfcchB != null){
            BeanUtils.copyProperties(tRvfcchB,result);
        }
        TRsvrfsrB tRsvrfsrBS = tRsvrfsrBDao.findByStcdAndFstp(stcd,"1");
        if(tRsvrfsrBS!=null){
            BeanUtils.copyProperties(tRsvrfsrBS,result);
        }
        return result;
    }

    @Override
    public Object getReservoirWaterLevelByTime(String stcd, String startTime, String endTime) throws ParseException {
        ReservoirDataTimeDto result = new ReservoirDataTimeDto();
        double low = 0;
        double high = 0;
        Date startDate = sdf.parse(startTime);
        Date endDate = sdf.parse(endTime);
        List<TRsvrR> tRsvrRS = tRsvrRDao.findByStcdAndTmBetweenOrderByTmDesc(stcd, startDate, endDate);
        List<ReservoirDataDetailDto> dataDetailDtos = new ArrayList<>();
        for (TRsvrR tRsvrR : tRsvrRS) {
            ReservoirDataDetailDto dto = new ReservoirDataDetailDto();
            //水位
            if(tRsvrR.getRz()!=null){
                double waterLevel = Double.parseDouble(tRsvrR.getRz());
                dto.setWaterLevel(waterLevel);
            }
            //入库流量
            if(tRsvrR.getInq()!=null){
                double inFlow = Double.parseDouble(tRsvrR.getInq());
                dto.setInFlow(inFlow);
            }
            //出库流量
            if(tRsvrR.getOtq()!=null){
                double outFlow = Double.parseDouble(tRsvrR.getOtq());
                dto.setOutFlow(outFlow);
            }
            dto.setWptn(tRsvrR.getRwptn());
            dto.setTm(tRsvrR.getTm());
            dataDetailDtos.add(dto);
        }
        //获取最大水位和最小水位
        List<TRsvrR> collectRZ = tRsvrRS.stream().filter(t -> t.getRz() != null).sorted(new Comparator<TRsvrR>() {
            @Override
            public int compare(TRsvrR o1, TRsvrR o2) {
                double v1 = Double.parseDouble(o1.getRz());
                double v2 = Double.parseDouble(o2.getRz());
                return Double.compare(v1, v2);
            }
        }).collect(Collectors.toList());
        //获取最大入库流量和最小入库流量
        List<TRsvrR> collectIQ = tRsvrRS.stream().filter(t -> t.getInq() != null).sorted(new Comparator<TRsvrR>() {
            @Override
            public int compare(TRsvrR o1, TRsvrR o2) {
                double v1 = Double.parseDouble(o1.getInq());
                double v2 = Double.parseDouble(o2.getInq());
                return Double.compare(v1, v2);
            }
        }).collect(Collectors.toList());
        //获取最大出库流量和最出库流量
        List<TRsvrR> collectOQ = tRsvrRS.stream().filter(t -> t.getInq() != null).sorted(new Comparator<TRsvrR>() {
            @Override
            public int compare(TRsvrR o1, TRsvrR o2) {
                double v1 = Double.parseDouble(o1.getOtq());
                double v2 = Double.parseDouble(o2.getOtq());
                return Double.compare(v1, v2);
            }
        }).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(collectIQ)){
            double max = Double.parseDouble(collectIQ.get(collectIQ.size() - 1).getInq());
            double min = Double.parseDouble(collectIQ.get(0).getInq());
            result.setMaxInFlow(max);
            result.setMinInFlow(min);
            result.setMaxInFlowTm(collectIQ.get(collectIQ.size()-1).getTm());
            result.setMinInFlowTm(collectIQ.get(0).getTm());
            high = max;
            low = min;
        }
        if(!CollectionUtils.isEmpty(collectRZ)){
            double max = Double.parseDouble(collectRZ.get(collectRZ.size() - 1).getRz());
            double min = Double.parseDouble(collectRZ.get(0).getRz());
            result.setMaxWaterLevel(max);
            result.setMinWaterLevel(min);
            result.setMaxWaterLevelTm(collectRZ.get(collectRZ.size()-1).getTm());
            result.setMinWaterLevelTm(collectRZ.get(0).getTm());
            high = max > high ? max : high;
            low = min < low ? min : low;
        }
        if(!CollectionUtils.isEmpty(collectOQ)){
            double max = Double.parseDouble(collectOQ.get(collectOQ.size() - 1).getOtq());
            double min = Double.parseDouble(collectOQ.get(0).getOtq());
            result.setMaxOutFlow(max);
            result.setMinOutFlow(min);
            result.setMaxOutFlowTm(collectOQ.get(collectOQ.size()-1).getTm());
            result.setMinOutFlowTm(collectOQ.get(0).getTm());
            high = max > high ? max : high;
            low = min < low ? min : low;
        }
        result.setHigh(Math.ceil(high));
        result.setLow(Math.floor(low));
        result.setReservoirDataDetailDtos(dataDetailDtos);
        result.setStcd(stcd);
        return result;
    }

    @Override
    public Object getWaterWayFloodWarningByTime(String startTime, String endTime) throws ParseException {
        FloodWarningDto result = new FloodWarningDto();
        List<RiverWayDataDto> surpassHistory = new ArrayList<>();
        List<RiverWayDataDto> surpassDesign = new ArrayList<>();
        List<RiverWayDataDto> surpassFloodLine = new ArrayList<>();
        List<RiverWayDataDto> surpassSafe = new ArrayList<>();
        //测站编码表
        List<StStbprpB> allList = stStbprpBDao.findAll();
        //获取河道站个数
        List<StStbprpB> stStbprpBS = allList.stream().filter(t -> "ZQ".equals(t.getSttp()) || "ZZ".equals(t.getSttp())).collect(Collectors.toList());
        //找到河道站点
        Map<String, StStbprpB> collectStation = stStbprpBS.stream().collect(Collectors.toMap(StStbprpB::getStcd, Function.identity()));
        Date startDate = sdf.parse(startTime);
        Date endDate = sdf.parse(endTime);
        List<Map<String, Object>> waterLevelMaxByTime = tRiverRDao.getWaterLevelMaxByTime(startDate, endDate);
        //24小时
        Date startDate24 = Date.from(LocalDateTime.ofInstant(endDate.toInstant(),ZoneId.systemDefault()).plusHours(-24).atZone(ZoneId.systemDefault()).toInstant());
        List<Map<String, Object>> waterLevelMaxBy24 = tRiverRDao.getWaterLevelMaxByTime(startDate24, endDate);
        Map<String, String> collectData = waterLevelMaxByTime.stream().filter(t -> t.get("stcd")!=null&&t.get("z")!=null).collect(Collectors.toMap(t -> t.get("stcd").toString(), t -> t.get("z").toString()));
        Map<String, String> collectData24 = waterLevelMaxBy24.stream().filter(t -> t.get("stcd")!=null&&t.get("z")!=null).collect(Collectors.toMap(t -> t.get("stcd").toString(), t -> t.get("z").toString()));
        //河道站防洪指标表
        List<TRvfcchB> tRvfcchBS = tRvfcchBDao.findAll();
        //获取河道站警戒信息
        Map<String, TRvfcchB> collectWarningHD = tRvfcchBS.stream().collect(Collectors.toMap(TRvfcchB::getStcd, Function.identity()));
        for (String stcd : collectStation.keySet()) {
            RiverWayDataDto dataDto = new RiverWayDataDto();
            StStbprpB stStbprpB = collectStation.get(stcd);
            BeanUtils.copyProperties(stStbprpB,dataDto);
            if(collectData.get(stcd) == null){
                //24
                surpassSafe.add(dataDto);
                continue;
            }
            double waterLevel = Double.parseDouble(collectData.get(stcd));
            dataDto.setWaterLevel(waterLevel);
            TRvfcchB tRvfcchB = collectWarningHD.get(stcd);
            if(tRvfcchB != null){
                String wrz = tRvfcchB.getWrz();
                //超过警戒
                if(wrz!=null&&Double.parseDouble(wrz)<waterLevel){
                    surpassFloodLine.add(dataDto);
                }
                String grz = tRvfcchB.getGrz();
                //超过保证
                if(grz!=null&&Double.parseDouble(grz)<waterLevel){
                    surpassDesign.add(dataDto);
                }
                //超历史
                String obhtz = tRvfcchB.getObhtz();
                if(obhtz!=null&&Double.parseDouble(obhtz)<waterLevel){
                    surpassHistory.add(dataDto);
                }
            }else {
                surpassSafe.add(dataDto);
                continue;
            }
            //剩余24
            if(collectData24.get(stcd) == null){
                //24
                surpassSafe.add(dataDto);
                continue;
            }
            double waterLevel24 = Double.parseDouble(collectData24.get(stcd));
            dataDto.setWaterLevel(waterLevel24);
            String wrz = tRvfcchB.getWrz();
            String grz = tRvfcchB.getGrz();
            String obhtz = tRvfcchB.getObhtz();
            if(wrz!=null&&Double.parseDouble(wrz)>waterLevel&&grz!=null&&Double.parseDouble(grz)>waterLevel&&obhtz!=null&&Double.parseDouble(obhtz)>waterLevel){
                surpassSafe.add(dataDto);
            }
        }
        result.setSurpassHistory(surpassHistory);
        result.setSurpassDesign(surpassDesign);
        result.setSurpassFloodLine(surpassFloodLine);
        result.setSurpassSafe(surpassSafe);
        return result;
    }

    @Override
    public Object getWaterWayFloodWarningDetailByTime(String startTime, String endTime) throws ParseException {
        WaterWayFloodWarningCountDto result = new WaterWayFloodWarningCountDto();
//        List<WaterWayFloodWarningDetailDto> surpassHistory = new ArrayList<>();
//        List<WaterWayFloodWarningDetailDto> surpassDesign = new ArrayList<>();
//        List<WaterWayFloodWarningDetailDto> surpassFloodLine = new ArrayList<>();
//        List<WaterWayFloodWarningDetailDto> surpassSafe = new ArrayList<>();
        List<WaterWayFloodWarningDetailDto> surpassList = new ArrayList<>();
        Integer surpassHistoryCount = 0;
        Integer surpassDesignCount = 0;
        Integer surpassFloodLineCount = 0;
        Integer surpassSafeCount = 0;

        //测站编码表
        List<StStbprpB> allList = stStbprpBDao.findAll();
        //获取河道站个数
        List<StStbprpB> stStbprpBS = allList.stream().filter(t -> "ZQ".equals(t.getSttp()) || "ZZ".equals(t.getSttp())).collect(Collectors.toList());
        //找到河道站点
        Map<String, StStbprpB> collectStation = stStbprpBS.stream().collect(Collectors.toMap(StStbprpB::getStcd, Function.identity()));
        Date startDate = sdf.parse(startTime);
        Date endDate = sdf.parse(endTime);
        List<TRiverR> tRiverRS = tRiverRDao.findByTmBetweenOrderByTmDesc(startDate, endDate);
        //24小时
        Date startDate24 = Date.from(LocalDateTime.ofInstant(endDate.toInstant(),ZoneId.systemDefault()).plusHours(-24).atZone(ZoneId.systemDefault()).toInstant());
        List<TRiverR> tRiverRS24 = tRiverRDao.findByTmBetweenOrderByTmDesc(startDate24, endDate);
        Map<String, List<TRiverR>> collectData = tRiverRS.stream().filter(t->t!=null).filter(t -> t.getStcd() != null && t.getZ() != null).collect(Collectors.groupingBy(TRiverR::getStcd));
        Map<String, List<TRiverR>> collectData24 = tRiverRS24.stream().filter(t -> t.getStcd() != null && t.getZ() != null).collect(Collectors.groupingBy(TRiverR::getStcd));
        //河道站防洪指标表
        List<TRvfcchB> tRvfcchBS = tRvfcchBDao.findAll();
        //获取河道站警戒信息
        Map<String, TRvfcchB> collectWarningHD = tRvfcchBS.stream().collect(Collectors.toMap(TRvfcchB::getStcd, Function.identity()));
        for (String stcd : collectData.keySet()) {
            WaterWayFloodWarningDetailDto dataDto = new WaterWayFloodWarningDetailDto();
            StStbprpB stStbprpB = collectStation.get(stcd);
            BeanUtils.copyProperties(stStbprpB,dataDto);
            dataDto.setFlag24("0");
            if(collectData.get(stcd) == null){
                continue;
            }
//            //处理24小时数据
//            List<TRiverR> list24 = collectData24.get(stcd);
//            double waterLevel24 =0.0;
//            if(!CollectionUtils.isEmpty(list24)&&list24.get(0).getZ()!=null){
//                waterLevel24 = Double.parseDouble(list24.get(0).getZ());
//                dataDto.setWaterLevelNew(waterLevel24);
//            }
            List<TRiverR> tRiverRList = collectData.get(stcd).stream().filter(t -> t.getStcd() != null && t.getZ() != null).sorted(new Comparator<TRiverR>() {
                @Override
                public int compare(TRiverR o1, TRiverR o2) {
                    double v1 = Double.parseDouble(o1.getZ());
                    double v2 = Double.parseDouble(o2.getZ());
                    return Double.compare(v2, v1);
                }
            }).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(tRiverRList)||tRiverRList.get(0).getZ() == null){
                continue;
            }
            double waterLevel = Double.parseDouble(tRiverRList.get(0).getZ());
            dataDto.setWaterLevel(waterLevel);
            dataDto.setWaterLevelTm(tRiverRList.get(0).getTm());
            dataDto.setFlow(tRiverRList.get(0).getQ());
            dataDto.setWptn(tRiverRList.get(0).getWptn());
            TRvfcchB tRvfcchB = collectWarningHD.get(stcd);
            if(tRvfcchB != null){
                //超过警戒
                String wrz = tRvfcchB.getWrz();
                if(wrz!=null){
                    double v = Double.parseDouble(wrz);
                    dataDto.setWaterLevelLine(v);
                    if(v<waterLevel){
                        dataDto.setWaterLevelLineDistance(waterLevel-v);
                        surpassFloodLineCount++;
                    }
                }
                String grz = tRvfcchB.getGrz();
                //超过保证
                if(grz!=null){
                    double v = Double.parseDouble(grz);
                    dataDto.setWaterLevelDesign(v);
                    if(v<waterLevel){
                        dataDto.setWaterLevelDesignDistance(waterLevel-v);
                        surpassDesignCount++;
                    }
                }
                //超历史
                String obhtz = tRvfcchB.getObhtz();
                if(obhtz!=null){
                    double v = Double.parseDouble(obhtz);
                    dataDto.setWaterLevelHistory(v);
                    if(v<waterLevel){
                        dataDto.setWaterLevelHistoryDistance(waterLevel-v);
                        surpassHistoryCount++;
                    }
                }
                surpassList.add(dataDto);
                //24个数
//                String wrz24 = tRvfcchB.getWrz();
//                String grz24 = tRvfcchB.getGrz();
//                String obhtz24 = tRvfcchB.getObhtz();
//                if(wrz24!=null&&Double.parseDouble(wrz24)>waterLevel&&grz24!=null&&Double.parseDouble(grz24)>waterLevel&&obhtz24!=null&&Double.parseDouble(obhtz24)>waterLevel){
//                    surpassSafeCount++;
//                }
            }else {
                surpassList.add(dataDto);
                continue;
            }

        }
        Map<String, WaterWayFloodWarningDetailDto> collect = surpassList.stream().collect(Collectors.toMap(WaterWayFloodWarningDetailDto::getStcd, Function.identity()));
        for (StStbprpB stbprpB : stStbprpBS) {
            String stcd = stbprpB.getStcd();
            if(collectData24!=null&&collectData24.get(stcd)!=null){
                //有数据返回
                continue;
            }
            if(collect.get(stcd)!=null){
                WaterWayFloodWarningDetailDto waterWayFloodWarningDetailDto = collect.get(stcd);
                waterWayFloodWarningDetailDto.setFlag24("1");
                surpassSafeCount++;
            }else {
                WaterWayFloodWarningDetailDto dataDto = new WaterWayFloodWarningDetailDto();
                BeanUtils.copyProperties(stbprpB,dataDto);
                TRvfcchB tRvfcchB = collectWarningHD.get(stcd);
                if(tRvfcchB != null) {
                    //超过警戒
                    String wrz = tRvfcchB.getWrz();
                    if (wrz != null) {
                        double v = Double.parseDouble(wrz);
                        dataDto.setWaterLevelLine(v);
                    }
                    String grz = tRvfcchB.getGrz();
                    //超过保证
                    if (grz != null) {
                        double v = Double.parseDouble(grz);
                        dataDto.setWaterLevelDesign(v);
                    }
                    //超历史
                    String obhtz = tRvfcchB.getObhtz();
                    if (obhtz != null) {
                        double v = Double.parseDouble(obhtz);
                        dataDto.setWaterLevelHistory(v);
                    }
                }
                dataDto.setFlag24("1");
                surpassSafeCount++;
                surpassList.add(dataDto);
            }//todo else

        }
        result.setSurpassList(surpassList);
        result.setSurpassDesignCount(surpassDesignCount);
        result.setSurpassFloodLineCount(surpassFloodLineCount);
        result.setSurpassHistoryCount(surpassHistoryCount);
        result.setSurpassSafeCount(surpassSafeCount);
        return result;
    }

    @Override
    public Object getReservoirFloodWarningByTime(String startTime, String endTime) throws ParseException {
        FloodWarningDto result = new FloodWarningDto();
        List<ReservoirDataDto> surpassHistory = new ArrayList<>();
        List<ReservoirDataDto> surpassDesign = new ArrayList<>();
        List<ReservoirDataDto> surpassFloodLine = new ArrayList<>();
        List<ReservoirDataDto> surpassSafe = new ArrayList<>();
        //测站编码表
        List<StStbprpB> stStbprpBS = stStbprpBDao.findBySttp("RR");
        //找到水库站点
        Map<String, StStbprpB> collectStation = stStbprpBS.stream().collect(Collectors.toMap(StStbprpB::getStcd, Function.identity()));
        Date startDate = sdf.parse(startTime);
        Date endDate = sdf.parse(endTime);
        List<Map<String, Object>> waterLevelMaxByTime = tRsvrRDao.getWaterLevelMaxByTime(startDate, endDate);
        //24小时
         Date startDate24 = Date.from(LocalDateTime.ofInstant(endDate.toInstant(),ZoneId.systemDefault()).plusHours(-24).atZone(ZoneId.systemDefault()).toInstant());
        List<Map<String, Object>> waterLevelMaxBy24 = tRsvrRDao.getWaterLevelMaxByTime(startDate24, endDate);
        Map<String, String> collectData = waterLevelMaxByTime.stream().filter(t -> t.get("stcd")!=null&&t.get("rz")!=null).collect(Collectors.toMap(t -> t.get("stcd").toString(), t -> t.get("rz").toString()));
        Map<String, String> collectData24 = waterLevelMaxBy24.stream().filter(t -> t.get("stcd")!=null&&t.get("rz")!=null).collect(Collectors.toMap(t -> t.get("stcd").toString(), t -> t.get("rz").toString()));
        //水库站防洪指标表
        List<TRsvrfcchB> tRsvrfcchBS = tRsvrfcchBDao.findAll();
        //获取水库站警戒信息
        Map<String, TRsvrfcchB> collectWarningHD = tRsvrfcchBS.stream().collect(Collectors.toMap(TRsvrfcchB::getStcd, Function.identity()));
        //库（湖）站汛限水位表 查询主汛期为1的
        List<TRsvrfsrB> tRsvrfsrBS = tRsvrfsrBDao.findByFstp("1");
        //获取水库警戒信息
        Map<String, TRsvrfsrB> collectWarningSK = tRsvrfsrBS.stream().collect(Collectors.toMap(TRsvrfsrB::getStcd, Function.identity()));

        for (String stcd : collectStation.keySet()) {
            ReservoirDataDto dataDto = new ReservoirDataDto();
            StStbprpB stStbprpB = collectStation.get(stcd);
            BeanUtils.copyProperties(stStbprpB,dataDto);
            if(collectData.get(stcd) == null){
                //24
                surpassSafe.add(dataDto);
                continue;
            }
            double waterLevel = Double.parseDouble(collectData.get(stcd));
            dataDto.setWaterLevel(waterLevel);
            TRsvrfcchB tRvfcchB = collectWarningHD.get(stcd);
            //找出汛限标准
            TRsvrfsrB tRsvrfsrB = collectWarningSK.get(stcd);
            if(tRvfcchB != null){
                String hhrz = tRvfcchB.getHhrz();
                //超过历史
                if(hhrz!=null&&Double.parseDouble(hhrz)<waterLevel){
                    surpassHistory.add(dataDto);
                }
                String dsflz = tRvfcchB.getDsflz();
                //超过设计
                if(dsflz!=null&&Double.parseDouble(dsflz)<waterLevel){
                    surpassDesign.add(dataDto);
                }
                if(tRsvrfsrB != null){
                    //超汛限
                    String fsltdz = tRsvrfsrB.getFsltdz();
                    if(fsltdz!=null&&Double.parseDouble(fsltdz)<waterLevel){
                        surpassFloodLine.add(dataDto);
                    }
                }
            }else if(tRsvrfsrB != null){
                    //超汛限
                    String fsltdz = tRsvrfsrB.getFsltdz();
                    if(fsltdz!=null&&Double.parseDouble(fsltdz)<waterLevel){
                        surpassFloodLine.add(dataDto);
                    }
            }else {
                surpassSafe.add(dataDto);
                continue;
            }
            //剩余24
            if(collectData24.get(stcd) == null){
                //24
                surpassSafe.add(dataDto);
                continue;
            }
            double waterLevel24 = Double.parseDouble(collectData24.get(stcd));
            dataDto.setWaterLevel(waterLevel24);
            String dsflz = tRvfcchB.getDsflz();
            String fsltdz = tRsvrfsrB.getFsltdz();
            String hhrz = tRvfcchB.getHhrz();
            if(dsflz!=null&&Double.parseDouble(dsflz)>waterLevel&&fsltdz!=null&&Double.parseDouble(fsltdz)>waterLevel&&hhrz!=null&&Double.parseDouble(hhrz)>waterLevel){
                surpassSafe.add(dataDto);
            }
        }
        result.setSurpassHistory(surpassHistory);
        result.setSurpassDesign(surpassDesign);
        result.setSurpassFloodLine(surpassFloodLine);
        result.setSurpassSafe(surpassSafe);
        return result;
    }

    @Override
    public Object getReservoirFloodWarningDetailByTime(String startTime, String endTime) throws ParseException {
        WaterWayFloodWarningCountDto result = new WaterWayFloodWarningCountDto();
        List<ReservoirFloodWarningDetailDto> surpassList = new ArrayList<>();
        Integer surpassHistoryCount = 0;
        Integer surpassDesignCount = 0;
        Integer surpassFloodLineCount = 0;
        Integer surpassSafeCount = 0;

        //测站编码表
        List<StStbprpB> stStbprpBS = stStbprpBDao.findBySttp("RR");
        //找到水库站点
        Map<String, StStbprpB> collectStation = stStbprpBS.stream().collect(Collectors.toMap(StStbprpB::getStcd, Function.identity()));
        Date startDate = sdf.parse(startTime);
        Date endDate = sdf.parse(endTime);
        List<TRsvrR> waterLevelMaxByTime = tRsvrRDao.findByTmBetweenOrderByTmDesc(startDate, endDate);
        //24小时
        Date startDate24 = Date.from(LocalDateTime.ofInstant(endDate.toInstant(),ZoneId.systemDefault()).plusHours(-24).atZone(ZoneId.systemDefault()).toInstant());
        List<TRsvrR> waterLevelMaxBy24 = tRsvrRDao.findByTmBetweenOrderByTmDesc(startDate24, endDate);
        Map<String, List<TRsvrR>> collectData = waterLevelMaxByTime.stream().filter(t -> t != null && t.getStcd() != null).collect(Collectors.groupingBy(TRsvrR::getStcd));
        Map<String, List<TRsvrR>> collectData24 = waterLevelMaxBy24.stream().filter(t -> t != null && t.getStcd() != null).collect(Collectors.groupingBy(TRsvrR::getStcd));
        //水库站防洪指标表
        List<TRsvrfcchB> tRsvrfcchBS = tRsvrfcchBDao.findAll();
        //获取水库站警戒信息
        Map<String, TRsvrfcchB> collectWarning = tRsvrfcchBS.stream().collect(Collectors.toMap(TRsvrfcchB::getStcd, Function.identity()));
        //库（湖）站汛限水位表 查询主汛期为1的
        List<TRsvrfsrB> tRsvrfsrBS = tRsvrfsrBDao.findByFstp("1");
        //获取水库警戒信息
        Map<String, TRsvrfsrB> collectWarningSK = tRsvrfsrBS.stream().collect(Collectors.toMap(TRsvrfsrB::getStcd, Function.identity()));
        for (String stcd : collectData.keySet()) {
            ReservoirFloodWarningDetailDto dataDto = new ReservoirFloodWarningDetailDto();
            StStbprpB stStbprpB = collectStation.get(stcd);
            BeanUtils.copyProperties(stStbprpB,dataDto);
            dataDto.setFlag24("0");
            if(collectData.get(stcd) == null){
                continue;
            }
            List<TRsvrR> tRiverRList = collectData.get(stcd).stream().filter(t -> t.getStcd() != null && t.getRz() != null).sorted(new Comparator<TRsvrR>() {
                @Override
                public int compare(TRsvrR o1, TRsvrR o2) {
                    double v1 = Double.parseDouble(o1.getRz());
                    double v2 = Double.parseDouble(o2.getRz());
                    return Double.compare(v2, v1);
                }
            }).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(tRiverRList)||tRiverRList.get(0).getRz() == null){
                continue;
            }
            double waterLevel = Double.parseDouble(tRiverRList.get(0).getRz());
            dataDto.setWaterLevel(waterLevel);
            dataDto.setTm(tRiverRList.get(0).getTm());
            dataDto.setFlow(tRiverRList.get(0).getInq());
            dataDto.setFlag24("0");
            TRsvrfsrB tRvfcchB = collectWarningSK.get(stcd);
            TRsvrfcchB tRsvrfcchB = collectWarning.get(stcd);
            if(tRvfcchB != null){
                //超过汛险
                String fsltdz = tRvfcchB.getFsltdz();
                if(fsltdz!=null){
                    double v = Double.parseDouble(fsltdz);
                    dataDto.setWaterLevelLine(v);
                    if(v<waterLevel){
                        dataDto.setMessage(dataDto.getMessage()+" 超过汛限水位"+(waterLevel-v)+"m");
                        surpassFloodLineCount++;
                    }
                }
            }
            if(tRsvrfcchB != null){
                String dsflz = tRsvrfcchB.getDsflz();
                //超过设计=
                if(dsflz!=null){
                    double v = Double.parseDouble(dsflz);
                    dataDto.setWaterLevelDesign(v);
                    if(v<waterLevel){
                        dataDto.setMessage(dataDto.getMessage()+" 超过设计水位"+(waterLevel-v)+"m");
                        surpassDesignCount++;
                    }
                }
                //超历史
                String hhrz = tRsvrfcchB.getHhrz();
                if(hhrz!=null){
                    double v = Double.parseDouble(hhrz);
                    dataDto.setWaterLevelHistory(v);
                    if(v<waterLevel){
                        dataDto.setMessage(dataDto.getMessage()+" 超过历史最高水位"+(waterLevel-v)+"m");
                        surpassHistoryCount++;
                    }
                }

            }
            surpassList.add(dataDto);
        }
        Map<String, ReservoirFloodWarningDetailDto> collect = surpassList.stream().collect(Collectors.toMap(ReservoirFloodWarningDetailDto::getStcd, Function.identity()));
        for (StStbprpB stbprpB : stStbprpBS) {
            String stcd = stbprpB.getStcd();
            if(collectData24!=null&&collectData24.get(stcd)!=null){
                //有数据返回
                continue;
            }
            //无数据但是查询范围内有数
            if(collect.get(stcd)!=null){
                ReservoirFloodWarningDetailDto reservoirFloodWarningDetailDto = collect.get(stcd);
                reservoirFloodWarningDetailDto.setFlag24("1");
                surpassSafeCount++;
            }else {

                ReservoirFloodWarningDetailDto dataDto = new ReservoirFloodWarningDetailDto();
                BeanUtils.copyProperties(stbprpB,dataDto);

                TRsvrfsrB tRvfcchB = collectWarningSK.get(stcd);
                TRsvrfcchB tRsvrfcchB = collectWarning.get(stcd);
                if(tRvfcchB != null){
                    //汛险
                    String fsltdz = tRvfcchB.getFsltdz();
                    if(fsltdz!=null){
                        double v = Double.parseDouble(fsltdz);
                        dataDto.setWaterLevelLine(v);
                    }
                }
                if(tRsvrfcchB != null){
                    String dsflz = tRsvrfcchB.getDsflz();
                    //设计=
                    if(dsflz!=null){
                        double v = Double.parseDouble(dsflz);
                        dataDto.setWaterLevelDesign(v);
                    }
                    //历史
                    String hhrz = tRsvrfcchB.getHhrz();
                    if(hhrz!=null){
                        double v = Double.parseDouble(hhrz);
                        dataDto.setWaterLevelHistory(v);
                    }
                }
                dataDto.setFlag24("1");
                surpassSafeCount++;
                surpassList.add(dataDto);
            }

        }
        result.setSurpassList(surpassList);
        result.setSurpassDesignCount(surpassDesignCount);
        result.setSurpassFloodLineCount(surpassFloodLineCount);
        result.setSurpassHistoryCount(surpassHistoryCount);
        result.setSurpassSafeCount(surpassSafeCount);
        return result;
    }


}
