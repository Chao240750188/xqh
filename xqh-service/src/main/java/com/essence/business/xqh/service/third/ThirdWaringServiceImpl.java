package com.essence.business.xqh.service.third;

import com.essence.business.xqh.api.Third.ThirdWaringService;
import com.essence.business.xqh.api.realtimemonitor.dto.RiverWayDataDto;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.dao.realtimemonitor.TRiverRODao;
import com.essence.business.xqh.dao.dao.realtimemonitor.TRvfcchBDao;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.realtimemonitor.TRvfcchB;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ThirdWaringServiceImpl implements ThirdWaringService {

    @Autowired
    StStbprpBDao stStbprpBDao;

    @Autowired
    private TRiverRODao tRiverRDao;

    @Autowired
    private TRvfcchBDao tRvfcchBDao;
    @Override
    public Object getRainWarning() {
        try {

            //Oracle默认大写改为小写返回 线程安全
            LocalDateTime currentTime = LocalDateTime.now();
            //当前时间
            Date time = Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant());

            //前3小时
            Date preHourTime3 = Date.from(currentTime.plusHours(-3).atZone(ZoneId.systemDefault()).toInstant());
            //前3小时
            Date preHourTime6 = Date.from(currentTime.plusHours(-6).atZone(ZoneId.systemDefault()).toInstant());
            //前12小时
            Date preHourTime12 = Date.from(currentTime.plusHours(-12).atZone(ZoneId.systemDefault()).toInstant());

            /**
             *          B.STCD,
             * 			B.STNM,
             * 			R.DRP,
             * 			R.TM
             */
            List<Map<String, Object>> pptns = stStbprpBDao.getRainWarnThird(preHourTime12, time);

            //todo 这个地方不能用引用方式，因为参数
            Map<String, Map<String, Object>> warnAllMap = new HashMap<>();

            for (Map pptnMap : pptns) {
                String stcd = pptnMap.get("STCD") + "";
                String stnm = pptnMap.get("STNM") + "";
                Date tm = (Date) pptnMap.get("TM");
                BigDecimal drp = (BigDecimal) pptnMap.get("DRP");
                Map<String, Object> warnNewMap = warnAllMap.get(stcd);

                if (warnNewMap == null) {
                    warnNewMap = new HashMap<>();
                    warnNewMap.put("stcd", stcd);
                    warnNewMap.put("stnm", stnm);
                    //todo 告警
                    warnNewMap.put("time3", new BigDecimal("0"));
                    warnNewMap.put("time6", new BigDecimal("0"));
                    warnNewMap.put("time12", new BigDecimal("0"));
                    warnAllMap.put(stcd, warnNewMap);
                }
                if (drp == null || drp.doubleValue() == 0d || tm == null) {
                    continue;
                }
                //前3小时数据
                if (tm.getTime() >= preHourTime3.getTime() && tm.getTime() <= time.getTime()) {
                    BigDecimal drpValue = (BigDecimal) warnNewMap.get("time3");
                    drpValue = drpValue.add(drp);
                    warnNewMap.put("time3", drpValue);
                }

                //前6小时数据
                if (tm.getTime() >= preHourTime6.getTime() && tm.getTime() <= time.getTime()) {
                    BigDecimal drpValue = (BigDecimal) warnNewMap.get("time6");
                    drpValue = drpValue.add(drp);
                    warnNewMap.put("time6", drpValue);
                }
                //前12小时数据
                if (tm.getTime() >= preHourTime12.getTime() && tm.getTime() <= time.getTime()) {
                    BigDecimal drpValue = (BigDecimal) warnNewMap.get("time12");
                    drpValue = drpValue.add(drp);
                    warnNewMap.put("time12", drpValue);
                }
            }
            List<Map<String, Object>> results = new ArrayList<>();
            for (Map.Entry<String, Map<String, Object>> entry : warnAllMap.entrySet()) {
                String stcd = entry.getKey();
                Map<String, Object> map = entry.getValue();
                String stnm = map.get("stnm") + "";
                BigDecimal time12Drp = ((BigDecimal) map.get("time12")).setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal time6Drp = ((BigDecimal) map.get("time6")).setScale(2, BigDecimal.ROUND_HALF_UP);
                BigDecimal time3Drp = ((BigDecimal) map.get("time3")).setScale(2, BigDecimal.ROUND_HALF_UP);
                int level = 0; //todo 0是安全 1是暴雨蓝色 2是暴雨黄色 3是暴雨橙色 4是暴雨红色
                String warningLevel = "";
                if (time3Drp.compareTo(new BigDecimal("100")) >= 0) { //大于等于
                    level = 4;
                    warningLevel = stnm + "3小时内降雨量为" + time3Drp + "毫米，超过100毫米";
                } else if (time3Drp.compareTo(new BigDecimal("50")) >= 0) {
                    level = 3;
                    warningLevel = stnm + "3小时内降雨量为" + time3Drp + "毫米，超过50毫米";
                } else if (time6Drp.compareTo(new BigDecimal("50")) >= 0) {
                    level = 2;
                    warningLevel = stnm + "6小时内降雨量为" + time6Drp + "毫米，超过50毫米";
                } else if (time12Drp.compareTo(new BigDecimal("50")) >= 0) {
                    level = 1;
                    warningLevel = stnm + "12小时内降雨量为" + time12Drp + "毫米，超过50毫米";
                }
                if (level != 0) {
                    Map resultMap = new HashMap();
                    resultMap.put("stcd", stcd);
                    resultMap.put("stnm", stnm);
                    resultMap.put("alarm_content", warningLevel);//告警内容
                    resultMap.put("alarm_lev", level);//级别
                    results.add(resultMap);
                }
            }
            return results;
        }catch (Exception e){
            return new ArrayList<>();
        }
    }


    @Override
    public Object getWaterWarning() {
        try {

            List<StStbprpB> useWaterLevelStbprpBStation = stStbprpBDao.findUseWaterLevelStbprpBStation();
            List<StStbprpB> collectHD = useWaterLevelStbprpBStation.stream().filter(s -> "ZQ".equals(s.getSttp()) || "ZZ".equals(s.getSttp())).collect(Collectors.toList());

            //获取河道水情信息，最新一条数据
            List<Map<String, Object>> riverRLastData = tRiverRDao.getRiverRLastData();
            Map<String, Map<String, Object>> riverWayDataMap = riverRLastData.stream().collect(Collectors.toMap(t -> t.get("stcd").toString(), Function.identity()));
            //获取河道站，堰闸站，潮汐站警戒信息
            //河道站防洪指标表
            List<TRvfcchB> tRvfcchBS = tRvfcchBDao.findAll();
            Map<String, TRvfcchB> collectWarningHD = tRvfcchBS.stream().collect(Collectors.toMap(TRvfcchB::getStcd, Function.identity()));

            List<Map<String, Object>> results = new ArrayList<>();
            for (StStbprpB stStbprpB : collectHD) {
                Double z = 0d;
                String stcd = stStbprpB.getStcd();

                Map<String, Object> resultMap = new HashMap();
                resultMap.put("stcd", stcd);
                resultMap.put("stnm", stStbprpB.getStnm());
                resultMap.put("alarm_lev", 0);
                resultMap.put("alarm_content","未超过超警戒水位");
                Map<String, Object> map = riverWayDataMap.get(stcd);
                if (map != null && map.get("z") != null) {
                    z = Double.parseDouble(map.get("z").toString());
                }
                TRvfcchB tRvfcchStandard = collectWarningHD.get(stcd);
                if (tRvfcchStandard != null) {
                    //警戒水位
                    double wrz = Double.parseDouble(tRvfcchStandard.getWrz() == null ? "0" : tRvfcchStandard.getWrz());
                    if (wrz < z) {
                        resultMap.put("alarm_lev", 1);
                        resultMap.put("alarm_content", "超过超警戒水位");
                    }

                }
                results.add(resultMap);
            }
            return results;
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
