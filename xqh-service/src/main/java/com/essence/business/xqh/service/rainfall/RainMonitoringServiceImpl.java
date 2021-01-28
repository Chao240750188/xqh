package com.essence.business.xqh.service.rainfall;

import com.essence.business.xqh.api.rainfall.STTPEnum;
import com.essence.business.xqh.api.rainfall.dto.rainmonitoring.*;
import com.essence.business.xqh.api.rainfall.service.RainMonitoringService;
import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.api.realtimemonitor.dto.*;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.dao.realtimemonitor.*;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.realtimemonitor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fengpp
 * 2021/1/21 18:23
 */
@Service
public class RainMonitoringServiceImpl implements RainMonitoringService {

    @Autowired
    StStbprpBDao stStbprpBDao;
    @Autowired
    TRvfcchBDao tRvfcchBDao;
    @Autowired
    TWasRDao wasRDao;
    @Autowired
    TTideRDao tideRDao;
    @Autowired
    TRiverRODao riverRODao;
    @Autowired
    TRsvrfsrBDao rsvrfsrBDao;
    @Autowired
    TRsvrfcchBDao rsvrfcchBDao;
    @Autowired
    TRsvrRDao rsvrRDao;

    @Override
    public Map<String, Object> rainSummary(QueryParamDto dto) {
        List<Map<String, Object>> list = stStbprpBDao.findDataGroupBySttp();

        int total = 0;
        StringBuffer buffer = new StringBuffer();
        for (Map<String, Object> map : list) {
            String sttp = map.get("STTP").toString();
            Integer count = Integer.valueOf(map.get("count").toString());
            String desc = STTPEnum.getDesc(sttp);
            buffer.append("其中").append(desc).append(count).append("个，");
            total += count;
        }
        StringBuffer stringBuffer = new StringBuffer("小清河流域报汛站点共").append(total).append("个，");
        String siteOverview = stringBuffer.append(buffer).toString();
        siteOverview = siteOverview.substring(0, siteOverview.length() - 1) + "；";//站点概况


        //todo sql可优化
        List<Map<String, Object>> rainSituation = stStbprpBDao.getRainSituation(dto.getStartTime(), dto.getEndTime());
        Map<String, Integer> rainDistributionMap = new LinkedHashMap<>();
        rainDistributionMap.put("0-10", 0);
        rainDistributionMap.put("10-25", 0);
        rainDistributionMap.put("25-50", 0);
        rainDistributionMap.put("50-100", 0);
        rainDistributionMap.put("100-250", 0);
        rainDistributionMap.put("≥250", 0);
        Map<String, Boolean> booleanMap = new HashMap<>();
        String max = null;
        if (rainSituation != null && rainSituation.size() > 0) {
            String stnm = "";
            BigDecimal drp = new BigDecimal(0);
            Map<String, Object> map = rainSituation.get(0);
            stnm = map.get("STNM").toString();
            drp = new BigDecimal(map.get("DRP") == null ? "0" : map.get("DRP").toString());
            for (Map<String, Object> tempMap : rainSituation) {
                String stcd = tempMap.get("STCD").toString();
                if (!booleanMap.containsKey(stcd)) {
                    booleanMap.put(stcd, true);
                }
                BigDecimal decimal = new BigDecimal(tempMap.get("DRP") == null ? "0" : tempMap.get("DRP").toString());
                String section = getRainSection(decimal);
                Integer integer = rainDistributionMap.get(section);
                integer++;
                rainDistributionMap.put(section, integer);
            }
            max = "最大值为" + drp + "（ " + stnm + " ）";
        }
        String rainfall = "当前发生降雨的站点共有" + booleanMap.size() + "个。";
        if (max != null && !"".equals(max)) {
            rainfall = rainfall + max;
        }

        List<Map<String, Object>> arrayList = new ArrayList<>();
        int size = rainSituation.size();
        for (Map.Entry<String, Integer> tempMap : rainDistributionMap.entrySet()) {
            String key = tempMap.getKey();
            Integer value = tempMap.getValue();
            Map<String, Object> hashMap = new HashMap<>();
            hashMap.put("section", key);//区间
            hashMap.put("count", value);
            double percent = 0;
            if (size > 0) {
                percent = 100.00 * value / size;
            }
            hashMap.put("percent", new BigDecimal(percent).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            arrayList.add(hashMap);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("siteOverview", siteOverview);//站点概况
        resultMap.put("rainfall", rainfall);//降雨情况
        resultMap.put("rainDistributionList", arrayList);//降雨分布
        return resultMap;
    }

    //获取降雨区间
    private String getRainSection(BigDecimal rain) {
        if (rain.compareTo(new BigDecimal(10)) == -1) {
            return "0-10";
        } else if (rain.compareTo(new BigDecimal(25)) == -1) {
            return "10-25";
        } else if (rain.compareTo(new BigDecimal(50)) == -1) {
            return "25-50";
        } else if (rain.compareTo(new BigDecimal(100)) == -1) {
            return "50-100";
        } else if (rain.compareTo(new BigDecimal(250)) == -1) {
            return "100-250";
        } else {
            return "≥250";
        }
    }

    @Override
    public List<Map<String, Object>> getRainDistributionList(QueryParamDto dto) {
        List<Map<String, Object>> rainDistributionList = stStbprpBDao.getRainDistributionList(dto.getStartTime(), dto.getEndTime(), dto.getName());
        List<Map<String, Object>> list = new ArrayList<>();
        //Oracle默认大写改为小写返回
        for (Map<String, Object> map : rainDistributionList) {
            Map<String, Object> hashMap = new HashMap<>();
            hashMap.put("stcd", map.get("STCD"));
            hashMap.put("stnm", map.get("STNM"));
            hashMap.put("rvnm", map.get("RVNM"));
            hashMap.put("total", map.get("TOTAL"));
            hashMap.put("lgtd", map.get("LGTD"));
            hashMap.put("lttd", map.get("LTTD"));
            list.add(hashMap);
        }
        return list;
    }

    @Override
    public Map<String, String> getInfo(String stcd) {
        StStbprpB byStcd = stStbprpBDao.findByStcd(stcd);
        Map<String, String> map = new HashMap<>();
        map.put("stnm", byStcd.getStnm());
        map.put("stcd", byStcd.getStcd());
        map.put("sttp", STTPEnum.getDesc(byStcd.getSttp()));
        map.put("rvnm", byStcd.getRvnm());
        map.put("stlc", byStcd.getStlc());
        map.put("admauth", byStcd.getAdmauth());
        map.put("lgtd", byStcd.getLgtd().toString());
        map.put("lttd", byStcd.getLttd().toString());
        map.put("esstym", byStcd.getEsstym());
        return map;
    }

    //实时监测-水情监测-闸坝
    @Override
    public List<SluiceDto> getSluiceList() {
        List<Map<String, Object>> list = stStbprpBDao.getSluiceList();
        List<SluiceDto> resultList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            SluiceDto dto = new SluiceDto();
            dto.setStcd(map.get("STCD") == null ? "" : map.get("STCD").toString());
            dto.setStnm(map.get("STNM") == null ? "" : map.get("STNM").toString());
            dto.setRvnm(map.get("RVNM") == null ? "" : map.get("RVNM").toString());
            dto.setLgtd(new BigDecimal(map.get("LGTD") == null ? "0" : map.get("LGTD").toString()));
            dto.setLttd(new BigDecimal(map.get("LTTD") == null ? "0" : map.get("LTTD").toString()));
            BigDecimal upz = new BigDecimal(map.get("UPZ") == null ? "0" : map.get("UPZ").toString());
            dto.setUpz(upz);
            dto.setDwz(new BigDecimal(map.get("DWZ") == null ? "0" : map.get("DWZ").toString()));
            dto.setTgtq(new BigDecimal(map.get("TGTQ") == null ? "0" : map.get("TGTQ").toString()));
            BigDecimal wrz = new BigDecimal(map.get("WRZ") == null ? "0" : map.get("WRZ").toString());
            dto.setWrz(wrz);
            BigDecimal grz = new BigDecimal(map.get("GRZ") == null ? "0" : map.get("GRZ").toString());//保证水位
            BigDecimal obhtz = new BigDecimal(map.get("OBHTZ") == null ? "0" : map.get("OBHTZ").toString());//最高水位
            String color = "black";
            if (upz.compareTo(wrz) == 1) {
                color = "red";
            } else if (upz.compareTo(grz) == 1) {
                color = "brown";
            } else if (upz.compareTo(obhtz) == 1) {
                color = "blue";
            }
            dto.setColor(color);
            resultList.add(dto);
        }
        return resultList;
    }

    //实时监视-水情监视-站点查询-站点信息-闸坝
    @Override
    public SluiceInfoDto getSluiceInfo(String stcd) {
        StStbprpB stbprpB = stStbprpBDao.findByStcd(stcd);
        TRvfcchB tRvfcchB = tRvfcchBDao.findByStcd(stcd);
        if (tRvfcchB == null) {
            tRvfcchB = new TRvfcchB();
        }
        SluiceInfoDto dto = new SluiceInfoDto();
        dto.setStnm(stbprpB.getStnm());
        dto.setStcd(stbprpB.getStcd());
        dto.setSttp(STTPEnum.getDesc(stbprpB.getSttp()));
        dto.setRvnm(stbprpB.getRvnm());
        dto.setAdmauth(stbprpB.getAdmauth());
        dto.setStlc(stbprpB.getStlc());
        dto.setLgtd(stbprpB.getLgtd());
        dto.setLttd(stbprpB.getLttd());
        dto.setEsstym(stbprpB.getEsstym());
        dto.setLdkel(tRvfcchB.getLdkel() == null ? "" : tRvfcchB.getLdkel());
        dto.setRdkel(tRvfcchB.getRdkel() == null ? "" : tRvfcchB.getRdkel());
        dto.setWrz(tRvfcchB.getWrz() == null ? "" : tRvfcchB.getWrz());
        dto.setGrz(tRvfcchB.getGrz() == null ? "" : tRvfcchB.getGrz());
        dto.setWrq(tRvfcchB.getWrq() == null ? "" : tRvfcchB.getWrq());
        dto.setGrq(tRvfcchB.getGrq() == null ? "" : tRvfcchB.getGrq());
        return dto;
    }

    //实时监视-水情监视-站点查询-水位流量过程线-闸坝
    @Override
    public SluiceTendencyDto getSluiceTendency(QueryParamDto paramDto) {
        List<String> stcdList = new ArrayList<>();
        stcdList.add(paramDto.getStcd());
        List<TWasR> wasRList = wasRDao.findByStcdAndTmBetweenAndOrderByTmDesc(stcdList, paramDto.getStartTime(), paramDto.getEndTime());
        if (wasRList.size() == 0) {
            return new SluiceTendencyDto();
        }
        TRvfcchB tRvfcchB = tRvfcchBDao.findByStcd(paramDto.getStcd());
        BigDecimal wrz = new BigDecimal(tRvfcchB.getWrz() == null ? "0" : tRvfcchB.getWrz());//警戒水位
        TreeSet<BigDecimal> sortSet = new TreeSet<>();
        sortSet.add(wrz);
        List<SluiceTendency> list = new ArrayList<>();
        for (TWasR wasR : wasRList) {
            SluiceTendency dto = new SluiceTendency();
            dto.setShowTm(DateUtil.dateToStringNormal(wasR.getTm()));
            dto.setTm(wasR.getTm());
            dto.setTgtq(wasR.getTgtq());
            sortSet.add(new BigDecimal(wasR.getTgtq() == null ? "0" : wasR.getTgtq()));
            String upz = wasR.getUpz();
            dto.setUpz(upz);
            sortSet.add(new BigDecimal(upz == null ? "0" : upz));
            dto.setDwz(wasR.getDwz());
            sortSet.add(new BigDecimal(wasR.getDwz() == null ? "0" : wasR.getDwz()));
            dto.setSupwptn(wasR.getSupwptn());
            dto.setWrz(wrz);
            BigDecimal warning = null;
            if (upz != null) {
                warning = new BigDecimal(upz).subtract(wrz);
            }
            dto.setWarning(warning);
            list.add(dto);
        }
        double low = sortSet.first().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        double high = sortSet.last().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        SluiceTendencyDto dto = new SluiceTendencyDto(Math.ceil(high), Math.floor(low), list);
        return dto;
    }

    //实时监测-水情监测-潮位
    @Override
    public List<TideListDto> getTideList() {

        List<Map<String, Object>> tideList = stStbprpBDao.getTideList();
        List<TideListDto> list = new ArrayList<>();
        for (Map<String, Object> map : tideList) {
            String stcd = map.get("STCD") == null ? "" : map.get("STCD").toString();
            String stnm = map.get("STNM") == null ? "" : map.get("STNM").toString();
            BigDecimal lgtd = new BigDecimal(map.get("LGTD") == null ? "" : map.get("LGTD").toString());
            BigDecimal lttd = new BigDecimal(map.get("LTTD") == null ? "" : map.get("LTTD").toString());
            BigDecimal tdz = new BigDecimal(map.get("TDZ") == null ? "" : map.get("TDZ").toString());
            BigDecimal airp = new BigDecimal(map.get("AIRP") == null ? "" : map.get("AIRP").toString());
            BigDecimal wrz = new BigDecimal(map.get("WRZ") == null ? "" : map.get("WRZ").toString());
            BigDecimal grz = new BigDecimal(map.get("GRZ") == null ? "" : map.get("GRZ").toString());
            BigDecimal obhtz = new BigDecimal(map.get("OBHTZ") == null ? "" : map.get("OBHTZ").toString());
            String color = "black";
            if (tdz.compareTo(wrz) == 1) {
                color = "red";
            } else if (tdz.compareTo(grz) == 1) {
                color = "brown";
            } else if (tdz.compareTo(obhtz) == 1) {
                color = "blue";
            }
            TideListDto dto = new TideListDto(stcd, stnm, lgtd, lttd, tdz, airp, color);
            list.add(dto);
        }
        return list;
    }

    //实时监视-水情监视-站点查询-站点信息-潮位
    @Override
    public TideInfoDto getTideInfo(String stcd) {
        StStbprpB stbprpB = stStbprpBDao.findByStcd(stcd);
        TRvfcchB tRvfcchB = tRvfcchBDao.findByStcd(stcd);
        TideInfoDto dto = new TideInfoDto();
        dto.setStnm(stbprpB.getStnm());
        dto.setStcd(stbprpB.getStcd());
        dto.setSttp(STTPEnum.getDesc(stbprpB.getSttp()));
        dto.setBsnm(stbprpB.getBsnm());
        dto.setAdmauth(stbprpB.getAdmauth());
        dto.setStlc(stbprpB.getStlc());
        dto.setLgtd(stbprpB.getLgtd());
        dto.setLttd(stbprpB.getLttd());
        dto.setEsstym(stbprpB.getEsstym());
        dto.setWrz(tRvfcchB.getWrz());
        dto.setGrz(tRvfcchB.getGrz());
        return dto;
    }

    //实时监视-水情监视-站点查询-水位流量过程线-潮位
    @Override
    public TideTendencyDto getTideTendency(QueryParamDto paramDto) {
        List<String> stcdList = new ArrayList<>();
        stcdList.add(paramDto.getStcd());
        List<TTideR> tideRList = tideRDao.findDataByStcdAndTime(stcdList, paramDto.getStartTime(), paramDto.getEndTime());
        if (tideRList.size() == 0) {
            return new TideTendencyDto();
        }

        TRvfcchB tRvfcchB = tRvfcchBDao.findByStcd(paramDto.getStcd());
        TreeSet<BigDecimal> sortSet = new TreeSet<>();
        BigDecimal wrz = new BigDecimal(tRvfcchB.getWrz() == null ? "0" : tRvfcchB.getWrz());//警戒水位
        sortSet.add(wrz);
        BigDecimal grz = new BigDecimal(tRvfcchB.getGrz() == null ? "0" : tRvfcchB.getGrz());//保证水位
        sortSet.add(grz);
        BigDecimal obhtz = new BigDecimal(tRvfcchB.getObhtz() == null ? "0" : tRvfcchB.getObhtz());//最高水位
        sortSet.add(obhtz);
        BigDecimal hlz = new BigDecimal(tRvfcchB.getHlz() == null ? "0" : tRvfcchB.getHlz());//最低水位
        sortSet.add(hlz);

        List<TideTendency> list = new ArrayList<>();
        for (TTideR tideR : tideRList) {
            TideTendency dto = new TideTendency();
            dto.setTm(tideR.getTm());
            dto.setShowTm(DateUtil.dateToStringNormal(tideR.getTm()));
            String tdz = tideR.getTdz() == null ? "0" : tideR.getTdz();
            dto.setTdz(tdz);
            sortSet.add(new BigDecimal(tdz));
            dto.setTdptn(tideR.getTdptn() == null ? "" : tideR.getTdptn());
            BigDecimal warning = null;
            if (tdz != null) {
                warning = new BigDecimal(tdz).subtract(wrz);
            }
            dto.setWarning(warning);
            dto.setWrz(wrz);
            dto.setGrz(grz);
            dto.setObhtz(obhtz);
            dto.setHlz(hlz);
            list.add(dto);
        }
        double low = sortSet.first().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        double high = sortSet.last().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        TideTendency maxTime = list.stream().sorted(Comparator.comparing(TideTendency::getObhtz).reversed()).collect(Collectors.toList()).get(0);
        TideTendency minTm = list.stream().sorted(Comparator.comparing(TideTendency::getHlz)).collect(Collectors.toList()).get(0);
        TideTendencyDto dto = new TideTendencyDto(high, low, maxTime.getObhtz(), minTm.getHlz(), maxTime.getShowTm(), minTm.getShowTm(), list);
        return dto;
    }

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
        List<TWasR> wasRList = wasRDao.findByStcdAndTmBetweenAndOrderByTmDesc(stcdList, paramDto.getStartTime(), paramDto.getEndTime());
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
        List<TTideR> tideRList = tideRDao.findDataByStcdAndTime(stcdList, paramDto.getStartTime(), paramDto.getEndTime());
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
        List<TWasR> wasRList = wasRDao.findByStcdAndTmBetweenAndOrderByTmDesc(stcdList, paramDto.getStartTime(), paramDto.getEndTime());
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
        List<TTideR> tideRList = tideRDao.findDataByStcdAndTime(stcdList, paramDto.getStartTime(), paramDto.getEndTime());
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


    /**
     * 水情服务-水情简报表
     *
     * @param dto
     * @return
     */
    @Override
    public List getList(QueryParamDto dto) {
        return null;
    }

    /**
     * 水情服务-河道水情表
     *
     * @param paramDto
     * @return
     */
    @Override
    public List<RiverListDto> getRiverList(QueryParamDto paramDto) {
        List<String> sttpList = new ArrayList<>();
        sttpList.add("ZZ");
        sttpList.add("ZQ");
        List<StStbprpB> stbprpBList = stStbprpBDao.findBySttpInAndUsfl(sttpList, "1");
        List<RiverListDto> list = new ArrayList<>();
        if (stbprpBList != null && stbprpBList.size() > 0) {
            List<String> stcdList = new ArrayList<>();
            stbprpBList.forEach(it -> {
                stcdList.add(it.getStcd());
            });
            List<TRiverR> riverRList = riverRODao.findByStcdInAndTmBetweenOrderByTmDesc(stcdList, paramDto.getStartTime(), paramDto.getEndTime());
            Map<String, List<TRiverR>> map = riverRList.stream().collect(Collectors.groupingBy(TRiverR::getStcd));
            for (StStbprpB stStbprpB : stbprpBList) {
                RiverListDto dto = new RiverListDto();
                dto.setStcd(stStbprpB.getStcd());
                dto.setStnm(stStbprpB.getStnm());
                if (map.containsKey(stStbprpB.getStcd())) {
                    List<TRiverR> values = map.get(stStbprpB.getStcd());
                    List<TRiverR> collect = values.stream().sorted(Comparator.comparing(TRiverR::getTm)).collect(Collectors.toList());
                    TRiverR last = collect.get(collect.size() - 1);
                    TRiverR first = collect.get(0);

                    BigDecimal lastWaterLevel = null;
                    BigDecimal firstWaterLevel = null;
                    if (last.getZ() != null) {
                        lastWaterLevel = new BigDecimal(last.getZ());
                    }
                    if (first.getZ() != null) {
                        firstWaterLevel = new BigDecimal(first.getZ());
                    }
                    dto.setWaterLevel(lastWaterLevel);
                    dto.setWaterLevelChange(getChange(lastWaterLevel, firstWaterLevel));

                    BigDecimal lastFlow = null;
                    BigDecimal firstFlow = null;
                    if (last.getQ() != null) {
                        lastFlow = new BigDecimal(last.getQ());
                    }
                    if (first.getQ() != null) {
                        firstFlow = new BigDecimal(first.getQ());
                    }
                    dto.setFlow(lastFlow);
                    dto.setFlowChange(getChange(lastFlow, firstFlow));
                }
                list.add(dto);
            }
        }
        return list;
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


    /**
     * 水情服务-水库水情表
     *
     * @param paramDto
     * @return
     */
    @Override
    public List<ReservoirListDto> getReservoirList(QueryParamDto paramDto) {
        List<Map<String, Object>> stbprpList = stStbprpBDao.getFloodWarningInfo("RR");
        List<String> stcdList = new ArrayList<>();
        for (Map<String, Object> map : stbprpList) {
            String stcd = map.get("STCD").toString();
            stcdList.add(stcd);
        }
        //水库水情表
        List<TRsvrR> rsvrRList = rsvrRDao.findByStcdInAndTmBetweenOrderByTmDesc(stcdList, paramDto.getStartTime(), paramDto.getEndTime());
        Map<String, List<TRsvrR>> rscrMap = rsvrRList.stream().collect(Collectors.groupingBy(TRsvrR::getStcd));

        //汛限水位表
        List<TRsvrfsrB> rsvrfsrBList = rsvrfsrBDao.findByStcdInaAndFstp(stcdList, "1");
        Map<String, String> rsvrfsrMap = rsvrfsrBList.stream().collect(Collectors.toMap(TRsvrfsrB::getStcd, TRsvrfsrB::getFsltdz));
        //防洪指标表
        List<TRsvrfcchB> rsvrfcchBList = rsvrfcchBDao.findByStcdIn(stcdList);
        Map<String, TRsvrfcchB> rsvrfcchBMap = rsvrfcchBList.stream().collect(Collectors.toMap(TRsvrfcchB::getStcd, tRsvrfcchB -> tRsvrfcchB));

        List<ReservoirListDto> resultList = new ArrayList<>();

        for (Map<String, Object> map : stbprpList) {
            ReservoirListDto dto = new ReservoirListDto();
            String stcd = map.get("STCD").toString();
            dto.setStcd(stcd);
            dto.setHnnm(map.get("HNNM").toString());
            dto.setStnm(map.get("STNM").toString());
            dto.setFsltdz(rsvrfsrMap.get(stcd));
            TRsvrfcchB tRsvrfcchB = rsvrfcchBMap.get(stcd) == null ? new TRsvrfcchB() : rsvrfcchBMap.get(stcd);
            dto.setDdz(tRsvrfcchB.getDdz());
            dto.setNormz(tRsvrfcchB.getNormz());

            List<TRsvrR> values = rscrMap.get(stcd);
            List<TRsvrR> collect = values.stream().sorted(Comparator.comparing(TRsvrR::getTm, Comparator.nullsFirst(Date::compareTo)).reversed()).collect(Collectors.toList());
            TRsvrR tRsvrR = collect.get(0);
            dto.setRz(tRsvrR.getRz());
            dto.setInq(tRsvrR.getInq());
            dto.setOtq(tRsvrR.getOtq());
            int rwptn;//水势 落:4 涨:5 平:6
            if (collect.size() > 2) {
                String rz = collect.get(1).getRz();
                BigDecimal first = null;
                if (dto.getRz() != null && !"".equals(dto.getRz())) {
                    first = new BigDecimal(dto.getRz());
                }
                BigDecimal last = null;
                if (rz != null && !"".equals(rz)) {
                    last = new BigDecimal(rz);
                }
                BigDecimal change = getChange(first, last);
                if (change != null && change.compareTo(new BigDecimal(0)) == 1) {
                    rwptn = 5;
                } else if (change != null && change.compareTo(new BigDecimal(0)) == 0) {
                    rwptn = 6;
                } else if (change != null && change.compareTo(new BigDecimal(0)) == -1) {
                    rwptn = 4;
                } else {
                    rwptn = 6;
                }
            } else {
                rwptn = 5;
            }
            dto.setRwptn(rwptn);

            resultList.add(dto);
        }
        return resultList;
    }
}
