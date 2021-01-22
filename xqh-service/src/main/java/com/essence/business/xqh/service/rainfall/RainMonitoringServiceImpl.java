package com.essence.business.xqh.service.rainfall;

import com.essence.business.xqh.api.rainfall.STTPEnum;
import com.essence.business.xqh.api.rainfall.service.RainMonitoringService;
import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author fengpp
 * 2021/1/21 18:23
 */
@Service
public class RainMonitoringServiceImpl implements RainMonitoringService {

    @Autowired
    StStbprpBDao stStbprpBDao;


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
            hashMap.put("percent", new BigDecimal(percent).setScale(2).doubleValue());
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
        List<Map<String, Object>> rainDistributionList = stStbprpBDao.getRainDistributionList(dto.getStartTime(), dto.getEndTime());
        return rainDistributionList;
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

}
