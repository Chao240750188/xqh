package com.essence.business.xqh.service.rainfall;

import com.essence.business.xqh.api.rainanalyse.vo.RainAnalyseReq;
import com.essence.business.xqh.api.rainfall.dto.dzm.StationRainDto;
import com.essence.business.xqh.api.rainfall.dto.dzm.StationRainVgeDto;
import com.essence.business.xqh.api.rainfall.vo.RainDzmReq;
import com.essence.business.xqh.common.RainConstants;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.dao.fhybdd.StStsmtaskBDao;
import com.essence.business.xqh.dao.dao.rainanalyse.dto.StPptnCommonRainfall;
import com.essence.business.xqh.dao.dao.rainfall.TStsmtaskBOldDao;
import com.essence.business.xqh.dao.dao.rainfall.dto.THdmisTotalRainfallDto;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.fhybdd.StStsmtaskB;
import com.essence.business.xqh.dao.entity.rainfall.TStsmtaskBOld;
import com.essence.business.xqh.service.rainanalyse.strategy.*;
import com.essence.framework.util.DateUtil;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName RainFallDzmServiceImpl
 * @Description 雨量查询通用
 * @Author zhichao.xing
 * @Date 2020/7/2 11:47
 * @Version 1.0
 **/
@Service
public abstract class AbstractRainFallDzmService {

    private DecimalFormat format1 = new DecimalFormat("##.0");

    @Autowired
    StStbprpBDao tStbprpBOldDao;
    @Autowired
    StStsmtaskBDao stStsmtaskBDao;

    /**
     * @Description 策略模式
     * @Author xzc
     * @Date 11:07 2020/7/4
     * @return
     **/
    @Autowired
    RainStrategyHourOnly rainStrategyHourOnly;
    @Autowired
    RainStrategyHourByDay rainStrategyHourByDay;
    @Autowired
    RainStrategyDayOnly rainStrategyDayOnly;
    @Autowired
    RainStrategyMonthOnly rainStrategyMonthOnly;
    @Autowired
    RainStrategyYearOnly rainStrategyYearOnly;

    /**
     * @return java.util.Map<java.lang.String, java.lang.Object>
     * @Description 查询所以雨量站
     * @Author xzc
     * @Date 16:34 2020/7/4
     **/
    protected Map<String, Object> getRainStation() {
        //查询所有雨量站
        List<StStsmtaskB> stsmtaskBList = stStsmtaskBDao.findByPfl(1L);
        //查询所有开启的站
        List<StStbprpB> stbprpBList = tStbprpBOldDao.findByUsfl("1");
        List<String> stcdList = stbprpBList.stream().map(StStbprpB::getStcd).collect(Collectors.toList());
        //所有开启的站的map  <测站编码， this>
        Map<String, StStbprpB> stcdBprpMap = stbprpBList.stream().collect(Collectors.toMap(StStbprpB::getStcd, Function.identity()));
        //所有开启的雨量站
        List<StStsmtaskB> validRainList = stsmtaskBList.stream().filter(item -> stcdList.contains(item.getStcd())).collect(Collectors.toList());
        Map map = new HashMap(2);
        map.put(RainConstants.STCDBPRPMAP, stcdBprpMap);
        map.put(RainConstants.VALIDRAINLIST, validRainList);
        return map;
    }

    /**
     * @return com.essence.tzsyq.rainfall.entity.dzm.StationRainVgeDto
     * @Description 前几个小时的雨量等值面通用逻辑
     * @Author xzc
     * @Date 15:56 2020/7/10
     **/
    protected StationRainVgeDto getAllStationTotalRainfall(RainDzmReq req) {
        Map rainStation = getRainStation();
        //所有开启的站的map  <测站编码， this>
        Map<String, StStbprpB> stcdBprpMap = (Map<String, StStbprpB>) rainStation.get(RainConstants.STCDBPRPMAP);
        //所有开启的雨量站
        List<TStsmtaskBOld> validRainList = (List<TStsmtaskBOld>) rainStation.get(RainConstants.VALIDRAINLIST);

        //所有开启的雨量站编码
        Set<String> validStcdList = validRainList.stream().map(TStsmtaskBOld::getStcd).collect(Collectors.toSet());

        List<THdmisTotalRainfallDto> dbCollect = getDbRainfall(req);
        List<StStbprpB> selectedAll = tStbprpBOldDao.findByAdmauthIn(req.getSource());
        Set<String> selectedStcdList = selectedAll.stream().map(StStbprpB::getStcd).collect(Collectors.toSet());
        //交集
        Sets.SetView<String> intersection = Sets.intersection(validStcdList, selectedStcdList);
        System.out.println("交集为：" + intersection);

        List<THdmisTotalRainfallDto> collect = dbCollect.stream().filter(item ->
                selectedStcdList.contains(item.getStcd()) && validStcdList.contains(item.getStcd())).collect(Collectors.toList());
        List<String> forStcds = collect.stream().map(THdmisTotalRainfallDto::getStcd).collect(Collectors.toList());
        Map<String, Double> stcdAndDrpMap = collect.stream().collect(
                Collectors.groupingBy(THdmisTotalRainfallDto::getStcd, Collectors.summingDouble(THdmisTotalRainfallDto::getDrp)));
        StationRainVgeDto stationRainVgeDto = new StationRainVgeDto();
        List<StationRainDto> stationRainList = new ArrayList<>();
        Double vegrage = 0.0;
        intersection.forEach(stcd -> {
            // 全站累计雨量，为了求平均值。
            StationRainDto stationRainDto = new StationRainDto();
            StStbprpB stStbprpB = stcdBprpMap.get(stcd);
            stationRainDto.setLgtd(stStbprpB.getLgtd());
            stationRainDto.setLttd(stStbprpB.getLttd());
            stationRainDto.setStnm(stStbprpB.getStnm());
            stationRainDto.setStcd(stcd);
            Double drp = stcdAndDrpMap.get(stcd);
            if (!ObjectUtils.isEmpty(drp)) {
                stationRainDto.setP(drp);
            } else {
                stationRainDto.setP(0.0D);
            }
            stationRainList.add(stationRainDto);
        });
        stationRainVgeDto.setList(stationRainList);
        return stationRainVgeDto;
    }

    /**
     * @param req
     * @return java.util.List<com.essence.tzsyq.rainfall.dto.THdmisTotalRainfallDto>
     * @Description 累积雨量的计算 子类自行实现
     * @Author xzc
     * @Date 15:33 2020/7/2
     * @Return List<THdmisTotalRainfallDto>
     **/
    protected abstract List<THdmisTotalRainfallDto> getDbRainfall(RainDzmReq req);


    protected List<StPptnCommonRainfall> getCurrentYear() {
        RainAnalyseReq reqMonth = new RainAnalyseReq();
        reqMonth.setType(3);
        LocalDate localDate = LocalDate.now();
        int year = localDate.getYear();
        reqMonth.setStartMonth(DateUtil.getDateByStringDay(year + "-01-01"));
        reqMonth.setEndMonth(new Date());
        List<StPptnCommonRainfall> commonRainfalls1 = dbFindByMonth(reqMonth);
        return commonRainfalls1;
    }


   /* protected List<StPptnCommonRainfall> dbFindByDay(RainAnalyseReq req) {
        List<StPptnCommonRainfall> currentMonth = null;
        String startDay = DateUtil.dateToStringDay(req.getStartDay());
        String endDay = DateUtil.dateToStringDay(req.getEndDay());
        List<StPptnCommonRainfall> currentHoursDb = new ArrayList<>();
        List<StPptnCommonRainfall> historyDay = new ArrayList<>();
        List<StPptnCommonRainfall> currentListTotalNew = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        String todayDay = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        //今天
        if (endDay.compareTo(todayDay) == 0) {
            req.setHourOnlyFlag(false);
            RainAanlyseContext rainAanlyseContext = new RainAanlyseContext(rainStrategyHourOnly);
            currentHoursDb = rainAanlyseContext.contextInterface(req);
            Map<String, List<StPptnCommonRainfall>> currentMap = currentHoursDb.stream().collect(Collectors.groupingBy(StPptnCommonRainfall::getStcd));
            currentMap.forEach((key, value) -> {
                double sum = value.stream().collect(Collectors.summarizingDouble(StPptnCommonRainfall::getDrp)).getSum();
                StPptnCommonRainfall current = new StPptnCommonRainfall();
                current.setStcd(key);
                current.setDrp(sum);
                current.setTm(new Timestamp(System.currentTimeMillis()));
                currentListTotalNew.add(current);
            });
        }
        //历史天
        if (DateUtil.getDateByStringDay2(startDay).before(DateUtil.getDateByStringDay(todayDay))) {
            RainAanlyseContext rainAanlyseContext = new RainAanlyseContext(rainStrategyDayOnly);
            historyDay = rainAanlyseContext.contextInterface(req);
        }
        currentListTotalNew.addAll(historyDay);
        currentMonth = currentListTotalNew;
        currentMonth.forEach(item -> {
            item.setShowTm(DateUtil.dateToStringWithFormat(item.getTm(), "yyyy_MM_dd"));
        });
        return currentMonth;
    }*/

    /**
     * 查询日数据
     *
     * @return java.util.List<com.essence.tzsyq.rainanalyse.dto.StPptnCommonRainfall>
     * @Author huangxiaoli
     * @Description
     * @Date 17:02 2020/11/2
     * @Param [req]
     **/
    protected List<StPptnCommonRainfall> dbFindByDay(RainAnalyseReq req) {
        List<StPptnCommonRainfall> currentMonth = null;
        LocalDateTime startLocalDateTime = req.getStartDay().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endLocalDateTime = req.getEndDay().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        //当前时间
        LocalDateTime nowLocalDateTime = LocalDateTime.now();

        //String startDay = DateUtil.dateToStringDay(req.getStartDay());
        // String endDay = DateUtil.dateToStringDay(req.getEndDay());
        List<StPptnCommonRainfall> currentHoursDb = new ArrayList<>();
        List<StPptnCommonRainfall> historyDay = new ArrayList<>();
        List<StPptnCommonRainfall> currentListTotalNew = new ArrayList<>();

        //今天
        if (endLocalDateTime.getDayOfMonth() == nowLocalDateTime.getDayOfMonth()) {
            req.setHourOnlyFlag(false);
            RainAanlyseContext rainAanlyseContext = new RainAanlyseContext(rainStrategyHourOnly);
            currentHoursDb = rainAanlyseContext.contextInterface(req);
            if (currentHoursDb.size() > 0) {
                Map<String, List<StPptnCommonRainfall>> currentMap = currentHoursDb.stream().collect(Collectors.groupingBy(StPptnCommonRainfall::getStcd));
                currentMap.forEach((key, value) -> {
                    double sum = value.stream().collect(Collectors.summarizingDouble(StPptnCommonRainfall::getDrp)).getSum();
                    StPptnCommonRainfall current = new StPptnCommonRainfall();
                    current.setStcd(key);
                    current.setDrp(sum);
                    current.setTm(new Timestamp(System.currentTimeMillis()));
                    currentListTotalNew.add(current);
                });
            }

        }
        //历史天
        if (startLocalDateTime.getDayOfMonth() < nowLocalDateTime.getDayOfMonth()) {
            RainAanlyseContext rainAanlyseContext = new RainAanlyseContext(rainStrategyDayOnly);
            historyDay = rainAanlyseContext.contextInterface(req);
        }
        currentListTotalNew.addAll(historyDay);
        currentMonth = currentListTotalNew;
        currentMonth.forEach(item -> {
            item.setShowTm(DateUtil.dateToStringWithFormat(item.getTm(), "yyyy_MM_dd"));
        });
        return currentMonth;
    }


    protected List<StPptnCommonRainfall> dbFindByMonth(RainAnalyseReq req) {
        List<StPptnCommonRainfall> currentMonth = new ArrayList<>();
        String startMonth = DateUtil.dateToStringWithFormat(req.getStartMonth(), "yyyy-MM");
        String endMonth = DateUtil.dateToStringWithFormat(req.getEndMonth(), "yyyy-MM");
        List<StPptnCommonRainfall> currentDayDb = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        String todayMonth = now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        //当月
        if (endMonth.equals(todayMonth)) {
            RainAnalyseReq reqMonth = new RainAnalyseReq();
            reqMonth.setType(2);
            reqMonth.setStartDay(DateUtil.getDateByStringDay(todayMonth + "-01"));
            reqMonth.setEndDay(new Date());
            currentDayDb = dbFindByDay(reqMonth);
        }
        //历史yue
        List<StPptnCommonRainfall> historyDay = new ArrayList<>();
        if (startMonth.compareTo(todayMonth) < 0) {
            RainAanlyseContext rainAanlyseContext = new RainAanlyseContext(rainStrategyMonthOnly);
            historyDay = rainAanlyseContext.contextInterface(req);
        }
        currentMonth.addAll(currentDayDb);
        currentMonth.addAll(historyDay);
        currentMonth.forEach(item -> {
            item.setShowTm(DateUtil.dateToStringWithFormat(item.getTm(), "yyyy_MM"));
        });
        return currentMonth;
    }


    protected <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map, Boolean asc) {
        Map<K, V> result = new LinkedHashMap<>();

        if (asc) {
            map.entrySet().stream()
                    .sorted(Map.Entry.<K, V>comparingByKey()
                    ).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        } else {

            map.entrySet().stream()
                    .sorted(Map.Entry.<K, V>comparingByKey()
                            .reversed()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        }
        return result;
    }

    protected <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, Boolean asc) {
        Map<K, V> result = new LinkedHashMap<>();
        if (asc) {
            map.entrySet().stream()
                    .sorted(Map.Entry.<K, V>comparingByValue()
                    ).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        } else {
            map.entrySet().stream()
                    .sorted(Map.Entry.<K, V>comparingByValue()
                            .reversed()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        }
        return result;
    }

}
