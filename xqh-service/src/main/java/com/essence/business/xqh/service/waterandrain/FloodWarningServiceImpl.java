package com.essence.business.xqh.service.waterandrain;

import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.api.realtimemonitor.dto.FloodWarningDto;
import com.essence.business.xqh.api.realtimemonitor.dto.RiverWayDataDto;
import com.essence.business.xqh.api.realtimemonitor.dto.WaterWayFloodWarningCountDto;
import com.essence.business.xqh.api.realtimemonitor.dto.WaterWayFloodWarningDetailDto;
import com.essence.business.xqh.api.waterandrain.service.FloodWarningService;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.dao.realtimemonitor.TRvfcchBDao;
import com.essence.business.xqh.dao.dao.realtimemonitor.TTideRDao;
import com.essence.business.xqh.dao.dao.realtimemonitor.TWasRDao;
import com.essence.business.xqh.dao.entity.realtimemonitor.TTideR;
import com.essence.business.xqh.dao.entity.realtimemonitor.TWasR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fengpp
 * 2021/2/2 11:14
 */
@Service
public class FloodWarningServiceImpl implements FloodWarningService {
    @Autowired
    StStbprpBDao stStbprpBDao;
    @Autowired
    TRvfcchBDao tRvfcchBDao;
    @Autowired
    TWasRDao wasRDao;
    @Autowired
    TTideRDao tideRDao;

    /**
     * 水雨情查询-洪水告警-闸坝
     *
     * @param paramDto
     * @return
     */
    @Override
    public FloodWarningDto getSluiceFloodWarning(QueryParamDto paramDto) {
        Date nextHour = DateUtil.getNextHour(paramDto.getEndTime(), -24);
        List<Map<String, Object>> list = stStbprpBDao.getFloodWarningInfo("DD");
        List<String> stcdList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            stcdList.add(map.get("STCD").toString());
        }
        List<TWasR> wasRList = wasRDao.findByStcdInAndTmBetweenOrderByTmDesc(stcdList, paramDto.getStartTime(), paramDto.getEndTime());
        Map<String, List<TWasR>> map = wasRList.stream().collect(Collectors.groupingBy(TWasR::getStcd));
        List<RiverWayDataDto> surpassHistory = new ArrayList<>();//超历史
        List<RiverWayDataDto> surpassDesign = new ArrayList<>();//超保证
        List<RiverWayDataDto> surpassFloodLine = new ArrayList<>();//超警戒
        List<RiverWayDataDto> surpassSafe = new ArrayList<>();//24小时无信息
        for (Map<String, Object> tempMap : list) {
            String stcd = tempMap.get("STCD").toString();
            String stnm = tempMap.get("STNM").toString();
            String rvnm = tempMap.get("RVNM").toString();
            Double lgtd = new Double(tempMap.get("LGTD") == null ? "0" : tempMap.get("LGTD").toString());
            Double lttd = new Double(tempMap.get("LTTD") == null ? "0" : tempMap.get("LTTD").toString());
            BigDecimal wrz = new BigDecimal(tempMap.get("WRZ") == null ? "0" : tempMap.get("WRZ").toString());
            BigDecimal grz = new BigDecimal(tempMap.get("GRZ") == null ? "0" : tempMap.get("GRZ").toString());
            BigDecimal obhtz = new BigDecimal(tempMap.get("OBHTZ") == null ? "0" : tempMap.get("OBHTZ").toString());
            RiverWayDataDto dto = new RiverWayDataDto(stcd, stnm, lgtd, lttd, rvnm, "DD");
            dto.setWarningWaterLevel(wrz.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
            if (map.containsKey(stcd)) {
                List<TWasR> tWasRList = map.get(stcd);
                String upzStr = tWasRList.stream().sorted(Comparator.comparing(TWasR::getUpz).reversed()).collect(Collectors.toList()).get(0).getUpz();
                BigDecimal upz = new BigDecimal(upzStr);
                dto.setWaterLevel(upz.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
                Date tm = tWasRList.stream().sorted(Comparator.comparing(TWasR::getTm).reversed()).collect(Collectors.toList()).get(0).getTm();
                if (upz.compareTo(obhtz) == 1) {
                    surpassHistory.add(dto);
                }
                if (upz.compareTo(grz) == 1) {
                    surpassDesign.add(dto);
                }
                if (upz.compareTo(wrz) == 1) {
                    surpassFloodLine.add(dto);
                }
                if (tm.before(nextHour)) {
                    surpassSafe.add(dto);
                }
            } else {
                surpassSafe.add(dto);
            }
        }
        FloodWarningDto dto = new FloodWarningDto();
        dto.setSurpassHistory(surpassHistory);
        dto.setSurpassDesign(surpassDesign);
        dto.setSurpassFloodLine(surpassFloodLine);
        dto.setSurpassSafe(surpassSafe);
        return dto;
    }

    /**
     * 水雨情查询-洪水告警-潮汐
     *
     * @param paramDto
     * @return
     */
    @Override
    public FloodWarningDto getTideFloodWarning(QueryParamDto paramDto) {
        Date nextHour = DateUtil.getNextHour(paramDto.getEndTime(), -24);
        List<Map<String, Object>> list = stStbprpBDao.getFloodWarningInfo("TT");
        List<String> stcdList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            stcdList.add(map.get("STCD").toString());
        }
        List<TTideR> tideRList = tideRDao.findByStcdInAndTmBetweenOrderByTmDesc(stcdList, paramDto.getStartTime(), paramDto.getEndTime());
        Map<String, List<TTideR>> map = tideRList.stream().collect(Collectors.groupingBy(TTideR::getStcd));

        List<RiverWayDataDto> surpassHistory = new ArrayList<>();//超历史
        List<RiverWayDataDto> surpassDesign = new ArrayList<>();//超保证
        List<RiverWayDataDto> surpassFloodLine = new ArrayList<>();//超警戒
        List<RiverWayDataDto> surpassSafe = new ArrayList<>();//24小时无信息
        for (Map<String, Object> tempMap : list) {
            String stcd = tempMap.get("STCD").toString();
            String stnm = tempMap.get("STNM").toString();
            String rvnm = tempMap.get("RVNM").toString();
            Double lgtd = new Double(tempMap.get("LGTD") == null ? "0" : tempMap.get("LGTD").toString());
            Double lttd = new Double(tempMap.get("LTTD") == null ? "0" : tempMap.get("LTTD").toString());
            BigDecimal wrz = new BigDecimal(tempMap.get("WRZ") == null ? "0" : tempMap.get("WRZ").toString());
            BigDecimal grz = new BigDecimal(tempMap.get("GRZ") == null ? "0" : tempMap.get("GRZ").toString());
            BigDecimal obhtz = new BigDecimal(tempMap.get("OBHTZ") == null ? "0" : tempMap.get("OBHTZ").toString());

            RiverWayDataDto dto = new RiverWayDataDto(stcd, stnm, lgtd, lttd, rvnm, "TT");
            dto.setWarningWaterLevel(wrz.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
            if (map.containsKey(stcd)) {
                List<TTideR> tTideRList = map.get(stcd);
                String tdzStr = tTideRList.stream().sorted(Comparator.comparing(TTideR::getTdz).reversed()).collect(Collectors.toList()).get(0).getTdz();
                BigDecimal tdz = new BigDecimal(tdzStr);
                dto.setWaterLevel(tdz.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
                Date tm = tTideRList.stream().sorted(Comparator.comparing(TTideR::getTm).reversed()).collect(Collectors.toList()).get(0).getTm();
                if (tdz.compareTo(obhtz) == 1) {
                    surpassHistory.add(dto);
                }
                if (tdz.compareTo(grz) == 1) {
                    surpassDesign.add(dto);
                }
                if (tdz.compareTo(wrz) == 1) {
                    surpassFloodLine.add(dto);
                }
                if (tm.before(nextHour)) {
                    surpassSafe.add(dto);
                }
            } else {
                surpassSafe.add(dto);
            }
        }
        FloodWarningDto dto = new FloodWarningDto();
        dto.setSurpassHistory(surpassHistory);
        dto.setSurpassDesign(surpassDesign);
        dto.setSurpassFloodLine(surpassFloodLine);
        dto.setSurpassSafe(surpassSafe);
        return dto;
    }

    /**
     * 水雨情查询-洪水告警-模态框-闸坝
     *
     * @param paramDto
     * @return
     */
    @Override
    public WaterWayFloodWarningCountDto getSluiceFloodWarningList(QueryParamDto paramDto) {
        List<Map<String, Object>> list = stStbprpBDao.getFloodWarningInfo("DD");
        List<String> stcdList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            stcdList.add(map.get("STCD").toString());
        }
        List<TWasR> wasRList = wasRDao.findByStcdInAndTmBetweenOrderByTmDesc(stcdList, paramDto.getStartTime(), paramDto.getEndTime());
        Map<String, List<TWasR>> map = wasRList.stream().collect(Collectors.groupingBy(TWasR::getStcd));

        List<WaterWayFloodWarningDetailDto> surpassList = new ArrayList<>();
        Integer surpassHistoryCount = 0;
        Integer surpassDesignCount = 0;
        Integer surpassFloodLineCount = 0;
        Integer surpassSafeCount = 0;
        Date hour8 = DateUtil.getNextHour(DateUtil.getThisDay(), 8);
        Date hour24 = DateUtil.getNextHour(paramDto.getEndTime(), -24);
        BigDecimal bigDecimal = new BigDecimal(0);
        for (Map<String, Object> tempMap : list) {
            String stcd = tempMap.get("STCD") == null ? "" : tempMap.get("STCD").toString();
            WaterWayFloodWarningDetailDto dto = new WaterWayFloodWarningDetailDto(stcd, "DD", tempMap);
            if (map.containsKey(stcd)) {
                List<TWasR> values = map.get(stcd);
                Map<Date, TWasR> tmMap = values.stream().collect(Collectors.toMap(TWasR::getTm, tWasR -> tWasR, (oldValue, newValue) -> oldValue));
                TWasR wasR = tmMap.get(hour8) == null ? new TWasR() : tmMap.get(hour8);
                dto.setWaterLevel8(new Double(wasR.getUpz() == null ? "0" : wasR.getUpz()));
                dto.setFlow8(wasR.getTgtq());

                TreeSet<BigDecimal> tgtqSet = new TreeSet<>();
                HashMap<BigDecimal, TWasR> tgtqMap = new HashMap<>();
                TreeSet<BigDecimal> upzSet = new TreeSet<>();
                HashMap<BigDecimal, TWasR> upzMap = new HashMap<>();
                for (TWasR tWasR : values) {
                    if (tWasR.getTgtq() != null && !"".equals(tWasR.getTgtq())) {
                        BigDecimal tgtq = new BigDecimal(tWasR.getTgtq());
                        tgtqSet.add(tgtq);
                        tgtqMap.put(tgtq, tWasR);
                    }
                    if (tWasR.getUpz() != null && !"".equals(tWasR.getUpz())) {
                        BigDecimal upz = new BigDecimal(tWasR.getUpz());
                        upzSet.add(upz);
                        upzMap.put(upz, tWasR);
                    }
                }
                if (tgtqSet.size() > 0) {
                    dto.setFlow(tgtqMap.get(tgtqSet.last()).getTgtq());
                    dto.setFlowTm(tgtqMap.get(tgtqSet.last()).getTm());
                }
                if (upzSet.size() > 0) {
                    dto.setWaterLevel(new Double(upzMap.get(upzSet.last()).getUpz()));
                    dto.setWaterLevelTm(upzMap.get(upzSet.last()).getTm());
                }

                BigDecimal waterLevelHistoryDistance = getChange(new BigDecimal(dto.getWaterLevel()), new BigDecimal(dto.getWaterLevelHistory()));
                BigDecimal waterLevelDesignDistance = getChange(new BigDecimal(dto.getWaterLevel()), new BigDecimal(dto.getWaterLevelDesign()));
                BigDecimal waterLevelLineDistance = getChange(new BigDecimal(dto.getWaterLevel()), new BigDecimal(dto.getWaterLevelLine()));
                dto.setWaterLevelHistoryDistance(waterLevelHistoryDistance.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
                dto.setWaterLevelDesignDistance(waterLevelDesignDistance.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
                dto.setWaterLevelLineDistance(waterLevelLineDistance.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());

                TreeSet<Date> set = new TreeSet<>(tmMap.keySet());
                if (set.last().before(hour24)) {
                    dto.setFlag24("0");
                    surpassSafeCount++;
                } else {
                    dto.setFlag24("1");
                }
                if (waterLevelHistoryDistance != null && waterLevelHistoryDistance.compareTo(bigDecimal) == 1) {
                    surpassHistoryCount++;
                }
                if (waterLevelDesignDistance != null && waterLevelDesignDistance.compareTo(bigDecimal) == 1) {
                    surpassDesignCount++;
                }
                if (waterLevelLineDistance != null && waterLevelLineDistance.compareTo(bigDecimal) == 1) {
                    surpassFloodLineCount++;
                }
            } else {
                dto.setFlag24("0");
            }
            surpassList.add(dto);
        }
        WaterWayFloodWarningCountDto dto = new WaterWayFloodWarningCountDto();
        dto.setSurpassList(surpassList);
        dto.setSurpassHistoryCount(surpassHistoryCount);
        dto.setSurpassDesignCount(surpassDesignCount);
        dto.setSurpassFloodLineCount(surpassFloodLineCount);
        dto.setSurpassSafeCount(surpassSafeCount);
        return dto;
    }

    /**
     * 水雨情查询-洪水告警-模态框-潮汐
     *
     * @param paramDto
     * @return
     */
    @Override
    public WaterWayFloodWarningCountDto getTideFloodWarningList(QueryParamDto paramDto) {
        List<Map<String, Object>> list = stStbprpBDao.getFloodWarningInfo("TT");
        List<String> stcdList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            stcdList.add(map.get("STCD").toString());
        }
        List<TTideR> tideRList = tideRDao.findByStcdInAndTmBetweenOrderByTmDesc(stcdList, paramDto.getStartTime(), paramDto.getEndTime());
        Map<String, List<TTideR>> map = tideRList.stream().collect(Collectors.groupingBy(TTideR::getStcd));

        List<WaterWayFloodWarningDetailDto> surpassList = new ArrayList<>();
        Integer surpassHistoryCount = 0;
        Integer surpassDesignCount = 0;
        Integer surpassFloodLineCount = 0;
        Integer surpassSafeCount = 0;
        Date hour8 = DateUtil.getNextHour(DateUtil.getThisDay(), 8);
        Date hour24 = DateUtil.getNextHour(paramDto.getEndTime(), -24);
        BigDecimal bigDecimal = new BigDecimal(0);
        for (Map<String, Object> tempMap : list) {
            String stcd = tempMap.get("STCD") == null ? "" : tempMap.get("STCD").toString();
            WaterWayFloodWarningDetailDto dto = new WaterWayFloodWarningDetailDto(stcd, "TT", tempMap);
            if (map.containsKey(stcd)) {
                List<TTideR> values = map.get(stcd);
                Map<Date, TTideR> tmMap = values.stream().collect(Collectors.toMap(TTideR::getTm, tTideR -> tTideR, (oldValue, newValue) -> oldValue));
                TTideR tideR = tmMap.get(hour8) == null ? new TTideR() : tmMap.get(hour8);
                dto.setWaterLevel8(new Double(tideR.getTdz() == null ? "0" : tideR.getTdz()));

                TreeSet<BigDecimal> treeSet = new TreeSet<>();
                Map<BigDecimal, TTideR> hashMap = new HashMap<>();

                for (TTideR tTideR : values) {
                    if (tTideR.getTdz() != null && !"".equals(tTideR.getTdz())) {
                        BigDecimal tdz = new BigDecimal(tTideR.getTdz());
                        treeSet.add(tdz);
                        hashMap.put(tdz, tTideR);
                    }
                }
                if (treeSet.size() > 0) {
                    TTideR tTideR = hashMap.get(treeSet.last());
                    dto.setWaterLevel(new Double(tTideR.getTdz()));
                    dto.setWaterLevelTm(tTideR.getTm());
                }

                BigDecimal waterLevelHistoryDistance = getChange(new BigDecimal(dto.getWaterLevel()), new BigDecimal(dto.getWaterLevelHistory()));
                BigDecimal waterLevelDesignDistance = getChange(new BigDecimal(dto.getWaterLevel()), new BigDecimal(dto.getWaterLevelDesign()));
                BigDecimal waterLevelLineDistance = getChange(new BigDecimal(dto.getWaterLevel()), new BigDecimal(dto.getWaterLevelLine()));
                dto.setWaterLevelHistoryDistance(waterLevelHistoryDistance.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
                dto.setWaterLevelDesignDistance(waterLevelDesignDistance.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());
                dto.setWaterLevelLineDistance(waterLevelLineDistance.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue());

                TreeSet<Date> set = new TreeSet<>(tmMap.keySet());
                if (set.last().before(hour24)) {
                    dto.setFlag24("0");
                    surpassSafeCount++;
                } else {
                    dto.setFlag24("1");
                }
                if (waterLevelHistoryDistance != null && waterLevelHistoryDistance.compareTo(bigDecimal) == 1) {
                    surpassHistoryCount++;
                }
                if (waterLevelDesignDistance != null && waterLevelDesignDistance.compareTo(bigDecimal) == 1) {
                    surpassDesignCount++;
                }
                if (waterLevelLineDistance != null && waterLevelLineDistance.compareTo(bigDecimal) == 1) {
                    surpassFloodLineCount++;
                }
            } else {
                dto.setFlag24("0");
            }
            surpassList.add(dto);
        }
        WaterWayFloodWarningCountDto dto = new WaterWayFloodWarningCountDto();
        dto.setSurpassList(surpassList);
        dto.setSurpassHistoryCount(surpassHistoryCount);
        dto.setSurpassDesignCount(surpassDesignCount);
        dto.setSurpassFloodLineCount(surpassFloodLineCount);
        dto.setSurpassSafeCount(surpassSafeCount);
        return dto;
    }

    private BigDecimal getChange(BigDecimal begin, BigDecimal end) {
        BigDecimal change = null;
        if (begin != null) {
            if (end != null) {
                change = begin.subtract(end);
            } else {
                change = begin;
            }
        } else if (end != null) {
            change = new BigDecimal(0).subtract(end);
        }
        return change;
    }
}
