package com.essence.business.xqh.service.waterandrain;

import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.api.waterandrain.dto.*;
import com.essence.business.xqh.api.waterandrain.service.WaterLevelRangeService;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.dao.realtimemonitor.*;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.realtimemonitor.TRiverR;
import com.essence.business.xqh.dao.entity.realtimemonitor.TRsvrR;
import com.essence.business.xqh.dao.entity.realtimemonitor.TTideR;
import com.essence.business.xqh.dao.entity.realtimemonitor.TWasR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fengpp
 * 2021/2/2 11:01
 */
@Service
public class WaterLevelRangeServiceImpl implements WaterLevelRangeService {

    @Autowired
    StStbprpBDao stStbprpBDao;
    @Autowired
    TWasRDao wasRDao;
    @Autowired
    TTideRDao tideRDao;
    @Autowired
    TRiverRODao riverRODao;
    @Autowired
    TRsvrRDao rsvrRDao;

    /**
     * 水位变幅-水位变幅
     *
     * @param paramDto
     * @return
     */
    @Override
    public WaterLevelRangeDto getWaterLevelRange(QueryParamDto paramDto) {
        List<WaterLevelDto> list = new ArrayList<>();
        String sttp = paramDto.getSttp();//站类
        if ("DD".equals(sttp)) {
            list = this.getSluiceWaterLevelRange(paramDto, "range");
        } else if ("ZZ".equals(sttp)) {
            list = this.getRiverWaterLevelRange(paramDto, "range");
        } else if ("TT".equals(sttp)) {
            list = this.getTideWaterLevelRange(paramDto, "range");
        } else if ("RR".equals(sttp)) {
            list = this.getReservoirWaterLevelRange(paramDto, "range");
        }

        List<WaterLevelDto> greaterThanOne = new ArrayList<>();//大于1
        List<WaterLevelDto> greaterThanZeroPointFive = new ArrayList<>();//0.5~1
        List<WaterLevelDto> greaterThanZero = new ArrayList<>();//0~0.5
        List<WaterLevelDto> noChange = new ArrayList<>();//无变化
        List<WaterLevelDto> lessThanZero = new ArrayList<>();//-0.5~0
        List<WaterLevelDto> lessThanMinusZeroPointFive = new ArrayList<>();//-1~-0.5
        List<WaterLevelDto> lessThanMinusOne = new ArrayList<>();//小于-1

        for (WaterLevelDto waterLevelDto : list) {

            BigDecimal min = waterLevelDto.getMinWaterLevel() == null ? new BigDecimal(0) : waterLevelDto.getMinWaterLevel();//首数据
            BigDecimal max = waterLevelDto.getMaxWaterLevel() == null ? new BigDecimal(0) : waterLevelDto.getMaxWaterLevel();//尾数据
            double subtract = min.subtract(max).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            if (subtract > 1) {
                greaterThanOne.add(waterLevelDto);
            } else if (subtract > 0.5 && subtract <= 1) {
                greaterThanZeroPointFive.add(waterLevelDto);
            } else if (subtract > 0 && subtract <= 0.5) {
                greaterThanZero.add(waterLevelDto);
            } else if (subtract == 0) {
                noChange.add(waterLevelDto);
            } else if (subtract > -0.5 && subtract < 0) {
                lessThanZero.add(waterLevelDto);
            } else if (subtract > -1 && subtract <= -0.5) {
                lessThanMinusZeroPointFive.add(waterLevelDto);
            } else if (subtract <= -1) {
                lessThanMinusOne.add(waterLevelDto);
            }
        }
        WaterLevelRangeDto dto = new WaterLevelRangeDto();
        dto.setGreaterThanOne(greaterThanOne);
        dto.setGreaterThanZeroPointFive(greaterThanZeroPointFive);
        dto.setGreaterThanZero(greaterThanZero);
        dto.setNoChange(noChange);
        dto.setLessThanZero(lessThanZero);
        dto.setLessThanMinusZeroPointFive(lessThanMinusZeroPointFive);
        dto.setLessThanMinusOne(lessThanMinusOne);
        return dto;
    }


    /**
     * 水位变幅-最大变幅
     *
     * @param paramDto
     * @return
     */
    @Override
    public WaterLevelMaxRangeDto getWaterLevelMaxRange(QueryParamDto paramDto) {
        List<WaterLevelDto> list = new ArrayList<>();
        String sttp = paramDto.getSttp();//站类
        if ("DD".equals(sttp)) {
            list = this.getSluiceWaterLevelRange(paramDto, "maxRange");
        } else if ("ZZ".equals(sttp)) {
            list = this.getRiverWaterLevelRange(paramDto, "maxRange");
        } else if ("TT".equals(sttp)) {
            list = this.getTideWaterLevelRange(paramDto, "maxRange");
        } else if ("RR".equals(sttp)) {
            list = this.getReservoirWaterLevelRange(paramDto, "maxRange");
        }

        List<WaterLevelDto> greaterThanOne = new ArrayList<>();//大于1
        List<WaterLevelDto> greaterThanZeroPointFive = new ArrayList<>();//0.5~1
        List<WaterLevelDto> greaterThanZero = new ArrayList<>();//0~0.5
        List<WaterLevelDto> noChange = new ArrayList<>();//无变化
        for (WaterLevelDto waterLevelDto : list) {
            BigDecimal min = waterLevelDto.getMinWaterLevel() == null ? new BigDecimal(0) : waterLevelDto.getMinWaterLevel();//最小值
            BigDecimal max = waterLevelDto.getMaxWaterLevel() == null ? new BigDecimal(0) : waterLevelDto.getMaxWaterLevel();//最大值
            double subtract = max.subtract(min).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            if (subtract > 1) {
                greaterThanOne.add(waterLevelDto);
            } else if (subtract > 0.5 && subtract <= 1) {
                greaterThanZeroPointFive.add(waterLevelDto);
            } else if (subtract > 0 && subtract <= 0.5) {
                greaterThanZero.add(waterLevelDto);
            } else if (subtract == 0) {
                noChange.add(waterLevelDto);
            }
        }
        WaterLevelMaxRangeDto dto = new WaterLevelMaxRangeDto();
        dto.setGreaterThanOne(greaterThanOne);
        dto.setGreaterThanZeroPointFive(greaterThanZeroPointFive);
        dto.setGreaterThanZero(greaterThanZero);
        dto.setNoChange(noChange);
        return dto;
    }

    //水库时间条件内最高最低水位,flag为range时，取时间段内首尾数据，为maxRange时，取时间段内最大最小数据
    private List<WaterLevelDto> getReservoirWaterLevelRange(QueryParamDto paramDto, String flag) {
        List<StStbprpB> stbprpBList = stStbprpBDao.findBySttp("RR");
        List<WaterLevelDto> list = new ArrayList<>();
        if (paramDto.getStartTime() == null) {
            list = this.getWaterLevelRange(stbprpBList, paramDto.getSttp(), paramDto.getEndTime(), flag);
        } else {
            List<String> stcdList = new ArrayList<>();
            stbprpBList.forEach(it -> {
                stcdList.add(it.getStcd());
            });
            List<TRsvrR> rsvrRList = rsvrRDao.findByStcdInAndTmBetweenOrderByTmDesc(stcdList, paramDto.getStartTime(), paramDto.getEndTime());
            Map<String, List<TRsvrR>> map = rsvrRList.stream().collect(Collectors.groupingBy(TRsvrR::getStcd));
            for (StStbprpB stStbprpB : stbprpBList) {
                String stcd = stStbprpB.getStcd();
                WaterLevelDto dto = new WaterLevelDto(stcd, stStbprpB.getStnm(), stStbprpB.getLgtd(), stStbprpB.getLttd(), "RR");
                if (map.containsKey(stcd)) {
                    List<TRsvrR> values = map.get(stcd);
                    if ("range".equals(flag)) {
                        List<TRsvrR> collect = values.stream().sorted(Comparator.comparing(TRsvrR::getTm, Comparator.nullsLast(Date::compareTo))).collect(Collectors.toList());
                        TRsvrR first = collect.get(0);
                        BigDecimal min = null;
                        if (first.getRz() != null) {
                            min = new BigDecimal(first.getRz());
                        }
                        dto.setMinWaterLevel(min);
                        BigDecimal max = null;
                        TRsvrR last = collect.get(collect.size() - 1);
                        if (last.getRz() != null) {
                            max = new BigDecimal(last.getRz());
                        }
                        dto.setMaxWaterLevel(max);
                    } else {
                        TreeSet<BigDecimal> set = new TreeSet<>();
                        Map<BigDecimal, TRsvrR> hashMap = new HashMap<>();
                        for (TRsvrR tRsvrR : values) {
                            String rz = tRsvrR.getRz();
                            if (rz != null && !"".equals(rz)) {
                                BigDecimal bigDecimal = new BigDecimal(rz);
                                set.add(bigDecimal);
                                hashMap.put(bigDecimal, tRsvrR);
                            }
                        }
                        if (set.size() > 0) {
                            TRsvrR first = hashMap.get(set.first());
                            BigDecimal min = null;
                            if (first.getRz() != null) {
                                min = new BigDecimal(first.getRz());
                            }
                            dto.setMinWaterLevel(min);
                            BigDecimal max = null;
                            TRsvrR last = hashMap.get(set.last());
                            if (last.getRz() != null) {
                                max = new BigDecimal(last.getRz());
                            }
                            dto.setMaxWaterLevel(max);
                        }
                    }
                }
                list.add(dto);
            }
        }
        return list;
    }

    //河道时间条件内最高最低水位,flag为range时，取时间段内首尾数据，为maxRange时，取时间段内最大最小数据
    private List<WaterLevelDto> getRiverWaterLevelRange(QueryParamDto paramDto, String flag) {
        List<String> sttpList = new ArrayList<>();
        sttpList.add("ZQ");
        sttpList.add("ZZ");
        List<StStbprpB> stbprpBList = stStbprpBDao.findBySttpInAndUsfl(sttpList, "1");

        List<WaterLevelDto> list = new ArrayList<>();
        if (paramDto.getStartTime() == null) {
            list = this.getWaterLevelRange(stbprpBList, paramDto.getSttp(), paramDto.getEndTime(), flag);
        } else {
            List<String> stcdList = new ArrayList<>();
            stbprpBList.forEach(it -> {
                stcdList.add(it.getStcd());
            });
            List<TRiverR> riverRList = riverRODao.findByStcdInAndTmBetweenOrderByTmDesc(stcdList, paramDto.getStartTime(), paramDto.getEndTime());
            Map<String, List<TRiverR>> map = riverRList.stream().collect(Collectors.groupingBy(TRiverR::getStcd));

            for (StStbprpB stStbprpB : stbprpBList) {
                String stcd = stStbprpB.getStcd();
                WaterLevelDto dto = new WaterLevelDto(stcd, stStbprpB.getStnm(), stStbprpB.getLgtd(), stStbprpB.getLttd(), "ZZ");
                if (map.containsKey(stcd)) {
                    List<TRiverR> values = map.get(stcd);
                    if ("range".equals(flag)) {
                        List<TRiverR> collect = values.stream().sorted(Comparator.comparing(TRiverR::getTm, Comparator.nullsLast(Date::compareTo))).collect(Collectors.toList());
                        TRiverR first = collect.get(0);
                        BigDecimal min = null;
                        if (first.getZ() != null) {
                            min = new BigDecimal(first.getZ());
                        }
                        dto.setMinWaterLevel(min);
                        BigDecimal max = null;
                        TRiverR last = collect.get(collect.size() - 1);
                        if (last.getZ() != null) {
                            max = new BigDecimal(last.getZ());
                        }
                        dto.setMaxWaterLevel(max);
                    } else {
                        TreeSet<BigDecimal> set = new TreeSet<>();
                        Map<BigDecimal, TRiverR> hashMap = new HashMap<>();
                        for (TRiverR tRiverR : values) {
                            String z = tRiverR.getZ();
                            if (z != null && !"".equals(z)) {
                                BigDecimal bigDecimal = new BigDecimal(z);
                                set.add(bigDecimal);
                                hashMap.put(bigDecimal, tRiverR);
                            }
                        }
                        if (set.size() > 0) {
                            TRiverR first = hashMap.get(set.first());
                            BigDecimal min = null;
                            if (first.getZ() != null) {
                                min = new BigDecimal(first.getZ());
                            }
                            dto.setMinWaterLevel(min);
                            BigDecimal max = null;
                            TRiverR last = hashMap.get(set.last());
                            if (last.getZ() != null) {
                                max = new BigDecimal(last.getZ());
                            }
                            dto.setMaxWaterLevel(max);
                        }
                    }
                }
                list.add(dto);
            }
        }
        return list;
    }

    //闸坝时间条件内最高最低水位,flag为range时，取时间段内首尾数据，为maxRange时，取时间段内最大最小数据
    private List<WaterLevelDto> getSluiceWaterLevelRange(QueryParamDto paramDto, String flag) {
        List<StStbprpB> stbprpBList = stStbprpBDao.findBySttp("DD");
        List<WaterLevelDto> list = new ArrayList<>();
        if (paramDto.getStartTime() == null) {
            list = this.getWaterLevelRange(stbprpBList, paramDto.getSttp(), paramDto.getEndTime(), flag);
        } else {
            List<String> stcdList = new ArrayList<>();
            stbprpBList.forEach(it -> {
                stcdList.add(it.getStcd());
            });
            List<TWasR> wasRList = wasRDao.findByStcdInAndTmBetweenOrderByTmDesc(stcdList, paramDto.getStartTime(), paramDto.getEndTime());
            Map<String, List<TWasR>> map = wasRList.stream().collect(Collectors.groupingBy(TWasR::getStcd));

            for (StStbprpB stStbprpB : stbprpBList) {
                String stcd = stStbprpB.getStcd();
                WaterLevelDto dto = new WaterLevelDto(stcd, stStbprpB.getStnm(), stStbprpB.getLgtd(), stStbprpB.getLttd(), "DD");
                if (map.containsKey(stcd)) {
                    List<TWasR> values = map.get(stcd);
                    if ("range".equals(flag)) {
                        List<TWasR> collect = values.stream().sorted(Comparator.comparing(TWasR::getTm, Comparator.nullsLast(Date::compareTo))).collect(Collectors.toList());
                        TWasR first = collect.get(0);
                        BigDecimal min = null;
                        if (first.getUpz() != null) {
                            min = new BigDecimal(first.getUpz());
                        }
                        dto.setMinWaterLevel(min);
                        BigDecimal max = null;
                        TWasR last = collect.get(collect.size() - 1);
                        if (last.getUpz() != null) {
                            max = new BigDecimal(last.getUpz());
                        }
                        dto.setMaxWaterLevel(max);
                    } else {
                        TreeSet<BigDecimal> set = new TreeSet<>();
                        Map<BigDecimal, TWasR> hashMap = new HashMap<>();
                        for (TWasR wasR : values) {
                            String upz = wasR.getUpz();
                            if (upz != null && !"".equals(upz)) {
                                BigDecimal bigDecimal = new BigDecimal(upz);
                                set.add(bigDecimal);
                                hashMap.put(bigDecimal, wasR);
                            }
                        }
                        if (set.size() > 0) {
                            TWasR first = hashMap.get(set.first());
                            BigDecimal min = null;
                            if (first.getUpz() != null) {
                                min = new BigDecimal(first.getUpz());
                            }
                            dto.setMinWaterLevel(min);
                            BigDecimal max = null;
                            TWasR last = hashMap.get(set.last());
                            if (last.getUpz() != null) {
                                max = new BigDecimal(last.getUpz());
                            }
                            dto.setMaxWaterLevel(max);
                        }
                    }
                }
                list.add(dto);
            }
        }
        return list;
    }

    //潮汐时间条件内最高最低水位,flag为range时，取时间段内首尾数据，为maxRange时，取时间段内最大最小数据
    private List<WaterLevelDto> getTideWaterLevelRange(QueryParamDto paramDto, String flag) {
        List<StStbprpB> stbprpBList = stStbprpBDao.findBySttp("TT");
        List<WaterLevelDto> list = new ArrayList<>();
        if (paramDto.getStartTime() == null) {
            list = this.getWaterLevelRange(stbprpBList, paramDto.getSttp(), paramDto.getEndTime(), flag);
        } else {
            List<String> stcdList = new ArrayList<>();
            stbprpBList.forEach(it -> {
                stcdList.add(it.getStcd());
            });
            List<TTideR> tideRList = tideRDao.findByStcdInAndTmBetweenOrderByTmDesc(stcdList, paramDto.getStartTime(), paramDto.getEndTime());
            Map<String, List<TTideR>> map = tideRList.stream().collect(Collectors.groupingBy(TTideR::getStcd));
            for (StStbprpB stStbprpB : stbprpBList) {
                String stcd = stStbprpB.getStcd();
                WaterLevelDto dto = new WaterLevelDto(stcd, stStbprpB.getStnm(), stStbprpB.getLgtd(), stStbprpB.getLttd(), "TT");
                if (map.containsKey(stcd)) {
                    List<TTideR> values = map.get(stcd);
                    if ("range".equals(flag)) {
                        List<TTideR> collect = values.stream().sorted(Comparator.comparing(TTideR::getTm, Comparator.nullsLast(Date::compareTo))).collect(Collectors.toList());
                        TTideR first = collect.get(0);
                        BigDecimal min = null;
                        if (first.getTdz() != null) {
                            min = new BigDecimal(first.getTdz());
                        }
                        dto.setMinWaterLevel(min);
                        BigDecimal max = null;
                        TTideR last = collect.get(collect.size() - 1);
                        if (last.getTdz() != null) {
                            max = new BigDecimal(last.getTdz());
                        }
                        dto.setMaxWaterLevel(max);
                    } else {
                        TreeSet<BigDecimal> set = new TreeSet<>();
                        Map<BigDecimal, TTideR> hashMap = new HashMap<>();
                        for (TTideR tideR : values) {
                            String tdz = tideR.getTdz();
                            if (tdz != null && !"".equals(tdz)) {
                                BigDecimal bigDecimal = new BigDecimal(tdz);
                                set.add(bigDecimal);
                                hashMap.put(bigDecimal, tideR);
                            }
                        }
                        if (set.size() > 0) {
                            TTideR first = hashMap.get(set.first());
                            BigDecimal min = null;
                            if (first.getTdz() != null) {
                                min = new BigDecimal(first.getTdz());
                            }
                            dto.setMinWaterLevel(min);
                            BigDecimal max = null;
                            TTideR last = hashMap.get(set.last());
                            if (last.getTdz() != null) {
                                max = new BigDecimal(last.getTdz());
                            }
                            dto.setMaxWaterLevel(max);
                        }
                    }
                }
                list.add(dto);
            }
        }
        return list;
    }


    /**
     * 最大变幅，水位变幅-时间选择最新
     *
     * @param list
     * @param sttp
     * @param endTime
     * @param flag    flag为range时，取时间段内首尾数据，为maxRange时，取时间段内最大最小数据
     */
    private List<WaterLevelDto> getWaterLevelRange(List<StStbprpB> list, String sttp, Date endTime, String flag) {
        List<String> stcdList = new ArrayList<>();
        list.forEach(it -> {
            stcdList.add(it.getStcd());
        });

        List<Map<String, Object>> dataList = new ArrayList<>();
        if ("DD".equals(sttp)) {//闸坝
            dataList = wasRDao.findSluiceLastData(stcdList, endTime);
        } else if ("ZZ".equals(sttp)) {//河道
            dataList = riverRODao.findRiverLastData(stcdList, endTime);
        } else if ("TT".equals(sttp)) {//潮汐
            dataList = tideRDao.findTideLastData(stcdList, endTime);
        } else if ("RR".equals(sttp)) {//水库
            dataList = rsvrRDao.findReservoirLastData(stcdList, endTime);
        }
        Map<String, LinkedList<Map<String, Object>>> map = this.handle(dataList);

        List<WaterLevelDto> resultList = new ArrayList<>();
        for (StStbprpB stStbprpB : list) {
            String stcd = stStbprpB.getStcd();
            WaterLevelDto dto = new WaterLevelDto(stcd, stStbprpB.getStnm(), stStbprpB.getLgtd(), stStbprpB.getLttd(), sttp);
            if (map.containsKey(stcd)) {
                BigDecimal maxWaterLevel = null;//最高水位
                BigDecimal minWaterLevel = null;//最低水位

                LinkedList<Map<String, Object>> values = map.get(stcd);
                Map<String, Object> newValue = values.get(0);
                BigDecimal newWaterLevel = null;
                if (newValue.get("WATERLEVEL") != null) {//闸坝
                    newWaterLevel = new BigDecimal(newValue.get("WATERLEVEL").toString());
                }
                BigDecimal oldWaterLevel = null;
                if (values.size() > 1) {
                    Map<String, Object> oldValue = values.get(1);
                    if (oldValue.get("WATERLEVEL") != null) {//闸坝
                        oldWaterLevel = new BigDecimal(oldValue.get("WATERLEVEL").toString());
                    }
                }

                if ("range".equals(flag)) {
                    maxWaterLevel = newWaterLevel;
                    minWaterLevel = oldWaterLevel;
                } else {
                    if (newWaterLevel != null && oldWaterLevel == null) {
                        maxWaterLevel = newWaterLevel;
                    } else if (newWaterLevel == null && oldWaterLevel != null) {
                        maxWaterLevel = oldWaterLevel;
                    } else if (newWaterLevel != null && oldWaterLevel != null) {
                        if (newWaterLevel.compareTo(oldWaterLevel) == 1) {
                            maxWaterLevel = newWaterLevel;
                            minWaterLevel = oldWaterLevel;
                        } else if (newWaterLevel.compareTo(oldWaterLevel) == -1) {
                            maxWaterLevel = oldWaterLevel;
                            minWaterLevel = newWaterLevel;
                        } else {
                            maxWaterLevel = newWaterLevel;
                            minWaterLevel = oldWaterLevel;
                        }
                    }
                }
                dto.setMaxWaterLevel(maxWaterLevel);
                dto.setMinWaterLevel(minWaterLevel);
            }
            resultList.add(dto);
        }
        return resultList;
    }


    /**
     * 最大变幅-闸坝、潮汐、河道、水库-模态框
     *
     * @param paramDto
     * @return
     */
    @Override
    public List<WaterLevelMaxChangeDto> getWaterLevelMaxChange(QueryParamDto paramDto) {
        String sttp = paramDto.getSttp();
        //查询站点
        List<StStbprpB> stbprpBList = new ArrayList<>();
        if ("ZZ".equals(sttp)) {//河道
            List<String> sttpList = new ArrayList<>();
            sttpList.add("ZZ");
            sttpList.add("ZQ");
            stbprpBList = stStbprpBDao.findBySttpInAndUsfl(sttpList, "1");
        } else {//闸坝和潮汐
            stbprpBList = stStbprpBDao.findBySttp(sttp);
        }
        //站点编码
        List<String> stcdList = new ArrayList<>();
        stbprpBList.forEach(it -> {
            stcdList.add(it.getStcd());
        });
        //最新两条数据，不同参数查不同的数据表
        List<Map<String, Object>> list = new ArrayList<>();
        if ("DD".equals(sttp)) {//闸坝
            list = wasRDao.findSluiceLastData(stcdList, paramDto.getEndTime());
        } else if ("TT".equals(sttp)) {//潮汐
            list = tideRDao.findTideLastData(stcdList, paramDto.getEndTime());
        } else if ("ZZ".equals(sttp)) {//河道
            list = riverRODao.findRiverLastData(stcdList, paramDto.getEndTime());
        }else if ("RR".equals(sttp)){
            list=rsvrRDao.findReservoirLastData(stcdList,paramDto.getEndTime());
        }
        Map<String, LinkedList<Map<String, Object>>> map = this.handle(list);

        //拼接返回值
        List<WaterLevelMaxChangeDto> resultList = new ArrayList<>();
        for (StStbprpB stStbprpB : stbprpBList) {
            WaterLevelMaxChangeDto dto = new WaterLevelMaxChangeDto();
            String stcd = stStbprpB.getStcd();
            dto.setStcd(stcd);
            dto.setStnm(stStbprpB.getStnm());
            if (map.containsKey(stcd)) {
                Date maxWaterLevelTm = null;
                BigDecimal maxWaterLevel = null;//最高水位
                Date minWaterLevelTm = null;
                BigDecimal minWaterLevel = null;//最低水位
                BigDecimal maxChange = null;//最新变幅

                BigDecimal newWaterLevel = null;//接收返回结果
                BigDecimal oldWaterLevel = null;
                Date newTm = null;
                Date oldTm = null;
                LinkedList<Map<String, Object>> values = map.get(stcd);
                Map<String, Object> newValue = values.get(0);
                if (newValue.get("TM") != null) {
                    newTm = (Date) newValue.get("TM");
                }
                if (newValue.get("WATERLEVEL") != null) {
                    newWaterLevel = new BigDecimal(newValue.get("WATERLEVEL").toString());
                }

                if (values.size() > 1) {
                    Map<String, Object> oldValue = values.get(1);
                    if (oldValue.get("TM") != null) {
                        oldTm = (Date) oldValue.get("TM");
                    }
                    if (oldValue.get("WATERLEVEL") != null) {
                        oldWaterLevel = new BigDecimal(oldValue.get("WATERLEVEL").toString());
                    }
                }
                if (newWaterLevel != null && oldWaterLevel == null) {
                    maxWaterLevel = newWaterLevel;
                    maxWaterLevelTm = newTm;
                } else if (newWaterLevel == null && oldWaterLevel != null) {
                    maxWaterLevel = oldWaterLevel;
                    maxWaterLevelTm = oldTm;
                } else if (newWaterLevel != null && oldWaterLevel != null) {
                    if (newWaterLevel.compareTo(oldWaterLevel) == 1) {
                        maxWaterLevel = newWaterLevel;
                        maxWaterLevelTm = newTm;
                        minWaterLevel = oldWaterLevel;
                        minWaterLevelTm = oldTm;
                    } else if (newWaterLevel.compareTo(oldWaterLevel) == 0) {
                        maxWaterLevel = newWaterLevel;
                        maxWaterLevelTm = newTm;
                        minWaterLevel = oldWaterLevel;
                        minWaterLevelTm = oldTm;
                    } else if (newWaterLevel.compareTo(oldWaterLevel) == -1) {
                        maxWaterLevel = oldWaterLevel;
                        maxWaterLevelTm = oldTm;
                        minWaterLevel = newWaterLevel;
                        minWaterLevelTm = newTm;
                    }
                    maxChange = maxWaterLevel.subtract(minWaterLevel);
                }
                dto.setMaxWaterLevel(maxWaterLevel);
                dto.setMaxWaterLevelTm(maxWaterLevelTm);
                dto.setMinWaterLevel(minWaterLevel);
                dto.setMinWaterLevelTm(minWaterLevelTm);
                dto.setMaxChange(maxChange);
            }
            resultList.add(dto);
        }
        return resultList;
    }

    /**
     * 水位变幅-水库-模态框
     *
     * @param paramDto
     * @return
     */
    @Override
    public List<ReservoirWaterLevelChangeDto> getReservoirWaterLevelChange(QueryParamDto paramDto) {
        List<StStbprpB> stbprpBList = stStbprpBDao.findBySttp("RR");
        List<String> stcdList = new ArrayList<>();
        stbprpBList.forEach(it -> {
            stcdList.add(it.getStcd());
        });
        //查最新两条数据
        List<Map<String, Object>> list = rsvrRDao.findReservoirLastData(stcdList, paramDto.getEndTime());
        Map<String, LinkedList<Map<String, Object>>> map = handle(list);

        List<ReservoirWaterLevelChangeDto> resultList = new ArrayList<>();
        for (StStbprpB stStbprpB : stbprpBList) {
            ReservoirWaterLevelChangeDto dto = new ReservoirWaterLevelChangeDto();
            String stcd = stStbprpB.getStcd();
            dto.setStcd(stcd);
            dto.setStnm(stStbprpB.getStnm());

            if (map.containsKey(stcd)) {
                LinkedList<Map<String, Object>> values = map.get(stcd);
                Date tm = null;
                String showTm = null;
                BigDecimal rz = null;//库水位
                BigDecimal inq = null;//入库流量
                Map<String, Object> newValue = values.get(0);
                if (newValue.get("TM") != null) {
                    tm = (Date) newValue.get("TM");
                    showTm = DateUtil.dateToStringNormal3(tm);
                }
                if (newValue.get("WATERLEVEL") != null) {
                    rz = new BigDecimal(newValue.get("WATERLEVEL").toString());
                }
                if (newValue.get("FLOW") != null) {
                    inq = new BigDecimal(newValue.get("FLOW").toString());
                }
                dto.setTm(tm);
                dto.setShowTm(showTm);
                dto.setRz(rz);
                dto.setInq(inq);
                BigDecimal newChange = null;//最新变化
                int rwptn = 6;//水势 落 4 涨 5 平 6
                if (values.size() > 1) {
                    Map<String, Object> oldValue = values.get(1);
                    BigDecimal oldRz = null;
                    if (oldValue.get("WATERLEVEL") != null) {
                        oldRz = new BigDecimal(oldValue.get("WATERLEVEL").toString());
                    }
                    if (rz != null && oldRz != null) {
                        newChange = rz.subtract(oldRz);
                        if (rz.compareTo(oldRz) == 1) {
                            rwptn = 5;
                        } else if (rz.compareTo(oldRz) == -1) {
                            rwptn = 4;
                        }
                    }
                }
                dto.setNewChange(newChange);
                dto.setRwptn(rwptn);
            }
            resultList.add(dto);
        }
        return resultList;
    }

    //将数据分组，其中list中第一个为时间最近的数据
    private Map<String, LinkedList<Map<String, Object>>> handle(List<Map<String, Object>> list) {
        Map<String, LinkedList<Map<String, Object>>> map = new HashMap<>();
        for (Map<String, Object> tempMap : list) {
            String stcd = tempMap.get("STCD").toString();
            if (map.containsKey(stcd)) {
                LinkedList<Map<String, Object>> linkedList = map.get(stcd);
                linkedList.add(tempMap);
                map.put(stcd, linkedList);
            } else {
                LinkedList<Map<String, Object>> linkedList = new LinkedList<>();
                linkedList.add(tempMap);
                map.put(stcd, linkedList);
            }
        }
        return map;
    }

    /**
     * 水位变幅-闸坝，潮汐，河道-模态框-最新
     *
     * @param paramDto
     * @return
     */
    @Override
    public List<WaterLevelChangeDto> getWaterLevelChange(QueryParamDto paramDto) {
        String sttp = paramDto.getSttp();
        //查询站点
        List<StStbprpB> stbprpBList = new ArrayList<>();
        if ("ZZ".equals(sttp)) {//河道
            List<String> sttpList = new ArrayList<>();
            sttpList.add("ZZ");
            sttpList.add("ZQ");
            stbprpBList = stStbprpBDao.findBySttpInAndUsfl(sttpList, "1");
        } else {//闸坝和潮汐
            stbprpBList = stStbprpBDao.findBySttp(sttp);
        }
        //站点编码
        List<String> stcdList = new ArrayList<>();
        stbprpBList.forEach(it -> {
            stcdList.add(it.getStcd());
        });
        //最新两条数据，不同参数查不同的数据表
        List<Map<String, Object>> list = new ArrayList<>();
        if ("DD".equals(sttp)) {//闸坝
            list = wasRDao.findSluiceLastData(stcdList, paramDto.getEndTime());
        } else if ("TT".equals(sttp)) {//潮汐
            list = tideRDao.findTideLastData(stcdList, paramDto.getEndTime());
        } else if ("ZZ".equals(sttp)) {//河道
            list = riverRODao.findRiverLastData(stcdList, paramDto.getEndTime());
        }
        Map<String, LinkedList<Map<String, Object>>> map = this.handle(list);

        List<WaterLevelChangeDto> resultList = new ArrayList<>();
        for (StStbprpB stStbprpB : stbprpBList) {
            WaterLevelChangeDto dto = new WaterLevelChangeDto();
            String stcd = stStbprpB.getStcd();
            String stnm = stStbprpB.getStnm();
            dto.setStcd(stcd);
            dto.setStnm(stnm);
            if (map.containsKey(stcd)) {
                LinkedList<Map<String, Object>> values = map.get(stcd);
                Map<String, Object> newValue = values.get(0);
                Date tm = null;
                BigDecimal waterLevel = null;
                BigDecimal flow = null;
                BigDecimal waterLevelChange = null;//水位变幅
                int rwptn = 6;//水势 落 4 涨 5 平 6
                if (newValue.get("WATERLEVEL") != null) {
                    waterLevel = new BigDecimal(newValue.get("WATERLEVEL").toString());
                }
                if (newValue.get("TM") != null) {
                    tm = (Date) newValue.get("TM");
                }
                if (newValue.get("FLOW") != null) {
                    flow = new BigDecimal(newValue.get("FLOW").toString());
                }

                dto.setTm(tm);
                dto.setWaterLevel(waterLevel);
                dto.setFlow(flow);
                if (values.size() > 1) {
                    Map<String, Object> oldValue = values.get(1);
                    BigDecimal oldWaterLevel = null;
                    if (oldValue.get("WATERLEVEL") != null) {
                        oldWaterLevel = new BigDecimal(oldValue.get("WATERLEVEL").toString());
                    }
                    if (waterLevel != null && oldWaterLevel != null) {
                        waterLevelChange = waterLevel.subtract(oldWaterLevel);
                        if (waterLevel.compareTo(oldWaterLevel) == 1) {
                            rwptn = 4;
                        } else if (waterLevel.compareTo(oldWaterLevel) == -1) {
                            rwptn = 5;
                        }
                    }
                }
                dto.setWaterLevelChange(waterLevelChange);
                dto.setRwptn(rwptn);
            }
            resultList.add(dto);
        }
        return resultList;
    }
}
