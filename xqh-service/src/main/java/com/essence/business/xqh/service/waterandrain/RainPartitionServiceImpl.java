package com.essence.business.xqh.service.waterandrain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.essence.business.xqh.api.rainfall.vo.*;
import com.essence.business.xqh.api.realtimemonitor.dto.WaterRegimenMessageDto;
import com.essence.business.xqh.api.waterandrain.service.RainPartitionService;
import com.essence.business.xqh.dao.dao.fhybdd.*;
import com.essence.business.xqh.dao.dao.realtimemonitor.*;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpPartRelate;
import com.essence.business.xqh.dao.entity.realtimemonitor.TRsvrR;
import com.essence.business.xqh.dao.entity.realtimemonitor.TRsvrfsrB;
import com.essence.business.xqh.dao.entity.realtimemonitor.TRvfcchB;
import com.essence.framework.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 雨水情查询-雨量信息查询分区业务实现层
 */
@Service
public class RainPartitionServiceImpl implements RainPartitionService {

    @Autowired
    private StStbprpPartitionDao stStbprpPartitionDao;
    @Autowired
    private StStbprpPartRelateDao stStbprpPartRelateDao;

    @Autowired
    private StStbprpBDao stStbprpBDao;

    @Autowired
    private TRiverRODao tRiverRDao;

    @Autowired
    private TRvfcchBDao tRvfcchBDao;

    @Autowired
    private TRsvrRDao tRsvrRDao;

    @Autowired
    private TRsvrfsrBDao tRsvrfsrBDao;

    /**
     * 按类型和时间查询分区雨量
     *
     * @param reqDto
     * @return
     */
    @Override
    public  List<RainPartitionDataDto> getPartRain(RainPartitionDto reqDto,Boolean stcdRain) {
        DecimalFormat df = new DecimalFormat("0.00");
        List<RainPartitionDataDto> list = new ArrayList<>();
        //获取查询起始时间
        Date startTime = reqDto.getStartTime();
        String type = reqDto.getType();
        Date endTime = reqDto.getEndTime();
        //查询类型  1日，2月，3年，4时段，5上旬，6中旬，7下旬
        if ("1".equals(type)) {
            endTime = DateUtil.getNextDay(startTime, 1);
        }
        if ("2".equals(type)) {
            endTime = DateUtil.getNextMonth(startTime, 1);
        }
        if ("3".equals(type)) {
            endTime = DateUtil.getNextYear(startTime, 1);
        }
        if ("5".equals(type)) {
            endTime = DateUtil.getNextDay(startTime, 10);
        }
        if ("6".equals(type)) {
            endTime = DateUtil.getNextDay(startTime, 20);
            startTime = DateUtil.getNextDay(startTime, 10);
        }
        if ("7".equals(type)) {
            endTime = DateUtil.getNextMonth(startTime, 1);
            startTime = DateUtil.getNextDay(startTime, 20);
        }
        //查询分区关系表
        List<StStbprpPartRelate> stPartList = stStbprpPartRelateDao.findAll();
        Map<String, List<StStbprpPartRelate>> stPartMap = stPartList.stream().collect(Collectors.groupingBy(StStbprpPartRelate::getPart));
        //存放测站信息
        Map<String, String> stcdMap = new HashMap<>();
        //封装分区测站
        Map<String, List<String>> stcdPartMap = new HashMap<>();
        for (Map.Entry<String, List<StStbprpPartRelate>> entry : stPartMap.entrySet()) {
            String key = entry.getKey();
            List<StStbprpPartRelate> lists = entry.getValue();
            List<String> stcdList = new ArrayList<>();
            for (StStbprpPartRelate stStbprpPartRelate : lists) {
                stcdList.add(stStbprpPartRelate.getStcd());
                stcdMap.put(stStbprpPartRelate.getStcd(), stStbprpPartRelate.getStnm());
            }
            stcdPartMap.put(key, stcdList);
        }
        //查询封装数据
        for (Map.Entry<String, List<String>> entry : stcdPartMap.entrySet()) {
            RainPartitionDataDto rainPartitionDataDto = new RainPartitionDataDto();
            //分区编码
            String part_cd = entry.getKey();
            List<String> stcdList = entry.getValue();
            //查询测站降雨数据（按测站分组求和）
            List<Map<String, Object>> stcdRainDataList = stStbprpPartRelateDao.getPartRainByTime(stcdList, startTime, endTime);
            List<RainStcdDataDto> stcdRainList = null;
            if(stcdRain){
                stcdRainList = new ArrayList<>();
                rainPartitionDataDto.setStcdRainList(stcdRainList);
            }

            //分区平均雨量
            Double avgDrp = 0.0;
            //测站最大雨量
            Double maxDrp = 0.0;
            //最大雨量测站编码
            String maxStcd = "";
            int count = 1;
            for (Map<String, Object> map : stcdRainDataList) {
                String stcd = map.get("STCD") + "";
                String stnm = stcdMap.get(stcd);
                Double drp = Double.parseDouble(map.get("DRP") + "");
                if(stcdRain && count<6){
                    stcdRainList.add(new RainStcdDataDto(stnm,drp,stcd));
                    count ++;
                }
                if (drp >= maxDrp) {
                    maxStcd = stcd;
                    maxDrp = drp;
                }
                avgDrp += drp;
            }
            //封装返回参数
            List<StStbprpPartRelate> stStbprpPartRelates = stPartMap.get(part_cd);
            rainPartitionDataDto.setPartName(stStbprpPartRelates.get(0).getPartNm());
            try {
                rainPartitionDataDto.setPartDrp(Double.parseDouble(df.format(avgDrp / stcdRainDataList.size()) + ""));
            } catch (Exception e) {
                rainPartitionDataDto.setPartDrp(0.0);
            }
            rainPartitionDataDto.setMaxStcd(maxStcd);
            rainPartitionDataDto.setMaxDrp(maxDrp);
            rainPartitionDataDto.setMaxStnm(stcdMap.get(maxStcd));
            list.add(rainPartitionDataDto);
        }
        list.sort((RainPartitionDataDto c1,RainPartitionDataDto c2)-> c2.getPartDrp().compareTo(c1.getPartDrp()));
        return list;
    }

    /**
     * 查询数据生成简报报告
     * @param reqDto
     * @return
     */
    @Override
    public RainWaterReportDto getRainWaterSimpleReport(RainPartitionDto reqDto) {
        RainWaterReportDto rainWaterReportDto = new RainWaterReportDto();
        DecimalFormat df = new DecimalFormat("0.00");
        //查询月份时间-数据时间
        Date startTime = reqDto.getStartTime();
        rainWaterReportDto.setDataTime(startTime);
        //简报生成时间
        Date createTime = DateUtil.getCurrentTime();
        rainWaterReportDto.setCreateTime(createTime);
        String createTstr = DateUtil.dateToStringNormal(createTime);
        rainWaterReportDto.setCreateTimeStr(createTstr.substring(0,4)+"年"+createTstr.substring(5,7)+"月"+createTstr.substring(8,10)+"日"+createTstr.substring(11,13)+"时");
        //查询数据结束时间
        Date endTime = DateUtil.getNextMonth(startTime,1);
        //查询雨情数据
        //月份
        Integer month = Integer.parseInt(DateUtil.dateToStringNormal(startTime).substring(5,7));
        //查月分区月雨量
        List<RainPartitionDataDto> rainMonthList = getPartRain(new RainPartitionDto(startTime, null, "2"),true);
        rainWaterReportDto.setRainMonthList(rainMonthList);
        Double avgRain = 0.0;
        for (RainPartitionDataDto rainDto:rainMonthList) {
            avgRain+=rainDto.getPartDrp();
        }
        try{
            avgRain = avgRain/rainMonthList.size();
        }catch (Exception e){}
        //查询去年分区同期月雨量
        List<RainPartitionDataDto> rainLastMonthList = getPartRain(new RainPartitionDto(DateUtil.getNextYear(startTime,-1), null, "2"),false);

        Double lastAvgRain = 0.0;
        for (RainPartitionDataDto rainDto:rainLastMonthList) {
            lastAvgRain+=rainDto.getPartDrp();
        }
        try{
            lastAvgRain = lastAvgRain/rainLastMonthList.size();
        }catch (Exception e){}
        Double thisLast = avgRain - lastAvgRain;
        String thisLastStr = thisLast>0?"增加":"减少";
        String rainStr = month+"月份，小清河流域面平均降水量";
        if(lastAvgRain==0.0){
             rainStr += df.format(avgRain)+"mm,上一年同期无降水。";
        }else{
             rainStr += df.format(avgRain)+"mm,上一年同期降水量为"+df.format(lastAvgRain)+"，同比"+thisLastStr+df.format(thisLast)+"mm。";
        }
        //上，中，下旬将水量
        //查月上旬降雨量
        List<RainPartitionDataDto> rainMonth1List = getPartRain(new RainPartitionDto(startTime, null, "5"),false);
        Double avgRain1 = 0.0;
        for (RainPartitionDataDto rainDto:rainMonth1List) {
            avgRain1+=rainDto.getPartDrp();
        }
        try{
            avgRain1 = avgRain1/rainMonth1List.size();
        }catch (Exception e){}
        //查月中旬降雨量
        List<RainPartitionDataDto> rainMonth2List = getPartRain(new RainPartitionDto(startTime, null, "6"),false);

        Double avgRain2 = 0.0;
        for (RainPartitionDataDto rainDto:rainMonth2List) {
            avgRain2+=rainDto.getPartDrp();
        }
        try{
            avgRain2 = avgRain2/rainMonth2List.size();
        }catch (Exception e){}
        //查下中旬降雨量
        List<RainPartitionDataDto> rainMonth3List = getPartRain(new RainPartitionDto(startTime, null, "7"),false);
        Double avgRain3 = 0.0;
        for (RainPartitionDataDto rainDto:rainMonth3List) {
            avgRain3+=rainDto.getPartDrp();
        }
        try{
            avgRain3 = avgRain3/rainMonth3List.size();
        }catch (Exception e){}

        rainStr+="上、中、下旬降水量分别为："+df.format(avgRain1)+"mm、"+df.format(avgRain2)+"mm、"+df.format(avgRain3)+"mm。";
        rainStr+="各流域分区降水量为：";
        for (RainPartitionDataDto rainDto:rainMonthList) {
            rainStr+=rainDto.getPartName()+df.format(rainDto.getPartDrp())+"mm,";
            if(rainDto.getMaxStnm()!=null){
                rainStr+="分区最大雨量站为："+rainDto.getMaxStnm()+df.format(rainDto.getMaxDrp())+"mm,";
            }
        }
        rainStr+=month+"月小清河流域降水情况见如下雨量实况分布图。";
        rainWaterReportDto.setRainInfo(rainStr);
        //水情
        String waterInfo = "9月份，小清河流域月内";
        waterInfo = getWaterRegimenMessage(waterInfo,startTime,DateUtil.getNextMillis(endTime,-1));
        rainWaterReportDto.setWaterInfo(waterInfo);
        //水库水位数据
        List<TRsvrR> swList = tRsvrRDao.findByTmBetweenOrderByRzDesc(startTime, DateUtil.getNextMillis(endTime,-1));
        LinkedHashMap<String,List<TRsvrR>> stcdRzMap = new LinkedHashMap<>();
        for (TRsvrR tRsvrR : swList) {
            List<TRsvrR> list = stcdRzMap.get(tRsvrR.getStcd());
            if(list==null){
                list = new ArrayList<>();
                stcdRzMap.put(tRsvrR.getStcd(),list);
            }
            list.add(tRsvrR);
        }
        //封装每个水库数据
        //测站编码表
        List<StStbprpB> stStbprpBS = stStbprpBDao.findAll();
        Map<String,StStbprpB> skMap = new HashMap<>();
        for (StStbprpB stStbprpB : stStbprpBS) {
            if("RR".equals(stStbprpB.getSttp())){
                skMap.put(stStbprpB.getStcd(),stStbprpB);
            }
        }
        //库（湖）站汛限水位表 查询主汛期为1的
        List<TRsvrfsrB> tRsvrfsrBS = tRsvrfsrBDao.findByFstp("1");
        //获取水库警戒信息
        Map<String, TRsvrfsrB> collectWarningSK = tRsvrfsrBS.stream().collect(Collectors.toMap(TRsvrfsrB::getStcd, Function.identity()));
        //封装水库数据
        List<WaterRsrDto> rsrList = new ArrayList<>();
        for (Map.Entry<String,StStbprpB> entry : skMap.entrySet()) {
            WaterRsrDto waterRsrDto = new WaterRsrDto();
            String stcd = entry.getKey();
            StStbprpB stStbprpB = entry.getValue();
            waterRsrDto.setRscd(stcd);
            waterRsrDto.setRsnm(stStbprpB.getStnm());
            //水位数据
            List<TRsvrR> rsList = stcdRzMap.get(stcd);
            int size = rsList.size();
            if(size>0){
                TRsvrR tRsvrRMax = rsList.get(0);
                waterRsrDto.setMaxTime(tRsvrRMax.getTm());
                waterRsrDto.setMaxZ(Double.parseDouble(df.format(Double.parseDouble(tRsvrRMax.getRz()))));
                if(size>1){
                    TRsvrR tRsvrRMin = rsList.get(size-1);
                    waterRsrDto.setMinTime(tRsvrRMin.getTm());
                    waterRsrDto.setMinZ(Double.parseDouble(df.format(Double.parseDouble(tRsvrRMin.getRz()))));
                }
            }
            TRsvrfsrB tRsvrfsrB = collectWarningSK.get(stcd);
            if(tRsvrfsrB!=null){
                waterRsrDto.setWrz(Double.parseDouble(tRsvrfsrB.getFsltdz()));
            }
            rsrList.add(waterRsrDto);
        }
        rainWaterReportDto.setRsrMonthList(rsrList);
        return rainWaterReportDto;
    }

    public String getWaterRegimenMessage(String waterInfo,Date startTime,Date endTime) {
        //测站编码表
        List<StStbprpB> stStbprpBS = stStbprpBDao.findAll();
        //河道站防洪指标表
        List<TRvfcchB> tRvfcchBS = tRvfcchBDao.findAll();
        //库（湖）站汛限水位表 查询主汛期为1的
        List<TRsvrfsrB> tRsvrfsrBS = tRsvrfsrBDao.findByFstp("1");
        //获取河道个数
        List<StStbprpB> collectHD = stStbprpBS.stream().filter(t -> "ZQ".equals(t.getSttp()) || "ZZ".equals(t.getSttp())).collect(Collectors.toList());
        //获取河道站警戒信息
        Map<String, TRvfcchB> collectWarningHD = tRvfcchBS.stream().collect(Collectors.toMap(TRvfcchB::getStcd, Function.identity()));
        //获取水库警戒信息
        Map<String, TRsvrfsrB> collectWarningSK = tRsvrfsrBS.stream().collect(Collectors.toMap(TRsvrfsrB::getStcd, Function.identity()));
        //获取河道水位数据
        List<Map<String, Object>> riverRLastData = tRiverRDao.getWaterLevelMaxByTime(startTime,endTime);
        //超警戒水位个数
        Set<String> cjjSet = new HashSet<>();
        for (Map<String, Object> map : riverRLastData) {
            try {
                String stcd = map.get("stcd").toString();
                //水位
                double z = Double.parseDouble(map.get("z").toString());
                TRvfcchB tRvfcchStandard = collectWarningHD.get(stcd);
                if(tRvfcchStandard==null){
                    continue;
                }
                //警戒水位
                double wrz = Double.parseDouble(tRvfcchStandard.getWrz()==null?"0":tRvfcchStandard.getWrz());
                if(wrz<z){
                    cjjSet.add(stcd);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        //获取水库个数
        List<StStbprpB> collectSK = stStbprpBS.stream().filter(t -> "RR".equals(t.getSttp())).collect(Collectors.toList());
        //获取水库数据最新一条记录
        List<Map<String, Object>> rsvrLastData = tRsvrRDao.getWaterLevelMaxByTime(startTime,endTime);
        //超警戒水库
        Set<String> cjjskSet = new HashSet<>();
        for (Map<String, Object> map : rsvrLastData) {
            try {
                String stcd = map.get("stcd").toString();
                //水位
                double z = Double.parseDouble(map.get("RZ").toString());
                TRsvrfsrB tRsvrfsrB = collectWarningSK.get(stcd);
                if(tRsvrfsrB == null){
                    continue;
                }
                //汛限水位
                double fsltdz = Double.parseDouble(tRsvrfsrB.getFsltdz()==null?"0":tRsvrfsrB.getFsltdz());
                if(fsltdz<z){
                    cjjskSet.add(stcd);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        waterInfo+="河道站共记"+collectHD.size()+"个，其中超警戒水位共记"+cjjSet.size()+"个。";
        waterInfo+="水库共记"+collectSK.size()+"个，其中超汛限水位共记"+cjjskSet.size()+"个。水库水位详情见下表。";
        return waterInfo;
    }
}
