package com.essence.business.xqh.service.rainanalyse.strategy;

import com.essence.business.xqh.api.rainanalyse.vo.RainAnalyseReq;
import com.essence.business.xqh.dao.dao.rainanalyse.StPptnDayRainfallDao;
import com.essence.business.xqh.dao.dao.rainanalyse.dto.StPptnCommonRainfall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
public class RainStrategyDayOnly extends Strategy {

    @Autowired
    StPptnDayRainfallDao stPptnDayRainfallDao;
    @Autowired
    private MongoTemplate mongoTemplate;

   /* @Override
    public List<StPptnCommonRainfall> algorithmInterface(RainAnalyseReq req) {
        String s = DateUtil.dateToStringDay(req.getStartDay());
        Date nextDay = DateUtil.getNextDay(req.getEndDay(), 1);
        String s1 = DateUtil.dateToStringDay(nextDay);
//        List<Map<String, Object>> byTmBetween = stPptnDayRainfallDao.findByTmBetween(s, s1);
        List<StPptnCommonRainfall> byTmBetween = stPptnDayRainfallDao.findByTmBetween(req.getStartDay(), nextDay);
//        List<StPptnCommonRainfall> historyList = BeanUtil.mapCopy(byTmBetween, StPptnCommonRainfall.class);
//        historyList.forEach(item -> item.setShowTm(DateUtil.dateToStringDay(item.getTm())));
        return byTmBetween;
    }*/


    @Override
    public List<StPptnCommonRainfall> algorithmInterface(RainAnalyseReq req) {
        Date startDay = req.getStartDay();
        LocalDateTime startLocalDateTime = startDay.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Date realStartDay = Date.from(startLocalDateTime.withHour(8).withMinute(0).withSecond(0).atZone(ZoneId.systemDefault()).toInstant());

        Date endDay = req.getEndDay();
        LocalDateTime endLocalDateTime = endDay.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Date realEndDay = Date.from(endLocalDateTime.withHour(8).withMinute(0).withSecond(0).atZone(ZoneId.systemDefault()).toInstant());

        AggregationOptions aggregationOptions = AggregationOptions.builder().build();
        Aggregation aggregation=Aggregation.newAggregation(Aggregation.match(Criteria.where("tm").gte(realStartDay)),
                Aggregation.match(Criteria.where("tm").lte(realEndDay))).withOptions(aggregationOptions);
        AggregationResults<StPptnCommonRainfall> stPptnHourRainfallEntity = mongoTemplate.aggregate(aggregation, "st_pptn_day_rainfall", StPptnCommonRainfall.class);
        List<StPptnCommonRainfall> historyList = stPptnHourRainfallEntity.getMappedResults();
        return historyList;
    }
}
