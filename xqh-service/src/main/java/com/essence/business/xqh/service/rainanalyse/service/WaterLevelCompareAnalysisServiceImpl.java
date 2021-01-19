package com.essence.business.xqh.service.rainanalyse.service;

import com.essence.business.xqh.api.rainanalyse.dto.WaterLevelAnalysisInfoDto;
import com.essence.business.xqh.api.rainanalyse.dto.WaterLevelCompareDetailInfoDto;
import com.essence.business.xqh.api.rainanalyse.dto.WaterLevelCompareInfoDto;
import com.essence.business.xqh.api.rainanalyse.dto.WaterLevelStationInfoDto;
import com.essence.business.xqh.api.rainanalyse.service.WaterLevelCompareAnalysisService;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.dao.information.StBRiverDao;
import com.essence.business.xqh.dao.dao.information.dto.StBRiverDto;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.rainfall.TRiverR;
import com.essence.framework.jpa.Criterion;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 雨水情测报分析--水情多维分析serviceImpl
 * @Author huangxiaoli
 * @Description
 * @Date 16:20 2020/9/3
 * @Param
 * @return
 **/
@Service
public class WaterLevelCompareAnalysisServiceImpl implements WaterLevelCompareAnalysisService {
    @Autowired
    private StStbprpBDao stbprpBDao;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private StBRiverDao stBRiverDao;

    /**
     * 水情分析--查询水位站信息
     * @Author huangxiaoli
     * @Description
     * @Date 10:53 2020/9/12
     * @Param []
     * @return java.util.List<com.essence.tzsyq.rainanalyse.dto.WaterLevelStationInfoDto>
     **/
    /*@Override
    public List<WaterLevelStationInfoDto> getWaterLevelStationInfoDto() {
        List<WaterLevelStationInfoDto> waterLevelStationInfoList = new ArrayList<>();

        //查询正在启用的远程传送的水位站信息
        List<TStbprpB> stbprpBList = stbprpBDao.findUseWaterLevelStbprpB("ZZ");
        if (stbprpBList.size()>0){
            for (TStbprpB tStbprpB : stbprpBList) {
                WaterLevelStationInfoDto waterLevelStationInfoDto = new WaterLevelStationInfoDto();
                waterLevelStationInfoDto.setStcd(tStbprpB.getStcd());
                waterLevelStationInfoDto.setStnm(tStbprpB.getStnm());
                waterLevelStationInfoDto.setType("0");
                waterLevelStationInfoList.add(waterLevelStationInfoDto);
            }
        }

        //查询公司最新创建的水位站信息
        List<BaseStStbprpB> baseStStbprpBList = baseStStbprpBDao.findBySttp("1");
        if (baseStStbprpBList.size()>0){
            for (BaseStStbprpB baseStStbprpB : baseStStbprpBList) {
                WaterLevelStationInfoDto waterLevelStationInfoDto = new WaterLevelStationInfoDto();
                waterLevelStationInfoDto.setStcd(baseStStbprpB.getStcd());
                waterLevelStationInfoDto.setStnm(baseStStbprpB.getStnm());
                waterLevelStationInfoDto.setType("1");
                waterLevelStationInfoList.add(waterLevelStationInfoDto);
            }
        }

        waterLevelStationInfoList.sort(Comparator.comparing(WaterLevelStationInfoDto::getStnm));

        return waterLevelStationInfoList;
    }*/

    /**
     * 水情分析--查询水位站信息
     * @Author huangxiaoli
     * @Description
     * @Date 11:12 2020/9/17
     * @Param []
     * @return java.util.List<com.essence.tzsyq.rainanalyse.dto.WaterLevelStationInfoDto>
     **/
    @Override
    public List<WaterLevelStationInfoDto> getWaterLevelStationInfoDto() {
        List<WaterLevelStationInfoDto> waterLevelStationInfoList = new ArrayList<>();

        //查询正在启用的远程传送的水位站信息
        List<StStbprpB> stbprpBList = stbprpBDao.findUseWaterLevelStbprpB("ZZ");
        if (stbprpBList.size()>0){
            for (StStbprpB stStbprpB : stbprpBList) {
                WaterLevelStationInfoDto waterLevelStationInfoDto = new WaterLevelStationInfoDto();
                waterLevelStationInfoDto.setStcd(stStbprpB.getStcd());
                waterLevelStationInfoDto.setStnm(stStbprpB.getStnm());
                waterLevelStationInfoDto.setType("0");
                waterLevelStationInfoList.add(waterLevelStationInfoDto);
            }
        }


        waterLevelStationInfoList.sort(Comparator.comparing(WaterLevelStationInfoDto::getStnm));

        return waterLevelStationInfoList;
    }

    /**
     * 水情分析--根据条件分页查询水位站水位信息
     * @Author huangxiaoli
     * @Description
     * @Date 13:47 2020/9/12
     * @Param [param]
     * @return com.essence.framework.jpa.Paginator<com.essence.tzsyq.rainanalyse.dto.WaterLevelAnalysisInfoDto>
     **/
   /* @Override
    public Paginator<WaterLevelAnalysisInfoDto> getWaterLevelAnalysisDataPageInfo(PaginatorParam param) {
        int currentPage = param.getCurrentPage();
        int pageSize = param.getPageSize();
        Paginator<WaterLevelAnalysisInfoDto> paginator = new Paginator<>(currentPage, pageSize);
        List<WaterLevelAnalysisInfoDto> waterLevelAnalysisInfoList = new ArrayList<>();

        List<WaterLevelAnalysisInfoDto> waterLevelAnalysisInfoPageList = new ArrayList<>();
        Map<String, WaterLevelAnalysisInfoDto> waterLevelAnalysisInfoMap = new HashMap<>();


        String type = "";
        String stcd = "";
        List<Criterion> conditions = param.getConditions();
        if (null != conditions && conditions.size() > 0) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

            for (Criterion condition : conditions) {
                String fieldName = condition.getFieldName();
                Object value = condition.getValue();
                if (null != value && !StringUtils.isEmpty(value.toString())) {
                    if ("type".equals(fieldName)) {
                        type = value.toString();
                    } else if ("stcd".equals(fieldName)) {
                        stcd = value.toString();
                    }
                }
            }
        }

        //查询河道数据
        List<StBRiverDto> stBRiverDtoList = stBRiverDao.findAllRiverData();
        Map<String, String> stBRiverDtoMap = new HashMap<>();
        if (stBRiverDtoList.size()>0){
            for (StBRiverDto stBRiverDto : stBRiverDtoList) {
                stBRiverDtoMap.put(stBRiverDto.getId(),stBRiverDto.getRiver());
            }
        }


        //查询各个测站最新水位(两天内的最新水位，两天内无数据，则实测水位为空)
        LocalDateTime currentTime = LocalDateTime.now();
        Date time = Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant());
        Date preDayTime2 = Date.from(currentTime.plusDays(-2).atZone(ZoneId.systemDefault()).toInstant());


        if (StringUtils.isEmpty(type)) {//查询所有测站

            //查询远程接收的测站信息
            List<TStbprpB> stbprpBList = stbprpBDao.findUseWaterLevelStbprpB("ZZ");
            List<String> stcdList = new ArrayList<>();
            if (stbprpBList.size() > 0) {
                for (TStbprpB tStbprpB : stbprpBList) {
                    stcdList.add(tStbprpB.getStcd());

                    WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = new WaterLevelAnalysisInfoDto();
                    waterLevelAnalysisInfoDto.setStcd(tStbprpB.getStcd());
                    waterLevelAnalysisInfoDto.setStnm(tStbprpB.getStnm());
                    waterLevelAnalysisInfoDto.setRvnm(tStbprpB.getRvnm());
                    waterLevelAnalysisInfoDto.setType("0");
                    waterLevelAnalysisInfoMap.put(tStbprpB.getStcd(), waterLevelAnalysisInfoDto);
                }


                //查询最新水位
                AggregationOptions latestAggregationOptions = AggregationOptions.builder().allowDiskUse(true).build();
                Aggregation latestAggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("stcd").in(stcdList)),
                        Aggregation.match(Criteria.where("tm").gte(preDayTime2)),
                        Aggregation.match(Criteria.where("tm").lte(time)),
                        Aggregation.group("stcd").max("tm").as("tm").first("z").as("z").first("stcd").as("stcd")).withOptions(latestAggregationOptions);

                AggregationResults<TRiverR> latestStRiverR = mongoTemplate.aggregate(latestAggregation, "st_river_r", TRiverR.class);
                List<TRiverR> data = latestStRiverR.getMappedResults();
                if (data.size()>0){
                    for (TRiverR datum : data) {
                        WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoMap.get(datum.getStcd());
                        waterLevelAnalysisInfoDto.setZ(datum.getZ());
                        waterLevelAnalysisInfoMap.put(datum.getStcd(),waterLevelAnalysisInfoDto);
                    }
                }


                //根据条件查询测站最高水位信息
                AggregationOptions aggregationOptions = AggregationOptions.builder().allowDiskUse(true).build();
                Aggregation maxAggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("stcd").in(stcdList)),
                        Aggregation.group("stcd").max("z").as("z").first("tm").as("tm").first("stcd").as("stcd")).withOptions(aggregationOptions);

                AggregationResults<TRiverR> maxStRiverR = mongoTemplate.aggregate(maxAggregation, "st_river_r", TRiverR.class);
                List<TRiverR> maxStcdDataList = maxStRiverR.getMappedResults();

                if (maxStcdDataList.size() > 0) {
                    for (TRiverR tRiverR : maxStcdDataList) {
                        WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoMap.get(tRiverR.getStcd());
                        waterLevelAnalysisInfoDto.setMaxZ(tRiverR.getZ());
                        waterLevelAnalysisInfoDto.setMaxZTm(tRiverR.getTm());
                        waterLevelAnalysisInfoMap.put(tRiverR.getStcd(),waterLevelAnalysisInfoDto);
                    }
                }

            }

            //----------------------查询新监测站信息----------------------
            List<BaseStStbprpB> baseStStbprpBList = baseStStbprpBDao.findBySttp("1");
            List<String> baseStcdList = new ArrayList<>();
            if (baseStStbprpBList.size()>0){

                for (BaseStStbprpB baseStStbprpB : baseStStbprpBList) {
                    baseStcdList.add(baseStStbprpB.getStcd());

                    WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = new WaterLevelAnalysisInfoDto();
                    waterLevelAnalysisInfoDto.setStcd(baseStStbprpB.getStcd());
                    waterLevelAnalysisInfoDto.setStnm(baseStStbprpB.getStnm());

                    String riversId = baseStStbprpB.getRvnm();
                    //查询河流名称
                    String rvnm = stBRiverDtoMap.get(riversId);
                    waterLevelAnalysisInfoDto.setRvnm(rvnm);
                    waterLevelAnalysisInfoDto.setType("1");
                    waterLevelAnalysisInfoMap.put(baseStStbprpB.getStcd(), waterLevelAnalysisInfoDto);
                }


                //查询测站最新水位
                List<TBaseStYcriverR> maxTimeRiverInfoList = baseStYcriverRDao.findMaxTimeByStcdInAndTmBetween(baseStcdList,preDayTime2,time);
                if (maxTimeRiverInfoList.size()>0){
                    for (TBaseStYcriverR tBaseStYcriverR : maxTimeRiverInfoList) {
                        WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoMap.get(tBaseStYcriverR.getStcd());
                        waterLevelAnalysisInfoDto.setZ(tBaseStYcriverR.getZ());
                        waterLevelAnalysisInfoMap.put(tBaseStYcriverR.getStcd(),waterLevelAnalysisInfoDto);
                    }
                }

                //查询测站最高水位
                List<TBaseStYcriverR> maxZRiverInfoList = baseStYcriverRDao.findMaxZByStcdIn(baseStcdList);
                if (maxZRiverInfoList.size()>0){
                    for (TBaseStYcriverR tBaseStYcriverR : maxZRiverInfoList) {
                        WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoMap.get(tBaseStYcriverR.getStcd());
                        waterLevelAnalysisInfoDto.setMaxZ(waterLevelAnalysisInfoDto.getZ());
                        waterLevelAnalysisInfoDto.setMaxZTm(waterLevelAnalysisInfoDto.getMaxZTm());
                        waterLevelAnalysisInfoMap.put(tBaseStYcriverR.getStcd(),waterLevelAnalysisInfoDto);
                    }
                }
            }
        }else {
            if ("0".equals(type)){//查询远程接收的测站信息

                TStbprpB stbprpB = stbprpBDao.findByStcd(stcd);
                if (null!=stbprpB){
                    WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = new WaterLevelAnalysisInfoDto();
                    waterLevelAnalysisInfoDto.setStcd(stbprpB.getStcd());
                    waterLevelAnalysisInfoDto.setStnm(stbprpB.getStnm());
                    String rvnm = stbprpB.getRvnm();
                    waterLevelAnalysisInfoDto.setRvnm(rvnm);
                    waterLevelAnalysisInfoDto.setType("0");

                    //查询最新水位
                    AggregationOptions latestAggregationOptions = AggregationOptions.builder().allowDiskUse(true).build();
                    Aggregation latestAggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("stcd").is(stbprpB.getStcd())),
                            Aggregation.match(Criteria.where("tm").gte(preDayTime2)),
                            Aggregation.match(Criteria.where("tm").lte(time)),
                            Aggregation.group("stcd").max("tm").as("tm").first("z").as("z").first("stcd").as("stcd")).withOptions(latestAggregationOptions);

                    AggregationResults<TRiverR> latestStRiverR = mongoTemplate.aggregate(latestAggregation, "st_river_r", TRiverR.class);
                    List<TRiverR> data = latestStRiverR.getMappedResults();
                    if (data.size()>0){
                        TRiverR riverR = data.get(0);
                        waterLevelAnalysisInfoDto.setZ(riverR.getZ());
                    }


                    //根据条件查询测站最高水位信息
                    AggregationOptions aggregationOptions = AggregationOptions.builder().allowDiskUse(true).build();
                    Aggregation maxAggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("stcd").is(stbprpB.getStcd())),
                            Aggregation.group("stcd").max("z").as("z").first("tm").as("tm").first("stcd").as("stcd")).withOptions(aggregationOptions);

                    AggregationResults<TRiverR> maxStRiverR = mongoTemplate.aggregate(maxAggregation, "st_river_r", TRiverR.class);
                    List<TRiverR> maxStcdDataList = maxStRiverR.getMappedResults();
                    if (maxStcdDataList.size()>0){
                        TRiverR riverR = maxStcdDataList.get(0);
                        waterLevelAnalysisInfoDto.setMaxZ(riverR.getZ());
                        waterLevelAnalysisInfoDto.setMaxZTm(riverR.getTm());
                    }
                    waterLevelAnalysisInfoMap.put(stbprpB.getStcd(),waterLevelAnalysisInfoDto);
                }


            }else if ("1".equals(type)){//查询新建测站信息
                BaseStStbprpB baseStStbprpB = baseStStbprpBDao.findByStcd(stcd);
                if (null!=baseStStbprpB){
                    WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = new WaterLevelAnalysisInfoDto();
                    waterLevelAnalysisInfoDto.setStcd(baseStStbprpB.getStcd());
                    waterLevelAnalysisInfoDto.setStnm(baseStStbprpB.getStnm());

                    String riversId = baseStStbprpB.getRvnm();
                    //查询河流名称
                    String rvnm = stBRiverDtoMap.get(riversId);
                    waterLevelAnalysisInfoDto.setRvnm(rvnm);
                    waterLevelAnalysisInfoDto.setType("1");

                    //查询测站最新水位
                    TBaseStYcriverR latestBaseStYcriverR = baseStYcriverRDao.findMaxTimeByStcdAndTmBetween(baseStStbprpB.getStcd(), preDayTime2, time);
                    if (null!=latestBaseStYcriverR){
                        waterLevelAnalysisInfoDto.setZ(latestBaseStYcriverR.getZ());
                    }

                    //查询测站最高水位
                    List<TBaseStYcriverR> maxZRiverInfoList = baseStYcriverRDao.findMaxZByStcd(baseStStbprpB.getStcd());
                    if (maxZRiverInfoList.size()>0){
                        TBaseStYcriverR tBaseStYcriverR = maxZRiverInfoList.get(0);
                        waterLevelAnalysisInfoDto.setMaxZ(tBaseStYcriverR.getZ());
                        waterLevelAnalysisInfoDto.setMaxZTm(tBaseStYcriverR.getTm());
                    }
                    waterLevelAnalysisInfoMap.put(baseStStbprpB.getStcd(), waterLevelAnalysisInfoDto);

                }

            }
        }


        //整合数据
        for (String stationStcd : waterLevelAnalysisInfoMap.keySet()) {
            WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoMap.get(stationStcd);
            waterLevelAnalysisInfoList.add(waterLevelAnalysisInfoDto);
        }

        waterLevelAnalysisInfoList.sort(Comparator.comparing(WaterLevelAnalysisInfoDto::getRvnm).thenComparing(Comparator.comparing(WaterLevelAnalysisInfoDto::getStnm)));



         //将数据进行分页
        int totalPage = ((waterLevelAnalysisInfoList.size() % pageSize) > 0) ? (waterLevelAnalysisInfoList.size() / pageSize + 1) : (waterLevelAnalysisInfoList.size() / pageSize);
        paginator.setPageCount(totalPage);
        paginator.setTotalCount(waterLevelAnalysisInfoList.size());
        Integer beginRecorde = pageSize * (currentPage - 1); //开始记录
        paginator.setStartIndex(beginRecorde);

        if (waterLevelAnalysisInfoList.size() >= (pageSize * currentPage)) {
            Integer endRecorde = pageSize * currentPage - 1; //结束记录
            paginator.setEndIndex(endRecorde);
            for (int i = beginRecorde; i <= endRecorde; i++) {
                WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoList.get(i);
                waterLevelAnalysisInfoPageList.add(waterLevelAnalysisInfoDto);
            }
        } else {
            paginator.setEndIndex(waterLevelAnalysisInfoList.size());
            if (waterLevelAnalysisInfoList.size()>0){
                for (int i = beginRecorde; i < waterLevelAnalysisInfoList.size(); i++) {
                    WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoList.get(i);
                    waterLevelAnalysisInfoPageList.add(waterLevelAnalysisInfoDto);
                }
            }
        }

        paginator.setItems(waterLevelAnalysisInfoPageList);
        return paginator;
    }*/

   /**
    * 水情分析--根据条件分页查询水位站水位信息
    * @Author huangxiaoli
    * @Description
    * @Date 11:11 2020/9/17
    * @Param [param]
    * @return com.essence.framework.jpa.Paginator<com.essence.tzsyq.rainanalyse.dto.WaterLevelAnalysisInfoDto>
    **/
    @Override
    public Paginator<WaterLevelAnalysisInfoDto> getWaterLevelAnalysisDataPageInfo(PaginatorParam param) {
        int currentPage = param.getCurrentPage();
        int pageSize = param.getPageSize();
        Paginator<WaterLevelAnalysisInfoDto> paginator = new Paginator<>(currentPage, pageSize);
        List<WaterLevelAnalysisInfoDto> waterLevelAnalysisInfoList = new ArrayList<>();

        List<WaterLevelAnalysisInfoDto> waterLevelAnalysisInfoPageList = new ArrayList<>();
        Map<String, WaterLevelAnalysisInfoDto> waterLevelAnalysisInfoMap = new HashMap<>();


        String type = "";
        String stcd = "";
        List<Criterion> conditions = param.getConditions();
        if (null != conditions && conditions.size() > 0) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

            for (Criterion condition : conditions) {
                String fieldName = condition.getFieldName();
                Object value = condition.getValue();
                if (null != value && !StringUtils.isEmpty(value.toString())) {
                    if ("type".equals(fieldName)) {
                        type = value.toString();
                    } else if ("stcd".equals(fieldName)) {
                        stcd = value.toString();
                    }
                }
            }
        }

        //查询河道数据
        List<StBRiverDto> stBRiverDtoList = stBRiverDao.findAllRiverData();
        Map<String, String> stBRiverDtoMap = new HashMap<>();
        if (stBRiverDtoList.size()>0){
            for (StBRiverDto stBRiverDto : stBRiverDtoList) {
                stBRiverDtoMap.put(stBRiverDto.getId(),stBRiverDto.getRiver());
            }
        }


        //查询各个测站最新水位(两天内的最新水位，两天内无数据，则实测水位为空)
        LocalDateTime currentTime = LocalDateTime.now();
        Date time = Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant());
        Date preDayTime2 = Date.from(currentTime.plusDays(-2).atZone(ZoneId.systemDefault()).toInstant());


        if (StringUtils.isEmpty(type)) {//查询所有测站

            //查询远程接收的测站信息
            List<StStbprpB> stbprpBList = stbprpBDao.findUseWaterLevelStbprpB("ZZ");
            List<String> stcdList = new ArrayList<>();
            if (stbprpBList.size() > 0) {
                for (StStbprpB stStbprpB : stbprpBList) {
                    stcdList.add(stStbprpB.getStcd());

                    WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = new WaterLevelAnalysisInfoDto();
                    waterLevelAnalysisInfoDto.setStcd(stStbprpB.getStcd());
                    waterLevelAnalysisInfoDto.setStnm(stStbprpB.getStnm());
                    waterLevelAnalysisInfoDto.setRvnm(stStbprpB.getRvnm());
                    waterLevelAnalysisInfoDto.setType("0");
                    waterLevelAnalysisInfoMap.put(stStbprpB.getStcd(), waterLevelAnalysisInfoDto);
                }


                //查询最新水位
                AggregationOptions latestAggregationOptions = AggregationOptions.builder().allowDiskUse(true).build();
                Aggregation latestAggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("stcd").in(stcdList)),
                        Aggregation.match(Criteria.where("tm").gte(preDayTime2)),
                        Aggregation.match(Criteria.where("tm").lte(time)),
                        Aggregation.group("stcd").max("tm").as("tm").first("z").as("z").first("stcd").as("stcd")).withOptions(latestAggregationOptions);

                AggregationResults<TRiverR> latestStRiverR = mongoTemplate.aggregate(latestAggregation, "st_river_r", TRiverR.class);
                List<TRiverR> data = latestStRiverR.getMappedResults();
                if (data.size()>0){
                    for (TRiverR datum : data) {
                        WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoMap.get(datum.getStcd());
                        waterLevelAnalysisInfoDto.setZ(datum.getZ());
                        waterLevelAnalysisInfoMap.put(datum.getStcd(),waterLevelAnalysisInfoDto);
                    }
                }


                //根据条件查询测站最高水位信息
                AggregationOptions aggregationOptions = AggregationOptions.builder().allowDiskUse(true).build();
                Aggregation maxAggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("stcd").in(stcdList)),
                        Aggregation.group("stcd").max("z").as("z").first("tm").as("tm").first("stcd").as("stcd")).withOptions(aggregationOptions);

                AggregationResults<TRiverR> maxStRiverR = mongoTemplate.aggregate(maxAggregation, "st_river_r", TRiverR.class);
                List<TRiverR> maxStcdDataList = maxStRiverR.getMappedResults();

                if (maxStcdDataList.size() > 0) {
                    for (TRiverR tRiverR : maxStcdDataList) {
                        WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoMap.get(tRiverR.getStcd());
                        waterLevelAnalysisInfoDto.setMaxZ(tRiverR.getZ());
                        waterLevelAnalysisInfoDto.setMaxZTm(tRiverR.getTm());
                        waterLevelAnalysisInfoMap.put(tRiverR.getStcd(),waterLevelAnalysisInfoDto);
                    }
                }

            }
        }else {


            StStbprpB stbprpB = stbprpBDao.findByStcd(stcd);
            if (null!=stbprpB){
                WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = new WaterLevelAnalysisInfoDto();
                waterLevelAnalysisInfoDto.setStcd(stbprpB.getStcd());
                waterLevelAnalysisInfoDto.setStnm(stbprpB.getStnm());
                String rvnm = stbprpB.getRvnm();
                waterLevelAnalysisInfoDto.setRvnm(rvnm);
                waterLevelAnalysisInfoDto.setType("0");

                //查询最新水位
                AggregationOptions latestAggregationOptions = AggregationOptions.builder().allowDiskUse(true).build();
                Aggregation latestAggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("stcd").is(stbprpB.getStcd())),
                        Aggregation.match(Criteria.where("tm").gte(preDayTime2)),
                        Aggregation.match(Criteria.where("tm").lte(time)),
                        Aggregation.group("stcd").max("tm").as("tm").first("z").as("z").first("stcd").as("stcd")).withOptions(latestAggregationOptions);

                AggregationResults<TRiverR> latestStRiverR = mongoTemplate.aggregate(latestAggregation, "st_river_r", TRiverR.class);
                List<TRiverR> data = latestStRiverR.getMappedResults();
                if (data.size()>0){
                    TRiverR riverR = data.get(0);
                    waterLevelAnalysisInfoDto.setZ(riverR.getZ());
                }


                //根据条件查询测站最高水位信息
                AggregationOptions aggregationOptions = AggregationOptions.builder().allowDiskUse(true).build();
                Aggregation maxAggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("stcd").is(stbprpB.getStcd())),
                        Aggregation.group("stcd").max("z").as("z").first("tm").as("tm").first("stcd").as("stcd")).withOptions(aggregationOptions);

                AggregationResults<TRiverR> maxStRiverR = mongoTemplate.aggregate(maxAggregation, "st_river_r", TRiverR.class);
                List<TRiverR> maxStcdDataList = maxStRiverR.getMappedResults();
                if (maxStcdDataList.size()>0){
                    TRiverR riverR = maxStcdDataList.get(0);
                    waterLevelAnalysisInfoDto.setMaxZ(riverR.getZ());
                    waterLevelAnalysisInfoDto.setMaxZTm(riverR.getTm());
                }
                waterLevelAnalysisInfoMap.put(stbprpB.getStcd(),waterLevelAnalysisInfoDto);
            }


        }


        //整合数据
        for (String stationStcd : waterLevelAnalysisInfoMap.keySet()) {
            WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoMap.get(stationStcd);
            waterLevelAnalysisInfoList.add(waterLevelAnalysisInfoDto);
        }

        waterLevelAnalysisInfoList.sort(Comparator.comparing(WaterLevelAnalysisInfoDto::getRvnm).thenComparing(Comparator.comparing(WaterLevelAnalysisInfoDto::getStnm)));



        //将数据进行分页
        int totalPage = ((waterLevelAnalysisInfoList.size() % pageSize) > 0) ? (waterLevelAnalysisInfoList.size() / pageSize + 1) : (waterLevelAnalysisInfoList.size() / pageSize);
        paginator.setPageCount(totalPage);
        paginator.setTotalCount(waterLevelAnalysisInfoList.size());
        Integer beginRecorde = pageSize * (currentPage - 1); //开始记录
        paginator.setStartIndex(beginRecorde);

        if (waterLevelAnalysisInfoList.size() >= (pageSize * currentPage)) {
            Integer endRecorde = pageSize * currentPage - 1; //结束记录
            paginator.setEndIndex(endRecorde);
            for (int i = beginRecorde; i <= endRecorde; i++) {
                WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoList.get(i);
                waterLevelAnalysisInfoPageList.add(waterLevelAnalysisInfoDto);
            }
        } else {
            paginator.setEndIndex(waterLevelAnalysisInfoList.size());
            if (waterLevelAnalysisInfoList.size()>0){
                for (int i = beginRecorde; i < waterLevelAnalysisInfoList.size(); i++) {
                    WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoList.get(i);
                    waterLevelAnalysisInfoPageList.add(waterLevelAnalysisInfoDto);
                }
            }
        }

        paginator.setItems(waterLevelAnalysisInfoPageList);
        return paginator;
    }

    /**
     * 水情形式分析--水情对比分析--根据条件分页查询水情信息
     * @Author huangxiaoli
     * @Description
     * @Date 16:20 2020/9/3
     * @Param [param]
     * @return com.essence.framework.jpa.Paginator<com.essence.tzsyq.rainanalyse.dto.WaterLevelAnalysisInfoDto>
     **/
    /*@Override
    public Paginator<WaterLevelAnalysisInfoDto> getWaterLevelAnalysisPageInfo(PaginatorParam param) {

        Paginator<WaterLevelAnalysisInfoDto> paginator = new Paginator<>(param.getCurrentPage(), param.getPageSize());

        List<WaterLevelAnalysisInfoDto> waterLevelAnalysisInfoList = new ArrayList<>();


        Date startTime=null;
        Date endTime=null;

        List<Criterion> conditions = param.getConditions();
        List<Criterion> conditionsNew = new ArrayList<>();
        if (null!=conditions && conditions.size()>0){
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

            for (Criterion condition : conditions) {
                String fieldName = condition.getFieldName();
                Object value = condition.getValue();
                if (null!=value && !StringUtils.isEmpty(value.toString())){

                    if ("stnm".equals(fieldName)){
                        conditionsNew.add(condition);
                    }else if("startTime".equals(fieldName)){
                        startTime=Date.from(LocalDateTime.parse(value.toString(),dateTimeFormatter).atZone(ZoneId.systemDefault()).toInstant());
                    }else if ("endTime".equals(fieldName)){
                        endTime=Date.from(LocalDateTime.parse(value.toString(),dateTimeFormatter).atZone(ZoneId.systemDefault()).toInstant());
                    }
                }
            }
        }

        param.setConditions(conditionsNew);

        //分页查询水位测站信息
        Paginator<TStbprpB> stbprpBPaginator = stbprpBDao.findAll(param);
        //拷贝分页信息
        BeanUtils.copyProperties(stbprpBPaginator,paginator);

        List<TStbprpB> items = stbprpBPaginator.getItems();
        if (items.size()>0){

            Map<String, WaterLevelAnalysisInfoDto> waterLevelAnalysisInfoMap = new HashMap<>();//key为测站编码

            List<String> stcdList = new ArrayList<>();
            for (int i=0;i<items.size();i++){
                TStbprpB tStbprpB = items.get(i);
                stcdList.add(tStbprpB.getStcd());

                WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = new WaterLevelAnalysisInfoDto();
                waterLevelAnalysisInfoDto.setStcd(tStbprpB.getStcd());
                waterLevelAnalysisInfoDto.setStnm(tStbprpB.getStnm());
                waterLevelAnalysisInfoDto.setRvnm(tStbprpB.getRvnm());

                waterLevelAnalysisInfoMap.put(tStbprpB.getStcd(),waterLevelAnalysisInfoDto);
            }

            //根据条件查询时间段内的最高水位信息
            Map<String, TRiverR> maxWaterLevelDataMap = new HashMap<>();//key为测站编码
            if (null!=startTime && null!=endTime){
                AggregationOptions aggregationOptions = AggregationOptions.builder().allowDiskUse(true).build();
                Aggregation maxAggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("stcd").in(stcdList)),
                        Aggregation.group("stcd").max("z").as("z").first("tm").as("tm").first("stcd").as("stcd")).withOptions(aggregationOptions);

                AggregationResults<TRiverR> maxStRiverR = mongoTemplate.aggregate(maxAggregation, "st_river_r", TRiverR.class);
                List<TRiverR> maxStcdDataList = maxStRiverR.getMappedResults();

                if (maxStcdDataList.size()>0){
                    for (TRiverR tRiverR : maxStcdDataList) {
                        maxWaterLevelDataMap.put(tRiverR.getStcd(),tRiverR);
                    }
                }
            }
            //整合数据
            for (String stcd : waterLevelAnalysisInfoMap.keySet()) {
                WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoMap.get(stcd);

                //查询时间段内的最高水位
                TRiverR tRiverR = maxWaterLevelDataMap.get(stcd);
                if (null!=tRiverR){
                    waterLevelAnalysisInfoDto.setMaxZ(tRiverR.getZ());
                    waterLevelAnalysisInfoDto.setMaxZTm(tRiverR.getTm());
                }

                waterLevelAnalysisInfoList.add(waterLevelAnalysisInfoDto);
            }

        }
        paginator.setItems(waterLevelAnalysisInfoList);
        return paginator;
    }*/



    /**
     * 水情形式分析--水情对比分析--根据条件分页查询水情信息
     * @Author huangxiaoli
     * @Description
     * @Date 15:20 2020/9/12
     * @Param [param]
     * @return com.essence.framework.jpa.Paginator<com.essence.tzsyq.rainanalyse.dto.WaterLevelAnalysisInfoDto>
     **/
   /* @Override
    public Paginator<WaterLevelAnalysisInfoDto> getWaterLevelAnalysisPageInfo(PaginatorParam param) {

        int currentPage = param.getCurrentPage();
        int pageSize = param.getPageSize();
        Paginator<WaterLevelAnalysisInfoDto> paginator = new Paginator<>(currentPage, pageSize);
        List<WaterLevelAnalysisInfoDto> waterLevelAnalysisInfoList = new ArrayList<>();

        List<WaterLevelAnalysisInfoDto> waterLevelAnalysisInfoPageList = new ArrayList<>();
        Map<String, WaterLevelAnalysisInfoDto> waterLevelAnalysisInfoMap = new HashMap<>();

        String type = "";
        String stcd = "";
        List<Criterion> conditions = param.getConditions();
        if (null != conditions && conditions.size() > 0) {
            for (Criterion condition : conditions) {
                String fieldName = condition.getFieldName();
                Object value = condition.getValue();
                if (null != value && !StringUtils.isEmpty(value.toString())) {
                    if ("type".equals(fieldName)) {
                        type = value.toString();
                    } else if ("stcd".equals(fieldName)) {
                        stcd = value.toString();
                    }
                }
            }
        }

        //查询河道数据
        List<StBRiverDto> stBRiverDtoList = stBRiverDao.findAllRiverData();
        Map<String, String> stBRiverDtoMap = new HashMap<>();
        if (stBRiverDtoList.size()>0){
            for (StBRiverDto stBRiverDto : stBRiverDtoList) {
                stBRiverDtoMap.put(stBRiverDto.getId(),stBRiverDto.getRiver());
            }
        }


        if (StringUtils.isEmpty(type)) {//查询所有测站

            //查询远程接收的测站信息
            List<TStbprpB> stbprpBList = stbprpBDao.findUseWaterLevelStbprpB("ZZ");
            List<String> stcdList = new ArrayList<>();
            if (stbprpBList.size() > 0) {
                for (TStbprpB tStbprpB : stbprpBList) {
                    stcdList.add(tStbprpB.getStcd());

                    WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = new WaterLevelAnalysisInfoDto();
                    waterLevelAnalysisInfoDto.setStcd(tStbprpB.getStcd());
                    waterLevelAnalysisInfoDto.setStnm(tStbprpB.getStnm());
                    waterLevelAnalysisInfoDto.setRvnm(tStbprpB.getRvnm());
                    waterLevelAnalysisInfoDto.setType("0");//远程对接的测站
                    waterLevelAnalysisInfoMap.put(tStbprpB.getStcd(), waterLevelAnalysisInfoDto);
                }


                //根据条件查询测站最高水位信息
                AggregationOptions aggregationOptions = AggregationOptions.builder().allowDiskUse(true).build();
                Aggregation maxAggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("stcd").in(stcdList)),
                        Aggregation.group("stcd").max("z").as("z").first("tm").as("tm").first("stcd").as("stcd")).withOptions(aggregationOptions);

                AggregationResults<TRiverR> maxStRiverR = mongoTemplate.aggregate(maxAggregation, "st_river_r", TRiverR.class);
                List<TRiverR> maxStcdDataList = maxStRiverR.getMappedResults();

                if (maxStcdDataList.size() > 0) {
                    for (TRiverR tRiverR : maxStcdDataList) {
                        WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoMap.get(tRiverR.getStcd());
                        waterLevelAnalysisInfoDto.setMaxZ(tRiverR.getZ());
                        waterLevelAnalysisInfoDto.setMaxZTm(tRiverR.getTm());
                        waterLevelAnalysisInfoMap.put(tRiverR.getStcd(),waterLevelAnalysisInfoDto);
                    }
                }

            }

            //----------------------查询新监测站信息----------------------
            List<BaseStStbprpB> baseStStbprpBList = baseStStbprpBDao.findBySttp("1");
            List<String> baseStcdList = new ArrayList<>();
            if (baseStStbprpBList.size()>0){

                for (BaseStStbprpB baseStStbprpB : baseStStbprpBList) {
                    baseStcdList.add(baseStStbprpB.getStcd());

                    WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = new WaterLevelAnalysisInfoDto();
                    waterLevelAnalysisInfoDto.setStcd(baseStStbprpB.getStcd());
                    waterLevelAnalysisInfoDto.setStnm(baseStStbprpB.getStnm());

                    String riversId = baseStStbprpB.getRvnm();
                    //查询河流名称
                    String rvnm = stBRiverDtoMap.get(riversId);
                    waterLevelAnalysisInfoDto.setRvnm(rvnm);
                    waterLevelAnalysisInfoDto.setType("1");//公司新建测站
                    waterLevelAnalysisInfoMap.put(baseStStbprpB.getStcd(), waterLevelAnalysisInfoDto);
                }


                //查询测站最高水位
                List<TBaseStYcriverR> maxZRiverInfoList = baseStYcriverRDao.findMaxZByStcdIn(baseStcdList);
                if (maxZRiverInfoList.size()>0){
                    for (TBaseStYcriverR tBaseStYcriverR : maxZRiverInfoList) {
                        WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoMap.get(tBaseStYcriverR.getStcd());
                        waterLevelAnalysisInfoDto.setMaxZ(waterLevelAnalysisInfoDto.getZ());
                        waterLevelAnalysisInfoDto.setMaxZTm(waterLevelAnalysisInfoDto.getMaxZTm());
                        waterLevelAnalysisInfoMap.put(tBaseStYcriverR.getStcd(),waterLevelAnalysisInfoDto);
                    }
                }
            }
        }else {
            if ("0".equals(type)){//查询远程接收的测站信息

                TStbprpB stbprpB = stbprpBDao.findByStcd(stcd);
                if (null!=stbprpB){
                    WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = new WaterLevelAnalysisInfoDto();
                    waterLevelAnalysisInfoDto.setStcd(stbprpB.getStcd());
                    waterLevelAnalysisInfoDto.setStnm(stbprpB.getStnm());
                    String rvnm = stbprpB.getRvnm();
                    waterLevelAnalysisInfoDto.setRvnm(rvnm);
                    waterLevelAnalysisInfoDto.setType("0");

                    //根据条件查询测站最高水位信息
                    AggregationOptions aggregationOptions = AggregationOptions.builder().allowDiskUse(true).build();
                    Aggregation maxAggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("stcd").is(stbprpB.getStcd())),
                            Aggregation.group("stcd").max("z").as("z").first("tm").as("tm").first("stcd").as("stcd")).withOptions(aggregationOptions);

                    AggregationResults<TRiverR> maxStRiverR = mongoTemplate.aggregate(maxAggregation, "st_river_r", TRiverR.class);
                    List<TRiverR> maxStcdDataList = maxStRiverR.getMappedResults();
                    if (maxStcdDataList.size()>0){
                        TRiverR riverR = maxStcdDataList.get(0);
                        waterLevelAnalysisInfoDto.setMaxZ(riverR.getZ());
                        waterLevelAnalysisInfoDto.setMaxZTm(riverR.getTm());
                    }
                    waterLevelAnalysisInfoMap.put(stbprpB.getStcd(),waterLevelAnalysisInfoDto);
                }


            }else if ("1".equals(type)){//查询新建测站信息
                BaseStStbprpB baseStStbprpB = baseStStbprpBDao.findByStcd(stcd);
                if (null!=baseStStbprpB){
                    WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = new WaterLevelAnalysisInfoDto();
                    waterLevelAnalysisInfoDto.setStcd(baseStStbprpB.getStcd());
                    waterLevelAnalysisInfoDto.setStnm(baseStStbprpB.getStnm());

                    String riversId = baseStStbprpB.getRvnm();
                    //查询河流名称
                    String rvnm = stBRiverDtoMap.get(riversId);
                    waterLevelAnalysisInfoDto.setRvnm(rvnm);
                    waterLevelAnalysisInfoDto.setType("1");

                    //查询测站最高水位
                    List<TBaseStYcriverR> maxZRiverInfoList = baseStYcriverRDao.findMaxZByStcd(baseStStbprpB.getStcd());
                    if (maxZRiverInfoList.size()>0){
                        TBaseStYcriverR tBaseStYcriverR = maxZRiverInfoList.get(0);
                        waterLevelAnalysisInfoDto.setMaxZ(tBaseStYcriverR.getZ());
                        waterLevelAnalysisInfoDto.setMaxZTm(tBaseStYcriverR.getTm());
                    }
                    waterLevelAnalysisInfoMap.put(baseStStbprpB.getStcd(), waterLevelAnalysisInfoDto);

                }

            }
        }


        //整合数据
        for (String stationStcd : waterLevelAnalysisInfoMap.keySet()) {
            WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoMap.get(stationStcd);
            waterLevelAnalysisInfoList.add(waterLevelAnalysisInfoDto);
        }

        waterLevelAnalysisInfoList.sort(Comparator.comparing(WaterLevelAnalysisInfoDto::getRvnm).thenComparing(Comparator.comparing(WaterLevelAnalysisInfoDto::getStnm)));



        //将数据进行分页
        int totalPage = ((waterLevelAnalysisInfoList.size() % pageSize) > 0) ? (waterLevelAnalysisInfoList.size() / pageSize + 1) : (waterLevelAnalysisInfoList.size() / pageSize);
        paginator.setPageCount(totalPage);
        paginator.setTotalCount(waterLevelAnalysisInfoList.size());
        Integer beginRecorde = pageSize * (currentPage - 1); //开始记录
        paginator.setStartIndex(beginRecorde);

        if (waterLevelAnalysisInfoList.size() >= (pageSize * currentPage)) {
            Integer endRecorde = pageSize * currentPage - 1; //结束记录
            paginator.setEndIndex(endRecorde);
            for (int i = beginRecorde; i <= endRecorde; i++) {
                WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoList.get(i);
                waterLevelAnalysisInfoPageList.add(waterLevelAnalysisInfoDto);
            }
        } else {
            paginator.setEndIndex(waterLevelAnalysisInfoList.size());
            if (waterLevelAnalysisInfoList.size()>0){
                for (int i = beginRecorde; i < waterLevelAnalysisInfoList.size(); i++) {
                    WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoList.get(i);
                    waterLevelAnalysisInfoPageList.add(waterLevelAnalysisInfoDto);
                }
            }
        }

        paginator.setItems(waterLevelAnalysisInfoPageList);
        return paginator;
    }*/


   /**
    * 水情形式分析--水情对比分析--根据条件分页查询水情信息
    * @Author huangxiaoli
    * @Description
    * @Date 11:13 2020/9/17
    * @Param [param]
    * @return com.essence.framework.jpa.Paginator<com.essence.tzsyq.rainanalyse.dto.WaterLevelAnalysisInfoDto>
    **/
    @Override
    public Paginator<WaterLevelAnalysisInfoDto> getWaterLevelAnalysisPageInfo(PaginatorParam param) {

        int currentPage = param.getCurrentPage();
        int pageSize = param.getPageSize();
        Paginator<WaterLevelAnalysisInfoDto> paginator = new Paginator<>(currentPage, pageSize);
        List<WaterLevelAnalysisInfoDto> waterLevelAnalysisInfoList = new ArrayList<>();

        List<WaterLevelAnalysisInfoDto> waterLevelAnalysisInfoPageList = new ArrayList<>();
        Map<String, WaterLevelAnalysisInfoDto> waterLevelAnalysisInfoMap = new HashMap<>();

        String type = "";
        String stcd = "";
        List<Criterion> conditions = param.getConditions();
        if (null != conditions && conditions.size() > 0) {
            for (Criterion condition : conditions) {
                String fieldName = condition.getFieldName();
                Object value = condition.getValue();
                if (null != value && !StringUtils.isEmpty(value.toString())) {
                    if ("type".equals(fieldName)) {
                        type = value.toString();
                    } else if ("stcd".equals(fieldName)) {
                        stcd = value.toString();
                    }
                }
            }
        }

        if (StringUtils.isEmpty(type)) {//查询所有测站

            //查询远程接收的测站信息
            List<StStbprpB> stbprpBList = stbprpBDao.findUseWaterLevelStbprpB("ZZ");
            List<String> stcdList = new ArrayList<>();
            if (stbprpBList.size() > 0) {
                for (StStbprpB stStbprpB : stbprpBList) {
                    stcdList.add(stStbprpB.getStcd());

                    WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = new WaterLevelAnalysisInfoDto();
                    waterLevelAnalysisInfoDto.setStcd(stStbprpB.getStcd());
                    waterLevelAnalysisInfoDto.setStnm(stStbprpB.getStnm());
                    waterLevelAnalysisInfoDto.setRvnm(stStbprpB.getRvnm());
                    waterLevelAnalysisInfoDto.setType("0");//远程对接的测站
                    waterLevelAnalysisInfoMap.put(stStbprpB.getStcd(), waterLevelAnalysisInfoDto);
                }


                //根据条件查询测站最高水位信息
                AggregationOptions aggregationOptions = AggregationOptions.builder().allowDiskUse(true).build();
                Aggregation maxAggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("stcd").in(stcdList)),
                        Aggregation.group("stcd").max("z").as("z").first("tm").as("tm").first("stcd").as("stcd")).withOptions(aggregationOptions);

                AggregationResults<TRiverR> maxStRiverR = mongoTemplate.aggregate(maxAggregation, "st_river_r", TRiverR.class);
                List<TRiverR> maxStcdDataList = maxStRiverR.getMappedResults();

                if (maxStcdDataList.size() > 0) {
                    for (TRiverR tRiverR : maxStcdDataList) {
                        WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoMap.get(tRiverR.getStcd());
                        waterLevelAnalysisInfoDto.setMaxZ(tRiverR.getZ());
                        waterLevelAnalysisInfoDto.setMaxZTm(tRiverR.getTm());
                        waterLevelAnalysisInfoMap.put(tRiverR.getStcd(),waterLevelAnalysisInfoDto);
                    }
                }

            }

        }else {
            if ("0".equals(type)){//查询远程接收的测站信息

                StStbprpB stbprpB = stbprpBDao.findByStcd(stcd);
                if (null!=stbprpB){
                    WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = new WaterLevelAnalysisInfoDto();
                    waterLevelAnalysisInfoDto.setStcd(stbprpB.getStcd());
                    waterLevelAnalysisInfoDto.setStnm(stbprpB.getStnm());
                    String rvnm = stbprpB.getRvnm();
                    waterLevelAnalysisInfoDto.setRvnm(rvnm);
                    waterLevelAnalysisInfoDto.setType("0");

                    //根据条件查询测站最高水位信息
                    AggregationOptions aggregationOptions = AggregationOptions.builder().allowDiskUse(true).build();
                    Aggregation maxAggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("stcd").is(stbprpB.getStcd())),
                            Aggregation.group("stcd").max("z").as("z").first("tm").as("tm").first("stcd").as("stcd")).withOptions(aggregationOptions);

                    AggregationResults<TRiverR> maxStRiverR = mongoTemplate.aggregate(maxAggregation, "st_river_r", TRiverR.class);
                    List<TRiverR> maxStcdDataList = maxStRiverR.getMappedResults();
                    if (maxStcdDataList.size()>0){
                        TRiverR riverR = maxStcdDataList.get(0);
                        waterLevelAnalysisInfoDto.setMaxZ(riverR.getZ());
                        waterLevelAnalysisInfoDto.setMaxZTm(riverR.getTm());
                    }
                    waterLevelAnalysisInfoMap.put(stbprpB.getStcd(),waterLevelAnalysisInfoDto);
                }


            }
        }


        //整合数据
        for (String stationStcd : waterLevelAnalysisInfoMap.keySet()) {
            WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoMap.get(stationStcd);
            waterLevelAnalysisInfoList.add(waterLevelAnalysisInfoDto);
        }

        waterLevelAnalysisInfoList.sort(Comparator.comparing(WaterLevelAnalysisInfoDto::getRvnm).thenComparing(Comparator.comparing(WaterLevelAnalysisInfoDto::getStnm)));



        //将数据进行分页
        int totalPage = ((waterLevelAnalysisInfoList.size() % pageSize) > 0) ? (waterLevelAnalysisInfoList.size() / pageSize + 1) : (waterLevelAnalysisInfoList.size() / pageSize);
        paginator.setPageCount(totalPage);
        paginator.setTotalCount(waterLevelAnalysisInfoList.size());
        Integer beginRecorde = pageSize * (currentPage - 1); //开始记录
        paginator.setStartIndex(beginRecorde);

        if (waterLevelAnalysisInfoList.size() >= (pageSize * currentPage)) {
            Integer endRecorde = pageSize * currentPage - 1; //结束记录
            paginator.setEndIndex(endRecorde);
            for (int i = beginRecorde; i <= endRecorde; i++) {
                WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoList.get(i);
                waterLevelAnalysisInfoPageList.add(waterLevelAnalysisInfoDto);
            }
        } else {
            paginator.setEndIndex(waterLevelAnalysisInfoList.size());
            if (waterLevelAnalysisInfoList.size()>0){
                for (int i = beginRecorde; i < waterLevelAnalysisInfoList.size(); i++) {
                    WaterLevelAnalysisInfoDto waterLevelAnalysisInfoDto = waterLevelAnalysisInfoList.get(i);
                    waterLevelAnalysisInfoPageList.add(waterLevelAnalysisInfoDto);
                }
            }
        }

        paginator.setItems(waterLevelAnalysisInfoPageList);
        return paginator;
    }

    /**
     * 水情形式分析--水情对比分析--查询单个测站去年同期水位对比数据
     * @Author huangxiaoli
     * @Description
     * @Date 17:31 2020/9/3
     * @Param [stcd, startTime, endTime]
     * @return com.essence.tzsyq.rainanalyse.dto.WaterLevelCompareInfoDto
     **/
    @Override
    public WaterLevelCompareInfoDto getWaterLevelCompareInfo(String type, String stcd, Date startTime, Date endTime) {
        WaterLevelCompareInfoDto waterLevelCompareInfoDto = new WaterLevelCompareInfoDto();
        List<WaterLevelCompareDetailInfoDto> searchTmInfoList=new ArrayList<>();//查询时间段的水位数据
        List<WaterLevelCompareDetailInfoDto> preSearchTmInfoList=new ArrayList<>();//查询时间段的去年同期水位数据


            //去年同期时间
            LocalDateTime startTimeLocalDateTime = startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            Date preYearStartTime = Date.from(startTimeLocalDateTime.plusYears(-1).atZone(ZoneId.systemDefault()).toInstant());

            LocalDateTime endTimeLocalDateTime = endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            Date preYearEndTime = Date.from(endTimeLocalDateTime.plusYears(-1).atZone(ZoneId.systemDefault()).toInstant());



            //获取查询时间段内的水位数据
            AggregationOptions aggregationOptions = AggregationOptions.builder().allowDiskUse(true).build();
            Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("stcd").is(stcd)),
                    Aggregation.match(Criteria.where("tm").gte(startTime)),
                    Aggregation.match(Criteria.where("tm").lte(endTime)),
                    Aggregation.sort(Sort.Direction.ASC, "tm")).withOptions(aggregationOptions);

            AggregationResults<TRiverR> stRiverR = mongoTemplate.aggregate(aggregation, "st_river_r", TRiverR.class);
            List<TRiverR> stcdDataList = stRiverR.getMappedResults();
            if (stcdDataList.size()>0){
                for (TRiverR tRiverR : stcdDataList) {
                    WaterLevelCompareDetailInfoDto waterLevelCompareDetailInfoDto = new WaterLevelCompareDetailInfoDto();
                    waterLevelCompareDetailInfoDto.setTm(tRiverR.getTm());
                    waterLevelCompareDetailInfoDto.setZ(tRiverR.getZ());
                    searchTmInfoList.add(waterLevelCompareDetailInfoDto);
                }
            }


            //获取查询时间段内的去年同期水位数据
            AggregationOptions preAggregationOptions = AggregationOptions.builder().allowDiskUse(true).build();
            Aggregation preAggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("stcd").is(stcd)),
                    Aggregation.match(Criteria.where("tm").gte(preYearStartTime)),
                    Aggregation.match(Criteria.where("tm").lte(preYearEndTime)),
                    Aggregation.sort(Sort.Direction.ASC, "tm")).withOptions(preAggregationOptions);

            AggregationResults<TRiverR> preStRiverR = mongoTemplate.aggregate(preAggregation, "st_river_r", TRiverR.class);
            List<TRiverR> preStcdDataList = preStRiverR.getMappedResults();
            if (preStcdDataList.size()>0){
                for (TRiverR tRiverR : preStcdDataList) {
                    WaterLevelCompareDetailInfoDto waterLevelCompareDetailInfoDto = new WaterLevelCompareDetailInfoDto();
                    waterLevelCompareDetailInfoDto.setTm(tRiverR.getTm());
                    waterLevelCompareDetailInfoDto.setZ(tRiverR.getZ());
                    preSearchTmInfoList.add(waterLevelCompareDetailInfoDto);
                }
            }



        waterLevelCompareInfoDto.setSearchTmInfoList(searchTmInfoList);
        waterLevelCompareInfoDto.setPreSearchTmInfoList(preSearchTmInfoList);
        return waterLevelCompareInfoDto;
    }
}
