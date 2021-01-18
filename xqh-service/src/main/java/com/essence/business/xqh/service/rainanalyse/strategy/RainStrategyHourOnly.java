package com.essence.business.xqh.service.rainanalyse.strategy;

import com.essence.business.xqh.api.rainanalyse.vo.RainAnalyseReq;
import com.essence.business.xqh.dao.dao.rainanalyse.StPptnHourRainfallDao;
import com.essence.business.xqh.dao.dao.rainanalyse.dto.StPptnCommonRainfall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * @ClassName RainStrategyHour
 * @Description 小时策略
 * @Author zhichao.xing
 * @Date 2020/7/3 20:37
 * @Version 1.0
 **/
@Component
public class RainStrategyHourOnly extends Strategy {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    StPptnHourRainfallDao stPptnHourRainfallDao;
    @Autowired
    private MongoTemplate mongoTemplate;

    /*@Override
    public List<StPptnCommonRainfall> algorithmInterface(RainAnalyseReq req) {
        String hourFormat = "yyyy-MM-dd HH";
        List<StPptnCommonRainfall> list = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        String todayDay = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        //今天
        // 0 点到 hours 的走小时表
        String dateHours = now.format(DateTimeFormatter.ofPattern(hourFormat));
        List<StPptnCommonRainfall> historyList = stPptnHourRainfallDao.findByTmBetween(DateUtil.getDateByStringDay(todayDay), DateUtil.getDateByStringDay(dateHours));
//        List<StPptnCommonRainfall> historyList = BeanUtil.mapCopy(byTmBetween, StPptnCommonRainfall.class);
        // 实时表  // TODO: 2020/7/4  若 执行在 23:59：59秒  此处时间 需要加上小于 （次日的0点0分0秒）
//        List<Map<String, Object>> mapList = pptnRDao.queryRainfallByTimeAfter(DateUtil.getDateWithFormat(dateHours, hourFormat));
//        List<StPptnCommonRainfall> currentList = BeanUtil.mapCopy(mapList, StPptnCommonRainfall.class);
        if (!req.getHourOnlyFlag()) {
            logger.info("当前是小时only  走 日月年的查询逻辑");
//            historyList.addAll(currentList);
            return historyList;
        }
        logger.info("当前是小时only  走 小时的查询逻辑，下面进行showTm赋值");
        List<StPptnCommonRainfall> currentListNew = new ArrayList<>();
//        if (!CollectionUtils.isEmpty(currentList)) {
//            Map<String, List<StPptnCommonRainfall>> currentMap = currentList.stream().collect(Collectors.groupingBy(StPptnCommonRainfall::getStcd));
//            currentMap.forEach((key, value) -> {
//                double sum = currentList.stream().collect(Collectors.summarizingDouble(StPptnCommonRainfall::getDrp)).getSum();
//                StPptnCommonRainfall current = new StPptnCommonRainfall();
//                if (currentList.get(0).getTm().getHours() == 0) {
//                    current.setShowTm(23 + "-" + 24);
//                } else {
//                    int hours = currentList.get(0).getTm().getHours() + 1;
//                    current.setShowTm(hours - 1 + "-" + hours);
//                }
//                current.setStcd(key);
//                current.setDrp(sum);
//                currentListNew.add(current);
//            });
//        }
        historyList.forEach(item -> {
            int hours = item.getTm().getHours();
            if (hours == 0) {
                item.setShowTm(23 + "-" + 24);
            } else {
                hours = hours + 1;
                item.setShowTm(hours - 1 + "-" + hours);
            }
        });
        historyList.addAll(currentListNew);
        return historyList;
    }*/


    /*@Override
    public List<StPptnCommonRainfall> algorithmInterface(RainAnalyseReq req) {
        String hourFormat = "yyyy-MM-dd HH";
        List<StPptnCommonRainfall> list = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        String todayDay = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        //今天
        // 0 点到 hours 的走小时表
        String dateHours = now.format(DateTimeFormatter.ofPattern(hourFormat));
        List<StPptnCommonRainfall> historyList = stPptnHourRainfallDao.findByTmBetween(DateUtil.getDateByStringDay(todayDay), DateUtil.getDateByStringDay(dateHours));
      if (!req.getHourOnlyFlag()) {
            logger.info("当前是小时only  走 日月年的查询逻辑");
            return historyList;
        }
        logger.info("当前是小时only  走 小时的查询逻辑，下面进行showTm赋值");
        List<StPptnCommonRainfall> currentListNew = new ArrayList<>();
        historyList.forEach(item -> {
            int hours = item.getTm().getHours();
            if (hours == 0) {
                item.setShowTm(23 + "-" + 24);
            } else {
                hours = hours + 1;
                item.setShowTm(hours - 1 + "-" + hours);
            }
        });
        historyList.addAll(currentListNew);
        return historyList;
    }*/

    /**
     * 查询当天小时数据
     * @Author huangxiaoli
     * @Description
     * @Date 16:10 2020/11/2
     * @Param [req]
     * @return java.util.List<com.essence.tzsyq.rainanalyse.dto.StPptnCommonRainfall>
     **/
    @Override
    public List<StPptnCommonRainfall> algorithmInterface(RainAnalyseReq req) {
        List<StPptnCommonRainfall> historyList =new ArrayList<>();

        LocalDateTime nowLocalDateTime = LocalDateTime.now();
        int hour = nowLocalDateTime.getHour();
        int minute = nowLocalDateTime.getMinute();
        int second = nowLocalDateTime.getSecond();

        Date realStartTm=null;
        Date realEndTm=Date.from(nowLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
        if (hour<8 ||(hour==8 && minute==0&& second==0)){
            //取前一天数据
            realStartTm=Date.from(nowLocalDateTime.plusDays(-1).withHour(8).withMinute(0).withSecond(0).atZone(ZoneId.systemDefault()).toInstant());
        }else {
            //当天数据
            realStartTm=Date.from(nowLocalDateTime.withHour(8).withMinute(0).withSecond(0).atZone(ZoneId.systemDefault()).toInstant());
        }

        AggregationOptions aggregationOptions = AggregationOptions.builder().build();
        Aggregation aggregation=Aggregation.newAggregation(Aggregation.match(Criteria.where("tm").gt(realStartTm)),
                Aggregation.match(Criteria.where("tm").lte(realEndTm))).withOptions(aggregationOptions);
        AggregationResults<StPptnCommonRainfall> stPptnHourRainfallEntity = mongoTemplate.aggregate(aggregation, "st_pptn_hour_rainfall", StPptnCommonRainfall.class);
        List<StPptnCommonRainfall> stPptnHourRainfallList = stPptnHourRainfallEntity.getMappedResults();
        Map<String, Map<Long, StPptnCommonRainfall>> stcdTmStPptnCommonRainfallMap = new HashMap<>();
        //查询最小小时时间
        Date minHourDate=null;
        if (stPptnHourRainfallList.size()>0){
            for (int i=0;i<stPptnHourRainfallList.size();i++){
                StPptnCommonRainfall stPptnCommonRainfall = stPptnHourRainfallList.get(i);
                String stcd = stPptnCommonRainfall.getStcd();
                Date tm = stPptnCommonRainfall.getTm();

                //查询最小小时时间
                if(null==minHourDate){
                    minHourDate=tm;
                }else {
                    if (tm.getTime()<minHourDate.getTime()){
                        minHourDate=tm;
                    }
                }

                Map<Long, StPptnCommonRainfall> longStPptnCommonRainfallMap = stcdTmStPptnCommonRainfallMap.get(stcd);
                if (null==longStPptnCommonRainfallMap){
                    longStPptnCommonRainfallMap=new TreeMap<>();
                }
                longStPptnCommonRainfallMap.put(tm.getTime(),stPptnCommonRainfall);
                stcdTmStPptnCommonRainfallMap.put(stcd,longStPptnCommonRainfallMap);
            }
        }

        //若结束时间为非整小时，则查询实时数据(5分钟)
        if (second>0){
            //为避免最近一个小时有可能未统计，则取小时数据中最小的小时时间，然后去重
            Date realMinuteStartDate = Date.from(nowLocalDateTime.withMinute(0).withSecond(0).atZone(ZoneId.systemDefault()).toInstant());
            if (minHourDate!=null){
                realMinuteStartDate=minHourDate;
            }

            AggregationOptions hourAggregationOptions = AggregationOptions.builder().build();
            Aggregation hourAggregation=Aggregation.newAggregation(Aggregation.match(Criteria.where("tm").gt(realMinuteStartDate)),
                    Aggregation.match(Criteria.where("tm").lte(realEndTm))).withOptions(hourAggregationOptions);
            AggregationResults<StPptnCommonRainfall> stPptnEntity = mongoTemplate.aggregate(hourAggregation, "st_pptn_r", StPptnCommonRainfall.class);
            List<StPptnCommonRainfall> stPptnRList = stPptnEntity.getMappedResults();
            if (stPptnRList.size()>0){
                Map<String,Map<Long,Double> > stcdHourDrpMap = new HashMap<>();
                for (StPptnCommonRainfall stPptnCommonRainfall : stPptnRList) {
                    String stcd = stPptnCommonRainfall.getStcd();
                    LocalDateTime localDateTime = stPptnCommonRainfall.getTm().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                    int timeMinute = localDateTime.getMinute();
                    int timeSecond = localDateTime.getSecond();

                    Date realTm=null;
                    if (timeMinute==0 && timeSecond==0){
                        realTm = Date.from(localDateTime.withMinute(0).withSecond(0).atZone(ZoneId.systemDefault()).toInstant());
                    }else {
                        realTm=Date.from(localDateTime.plusHours(1).withMinute(0).withSecond(0).atZone(ZoneId.systemDefault()).toInstant());
                    }

                    Map<Long, Double> hourDrpMap = stcdHourDrpMap.get(stcd);
                    if (null==hourDrpMap){
                        hourDrpMap=new TreeMap<>();
                    }
                    Double realData = hourDrpMap.get(realTm.getTime());
                    if (null==realData){
                        hourDrpMap.put(realTm.getTime(),stPptnCommonRainfall.getDrp());
                    }else {
                        hourDrpMap.put(realTm.getTime(),realData+stPptnCommonRainfall.getDrp());
                    }
                    stcdHourDrpMap.put(stcd,hourDrpMap);
                }

                for (String stcd : stcdHourDrpMap.keySet()) {
                    Map<Long, Double> hourDrpMap = stcdHourDrpMap.get(stcd);

                    //小时数据
                    Map<Long, StPptnCommonRainfall> longStPptnCommonRainfallMap = stcdTmStPptnCommonRainfallMap.get(stcd);

                    for (Long tmLong : hourDrpMap.keySet()) {

                        StPptnCommonRainfall stPptnCommonRainfall = longStPptnCommonRainfallMap.get(tmLong);
                        if (null==stPptnCommonRainfall){//若小时数据中不存在，则查询分钟数据
                            stPptnCommonRainfall = new StPptnCommonRainfall();

                            Double drp = hourDrpMap.get(tmLong);
                            stPptnCommonRainfall.setStcd(stcd);
                            //半点数据属于下一个小时，如9：30的数据数据10点的小时数据
                            Date minuteHourTm = Date.from(nowLocalDateTime.plusHours(1).withMinute(0).withSecond(0).atZone(ZoneId.systemDefault()).toInstant());
                            stPptnCommonRainfall.setTm(minuteHourTm);
                            stPptnCommonRainfall.setDrp(drp);
                            longStPptnCommonRainfallMap.put(tmLong,stPptnCommonRainfall);
                        }
                    }
                    stcdTmStPptnCommonRainfallMap.put(stcd,longStPptnCommonRainfallMap);
                }

            }
        }

        //整合数据
        if (stcdTmStPptnCommonRainfallMap.keySet().size()>0){
            for (String stcd : stcdTmStPptnCommonRainfallMap.keySet()) {
                Map<Long, StPptnCommonRainfall> longStPptnCommonRainfallMap = stcdTmStPptnCommonRainfallMap.get(stcd);
                for (Long tmLong : longStPptnCommonRainfallMap.keySet()) {
                    StPptnCommonRainfall stPptnCommonRainfall = longStPptnCommonRainfallMap.get(tmLong);
                    historyList.add(stPptnCommonRainfall);
                }
            }
        }

        if (null!=req.getHourOnlyFlag() && !req.getHourOnlyFlag()) {
            logger.info("当前是小时only  走 日月年的查询逻辑");
            return historyList;
        }
        logger.info("当前是小时only  走 小时的查询逻辑，下面进行showTm赋值");
        historyList.forEach(item -> {
            LocalDateTime tmLocalDateTime = item.getTm().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            int hours = tmLocalDateTime.getHour();
            int dayOfMonth = tmLocalDateTime.getDayOfMonth();
            if (hours == 0) {
                dayOfMonth = tmLocalDateTime.plusDays(-1).getDayOfMonth();
                item.setShowTm(String.format("%02d", dayOfMonth)+"_"+23 + "_" + 24);
            } else {
                item.setShowTm(String.format("%02d", dayOfMonth)+"_"+String.format("%02d", (hours - 1)) + "_" + String.format("%02d", hours));
            }
        });
        return historyList;
    }
}
