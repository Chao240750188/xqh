package com.essence.business.xqh.service.fhybdd;

import com.essence.business.xqh.api.fhybdd.service.AnalysisToolsService;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.dao.dao.fhybdd.StZvarlBDao;
import com.essence.business.xqh.dao.dao.fhybdd.WrpRsrBsinDao;
import com.essence.business.xqh.dao.dao.floodScheduling.SkddStZvarlBDao;
import com.essence.business.xqh.dao.entity.fhybdd.StZvarlB;
import com.essence.business.xqh.dao.entity.floodScheduling.SkddStZvarlB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalysisToolsServiceImpl implements AnalysisToolsService {

    @Autowired
   StZvarlBDao stZvarlBDao;

    @Autowired
    WrpRsrBsinDao wrpRsrBsinDao;
    /**
     *获取水文站的涨差分析
     * @param map
     * @return
     */
    @Override
    public List<Map<String, Object>> getAnalysisOfPriceDifference(Map map) {
        Integer station = Integer.parseInt(map.get("station")+""); //水文站的排号 1 2 3
        Integer dataType = Integer.parseInt(map.get("dataType")+"");//数据类型 1水位涨幅 2 流量差
        BigDecimal value = new BigDecimal(map.get("value")+"");//数据值
        List<Map<String,Object>> results = new ArrayList<>();

        /**
         * 黄台桥-岔河水位	0.5775	0.0223	y=kx+b
         * 黄台桥-岔河流量	0.8102	1.2368	y=kx+b
         * 岔河-石村水位	0.4185	0.0094	y=kx+b
         * 岔河-石村流量	0.4349	1.2353	y=kx+b
         */
        BigDecimal onek  = new BigDecimal("0");
        BigDecimal oneb  = new BigDecimal("0");
        BigDecimal twok  = new BigDecimal("0");
        BigDecimal twob  = new BigDecimal("0");
        switch (dataType){
            case 1 :
                onek = new BigDecimal("0.5775");
                oneb = new BigDecimal("0.0223");
                twok = new BigDecimal("0.4185");
                twob = new BigDecimal("0.0094");
                break;
            case 2 :
                onek = new BigDecimal("0.8102");
                oneb = new BigDecimal("1.2368");
                twok = new BigDecimal("0.4349");
                twob = new BigDecimal("1.2353");
                break;
        }

      switch (station){
          case 1 :
             BigDecimal oneValue = onek.multiply(value).add(oneb);
              Map oneMap = new HashMap();
              oneMap.put("station",2);
              oneMap.put("value",oneValue);
              results.add(oneMap);
             BigDecimal twoValue = twok.multiply(oneValue).add(twob);
              Map twoMap = new HashMap();
              twoMap.put("station",3);
              twoMap.put("value",twoValue);
              results.add(twoMap);
              break;
          case 2 :
              BigDecimal valuel = twok.multiply(value).add(twob);
              Map mapp = new HashMap();
              mapp.put("station",3);
              mapp.put("value",valuel);
              results.add(mapp);
              break;
          case 3 :
              break;
      }
        return results;
    }

    /**
     * 获取分洪区列表
     * @return
     */
    @Override
    public List<Map<String, Object>> getFloodList() {

        List<Map<String,Object>> results = new ArrayList<>();

        Map<String,Object> map1 = new HashMap<>();
        map1.put("name","腊山分洪道");
        map1.put("type",1);
        Map<String,Object> map2 = new HashMap<>();
        map2.put("name","干流分洪道");
        map2.put("type",1);
        Map<String,Object> map3 = new HashMap<>();
        map3.put("name","白云湖");
        map3.put("type",2);
        Map<String,Object> map4 = new HashMap<>();
        map4.put("name","芽庄湖");
        map4.put("type",2);
        Map<String,Object> map5 = new HashMap<>();
        map5.put("name","青纱湖");
        map5.put("type",2);
        Map<String,Object> map6 = new HashMap<>();
        map6.put("name","麻大湖");
        map6.put("type",2);
        Map<String,Object> map7 = new HashMap<>();
        map7.put("name","巨淀湖");
        map7.put("type",2);
        Map<String,Object> map8 = new HashMap<>();
        map8.put("name","华山洼");
        map8.put("type",2);
        results.add(map1);
        results.add(map2);
        results.add(map3);
        results.add(map4);
        results.add(map5);
        results.add(map6);
        results.add(map7);
        results.add(map8);
        return results;
    }

    /**
     * 获取分流比计算
     * @param map
     * @return
     */
    @Override
    public Map<String, Object> getSplitRatioCalculation(Map map) {

            BigDecimal peakDischarge = new BigDecimal(map.get("peakDischarge")+""); //洪峰流量
            String name = map.get("name")+"";
            //todo 根据洪峰流量获取数据
            //Spillway 分洪道流量  FloodDischargeArea 洪泄区流量
            BigDecimal value = new BigDecimal("0");

            BigDecimal one = new BigDecimal("0");
            BigDecimal two = new BigDecimal("0");

            Map<String,Object> resultMap = new HashMap<>();
            //腊山分洪道
            switch (name){
                case "腊山分洪道":
                    if (peakDischarge.compareTo(new BigDecimal("200")) <= 0){
                        value = peakDischarge;
                    }else {
                        value = peakDischarge.subtract(new BigDecimal("200"));
                    }
                    resultMap.put("value",value);
                    return resultMap;
                case "干流分洪道":
                    one = new BigDecimal("500");
                    two = new BigDecimal("1800");
                    break;
                case "白云湖":
                    one = new BigDecimal("120");
                    two = new BigDecimal("360");
                    break;
                case "芽庄湖":
                    one = new BigDecimal("146");
                    two = new BigDecimal("474");
                    break;
                case "青纱湖":
                    one = new BigDecimal("660");
                    two = new BigDecimal("1211");
                    break;
                case "麻大湖":
                    one = new BigDecimal("115");
                    two = new BigDecimal("375");
                    break;
                case "巨淀湖":
                    one = new BigDecimal("50");
                    two = new BigDecimal("100");
                    break;
                case "华山洼":
                    one = new BigDecimal("700");
                    two = new BigDecimal("766");
                    break;
            }

            if (peakDischarge.compareTo(one) <= 0){
                value = peakDischarge;
            }else if (peakDischarge.compareTo(one) > 0  && peakDischarge.compareTo(two) <= 0 ){
                value = peakDischarge.subtract(two);
            }else {
                value = two.subtract(one);
            }
            resultMap.put("value",value);
            return resultMap;
    }


    @Override
    public Map<String, Object> getJkrftrkInformation(Map map) throws Exception{
        Map<String,Object> resultMap = new HashMap<>();
        Double startWaterLevel = Double.parseDouble(map.get("startWaterLevel")+""); //开始水位
        Double  endWaterLevel = Double.parseDouble(map.get("endWaterLevel")+""); //结束水位

        Double deliveryValue = Double.parseDouble(map.get("deliveryValue")+""); //出库流量 m3/h

        String  rsrId = map.get("rsrId")+""; //水库编码
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
        Date startTime =  format.parse(map.get("startTime")+"");
        Date endTime =  format.parse(map.get("endTime")+"");
        int time = 0;
        while (startTime.before(endTime)){
            time ++ ;
            startTime = DateUtil.getNextHour(startTime,1);
        }
        if (time == 0 ){
            return new HashMap<>();
        }
//
        Double deliveryCapacity = deliveryValue * time;//出库体积  m3

        List<StZvarlB> stZvarlBS = stZvarlBDao.getListByRsrId(rsrId);
        Map<BigDecimal, BigDecimal> stZvarlBMap = stZvarlBS.stream().collect(Collectors.toMap(StZvarlB::getRz, StZvarlB::getW));

        BigDecimal startCapacity = stZvarlBMap.get(startWaterLevel);
        BigDecimal endCapacity = stZvarlBMap.get(endWaterLevel);

        if (startCapacity == null){
            switch (rsrId){
                case "RSR_001"://y = 2.6198x2 - 867.14x + 71775
                    startCapacity = new BigDecimal("2.6198").multiply(new BigDecimal(startWaterLevel).pow(2))
                    .subtract(new BigDecimal("867.14").multiply(new BigDecimal(startWaterLevel))).add(new BigDecimal("71775"));
                    break;
                case "RSR_002"://y = 8.9233x2 - 1206.3x + 40828

                    startCapacity = new BigDecimal("8.9233").multiply(new BigDecimal(startWaterLevel).pow(2))
                    .subtract(new BigDecimal("1206.3").multiply(new BigDecimal(startWaterLevel))).add(new BigDecimal("40828"));
                    break;
                case "RSR_003"://y = 2.8675x2 - 142.23x + 1322.8

                    startCapacity = new BigDecimal("2.8675").multiply(new BigDecimal(startWaterLevel).pow(2))
                            .subtract(new BigDecimal("142.23").multiply(new BigDecimal(startWaterLevel))).add(new BigDecimal("1322.8"));

                    break;

                case "RSR_004"://y = 1.3855x2 - 764.01x + 105316


                    startCapacity = new BigDecimal("1.3855").multiply(new BigDecimal(startWaterLevel).pow(2))
                            .subtract(new BigDecimal("764.01").multiply(new BigDecimal(startWaterLevel))).add(new BigDecimal("105316"));

                    break;
                case "RSR_005"://y = 5.497x2 - 1020.7x + 47394


                    startCapacity = new BigDecimal("5.497").multiply(new BigDecimal(startWaterLevel).pow(2))
                            .subtract(new BigDecimal("1020.7").multiply(new BigDecimal(startWaterLevel))).add(new BigDecimal("47394"));

                    break;
                case "RSR_006"://y = 1.5041x2 - 896.85x + 133839

                    startCapacity = new BigDecimal("1.5041").multiply(new BigDecimal(startWaterLevel).pow(2))
                            .subtract(new BigDecimal("896.85").multiply(new BigDecimal(startWaterLevel))).add(new BigDecimal("133839"));

                    break;
                case "RSR_007"://y = 8.6731x2 - 5857.2x + 988887

                    startCapacity = new BigDecimal("8.6731").multiply(new BigDecimal(startWaterLevel).pow(2))
                            .subtract(new BigDecimal("5857.2").multiply(new BigDecimal(startWaterLevel))).add(new BigDecimal("988887"));

                    break;
                case "RSR_008"://y = 9.8014x2 - 3900x + 388112

                    startCapacity = new BigDecimal("9.8014").multiply(new BigDecimal(startWaterLevel).pow(2))
                            .subtract(new BigDecimal("3900").multiply(new BigDecimal(startWaterLevel))).add(new BigDecimal("388112"));

                    break;
                case "RSR_009"://y = 13.174x2 - 1685.4x + 53907  //得到的是万
                    startCapacity = new BigDecimal("13.174").multiply(new BigDecimal(startWaterLevel).pow(2))
                            .subtract(new BigDecimal("1685.4").multiply(new BigDecimal(startWaterLevel))).add(new BigDecimal("53907"));

                    break;
                    default:
            }
            startCapacity = startCapacity.multiply(new BigDecimal("10000"));
        }
        if (endCapacity == null){
            switch (rsrId){
                case "RSR_001"://y = 2.6198x2 - 867.14x + 71775

                    endCapacity =  new BigDecimal("2.6198").multiply(new BigDecimal(endWaterLevel).pow(2))
                            .subtract(new BigDecimal("867.14").multiply(new BigDecimal(endWaterLevel))).add(new BigDecimal("71775"));;
                    break;
                case "RSR_002"://y = 8.9233x2 - 1206.3x + 40828

                    endCapacity = new BigDecimal("8.9233").multiply(new BigDecimal(endWaterLevel).pow(2))
                            .subtract(new BigDecimal("1206.3").multiply(new BigDecimal(endWaterLevel))).add(new BigDecimal("40828"));
                    break;
                case "RSR_003"://y = 2.8675x2 - 142.23x + 1322.8

                    endCapacity = new BigDecimal("2.8675").multiply(new BigDecimal(endWaterLevel).pow(2))
                            .subtract(new BigDecimal("142.23").multiply(new BigDecimal(endWaterLevel))).add(new BigDecimal("1322.8"));
                    break;

                case "RSR_004"://y = 1.3855x2 - 764.01x + 105316

                    endCapacity = new BigDecimal("1.3855").multiply(new BigDecimal(endWaterLevel).pow(2))
                            .subtract(new BigDecimal("764.01").multiply(new BigDecimal(endWaterLevel))).add(new BigDecimal("105316"));
                    break;
                case "RSR_005"://y = 5.497x2 - 1020.7x + 47394

                    endCapacity = new BigDecimal("5.497").multiply(new BigDecimal(endWaterLevel).pow(2))
                            .subtract(new BigDecimal("1020.7").multiply(new BigDecimal(endWaterLevel))).add(new BigDecimal("47394"));

                    break;
                case "RSR_006"://y = 1.5041x2 - 896.85x + 133839

                    endCapacity = new BigDecimal("1.5041").multiply(new BigDecimal(endWaterLevel).pow(2))
                            .subtract(new BigDecimal("896.85").multiply(new BigDecimal(endWaterLevel))).add(new BigDecimal("133839"));

                    break;
                case "RSR_007"://y = 8.6731x2 - 5857.2x + 988887

                    endCapacity = new BigDecimal("8.6731").multiply(new BigDecimal(endWaterLevel).pow(2))
                            .subtract(new BigDecimal("5857.2").multiply(new BigDecimal(endWaterLevel))).add(new BigDecimal("988887"));

                    break;
                case "RSR_008"://y = 9.8014x2 - 3900x + 388112

                    endCapacity = new BigDecimal("9.8014").multiply(new BigDecimal(endWaterLevel).pow(2))
                            .subtract(new BigDecimal("3900").multiply(new BigDecimal(endWaterLevel))).add(new BigDecimal("388112"));
                    break;
                case "RSR_009"://y = 13.174x2 - 1685.4x + 53907

                    endCapacity = new BigDecimal("13.174").multiply(new BigDecimal(endWaterLevel).pow(2))
                            .subtract(new BigDecimal("1685.4").multiply(new BigDecimal(endWaterLevel))).add(new BigDecimal("53907"));
                    break;
                default:
            }

            endCapacity = endCapacity.multiply(new BigDecimal("10000"));

        }

        //todo  这个地方  如果找不到的话就是根据函数找  佳媛说的不加绝对值，出问题找他
        BigDecimal abs = endCapacity.subtract(startCapacity);
        BigDecimal ruku = abs.add(new BigDecimal(deliveryCapacity+"")); //变化的库容
        if (ruku.compareTo(new BigDecimal("0")) == -1){//小于0
            ruku = new BigDecimal("0");
        }
        resultMap.put("capacity",abs.abs());
        resultMap.put("warehousingValue",ruku.divide(new BigDecimal(time),2,BigDecimal.ROUND_HALF_UP));
        return resultMap;
    }


    @Override
    public Object getReservoirList() {

        return wrpRsrBsinDao.findAll();
    }

    @Override
    public Object beforeGetJkrftrkInformationWithSection(Map map) throws Exception{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
        Date startTime =  format.parse(map.get("startTime")+"");
        Date endTime =  format.parse(map.get("endTime")+"");
        Integer step = Integer.parseInt(map.get("step")+"");//步长

        List<String> timeResults = new ArrayList<>();
        while (startTime.before(DateUtil.getNextMillis(endTime,1))){
            String hourStart = format.format(startTime);
            timeResults.add(hourStart);
            startTime = DateUtil.getNextHour(startTime, step.intValue());
        }
        return timeResults;
    }

    @Override
    public Object getJkrftrkInformationWithSection(List<Map> list) throws Exception{

        List<Map> results = new ArrayList<>();
        for (Map<String,Object> map : list){
            results.add(getJkrftrkInformation(map));
        }
        return results;
    }
}
