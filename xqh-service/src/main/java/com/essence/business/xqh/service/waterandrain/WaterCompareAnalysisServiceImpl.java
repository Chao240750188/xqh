package com.essence.business.xqh.service.waterandrain;

import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.api.waterandrain.service.WaterCompareAnalysisService;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.dao.realtimemonitor.TRiverRODao;
import com.essence.business.xqh.dao.dao.realtimemonitor.TRsvrRDao;
import com.essence.business.xqh.dao.dao.realtimemonitor.TWasRDao;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.realtimemonitor.TRiverR;
import com.essence.business.xqh.dao.entity.realtimemonitor.TRsvrR;
import com.essence.business.xqh.dao.entity.realtimemonitor.TWasR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 水情信息查询-水情对比分析
 *
 * @author fengpp
 * 2021/3/9 14:07
 */
@Service
public class WaterCompareAnalysisServiceImpl implements WaterCompareAnalysisService {
    @Autowired
    StStbprpBDao stStbprpBDao;
    @Autowired
    TRiverRODao tRiverRODao;
    @Autowired
    TRsvrRDao tRsvrRDao;
    @Autowired
    TWasRDao tWasRDao;

    /**
     * 水情信息查询-水情对比分析-左侧树
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getStnmList(QueryParamDto dto) {
        String sttp = dto.getSttp();
        List<Map<String, Object>> list = new ArrayList<>();
        if ("level".equals(sttp)) {
            list = stStbprpBDao.findUseWaterLevelStbprpb();
        } else if ("flow".equals(sttp)) {
            list = stStbprpBDao.findUseWaterFlowStbprpb();
        }
        Map<String, List<Map<String, String>>> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> tempMap = list.get(i);
            String rvnm = tempMap.get("RVNM") == null ? "其他" : tempMap.get("RVNM").toString();
            String stcd = tempMap.get("STCD").toString();
            String stnm = tempMap.get("STNM").toString();
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("stcd", stcd);
            hashMap.put("stnm", stnm);
            List<Map<String, String>> mapList = map.get(rvnm) == null ? new ArrayList<>() : map.get(rvnm);
            mapList.add(hashMap);
            map.put(rvnm, mapList);
        }

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map.Entry<String, List<Map<String, String>>> tempMap : map.entrySet()) {
            Map<String, Object> hashMap = new HashMap<>();
            hashMap.put("rvnm", tempMap.getKey());
            hashMap.put("children", tempMap.getValue());
            resultList.add(hashMap);
        }
        return resultList;
    }

    /**
     * 水情信息查询-水情对比分析-水位
     *
     * @param dto
     * @return
     */
    @Override
    public Map<String, Object> getWaterLevelTendency(QueryParamDto dto, String flag) {
        Date startTime = dto.getStartTime();
        Date endTime = dto.getEndTime();
        List<String> stcdList = dto.getStcds();
        BigDecimal min = new BigDecimal(0);
        BigDecimal max = new BigDecimal(0);
        List<Map<String, Object>> list = new ArrayList<>();

        for (String stcd : stcdList) {
            StStbprpB stbprpB = stStbprpBDao.findByStcd(stcd);
            String sttp = stbprpB.getSttp();
            String stnm = stbprpB.getStnm();
            Map<String, Object> dataMap = new HashMap<>();

            if (sttp.equals("ZQ") || sttp.equals("ZZ")) {
                dataMap = this.getRiverList(stcd, startTime, endTime, flag);
            } else if (sttp.equals("RR")) {
                dataMap = this.getRsvrList(stcd, startTime, endTime, flag);
            } else if (sttp.equals("DD")) {
                dataMap = this.getwasList(stcd, startTime, endTime, flag);
            }

            BigDecimal min1 = new BigDecimal(dataMap.get("min").toString());
            if (min.compareTo(new BigDecimal(0)) == 0 || min.compareTo(min1) == 1) {
                min = min1;
            }
            BigDecimal max1 = new BigDecimal(dataMap.get("max").toString());
            if (max.compareTo(new BigDecimal(0)) == 0 || max.compareTo(max1) == -1) {
                max = max1;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("stcd", stcd);
            map.put("stnm", stnm);
            map.put("list", dataMap.get("list"));
            list.add(map);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("max", max);
        resultMap.put("min", min);
        resultMap.put("data", list);
        return resultMap;
    }


    //河道
    private Map<String, Object> getRiverList(String stcd, Date startTime, Date endTime, String flag) {
        List<TRiverR> list = tRiverRODao.findByStcdAndTmBetweenOrderByTmDesc(stcd, startTime, endTime);
        BigDecimal min = new BigDecimal(0);
        BigDecimal max = new BigDecimal(0);
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            TRiverR tRiverR = list.get(i);
            BigDecimal value = new BigDecimal(0);
            //flow:流量  level:水位
            if ("flow".equals(flag) && tRiverR.getQ() != null && !"".equals(tRiverR.getQ())) {
                value = new BigDecimal(tRiverR.getQ());
            } else if ("level".equals(flag) && tRiverR.getZ() != null && !"".equals(tRiverR.getZ())) {
                value = new BigDecimal(tRiverR.getZ());
            }
            Map<String, Object> map = new HashMap<>();
            map.put("timeline", tRiverR.getTm().getTime());
            map.put("value", value);
            resultList.add(map);

            if (min.compareTo(new BigDecimal(0)) == 0 || min.compareTo(value) == 1) {
                min = value;
            }
            if (max.compareTo(new BigDecimal(0)) == 0 || max.compareTo(value) == -1) {
                max = value;
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("min", min);
        resultMap.put("max", max);
        resultMap.put("list", resultList);
        return resultMap;
    }

    //水库
    private Map<String, Object> getRsvrList(String stcd, Date startTime, Date endTime, String flag) {
        List<TRsvrR> list = tRsvrRDao.findByStcdAndTmBetweenOrderByTmDesc(stcd, startTime, endTime);
        BigDecimal min = new BigDecimal(0);
        BigDecimal max = new BigDecimal(0);
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            TRsvrR tRsvrR = list.get(i);
            BigDecimal value = new BigDecimal(0);
            //flow:流量  level:水位
            if ("flow".equals(flag) && tRsvrR.getInq() != null && !"".equals(tRsvrR.getInq())) {
                value = new BigDecimal(tRsvrR.getInq());
            } else if ("level".equals(flag) && tRsvrR.getRz() != null && !"".equals(tRsvrR.getRz())) {
                value = new BigDecimal(tRsvrR.getRz());
            }
            Map<String, Object> map = new HashMap<>();
            map.put("timeline", tRsvrR.getTm().getTime());
            map.put("value", value);
            resultList.add(map);

            if (min.compareTo(new BigDecimal(0)) == 0 || min.compareTo(value) == 1) {
                min = value;
            }
            if (max.compareTo(new BigDecimal(0)) == 0 || max.compareTo(value) == -1) {
                max = value;
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("min", min);
        resultMap.put("max", max);
        resultMap.put("list", resultList);
        return resultMap;
    }

    //闸坝
    private Map<String, Object> getwasList(String stcd, Date startTime, Date endTime, String flag) {
        List<TWasR> list = tWasRDao.findByStcdAndTmBetweenOrderByTmDesc(stcd, startTime, endTime);
        BigDecimal min = new BigDecimal(0);
        BigDecimal max = new BigDecimal(0);
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            TWasR tWasR = list.get(i);
            BigDecimal value = new BigDecimal(0);
            //flwo:流量  level:水位
            if ("flow".equals(flag) && tWasR.getTgtq() != null && !"".equals(tWasR.getTgtq())) {
                value = new BigDecimal(tWasR.getTgtq());
            } else if ("level".equals(flag) && tWasR.getUpz() != null && !"".equals(tWasR.getUpz())) {
                value = new BigDecimal(tWasR.getUpz());
            }
            Map<String, Object> map = new HashMap<>();
            map.put("timeline", tWasR.getTm().getTime());
            map.put("value", value);
            resultList.add(map);

            if (min.compareTo(new BigDecimal(0)) == 0 || min.compareTo(value) == 1) {
                min = value;
            }
            if (max.compareTo(new BigDecimal(0)) == 0 || max.compareTo(value) == -1) {
                max = value;
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("min", min);
        resultMap.put("max", max);
        resultMap.put("list", resultList);
        return resultMap;
    }
}
