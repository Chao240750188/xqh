package com.essence.business.xqh.service.waterandrain;

import com.essence.business.xqh.api.waterandrain.STTPEnum;
import com.essence.business.xqh.api.rainfall.dto.rainmonitoring.*;
import com.essence.business.xqh.api.waterandrain.dto.*;
import com.essence.business.xqh.api.waterandrain.service.RainMonitoringService;
import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
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
        List<TWasR> wasRList = wasRDao.findByStcdInAndTmBetweenOrderByTmDesc(stcdList, paramDto.getStartTime(), paramDto.getEndTime());
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
        List<TTideR> tideRList = tideRDao.findByStcdInAndTmBetweenOrderByTmDesc(stcdList, paramDto.getStartTime(), paramDto.getEndTime());
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
}
