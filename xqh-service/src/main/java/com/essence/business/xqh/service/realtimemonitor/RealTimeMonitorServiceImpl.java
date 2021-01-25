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
                if(tRvfcchStandard != null && tRvfcchStandard.getWrz() != null){
                    dataDto.setWarningWaterLevel(Double.parseDouble(tRvfcchStandard.getWrz()));
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
                dto.setFlow(flow);
            }
            if(riverR.getZ()!=null){
                double waterLevel = Double.parseDouble(riverR.getZ());
                dto.setWaterLevel(waterLevel);
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
            double max = Double.parseDouble(collectQ.get(collectQ.size() - 1).getQ());
            double min = Double.parseDouble(collectQ.get(0).getQ());
            result.setMaxFlow(max);
            result.setMinFlow(min);
            result.setMaxFlowTm(collectQ.get(collectQ.size()-1).getTm());
            result.setMinFlowTm(collectQ.get(0).getTm());
        }
        if(!CollectionUtils.isEmpty(collectZ)){
            double max = Double.parseDouble(collectZ.get(collectZ.size() - 1).getZ());
            double min = Double.parseDouble(collectZ.get(0).getZ());
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
                String stcd = stStbprpB.getStcd();
                Map<String, Object> map = rsvrLastDataMap.get(stcd);
                if(map!=null && map.get("RZ") != null){
                    dataDto.setWaterLevel(Double.parseDouble(map.get("RZ").toString()));
                }
                TRsvrfsrB tRsvrfsrB = collectWarningSK.get(stcd);
                if(tRsvrfsrB != null && tRsvrfsrB.getFsltdz() != null){
                    dataDto.setWaterLevelLine(Double.parseDouble(tRsvrfsrB.getFsltdz()));
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
        List<RiverWayDataDetailDto> dataDetailDtos = new ArrayList<>();
//        //河道站防洪指标表
//        TRvfcchB tRvfcchB = tRvfcchBDao.findByStcd(stcd);
//        for (TRiverR riverR : riverRS) {
//            RiverWayDataDetailDto dto = new RiverWayDataDetailDto();
//            BeanUtils.copyProperties(tRvfcchB,dto);
//            if(riverR.getQ()!=null){
//                double flow = Double.parseDouble(riverR.getQ());
//                dto.setFlow(flow);
//            }
//            if(riverR.getZ()!=null){
//                double waterLevel = Double.parseDouble(riverR.getZ());
//                dto.setWaterLevel(waterLevel);
//            }
//            //获取警戒水位
//            String wrz = tRvfcchB.getWrz();
//            //距警戒
//            if(wrz!=null && riverR.getZ()!=null){
//                dto.setDistance(Double.parseDouble(riverR.getZ())-Double.parseDouble(wrz));
//            }
//            dto.setTm(riverR.getTm());
//            dto.setWptn(riverR.getWptn());
//            dataDetailDtos.add(dto);
//        }
//        //获取最大流量和最小流量
//        List<TRiverR> collectQ = riverRS.stream().filter(t -> t.getQ() != null).sorted(new Comparator<TRiverR>() {
//            @Override
//            public int compare(TRiverR o1, TRiverR o2) {
//                double v1 = Double.parseDouble(o1.getQ());
//                double v2 = Double.parseDouble(o2.getQ());
//                return Double.compare(v1, v2);
//            }
//        }).collect(Collectors.toList());
//        //获取最大水位和最小水位
//        List<TRiverR> collectZ = riverRS.stream().filter(t -> t.getZ() != null).sorted(new Comparator<TRiverR>() {
//            @Override
//            public int compare(TRiverR o1, TRiverR o2) {
//                double v1 = Double.parseDouble(o1.getZ());
//                double v2 = Double.parseDouble(o2.getZ());
//                return Double.compare(v1, v2);
//            }
//        }).collect(Collectors.toList());
//        if(!CollectionUtils.isEmpty(collectQ)){
//            double max = Double.parseDouble(collectQ.get(collectQ.size() - 1).getQ());
//            double min = Double.parseDouble(collectQ.get(0).getQ());
//            result.setMaxFlow(max);
//            result.setMinFlow(min);
//            result.setMaxFlowTm(collectQ.get(collectQ.size()-1).getTm());
//            result.setMinFlowTm(collectQ.get(0).getTm());
//            high = max;
//            low = min;
//        }
//        if(!CollectionUtils.isEmpty(collectZ)){
//            double max = Double.parseDouble(collectZ.get(collectZ.size() - 1).getZ());
//            double min = Double.parseDouble(collectZ.get(0).getZ());
//            result.setMaxWaterLevel(max);
//            result.setMinWaterLevel(min);
//            result.setMaxWaterLevelTm(collectZ.get(collectZ.size()-1).getTm());
//            result.setMinWaterLevelTm(collectZ.get(0).getTm());
//            high = max > high ? max : high;
//            low = min < low ? min : low;
//        }
//        result.setHigh(Math.ceil(high));
//        result.setLow(Math.floor(low));
//        result.setRiverWayDataDetailDtos(dataDetailDtos);
//        result.setStcd(stcd);
//        return result;

        return null;
    }


}
