package com.essence.business.xqh.service.rainanalyse.service;

import com.alibaba.fastjson.JSON;
import com.essence.business.xqh.api.rainanalyse.dto.BeanCeater;
import com.essence.business.xqh.api.rainanalyse.service.BaseStPptnRainfallService;
import com.essence.business.xqh.api.rainanalyse.vo.RainAnalyseReq;
import com.essence.business.xqh.api.rainfall.vo.RainDzmReq;
import com.essence.business.xqh.common.RainConstants;
import com.essence.business.xqh.dao.dao.rainanalyse.StPptnDayRainfallDao;
import com.essence.business.xqh.dao.dao.rainanalyse.StPptnHourRainfallDao;
import com.essence.business.xqh.dao.dao.rainanalyse.dto.StPptnCommonRainfall;
import com.essence.business.xqh.dao.dao.rainfall.dto.THdmisTotalRainfallDto;
import com.essence.business.xqh.dao.entity.rainfall.TStbprpBOld;
import com.essence.business.xqh.dao.entity.rainfall.TStsmtaskBOld;
import com.essence.business.xqh.service.rainanalyse.strategy.*;
import com.essence.business.xqh.service.rainfall.AbstractRainFallDzmService;
import com.essence.framework.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName BaseStPptnRainfallServiceImpl
 * @Description 雨情分析
 * @Author zhichao.xing
 * @Date 2020/7/4 14:17
 * @Version 1.0
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseStPptnRainfallServiceImpl extends AbstractRainFallDzmService implements BaseStPptnRainfallService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StPptnHourRainfallDao stPptnHourRainfallDao;
    @Autowired
    StPptnDayRainfallDao stPptnDayRainfallDao;

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
     * @return java.util.List<java.lang.Object>
     * @Description 入口  todo 平均值
     * @Author xzc
     * @Date 10:13 2020/7/7
     **/
    @Override
    public List<Object> getRainfallByTypeNew(RainAnalyseReq req) {
        logger.info("雨晴多维分析-请求参数为{}", req.toString());
        List<Object> objectList = new ArrayList<>();
        Map common = common(req);
        List<StPptnCommonRainfall> validRainfallStation = (List<StPptnCommonRainfall>) common.get("validRainfallStation");
        Map<String, List<StPptnCommonRainfall>> collectSort = (Map<String, List<StPptnCommonRainfall>>) common.get("collectSort");
        Map<String, List<StPptnCommonRainfall>> collect = (Map<String, List<StPptnCommonRainfall>>) common.get("collect");
        Map<String, TStbprpBOld> stcdBprpMap = (Map<String, TStbprpBOld>) common.get("stcdBprpMap");

        switch (req.getType()) {
            //年
            case 4:
                objectList = dealWithYearNew(validRainfallStation, collectSort, collect, stcdBprpMap);
                break;
            //月
            case 3:
                objectList = dealWithMonthNew(validRainfallStation, collectSort, collect, stcdBprpMap);
                break;
            //日
            case 2:
                objectList = dealWithDayNew(validRainfallStation, collectSort, collect, stcdBprpMap);
                break;
            //小时
            case 1:
                objectList = dealWithHourNew(validRainfallStation, collectSort, collect, stcdBprpMap);
                break;
            default:
                break;
        }
        return objectList;
    }

    private BigDecimal doubel2BigDecimal(Double avgDrp) {
        if (ObjectUtils.isEmpty(avgDrp)) {
            return null;
        }
        return BigDecimal.valueOf(avgDrp.doubleValue()).setScale(1, RoundingMode.HALF_UP);
    }

    /**
     * todo  修改魔法值，去掉多余变量
     *
     * @return java.util.Map
     * @Description 通用逻辑
     * @Author xzc
     * @Date 10:04 2020/7/7
     **/
    private Map common(RainAnalyseReq req) {
        List<StPptnCommonRainfall> commonRainfalls = new ArrayList<>();
        if (req.getType() == 1) {
            RainAanlyseContext rainAanlyseContext = new RainAanlyseContext(rainStrategyHourByDay);
            commonRainfalls = rainAanlyseContext.contextInterface(req);
        } else if (req.getType() == 2) {
            commonRainfalls = dbFindByDay(req);
        } else if (req.getType() == 3) {
            commonRainfalls = dbFindByMonth(req);
        } else if (req.getType() == 4) {
            RainAanlyseContext rainAanlyseContext = new RainAanlyseContext(rainStrategyYearOnly);
            commonRainfalls = rainAanlyseContext.contextInterface(req);
            commonRainfalls.addAll(getCurrentYear());
            commonRainfalls.forEach(item -> {
                item.setShowTm(DateUtil.dateToStringWithFormat(item.getTm(), "yyyy"));
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
        Map<String, List<StPptnCommonRainfall>> collect = validRainfallStation.stream().collect(Collectors.groupingBy(StPptnCommonRainfall::getStcd));
        Map<String, List<StPptnCommonRainfall>> collectSort = sortByKey(collect, true);
        Map<String, Object> map = new HashMap();
        map.put("validRainfallStation", validRainfallStation);
        map.put("collect", collect);
        map.put("stcdBprpMap", stcdBprpMap);
        map.put("collectSort", collectSort);
        return map;
    }


    /**
     * @return java.util.List<java.lang.Object>
     * @Description 年
     * @Author xzc
     * @Date 9:53 2020/7/7
     **/
    private List<Object> dealWithYearNew(
            List<StPptnCommonRainfall> validRainfallStation,
            Map<String, List<StPptnCommonRainfall>> collectSort,
            Map<String, List<StPptnCommonRainfall>> collect,
            Map<String, TStbprpBOld> stcdBprpMap) {
        return dealWithDayNew(validRainfallStation, collectSort, collect, stcdBprpMap);
    }

    /**
     * @return java.util.List<java.lang.Object>
     * @Description 月
     * @Author xzc
     * @Date 9:52 2020/7/7
     **/
    private List<Object> dealWithMonthNew(List<StPptnCommonRainfall> validRainfallStation,
                                          Map<String, List<StPptnCommonRainfall>> collectSort,
                                          Map<String, List<StPptnCommonRainfall>> collect,
                                          Map<String, TStbprpBOld> stcdBprpMap) {
        return dealWithDayNew(validRainfallStation, collectSort, collect, stcdBprpMap);
    }

    /**
     * @return java.util.List<java.lang.Object>
     * @Description 天
     * @Author xzc
     * @Date 9:52 2020/7/7
     **/
    private List<Object> dealWithDayNew(List<StPptnCommonRainfall> validRainfallStation,
                                        Map<String, List<StPptnCommonRainfall>> collectSort,
                                        Map<String, List<StPptnCommonRainfall>> collect,
                                        Map<String, TStbprpBOld> stcdBprpMap) {

        List<Object> objectList = new ArrayList<>();
        //平均值
        Map<String, Double> tmAndDrpAvg = new HashMap<>();

        List<String> tmSet = validRainfallStation.stream().map(StPptnCommonRainfall::getShowTm).distinct().collect(Collectors.toList()).stream().sorted().collect(Collectors.toList());
        Map stcdAllshowTmMap = new HashMap(24);
        Map stcdShowTmList = new HashMap(24);
        Map<String, List<StPptnCommonRainfall>> showTmAndListMap = validRainfallStation.stream().collect(Collectors.groupingBy(StPptnCommonRainfall::getShowTm));
        showTmAndListMap.forEach((showTm, list) -> {
            long count = list.stream().map(StPptnCommonRainfall::getStcd).distinct().count();
            Double avgDrpByShowTm = list.stream().collect(Collectors.summingDouble(StPptnCommonRainfall::getDrp));
            Map<String, Double> stcdAndSummaryDrpMap = new HashMap<>();
            Map<String, List<StPptnCommonRainfall>> stcdAndSummaryDrpMapDb = list.stream().collect(Collectors.groupingBy(StPptnCommonRainfall::getStcd));
            stcdAndSummaryDrpMapDb.forEach((getStcd, list1) -> {
                Double collect1 = list1.stream().collect(Collectors.summingDouble(StPptnCommonRainfall::getDrp));
                stcdAndSummaryDrpMap.put(stcdBprpMap.get(getStcd).getStnm(), doubel2BigDecimal(collect1).doubleValue());
            });
            Map<String, Double> stringDoubleMap = sortByKey(stcdAndSummaryDrpMap, true);
            stcdShowTmList.put(showTm, stringDoubleMap);
            double v = BigDecimal.valueOf(avgDrpByShowTm).divide(BigDecimal.valueOf(count), 1, RoundingMode.HALF_UP).doubleValue();
            stcdAllshowTmMap.put(showTm, v);
            tmAndDrpAvg.put(showTm, v);
        });
        Map stcdAllshowTmMapSorted = sortByKey(stcdAllshowTmMap, true);

        try {
            Map properties = new LinkedHashMap();
            createProperties(tmSet, properties);

            if (!ObjectUtils.isEmpty(collectSort)) {
                Object stuAvg = BeanCeater.generateObject(properties);
                BeanCeater.setValue(stuAvg, "stcd", "平均值");
                BeanCeater.setValue(stuAvg, "stnm", "平均值");
                tmAndDrpAvg.forEach((tm, avgDrp) -> {
                    BeanCeater.setValue(stuAvg, "tm" + tm, doubel2BigDecimal(avgDrp));
                });
                //点击 每个 showtm 的 显示的 各个雨量站 ，由大到小的顺序拍序
                BeanCeater.setValue(stuAvg, "stcdShowTmList", stcdShowTmList);
                // 全区雨量分析  （当天累积平均）
                BeanCeater.setValue(stuAvg, "stcdByShowTmTotalAvgRainList", stcdAllshowTmMapSorted);
                objectList.add(stuAvg);
            }

            collectSort.forEach((stcd, listRain) -> {
                Object stu = BeanCeater.generateObject(properties);
                objectList.add(stu);
                Map<String, List<StPptnCommonRainfall>> collect1 = listRain.stream().collect(Collectors.groupingBy(StPptnCommonRainfall::getShowTm));
                Map<String, BigDecimal> tmDrpMapStr = new HashMap<>(10);
                collect1.forEach((key, value) -> {
                    DoubleSummaryStatistics collect2 = value.stream().collect(Collectors.summarizingDouble(StPptnCommonRainfall::getDrp));
                    tmDrpMapStr.put(key, doubel2BigDecimal(collect2.getSum()));
                });
                BeanCeater.setValue(stu, "stcd", stcd);
                BeanCeater.setValue(stu, "stnm", listRain.get(0).getStnm());
                //  若小时没有数据，则需要补位 done
                tmSet.forEach(item -> {
                    BigDecimal aDouble = tmDrpMapStr.get(item);
                    BeanCeater.setValue(stu, "tm" + item, aDouble);
                });
                //每个站的明细
                List<StPptnCommonRainfall> stPptnCommonRainfalls = collect.get(stcd);
//
                // 2020/7/6 类型转换 done
                Map<String, List<StPptnCommonRainfall>> showTmList = stPptnCommonRainfalls.stream().collect(Collectors.groupingBy(StPptnCommonRainfall::getShowTm));
                List<StPptnCommonRainfall> stcdList = new ArrayList<>();
                showTmList.forEach((showTm, list) -> {
                    StPptnCommonRainfall one = new StPptnCommonRainfall();
                    one.setShowTm(showTm);
                    Double summary = list.stream().collect(Collectors.summingDouble(StPptnCommonRainfall::getDrp));
                    one.setDrp(doubel2BigDecimal(summary).doubleValue());
                    stcdList.add(one);
                });
                stcdList.sort(Comparator.comparing(StPptnCommonRainfall::getShowTm));
                BeanCeater.setValue(stu, "stcdList", stcdList);
            });
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return objectList;
    }

    /**
     * @return java.util.List<java.lang.Object>
     * @Description 小时雨情分析
     * @Author xzc
     * @Date 9:48 2020/7/7
     **/
    private List<Object> dealWithHourNew(List<StPptnCommonRainfall> validRainfallStation,
                                         Map<String, List<StPptnCommonRainfall>> collectSort,
                                         Map<String, List<StPptnCommonRainfall>> collect,
                                         Map<String, TStbprpBOld> stcdBprpMap) {
        List<Object> objectList = new ArrayList<>();
        //根据时间分组得到每个事件的平均值
        Map<String, Double> tmAndDrpAvg = validRainfallStation.stream().collect(Collectors.groupingBy(StPptnCommonRainfall::getShowTm, Collectors.averagingDouble(StPptnCommonRainfall::getDrp)));
        //获取各个时间段
        List<String> tmSet = validRainfallStation.stream().map(StPptnCommonRainfall::getShowTm).distinct().collect(Collectors.toList()).stream().sorted().collect(Collectors.toList());

        Map stcdAllshowTmMap = new HashMap(24);//key为时间，value为平均雨量

        Map stcdShowTmList = new HashMap(24);//key为时间，value中key为测站名称，value中key为总雨量

        //根据事件分组，key为时间，value为此时间雨量信息
        Map<String, List<StPptnCommonRainfall>> showTmAndListMap = validRainfallStation.stream().collect(Collectors.groupingBy(StPptnCommonRainfall::getShowTm));
        showTmAndListMap.forEach((showTm, list) -> {
            //获取时间段的平均雨量
            Double avgDrpByShowTm = list.stream().collect(Collectors.averagingDouble(StPptnCommonRainfall::getDrp));
            BigDecimal bigDecimalAvgDrp=null;
            if (avgDrpByShowTm==0){
                bigDecimalAvgDrp=new BigDecimal(0);
            }else {
                bigDecimalAvgDrp = doubel2BigDecimal(avgDrpByShowTm);
            }

            //获取各个测站的总雨量
            Map<String, Double> stcdAndSummaryDrpMap = list.stream().collect(Collectors.groupingBy(StPptnCommonRainfall::getStnm, Collectors.summingDouble(StPptnCommonRainfall::getDrp)));
            //各个测站的总雨量map,key为雨量站名称，value为总雨量
            Map<String, Double> stcdAndSummaryDrpMapNewVal = new HashMap<>(24);
            stcdAndSummaryDrpMap.forEach((stnm, sumDrp) -> stcdAndSummaryDrpMapNewVal.put(stnm, sumDrp==0? 0.0:doubel2BigDecimal(sumDrp).doubleValue()));

            //根据键排序
            Map<String, Double> stringDoubleMap = sortByKey(stcdAndSummaryDrpMapNewVal, true);
            stcdShowTmList.put(showTm, stringDoubleMap);
            stcdAllshowTmMap.put(showTm, bigDecimalAvgDrp);
        });
        Map stcdAllshowTmMapSorted = sortByKey(stcdAllshowTmMap, true);//根据键排序
        try {
            Map properties = new LinkedHashMap();
            createProperties(tmSet, properties);
            if (!ObjectUtils.isEmpty(collectSort)) {
                Object stuAvg = BeanCeater.generateObject(properties);
                BeanCeater.setValue(stuAvg, "stcd", "平均值");
                BeanCeater.setValue(stuAvg, "stnm", "平均值");
                tmAndDrpAvg.forEach((tm, avgDrp) -> BeanCeater.setValue(stuAvg, "tm" + tm, avgDrp==0?new BigDecimal(0.0):doubel2BigDecimal(avgDrp)));
                //点击 每个 showtm 的 显示的 各个雨量站 ，由大到小的顺序拍序
                BeanCeater.setValue(stuAvg, "stcdShowTmList", stcdShowTmList);
                // 全区雨量分析  （当天累积平均）
                BeanCeater.setValue(stuAvg, "stcdByShowTmTotalAvgRainList", stcdAllshowTmMapSorted);
                objectList.add(stuAvg);
            }

            collectSort.forEach((stcd, listRain) -> {
                Object stu = BeanCeater.generateObject(properties);
                objectList.add(stu);
                //在有些业务场景中会出现如下异常：Duplicate key ，map的key重复,使用toMap()的重载方法，如果已经存在则不再修改，直接使用上一个数据
                Map<String, Double> tmDrpMap = listRain.stream().collect(Collectors.toMap(StPptnCommonRainfall::getShowTm, StPptnCommonRainfall::getDrp, (entity1, entity2) -> entity1));
                Map<String, BigDecimal> tmDrpMapStr = new HashMap<>(10);
                tmDrpMap.forEach((key, value) -> {
                    if (value==0){
                        tmDrpMapStr.put("tm" + key, new BigDecimal(value));
                    }else {
                        tmDrpMapStr.put("tm" + key, doubel2BigDecimal(value));
                    }

                });
                BeanCeater.setValue(stu, "stcd", stcd);
                BeanCeater.setValue(stu, "stnm", listRain.get(0).getStnm());
                //  若小时没有数据，则需要补位 done
                tmSet.forEach(item -> {
                    BigDecimal aDouble = tmDrpMapStr.get("tm" + item);
                    BeanCeater.setValue(stu, "tm" + item, aDouble);
                });
                List<StPptnCommonRainfall> stPptnCommonRainfalls = collect.get(stcd);
                stPptnCommonRainfalls.sort(Comparator.comparing(StPptnCommonRainfall::getTm));
                // 2020/7/6 类型转换 done
                stPptnCommonRainfalls.stream().forEach(item -> {
                    item.setDrp(item.getDrp()==0?0.0:doubel2BigDecimal(item.getDrp()).doubleValue());
                });
                BeanCeater.setValue(stu, "stcdList", stPptnCommonRainfalls);
            });
            if (logger.isDebugEnabled()) {
                logger.debug(JSON.toJSONString(objectList));
            }
        } catch (Exception e) {
            logger.error("{}", e);
        }
        return objectList;
    }

    private void createProperties(List<String> tmSet, Map properties) {
        try {
            //保证属性 是字典顺序
            properties.put("stcdList", Class.forName("java.util.List"));
            properties.put("stcdShowTmList", Class.forName("java.util.Map"));
            properties.put("stcdByShowTmTotalAvgRainList", Class.forName("java.util.Map"));
            properties.put("stcd", Class.forName("java.lang.String"));
            properties.put("stnm", Class.forName("java.lang.String"));
            tmSet.forEach(item -> {
                try {
                    properties.put("tm" + item, Class.forName("java.math.BigDecimal"));
                } catch (ClassNotFoundException e) {
                    logger.error("生成类异常" + e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            logger.error("生成类异常", e);
        }
    }

    @Override
    protected List<THdmisTotalRainfallDto> getDbRainfall(RainDzmReq req) {
        return null;
    }
}
