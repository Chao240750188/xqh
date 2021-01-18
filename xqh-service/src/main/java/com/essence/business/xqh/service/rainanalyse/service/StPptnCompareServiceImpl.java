package com.essence.business.xqh.service.rainanalyse.service;

import com.essence.business.xqh.api.rainanalyse.service.StPptnCompareService;
import com.essence.business.xqh.api.rainanalyse.vo.RainAnalyseReq;
import com.essence.business.xqh.api.rainanalyse.vo.RainCompareAnalyseReq;
import com.essence.business.xqh.api.rainfall.vo.RainDzmReq;
import com.essence.business.xqh.common.RainConstants;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.dao.dao.fhybdd.StPptnRDao;
import com.essence.business.xqh.dao.dao.rainanalyse.StPptnYearRainfallDao;
import com.essence.business.xqh.dao.dao.rainanalyse.dto.StPptnCommonRainfall;
import com.essence.business.xqh.dao.dao.rainfall.dto.THdmisTotalRainfallDto;
import com.essence.business.xqh.dao.entity.rainanalyse.StPptnYearRainfall;
import com.essence.business.xqh.dao.entity.rainfall.TStbprpBOld;
import com.essence.business.xqh.dao.entity.rainfall.TStsmtaskBOld;
import com.essence.business.xqh.service.rainfall.AbstractRainFallDzmService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName StPptnCompareService
 * @Description TODO
 * @Author zhichao.xing
 * @Date 2020/7/28 17:20
 * @Version 1.0
 **/
@Service
public class StPptnCompareServiceImpl extends AbstractRainFallDzmService implements StPptnCompareService {

    /*@Autowired
    StPptnRaintimedataDao stPptnRaintimedataDao;*/
    @Autowired
    private StPptnRDao pptnRDao;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private StPptnYearRainfallDao stPptnYearRainfallDao;

    /**
     * 类型 type 1 小时 ; 2 日 ;3 月 ； 4 年
     *
     * @return void
     * @Description
     * @Author xzc
     * @Date 18:58 2020/7/28
     **/
   /* @Override
    @Cacheable(value = "rainCompareAnalyseCache", key = "#p0")
    public Map<String, Double> findSummaryAnalyse(RainCompareAnalyseReq req, String format) {
        List<Integer> yearList = req.getYearList();
        Map<String, Double> map = new HashMap<>(yearList.size());
        yearList.forEach(year -> {
            Date startDate = DateUtil.getDateByStringDay(year + "-01-01");
            Date endDate = DateUtil.getDateByStringDay(year + 1 + "-01-01");
            List<THdmisTotalRainfallDto> tHdmisTotalRainfallDtos = pptnRDao.queryByTmBetween(startDate, endDate);
            if (req.getType().compareTo(4) == 0) {
                calcSummaryAvgValue(map, tHdmisTotalRainfallDtos, year + "");
            } else {
                tHdmisTotalRainfallDtos.parallelStream().forEach(i -> i.setTempTm(DateUtil.dateToStringWithFormat(i.getTm(), format)));
                Map<String, List<THdmisTotalRainfallDto>> collect = tHdmisTotalRainfallDtos.parallelStream().collect(Collectors.groupingBy(THdmisTotalRainfallDto::getTempTm));
                collect.forEach((tm, list) -> calcSummaryAvgValue(map, list, tm));
            }
        });
        return map;
    }*/

   /**
    * 查询雨量分析数据
    * @Author huangxiaoli
    * @Description
    * @Date 17:50 2020/11/2
    * @Param [req, format]
    * @return java.util.Map<java.lang.String,java.lang.Double>
    **/
    /*@Override
    public Map<String, Double> findSummaryAnalyse(RainCompareAnalyseReq req, String format) {
        Integer type = req.getType();
        List<Integer> yearList = req.getYearList();
        Map<String, Double> map = new TreeMap<>();

        LocalDateTime nowLocalDateTime = LocalDateTime.now();

        Date currentDate = Date.from(nowLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

        if (type==4){
            Map<String, String> stcdMap = new HashMap<>();//测站编码信息

            yearList.forEach(year -> {

                String realYear = String.valueOf(year);


                String yearStartTm=year+"/01/01 08:00:00";
                LocalDateTime startDateTime = LocalDateTime.parse(yearStartTm, df);

                //查询指定年份的开始时间
                LocalDateTime localDateTime = startDateTime.withMonth(1).withDayOfMonth(1).withHour(8).withMinute(0).withSecond(0);
                Date currentYearStartTime = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

                Date currentDayStartTime=null;
                int hour = nowLocalDateTime.getHour();
                if (hour>8){//若当前时间大于8点则取当天8点到当前时间
                    currentDayStartTime = Date.from(nowLocalDateTime.withHour(8).withMinute(0).withSecond(0).atZone(ZoneId.systemDefault()).toInstant());
                }else {//否则从昨天8点到当前时间
                    currentDayStartTime = Date.from(nowLocalDateTime.plusDays(-1).withHour(8).withMinute(0).withSecond(0).atZone(ZoneId.systemDefault()).toInstant());
                }

                //当前年数据
                if (year==nowLocalDateTime.getYear()){

                    //查询月数据
                    Aggregation latestAggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("tm").gte(currentYearStartTime)),
                            Aggregation.match(Criteria.where("tm").lte(currentDate)));

                    AggregationResults<StPptnCommonRainfall> pptnMonthRainfallResults = mongoTemplate.aggregate(latestAggregation, "st_pptn_month_rainfall", StPptnCommonRainfall.class);
                    List<StPptnCommonRainfall> pptnMonthRainfallList = pptnMonthRainfallResults.getMappedResults();

                    if (pptnMonthRainfallList.size() > 0) {
                        for (StPptnCommonRainfall stPptnCommonRainfall : pptnMonthRainfallList) {

                            stcdMap.put(stPptnCommonRainfall.getStcd(),stPptnCommonRainfall.getStcd());

                            //今年累计
                            Double yearSumDrp = map.get(realYear);
                            if (null==yearSumDrp){
                                map.put(realYear,stPptnCommonRainfall.getDrp());
                            }else {
                                map.put(realYear,stPptnCommonRainfall.getDrp()+yearSumDrp);
                            }
                        }
                    }


                    //查询日数据
                    int dayOfMonth = nowLocalDateTime.getDayOfMonth();
                    if (dayOfMonth>1){
                        //查询当前月份第一天数据
                        Date time = Date.from(nowLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
                        Date monthFirstDay = DateUtil.getRainMonthFirstDay(time);

                        //查询日数据
                        Aggregation searchDayAggregation = Aggregation.newAggregation(
                                Aggregation.match(Criteria.where("tm").gte(monthFirstDay)),
                                Aggregation.match(Criteria.where("tm").lt(currentDate)));

                        AggregationResults<StPptnCommonRainfall> pptnDayRainfallResults = mongoTemplate.aggregate(searchDayAggregation, "st_pptn_day_rainfall", StPptnCommonRainfall.class);
                        List<StPptnCommonRainfall> pptnDayRainfallList = pptnDayRainfallResults.getMappedResults();
                        if (pptnDayRainfallList.size() > 0) {
                            for (StPptnCommonRainfall stPptnCommonRainfall : pptnDayRainfallList) {

                                stcdMap.put(stPptnCommonRainfall.getStcd(),stPptnCommonRainfall.getStcd());

                                //今年累计
                                Double yearSumDrp = map.get(realYear);
                                if (null==yearSumDrp){
                                    map.put(realYear,stPptnCommonRainfall.getDrp());
                                }else {
                                    map.put(realYear,stPptnCommonRainfall.getDrp()+yearSumDrp);
                                }
                            }
                        }
                    }
                    //查询时段数据
                    Aggregation searchTimeAggregation = Aggregation.newAggregation(
                            Aggregation.match(Criteria.where("tm").gt(currentDayStartTime)),
                            Aggregation.match(Criteria.where("tm").lte(currentDate)));

                    AggregationResults<StPptnCommonRainfall> pptnTimeRainfallResults = mongoTemplate.aggregate(searchTimeAggregation, "st_pptn_r", StPptnCommonRainfall.class);
                    List<StPptnCommonRainfall> timeRainfallList = pptnTimeRainfallResults.getMappedResults();
                    if (timeRainfallList.size() > 0) {
                        for (StPptnCommonRainfall stPptnCommonRainfall : timeRainfallList) {

                            stcdMap.put(stPptnCommonRainfall.getStcd(),stPptnCommonRainfall.getStcd());

                            //今年累计
                            Double yearSumDrp = map.get(realYear);
                            if (null==yearSumDrp){
                                map.put(realYear,stPptnCommonRainfall.getDrp());
                            }else {
                                map.put(realYear,stPptnCommonRainfall.getDrp()+yearSumDrp);
                            }

                        }
                    }

                }else {
                    //查询年数据
                    Aggregation searchTimeAggregation = Aggregation.newAggregation(
                            Aggregation.match(Criteria.where("tm").gte(currentYearStartTime)),
                            Aggregation.match(Criteria.where("tm").lt(DateUtil.getNextYear(currentYearStartTime,1))));

                    AggregationResults<StPptnCommonRainfall> stPptnYearRainfallResults = mongoTemplate.aggregate(searchTimeAggregation, "st_pptn_year_rainfall", StPptnCommonRainfall.class);
                    List<StPptnCommonRainfall> stPptnYearRainfallList = stPptnYearRainfallResults.getMappedResults();
                    if (stPptnYearRainfallList.size()>0){

                        for (StPptnCommonRainfall stPptnCommonRainfall : stPptnYearRainfallList) {

                            stcdMap.put(stPptnCommonRainfall.getStcd(),stPptnCommonRainfall.getStcd());

                            Double yearSumDrp = map.get(realYear);
                            if (null==yearSumDrp){
                                map.put(realYear,stPptnCommonRainfall.getDrp());
                            }else {
                                map.put(realYear,stPptnCommonRainfall.getDrp()+yearSumDrp);
                            }
                        }
                    }
                }

            });

            //求平均值
            if (map.keySet().size()>0){
                for (String tm : map.keySet()) {
                    Double tmSumDrp = map.get(tm);
                    BigDecimal bigDecimal = BigDecimal.valueOf(tmSumDrp).divide(BigDecimal.valueOf(stcdMap.keySet().size()), 2, RoundingMode.HALF_UP);
                    double v = bigDecimal.doubleValue();
                    map.put(tm, v);
                }
            }

        }else if (type==3){
            Map<String, String> stcdMap = new HashMap<>();//测站编码信息

            yearList.forEach(year -> {

                String yearStartTm=year+"/01/01 08:00:00";
                LocalDateTime startDateTime = LocalDateTime.parse(yearStartTm, df);

                //查询当前年的开始时间
                LocalDateTime localDateTime = startDateTime.withMonth(1).withDayOfMonth(1).withHour(8).withMinute(0).withSecond(0);
                Date currentYearStartTime = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());


                Date currentDayStartTime=null;
                int hour = startDateTime.getHour();
                if (hour>8){//若当前时间大于8点则取当天8点到当前时间
                    currentDayStartTime = Date.from(nowLocalDateTime.withHour(8).withMinute(0).withSecond(0).atZone(ZoneId.systemDefault()).toInstant());
                }else {//否则从昨天8点到当前时间
                    currentDayStartTime = Date.from(nowLocalDateTime.plusDays(-1).withHour(8).withMinute(0).withSecond(0).atZone(ZoneId.systemDefault()).toInstant());
                }

                //当前年数据
                if (year==nowLocalDateTime.getYear()){

                    //查询月数据
                    Aggregation latestAggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("tm").gte(currentYearStartTime)),
                            Aggregation.match(Criteria.where("tm").lt(currentDate)));

                    AggregationResults<StPptnCommonRainfall> pptnMonthRainfallResults = mongoTemplate.aggregate(latestAggregation, "st_pptn_month_rainfall", StPptnCommonRainfall.class);
                    List<StPptnCommonRainfall> pptnMonthRainfallList = pptnMonthRainfallResults.getMappedResults();

                    if (pptnMonthRainfallList.size() > 0) {
                        for (StPptnCommonRainfall stPptnCommonRainfall : pptnMonthRainfallList) {

                            stcdMap.put(stPptnCommonRainfall.getStcd(),stPptnCommonRainfall.getStcd());

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
                            String monthTime = dateFormat.format(stPptnCommonRainfall.getTm());

                            //今年累计
                            Double yearSumDrp = map.get(monthTime);
                            if (null==yearSumDrp){
                                map.put(monthTime,stPptnCommonRainfall.getDrp());
                            }else {
                                map.put(monthTime,stPptnCommonRainfall.getDrp()+yearSumDrp);
                            }
                        }
                    }


                    //查询日数据
                    int dayOfMonth = nowLocalDateTime.getDayOfMonth();
                    if (dayOfMonth>1){
                        //查询当前月份第一天数据
                        Date time = Date.from(nowLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());
                        Date monthFirstDay = DateUtil.getRainMonthFirstDay(time);

                        //查询日数据
                        Aggregation searchDayAggregation = Aggregation.newAggregation(
                                Aggregation.match(Criteria.where("tm").gte(monthFirstDay)),
                                Aggregation.match(Criteria.where("tm").lt(currentDate)));

                        AggregationResults<StPptnCommonRainfall> pptnDayRainfallResults = mongoTemplate.aggregate(searchDayAggregation, "st_pptn_day_rainfall", StPptnCommonRainfall.class);
                        List<StPptnCommonRainfall> pptnDayRainfallList = pptnDayRainfallResults.getMappedResults();
                        if (pptnDayRainfallList.size() > 0) {
                            for (StPptnCommonRainfall stPptnCommonRainfall : pptnDayRainfallList) {

                                stcdMap.put(stPptnCommonRainfall.getStcd(),stPptnCommonRainfall.getStcd());

                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
                                String monthTime = dateFormat.format(stPptnCommonRainfall.getTm());

                                //今年累计
                                Double yearSumDrp = map.get(monthTime);
                                if (null==yearSumDrp){
                                    map.put(monthTime,stPptnCommonRainfall.getDrp());
                                }else {
                                    map.put(monthTime,stPptnCommonRainfall.getDrp()+yearSumDrp);
                                }
                            }
                        }
                    }
                    //查询时段数据
                    Aggregation searchTimeAggregation = Aggregation.newAggregation(
                            Aggregation.match(Criteria.where("tm").gt(currentDayStartTime)),
                            Aggregation.match(Criteria.where("tm").lte(currentDate)));

                    AggregationResults<StPptnCommonRainfall> pptnTimeRainfallResults = mongoTemplate.aggregate(searchTimeAggregation, "st_pptn_r", StPptnCommonRainfall.class);
                    List<StPptnCommonRainfall> timeRainfallList = pptnTimeRainfallResults.getMappedResults();
                    if (timeRainfallList.size() > 0) {
                        for (StPptnCommonRainfall stPptnCommonRainfall : timeRainfallList) {

                            stcdMap.put(stPptnCommonRainfall.getStcd(),stPptnCommonRainfall.getStcd());

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
                            String monthTime = dateFormat.format(stPptnCommonRainfall.getTm());

                            //今年累计
                            Double yearSumDrp = map.get(monthTime);
                            if (null==yearSumDrp){
                                map.put(monthTime,stPptnCommonRainfall.getDrp());
                            }else {
                                map.put(monthTime,stPptnCommonRainfall.getDrp()+yearSumDrp);
                            }

                        }
                    }

                }else {
                    //查询月数据
                    Aggregation searchTimeAggregation = Aggregation.newAggregation(
                            Aggregation.match(Criteria.where("tm").gte(currentYearStartTime)),
                            Aggregation.match(Criteria.where("tm").lt(DateUtil.getNextYear(currentYearStartTime,1))));

                    AggregationResults<StPptnCommonRainfall> stPptnYearRainfallResults = mongoTemplate.aggregate(searchTimeAggregation, "st_pptn_month_rainfall", StPptnCommonRainfall.class);
                    List<StPptnCommonRainfall> stPptnYearRainfallList = stPptnYearRainfallResults.getMappedResults();
                    if (stPptnYearRainfallList.size()>0){
                        for (StPptnCommonRainfall stPptnCommonRainfall : stPptnYearRainfallList) {

                            stcdMap.put(stPptnCommonRainfall.getStcd(),stPptnCommonRainfall.getStcd());

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
                            String monthTime = dateFormat.format(stPptnCommonRainfall.getTm());

                            //今年累计
                            Double yearSumDrp = map.get(monthTime);
                            if (null==yearSumDrp){
                                map.put(monthTime,stPptnCommonRainfall.getDrp());
                            }else {
                                map.put(monthTime,stPptnCommonRainfall.getDrp()+yearSumDrp);
                            }
                        }

                    }

                }

            });

            //求平均值
            if (map.keySet().size()>0){
                for (String tm : map.keySet()) {
                    Double tmSumDrp = map.get(tm);
                    BigDecimal bigDecimal = BigDecimal.valueOf(tmSumDrp).divide(BigDecimal.valueOf(stcdMap.keySet().size()), 2, RoundingMode.HALF_UP);
                    double v = bigDecimal.doubleValue();
                    map.put(tm, v);
                }
            }
        }

        return map;
    }*/

    /**
     * 查询雨量分析数据
     * @Author huangxiaoli
     * @Description
     * @Date 13:57 2020/11/6
     * @Param [req, format]
     * @return java.util.Map<java.lang.String,java.lang.Double>
     **/
   @Override
   public Map<String, Double> findSummaryAnalyse(RainCompareAnalyseReq req, String format) {
       Integer type = req.getType();
       List<Integer> yearList = req.getYearList();
       Map<String, Double> map = new TreeMap<>();//key为年份

       LocalDateTime nowLocalDateTime = LocalDateTime.now();
       Date currentDate = Date.from(nowLocalDateTime.atZone(ZoneId.systemDefault()).toInstant());

       DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

       yearList.forEach(year -> {
           List<StPptnCommonRainfall> commonRainfalls = new ArrayList<>();
           if (type==4) {

              //当前年数据
              if (year == nowLocalDateTime.getYear()) {
                  RainAnalyseReq reqMonth = new RainAnalyseReq();
                  reqMonth.setType(3);
                  reqMonth.setStartMonth(DateUtil.getDateByStringDay(year + "-01-01"));
                  reqMonth.setEndMonth(currentDate);
                  commonRainfalls = dbFindByMonth(reqMonth);

              } else {

                  LocalDateTime localDateTime = LocalDateTime.parse(year + "-01-01 00:00:00", dateTimeFormatter);
                  //获取当前年的最后一天
                  Date yearStartTm = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                  Date yearLastTm = DateUtil.getYearLast(yearStartTm);

                  List<StPptnYearRainfall> stPptnYearRainfallList = stPptnYearRainfallDao.findByTmBetween(yearStartTm, yearLastTm);
                  List<StPptnCommonRainfall> listReturn = new ArrayList<>();
                  stPptnYearRainfallList.forEach(item -> {
                      StPptnCommonRainfall commonRainfall = new StPptnCommonRainfall();
                      BeanUtils.copyProperties(item, commonRainfall);
                      listReturn.add(commonRainfall);
                  });
                  commonRainfalls = listReturn;
            }

           commonRainfalls.forEach(item -> {
               item.setShowTm(DateUtil.dateToStringWithFormat(item.getTm(), "yyyy"));
           });
        }else if (type==3){
               //年份开始时间
               LocalDateTime startDateTime = LocalDateTime.parse(year+"-01-01 08:00:00", dateTimeFormatter);
               Date yearStartTm = Date.from(startDateTime.atZone(ZoneId.systemDefault()).toInstant());

               //年份结束时间
               Date yearLastTm = null;
               if (year == nowLocalDateTime.getYear()){
                   yearLastTm=currentDate;
               }else {
                   yearLastTm = DateUtil.getYearLast(yearStartTm);
               }
               RainAnalyseReq reqMonth = new RainAnalyseReq();
               reqMonth.setType(3);
               reqMonth.setStartMonth(yearStartTm);
               reqMonth.setEndMonth(yearLastTm);
               commonRainfalls = dbFindByMonth(reqMonth);
               commonRainfalls.forEach(item -> {
                   item.setShowTm(DateUtil.dateToStringWithFormat(item.getTm(), "yyyy-MM"));
               });
           }

           // 公共逻辑 开始
           Map<String, Object> rainStation = getRainStation();
           //所有开启的站的map  <测站编码， this>
           Map<String, TStbprpBOld> stcdBprpMap = (Map<String, TStbprpBOld>) rainStation.get(RainConstants.STCDBPRPMAP);
           //所有开启的雨量站
           List<TStsmtaskBOld> validRainList = (List<TStsmtaskBOld>) rainStation.get(RainConstants.VALIDRAINLIST);
           //所有开启的雨量站编码
           List<String> validStcdList = validRainList.stream().map(TStsmtaskBOld::getStcd).collect(Collectors.toList());
           List<StPptnCommonRainfall> validRainfallStation = commonRainfalls.stream().filter(item -> validStcdList.contains(item.getStcd())).collect(Collectors.toList());
           validRainfallStation.stream().forEach(item -> item.setStnm(stcdBprpMap.get(item.getStcd()).getStnm()));
           Map<String, List<StPptnCommonRainfall>> showTmAndListMap = validRainfallStation.stream().collect(Collectors.groupingBy(StPptnCommonRainfall::getShowTm));
           showTmAndListMap.forEach((showTm, list) -> {
               long count = list.stream().map(StPptnCommonRainfall::getStcd).distinct().count();
               Double avgDrpByShowTm = list.stream().collect(Collectors.summingDouble(StPptnCommonRainfall::getDrp));
               double v = BigDecimal.valueOf(avgDrpByShowTm).divide(BigDecimal.valueOf(count), 1, RoundingMode.HALF_UP).doubleValue();
               map.put(showTm, v);
           });

        });

       return map;
   }

    private void calcSummaryAvgValue(Map<String, Double> map, List<THdmisTotalRainfallDto> list, String tm) {
        Double tmSum = list.parallelStream().collect(Collectors.summingDouble(THdmisTotalRainfallDto::getDrp));
        int size = list.parallelStream().map((THdmisTotalRainfallDto::getStcd)).collect(Collectors.toSet()).size();
        if (size != 0) {
            BigDecimal bigDecimal = BigDecimal.valueOf(tmSum.doubleValue()).divide(BigDecimal.valueOf(size), 1, RoundingMode.HALF_UP);
            double v = bigDecimal.doubleValue();
            map.put(tm, v);
        } else {
            map.put(tm, 0D);
        }
    }

    @Override
    protected List<THdmisTotalRainfallDto> getDbRainfall(RainDzmReq req) {
        return null;
    }

}
