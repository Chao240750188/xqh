package com.essence.business.xqh.service.rainfall;

import com.essence.business.xqh.api.rainfall.dto.*;
import com.essence.business.xqh.api.rainfall.service.RainFallService;
import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.api.rainfall.vo.RainDzmReq;
import com.essence.business.xqh.dao.dao.baseInfoManage.HbmAddvcdDDao;
import com.essence.business.xqh.dao.dao.fhybdd.StPptnRDao;
import com.essence.business.xqh.dao.dao.rainfall.TRiverRDao;
import com.essence.business.xqh.dao.dao.rainfall.TStbprpBDao;
import com.essence.business.xqh.dao.dao.rainfall.dto.THdmisTotalRainfallDto;
import com.essence.business.xqh.dao.entity.baseInfoManage.HbmAddvcdD;
import com.essence.business.xqh.dao.entity.rainfall.StPptnR;
import com.essence.business.xqh.dao.entity.rainfall.TRiverR;
import com.essence.business.xqh.dao.entity.rainfall.TStbprpBOld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class RainFallServiceImpl extends AbstractRainFallDzmService implements RainFallService {

    Logger logger = LoggerFactory.getLogger(RainFallServiceImpl.class);

    @Autowired
    private HbmAddvcdDDao hbmAddvcdDDao;

    @Autowired
    TStbprpBDao tStbprpBDao;

    @Autowired
    private StPptnRDao pptnRDao;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    TRiverRDao tRiverRDao;

    @Override
    protected List<THdmisTotalRainfallDto> getDbRainfall(RainDzmReq req) {
        return null;
    }

    @Override
    public List<RainFallDto> getRainFallAllByTime(QueryParamDto dto) {
        List<RainFallDto> rainFallDtosList = new ArrayList<>();
        Map<String, RainFallDto> rainFallDtosMap = new HashMap<>();

        List<String> sourceList = new ArrayList<>();//dto.getSource(); xc 注掉
        sourceList.add("1");//取消图上的tab标签，然后默认查询全部的水务局、气象局、供排水事务中心
        sourceList.add("2");
        sourceList.add("3");
        LocalDateTime currentTime = LocalDateTime.now();
        //当前时间
        Date time = Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant());
        //前1小时
        Date preHourTime1 = Date.from(currentTime.plusHours(-1).atZone(ZoneId.systemDefault()).toInstant());
        //前2小时
        Date preHourTime2 = Date.from(currentTime.plusHours(-2).atZone(ZoneId.systemDefault()).toInstant());
        //前3小时
        Date preHourTime3 = Date.from(currentTime.plusHours(-3).atZone(ZoneId.systemDefault()).toInstant());
        //前12小时
        Date preHourTime12 = Date.from(currentTime.plusHours(-12).atZone(ZoneId.systemDefault()).toInstant());
        //前24小时
        Date preHourTime24 = Date.from(currentTime.plusHours(-24).atZone(ZoneId.systemDefault()).toInstant());
        //当日开始时间
        Date currentDayStartTime = Date.from(currentTime.withHour(8).withMinute(0).withSecond(0).atZone(ZoneId.systemDefault()).toInstant());

        //查询所有乡镇信息
        List<HbmAddvcdD> hbmAddvcdDList = hbmAddvcdDDao.findAll();
        Map<String, String> hbmAddvcdDMap = new HashMap<>();
        if (hbmAddvcdDList.size() > 0) {
            for (HbmAddvcdD hbmAddvcdD : hbmAddvcdDList) {
                hbmAddvcdDMap.put(hbmAddvcdD.getAddvcd(), hbmAddvcdD.getAddvnm());
            }
        }


        //查询筛选的所有开启的测站信息
        List<TStbprpBOld> stbprpBList = tStbprpBDao.findUseStationByAdmauthInAndSttp(sourceList, "PP");
        List<String> stcdList = new ArrayList<>();
        if (stbprpBList.size() > 0) {
            for (int i = 0; i < stbprpBList.size(); i++) {
                TStbprpBOld tStbprpBOld = stbprpBList.get(i);
                stcdList.add(tStbprpBOld.getStcd());

                RainFallDto rainFallDto = new RainFallDto();

                String addvcd = tStbprpBOld.getAddvcd();
                String addvnm = hbmAddvcdDMap.get(addvcd);
                if (null != addvnm) {
                    rainFallDto.setTownship(addvnm);
                }
                rainFallDto.setStcd(tStbprpBOld.getStcd());
                rainFallDto.setStnm(tStbprpBOld.getStnm());
                rainFallDto.setLgtd(tStbprpBOld.getLgtd());
                rainFallDto.setLttd(tStbprpBOld.getLttd());
                rainFallDto.setSource(tStbprpBOld.getLocality());
                rainFallDto.setOneh(0d);
                rainFallDto.setTwoh(0d);
                rainFallDto.setThreeh(0d);
                rainFallDto.setTwelveh(0d);
                rainFallDto.setTwentyh(0d);
                rainFallDto.setFrameh(0d);
                rainFallDto.setCurrenth(0d);
                rainFallDto.setCount(0d);
                rainFallDtosMap.put(tStbprpBOld.getStcd(), rainFallDto);
            }

            // 把前24小时查出来
            List<THdmisTotalRainfallDto> hdmisTotalRainfallDtoList = pptnRDao.queryByStcdInAndTmBetween(stcdList, preHourTime24, time);
            if (hdmisTotalRainfallDtoList.size() > 0) {
                for (THdmisTotalRainfallDto tHdmisTotalRainfallDto : hdmisTotalRainfallDtoList) {
                    String stcd = tHdmisTotalRainfallDto.getStcd();
                    Date tm = tHdmisTotalRainfallDto.getTm();
                    Double drp = tHdmisTotalRainfallDto.getDrp();

                    RainFallDto rainConditionDetailInfoDto = rainFallDtosMap.get(stcd);

                    //前1小时数据
                    if (tm.getTime() >= preHourTime1.getTime() && tm.getTime() <= time.getTime()) {
                        rainConditionDetailInfoDto.setOneh(rainConditionDetailInfoDto.getOneh() + drp);
                    }

                    //前2小时数据
                    if (tm.getTime() >= preHourTime2.getTime() && tm.getTime() <= time.getTime()) {
                        rainConditionDetailInfoDto.setTwoh(rainConditionDetailInfoDto.getTwoh() + drp);
                    }


                    //前3小时数据
                    if (tm.getTime() >= preHourTime3.getTime() && tm.getTime() <= time.getTime()) {
                        rainConditionDetailInfoDto.setThreeh(rainConditionDetailInfoDto.getThreeh() + drp);
                    }

                    //前12小时数据
                    if (tm.getTime() >= preHourTime12.getTime() && tm.getTime() <= time.getTime()) {
                        rainConditionDetailInfoDto.setTwelveh(rainConditionDetailInfoDto.getTwelveh() + drp);
                    }


                    //当日雨量数据
                    if (tm.getTime() >= currentDayStartTime.getTime() && tm.getTime() <= time.getTime()) {
                        rainConditionDetailInfoDto.setCurrenth(rainConditionDetailInfoDto.getCurrenth() + drp);
                    }


                    //前24小时雨量数据
                    rainConditionDetailInfoDto.setTwentyh(rainConditionDetailInfoDto.getTwentyh() + drp);
                    rainFallDtosMap.put(stcd, rainConditionDetailInfoDto);
                }
            }


            //查询时段数据
            if (!ObjectUtils.isEmpty(dto.getStartTime()) && !ObjectUtils.isEmpty(dto.getEndTime())) {
                // 时段
                List<THdmisTotalRainfallDto> tPptnRSRegionList = pptnRDao.queryByStcdInAndTmBetween(stcdList, dto.getStartTime(), dto.getEndTime());
                if (tPptnRSRegionList.size() > 0) {
                    for (THdmisTotalRainfallDto tHdmisTotalRainfallDto : tPptnRSRegionList) {
                        String stcd = tHdmisTotalRainfallDto.getStcd();
                        Double drp = tHdmisTotalRainfallDto.getDrp();

                        RainFallDto rainConditionDetailInfoDto = rainFallDtosMap.get(stcd);
                        rainConditionDetailInfoDto.setFrameh(rainConditionDetailInfoDto.getFrameh() + drp);
                        rainFallDtosMap.put(stcd, rainConditionDetailInfoDto);
                    }
                }
            }
        }


        for (String stcd : rainFallDtosMap.keySet()) {
            RainFallDto rainFallDto = rainFallDtosMap.get(stcd);

            //保留两位小数
            BigDecimal dayDrpBigDecimal = new BigDecimal(rainFallDto.getCurrenth());
            rainFallDto.setCurrenth(dayDrpBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

            BigDecimal preHour1BigDecimal = new BigDecimal(rainFallDto.getOneh());
            rainFallDto.setOneh(preHour1BigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

            BigDecimal preHour2BigDecimal = new BigDecimal(rainFallDto.getTwoh());
            rainFallDto.setTwoh(preHour2BigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());


            BigDecimal preHour3BigDecimal = new BigDecimal(rainFallDto.getThreeh());
            rainFallDto.setThreeh(preHour3BigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

            BigDecimal preHour12BigDecimal = new BigDecimal(rainFallDto.getTwelveh());
            rainFallDto.setTwelveh(preHour12BigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());


            BigDecimal preHour24BigDecimal = new BigDecimal(rainFallDto.getTwentyh());
            rainFallDto.setTwentyh(preHour24BigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

            BigDecimal preHourFramehBigDecimal = new BigDecimal(rainFallDto.getFrameh());
            rainFallDto.setFrameh(preHourFramehBigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

            rainFallDtosList.add(rainFallDto);
        }

        //将数据按当日雨量倒序排序
        rainFallDtosList.sort(Comparator.comparing(RainFallDto::getCurrenth).reversed());

        return rainFallDtosList;
    }


    @Override
    public RainFallDto getStationRainFallByTime(QueryParamDto dto) {
        LocalDateTime currentTime = LocalDateTime.now();
        Date time = Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant());
        Date startTime;
        switch (dto.getTimeSection()) {
            // 当日
            case "0":
                startTime = Date.from(
                        currentTime.withHour(8).withMinute(0).withSecond(0).atZone(ZoneId.systemDefault()).toInstant());
                break;
            case "1":
                startTime = Date.from(currentTime.plusHours(-1).atZone(ZoneId.systemDefault()).toInstant());
                break;
            case "2":
                startTime = Date.from(currentTime.plusHours(-2).atZone(ZoneId.systemDefault()).toInstant());
                break;
            case "3":
                startTime = Date.from(currentTime.plusHours(-3).atZone(ZoneId.systemDefault()).toInstant());
                break;
            case "12":
                startTime = Date.from(currentTime.plusHours(-12).atZone(ZoneId.systemDefault()).toInstant());
                break;
            case "24":
                startTime = Date.from(currentTime.plusHours(-24).atZone(ZoneId.systemDefault()).toInstant());
                break;
            // 时段
            default:
                startTime = dto.getStartTime();
                time = dto.getEndTime();
                break;
        }
        // 把前24小时查出来
        List<StPptnR> tPptnRS = pptnRDao.findByStcdAndTmBetweenOrderByTm(dto.getStcd(), startTime, time);
        RainFallDto stationRainFall = getStationRainFall(tPptnRS, startTime, time, dto.getStcd());
        return stationRainFall;
    }


    private RainFallDto getStationRainFall(List<StPptnR> list, Date startTime, Date endTime, String stcd) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<RainFallDto> resultList = new ArrayList<>();
        LocalDateTime startLocalTime = LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.systemDefault());
        LocalDateTime endLocalTime = LocalDateTime.ofInstant(endTime.toInstant(), ZoneId.systemDefault());
        // 总降雨量
        double sum;
        List<ValueTimeDto> countL = new ArrayList<>();
        if (list != null && list.size() != 0) {
            sum = list.stream().filter(TPptnR -> TPptnR.getDrp() != null)
                    .collect(Collectors.summarizingDouble(StPptnR::getDrp)).getSum();
            // 找到时段降雨量
            // int judgeNum = endLocalTime.getHour() - startLocalTime.getHour();
            long end = endLocalTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
            long start = startLocalTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
            int judgeNum = (int) ((end - start) / (1000 * 60 * 60));
            if (judgeNum > 3 || judgeNum == 0) {
                // 说明是非前3小时，时间间隔为一个小时
                for (int i = 0; i <= 24; i++) {

                    LocalDateTime startLocal = startLocalTime;
                    LocalDateTime judgeTime = startLocalTime.plusHours(1);

                    if (judgeTime.isBefore(endLocalTime)) {
                        // 开始时间在结束时间之前
                        double spaceSum = list.stream()
                                .filter(TPptnR -> TPptnR.getTm()
                                        .after(Date.from(startLocal.atZone(ZoneId.systemDefault()).toInstant())))
                                .filter(TPptnR -> TPptnR.getTm()
                                        .before(Date.from(judgeTime.atZone(ZoneId.systemDefault()).toInstant())))
                                .collect(Collectors.summarizingDouble(StPptnR::getDrp)).getSum();
                        ValueTimeDto dto = new ValueTimeDto();
                        dto.setValue(doubel2BigDecimal(spaceSum).doubleValue());
                        dto.setTime(judgeTime.format(DateTimeFormatter.ofPattern("y-MM-d HH:mm")));
                        countL.add(dto);
                    }
                    startLocalTime = judgeTime;
                }
            } else {
                // 时间间隔为10分钟
                double spaceCount = 0;
                for (int i = 1; i <= list.size(); i++) {
                    Date tm = list.get(i - 1).getTm();
                    Double drp = list.get(i - 1).getDrp();
                    String spaceTime = sdf.format(tm);
                    spaceCount += drp;
                    if (i % 2 == 0) {
                        ValueTimeDto dto = new ValueTimeDto();
                        dto.setTime(spaceTime);
                        dto.setValue(doubel2BigDecimal(spaceCount).doubleValue());
                        countL.add(dto);
                        spaceCount = 0;
                    }
                }
            }
        } else {
            sum = 0.0;
        }
        RainFallDto fallDto = new RainFallDto();
        fallDto.setCount(doubel2BigDecimal(sum).doubleValue());
        fallDto.setStcd(stcd);
        fallDto.setCountL(countL);
        resultList.add(fallDto);
        return fallDto;
    }


    private BigDecimal doubel2BigDecimal(Double avgDrp) {
        if (ObjectUtils.isEmpty(avgDrp)) {
            return null;
        }

        return BigDecimal.valueOf(avgDrp.doubleValue()).setScale(1, RoundingMode.HALF_UP);
    }


    /**
     * 实时水情
     *
     * @return java.util.List<com.essence.tzsyq.rainfall.dto.WalterLevelDto>
     * @Author huangxiaoli
     * @Description
     * @Date 17:57 2020/9/11
     * @Param [dto]
     **/
    @Override
    public List<WalterLevelDto> getWaterLevelByTime(QueryParamDto dto) {
        List<WalterLevelDto> walterLevelDtoList = new ArrayList<>();
        Map<String, WalterLevelDto> walterLevelDtoMap = new HashMap<>();

        //查询各个测站最新水位(两天内的最新水位，两天内无数据，则实测水位为空)
        LocalDateTime currentTime = LocalDateTime.now();
        Date time = Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant());
        Date preDayTime2 = Date.from(currentTime.plusDays(-2).atZone(ZoneId.systemDefault()).toInstant());
        List<String> source = new ArrayList<>();  //dto.getSource();
        source.add("1");
        source.add("4");
        List<TStbprpBOld> tStbprpBSListOld = tStbprpBDao.findUseStationByAdmauthInAndSttp(source, "ZZ");
        List<String> stcdList = new ArrayList<>();
        if (tStbprpBSListOld.size() > 0) {
            for (TStbprpBOld tStbprpBOld : tStbprpBSListOld) {
                stcdList.add(tStbprpBOld.getStcd());

                WalterLevelDto walterLevelDto = new WalterLevelDto();
                walterLevelDto.setStcd(tStbprpBOld.getStcd());
                walterLevelDto.setStnm(tStbprpBOld.getStnm());
                walterLevelDto.setRvnm(tStbprpBOld.getRvnm());
                walterLevelDto.setLgtd(tStbprpBOld.getLgtd());
                walterLevelDto.setLttd(tStbprpBOld.getLttd());
                List<Double> waterLevelList = new ArrayList<>();
                walterLevelDto.setWaterlevel(waterLevelList);
                walterLevelDtoMap.put(tStbprpBOld.getStcd(), walterLevelDto);
            }

            //查询最新水位
            AggregationOptions latestAggregationOptions = AggregationOptions.builder().allowDiskUse(true).build();
            Aggregation latestAggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("stcd").in(stcdList)),
                    Aggregation.match(Criteria.where("tm").gte(preDayTime2)),
                    Aggregation.match(Criteria.where("tm").lte(time)),
                    Aggregation.group("stcd").max("tm").as("tm").first("z").as("z").first("stcd").as("stcd")).withOptions(latestAggregationOptions);

            AggregationResults<TRiverR> latestStRiverR = mongoTemplate.aggregate(latestAggregation, "st_river_r", TRiverR.class);
            List<TRiverR> data = latestStRiverR.getMappedResults();
            if (data.size() > 0) {
                for (TRiverR datum : data) {
                    WalterLevelDto walterLevelDto = walterLevelDtoMap.get(datum.getStcd());
                    List<Double> waterlevelList = walterLevelDto.getWaterlevel();
                    waterlevelList.add(datum.getZ());
                    walterLevelDto.setUpdateTime(datum.getTm());
                    walterLevelDtoMap.put(walterLevelDto.getStcd(), walterLevelDto);
                }
            }
        }


        //整合数据
        for (String stcd : walterLevelDtoMap.keySet()) {
            WalterLevelDto walterLevelDto = walterLevelDtoMap.get(stcd);
            walterLevelDtoList.add(walterLevelDto);
        }

        walterLevelDtoList.sort(Comparator.comparing(WalterLevelDto::getRvnm).thenComparing(Comparator.comparing(WalterLevelDto::getStnm)));

        return walterLevelDtoList;
    }


    @Override
    public StationWaterDto getWaterLevelByStationAndTime(QueryParamDto dto) {
        String name = dto.getName();
        List<TStbprpBOld> tStbprpBOlds = tStbprpBDao.findByName(name);
        List<String> stcds = new ArrayList<>();
        Map<String, String> nameMap = new HashMap<>(2);
        for (TStbprpBOld tStbprpBOld : tStbprpBOlds) {
            String stcd = tStbprpBOld.getStcd();
            String stnm = tStbprpBOld.getStnm();
            stcds.add(stcd);
            nameMap.put(stcd, stnm);
        }
        List<TRiverR> tRiverRS = tRiverRDao.findByStcdInAndTmIsBetweenOrderByTm(stcds, dto.getStartTime(),
                dto.getEndTime());
        StationWaterDto stationWaterDto = new StationWaterDto();
        Map<String, List<TRiverR>> collect = tRiverRS.stream().collect(Collectors.groupingBy(TRiverR::getStcd));
        for (String stcd : collect.keySet()) {
            List<TRiverR> tRiverRS1 = collect.get(stcd);
            List<TRiverRDto> tRiverDto = new ArrayList();
            if (!CollectionUtils.isEmpty(tRiverRS1)) {
                for (TRiverR t : tRiverRS1) {
                    TRiverRDto d = new TRiverRDto();
                    BeanUtils.copyProperties(t, d);
                    tRiverDto.add(d);
                }
            }
            if (nameMap.get(stcd).indexOf("上") != -1) {
                stationWaterDto.setGateUp(tRiverDto);
            } else if (nameMap.get(stcd).indexOf("下") != -1) {
                stationWaterDto.setGateDown(tRiverDto);
            } else {
                stationWaterDto.setGateUp(tRiverDto);
            }
        }
        return stationWaterDto;
    }
}