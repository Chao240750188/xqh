package com.essence.business.xqh.service.rainanalyse.strategy;

import com.essence.business.xqh.api.rainanalyse.vo.RainAnalyseReq;
import com.essence.business.xqh.dao.dao.rainanalyse.StPptnHourRainfallDao;
import com.essence.business.xqh.dao.dao.rainanalyse.dto.StPptnCommonRainfall;
import com.essence.framework.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName RainStrategyHour
 * @Description 小时策略
 * @Author zhichao.xing
 * @Date 2020/7/3 20:37
 * @Version 1.0
 **/
@Component
public class RainStrategyHourByDay extends Strategy {

    @Autowired
    StPptnHourRainfallDao stPptnHourRainfallDao;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    RainStrategyHourOnly rainStrategyHourOnly;

    /**
     * @return java.util.List<com.essence.tzsyq.rainanalyse.dto.StPptnCommonRainfall>
     * @Description 历史小时  按天
     * @Author xzc
     * @Date 21:03 2020/7/3
     **/
    /*@Override
    public List<StPptnCommonRainfall> algorithmInterface(RainAnalyseReq req) {
        String s = "";
        String s1 = "";
        Date nextDay = new Date();
        if (req.getType() == 1) {
            s = DateUtil.dateToStringDay(req.getDay());
            nextDay = DateUtil.getNextDay(req.getDay(), 1);
            s1 = DateUtil.dateToStringDay(nextDay);
        }
//        else if (req.getType() == 2) {
//            s = DateUtil.dateToStringDay(req.getStartDay());
//            Date nextDay = DateUtil.getNextDay(req.getEndDay(), 1);
//            s1 = DateUtil.dateToStringDay(nextDay);
//        } else if (req.getType() == 3) {
//            s = DateUtil.dateToStringDay(req.getStartMonth());
//            Date nextDay = DateUtil.getNextMonth(req.getEndMonth(), 1);
//            s1 = DateUtil.dateToStringDay(nextDay);
//        } else if (req.getType() == 4) {
//            s = "1900";
//            Date nextDay = DateUtil.getNextYear(new Date(), 1);
//            s1 = DateUtil.dateToStringDay(nextDay);
//        }

//        List<Map<String, Object>> byTmBetween = stPptnHourRainfallDao.findByTmBetween(s, s1);
        List<StPptnCommonRainfall> historyList = stPptnHourRainfallDao.findByTmBetween(req.getDay(), nextDay);
//        List<StPptnCommonRainfall> historyList = BeanUtil.mapCopy(byTmBetween, StPptnCommonRainfall.class);
        if (req.getType() == 1) {
            historyList.forEach(item -> {
                if (item.getTm().getHours() == 0) {
                    item.setShowTm(23 + "_" + 24);
                } else {
                    int hours = item.getTm().getHours();
                    item.setShowTm(hours - 1 + "_" + hours);
                    item.setShowTm(String.format("%02d", (hours - 1)) + "_" + String.format("%02d", hours));
                }
            });
        }
//        else if (req.getType() == 2) {
//            historyList.forEach(item -> {
////                item.setShowTm(DateUtil.dateToStringDay(item.getTm()));
//                item.setShowTm(DateUtil.dateToStringWithFormat(item.getTm(), "yyyy_MM_dd"));
//            });
//        } else if (req.getType() == 3) {
//            historyList.forEach(item -> {
//                item.setShowTm(DateUtil.dateToStringWithFormat(item.getTm(), "yyyy_MM"));
//            });
//        } else if (req.getType() == 4) {
//            historyList.forEach(item -> {
//                item.setShowTm(DateUtil.dateToStringWithFormat(item.getTm(), "yyyy"));
//            });
//        }

        return historyList;
    }*/

    /**
     * 历史小时  按天
     * @Author huangxiaoli
     * @Description
     * @Date 16:09 2020/11/2
     * @Param [req]
     * @return java.util.List<com.essence.tzsyq.rainanalyse.dto.StPptnCommonRainfall>
     **/
    @Override
    public List<StPptnCommonRainfall> algorithmInterface(RainAnalyseReq req) {
        List<StPptnCommonRainfall> historyList =new ArrayList<>();

        Date day = req.getDay();
        LocalDateTime localDateTime = day.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        //当天时间
        LocalDateTime nowLocalDateTime = LocalDateTime.now();

        if (localDateTime.getDayOfMonth()==nowLocalDateTime.getDayOfMonth()){//查询当天数据
            RainAanlyseContext rainAanlyseContext = new RainAanlyseContext(rainStrategyHourOnly);
            historyList = rainAanlyseContext.contextInterface(req);
        }else {//查询历史数据
            //每天数据从当天8点到次日8点
            Date realDay = Date.from(localDateTime.withHour(8).withMinute(0).withSecond(0).atZone(ZoneId.systemDefault()).toInstant());

            Date nextDay = new Date();
            if (req.getType() == 1) {
                nextDay = DateUtil.getNextDay(realDay, 1);
            }
            AggregationOptions aggregationOptions = AggregationOptions.builder().build();
            Aggregation aggregation=Aggregation.newAggregation(Aggregation.match(Criteria.where("tm").gt(realDay)),
                    Aggregation.match(Criteria.where("tm").lte(nextDay))).withOptions(aggregationOptions);
            AggregationResults<StPptnCommonRainfall> stPptnHourRainfallEntity = mongoTemplate.aggregate(aggregation, "st_pptn_hour_rainfall", StPptnCommonRainfall.class);
            historyList = stPptnHourRainfallEntity.getMappedResults();

            if (historyList.size()>0){
                if (req.getType() == 1) {
                    historyList.forEach(item -> {
                        Date tm = item.getTm();
                        LocalDateTime tmLocalDateTime = tm.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                        int hour = tmLocalDateTime.getHour();
                        int dayOfMonth = tmLocalDateTime.getDayOfMonth();
                        if (hour == 0) {
                            dayOfMonth = tmLocalDateTime.plusDays(-1).getDayOfMonth();
                            item.setShowTm(String.format("%02d", dayOfMonth)+"_"+23 + "_" + 24);
                        } else {
                            item.setShowTm(String.format("%02d", dayOfMonth)+"_"+String.format("%02d", (hour - 1)) + "_" + String.format("%02d", hour));
                        }

                    });
                }
            }

        }


        return historyList;
    }
}
