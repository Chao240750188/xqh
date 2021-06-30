package com.essence.business.xqh.service.waterandrain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.api.rainfall.vo.RainPartitionDataDto;
import com.essence.business.xqh.api.rainfall.vo.RainPartitionDto;

import com.essence.business.xqh.api.realtimemonitor.dto.*;
import com.essence.business.xqh.api.waterandrain.service.AnalysisOfFloodService;
import com.essence.business.xqh.api.waterandrain.service.RainPartitionService;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpPartRelateDao;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpPartitionDao;
import com.essence.business.xqh.dao.dao.fhybdd.YwkRainWaterReportDao;

import com.essence.business.xqh.dao.dao.rainfall.YwkReportDataFhfxDao;
import com.essence.business.xqh.dao.dao.rainfall.YwkReportDataFhfxInfoDao;
import com.essence.business.xqh.dao.dao.rainfall.YwkReportDataFhfxSkDao;
import com.essence.business.xqh.dao.dao.realtimemonitor.*;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpPartRelate;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpPartition;
import com.essence.business.xqh.dao.entity.fhybdd.YwkRainWaterReport;
import com.essence.business.xqh.dao.entity.rainfall.YwkReportDataFhfx;
import com.essence.business.xqh.dao.entity.rainfall.YwkReportDataFhfxInfo;
import com.essence.business.xqh.dao.entity.rainfall.YwkReportDataFhfxSk;
import com.essence.business.xqh.dao.entity.realtimemonitor.*;
import com.essence.framework.jpa.Criterion;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import com.essence.framework.util.DateUtil;
import com.essence.framework.util.StrUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class AnalysisOfFloodServiceImpl implements AnalysisOfFloodService {

    //公报小清河流域面积㎡
    private static final double XQH_AREA = 9533094095.0;
    //公报签发
    private static final String XQH_SIGN = "小清河流域中心";
    //公报审定
    private static final String XQH_VERIFICATION = "张三";
    //公报核稿
    private static final String XQH_ENGAGEMENT = "李四";
    //公报拟稿
    private static final String XQH_DARFT = "王五";
    @Autowired
    YwkRainWaterReportDao ywkRainWaterReportDao;

    //ST_STBPRP_PARTITION
    @Autowired
    StStbprpPartitionDao stStbprpPartitionDao;

    @Autowired
    RainPartitionService rainPartitionService;

    private static final Map<String, String> SK_DD = new HashMap<>();
    static {
        SK_DD.put("RSR_001","起调水位为兴利水位187.00m，按50年一遇防洪高水位190.73m控泄265m3/s；当库水位高于50年一遇防洪高水位时不再控泄，按自由出流敞泄。");
        SK_DD.put("RSR_002","起调水位为兴利水位78.00m，水库洪水调节计算按20年一遇防洪高水位78.63m控泄509m3/s；当库水位高于20年一遇防洪高水位时不再控泄，按自由出流敞泄。");
        SK_DD.put("RSR_003","起调水位为汛限水位46.60m，按20年一遇防洪高水位47.10m控泄570m3/s；当库水位高于20年一遇防洪高水位时不再控泄，按自由出流敞泄。");
        SK_DD.put("RSR_004","起调水位为兴利水位304.00m，按20年一遇防洪高水位306.66m控泄218m3/s；当库水位高于20年一遇防洪高水位时不再控泄，按自由出流敞泄。");
        SK_DD.put("RSR_005","起调水位为兴利水位103.3m，在警戒水位106.3m以下控制250m3/s，超警戒水位敞泄；充分利用芽庄湖调蓄，浒山闸过流能力148m3/s；继续发挥浒山泺滞洪区的滞蓄作用，遇超过20年一遇洪水时，临时滞蓄，错峰下泄，分洪流量60m3/s。");
        SK_DD.put("RSR_006","起调水位为兴利水位336.10m，水库无闸控制，自由出流敞泄。");
        SK_DD.put("RSR_007","汛中限制水位（汛限水位）：348.00m，相应库容904.6万m3；\n" +
                "警戒水位：349.00m, 相应库容1086万m3，相应泄量309m3/s；允许最高水位：351.38m，相应库容1633万m3，相应泄量809m3/s。");
        SK_DD.put("RSR_008","起调水位为汛期水位233.00m，水库为二级控泄，一级按20年一遇防洪高水位234.63m控泄700m3/s，二级按100年一遇防洪高水位235.77m控泄2910m3/s；当库水位高于100年一遇防洪高水位时不再控泄，按自由出流敞泄。");
        SK_DD.put("RSR_009","起调水位为兴利水位83.00m，水库为二级控泄，一级按20年一遇防洪高水位83.34m控泄300m3/s，二级按50年一遇防洪高水位83.75m控泄526m3/s；当库水位高于50年一遇防洪高水位时不再控泄，按自由出流敞泄。");
    }

    @Autowired
    StStbprpPartRelateDao stStbprpPartRelateDao;

    @Autowired
    private TWasRDao tWasRDao;

    @Autowired
    private TTideRDao tTideRDao;

    @Override
    public Object getRainWaterCommonReport(RainPartitionDto reqDto) {

        Map resultMap = new HashMap();
        //查询月份时间-数据时间
        resultMap.put("dataTime",DateUtil.dateToStringNormal(reqDto.getStartTime()));
        resultMap.put("endTime",DateUtil.dateToStringNormal(reqDto.getEndTime()));
        resultMap.put("createTime",DateUtil.dateToStringNormal(DateUtil.getCurrentTime()));
        String createTime = DateUtil.dateToStringNormal(DateUtil.getCurrentTime());
        resultMap.put("createTimeStr",Integer.parseInt(createTime.substring(0, 4)) + "年" + Integer.parseInt(createTime.substring(5, 7)) + "月" + Integer.parseInt(createTime.substring(8, 10)) + "日" + Integer.parseInt(createTime.substring(11, 13)) + "时");

        //简报名称
        String startTimeStr = DateUtil.dateToStringNormal(reqDto.getStartTime());
        //数据年份
        int reportYear = Integer.parseInt(startTimeStr.substring(0, 4));
        resultMap.put("reportName",reqDto.getAreaName() + reportYear + "年" + startTimeStr.substring(5, 7) + "月防洪形势分析报告");
        //查时段分区雨量  3个分区的 todo 从这开始
        String rainInfo = getPartNewRain(reqDto);
        //获取水情。
        Map<String,Object> waterInfo = getWaterInfomation(reqDto);
        waterInfo.put("rainInfo",rainInfo);
        //resultMap.put("dataMap",waterInfo);
        resultMap.put("sign",XQH_SIGN);
        resultMap.put("engagement",XQH_ENGAGEMENT);
        resultMap.put("darft",XQH_DARFT);
        resultMap.put("verification",XQH_VERIFICATION);
        resultMap.put("cPartId",reqDto.getAreaId());
        resultMap.put("cPartName",reqDto.getAreaName());
        resultMap.putAll(waterInfo);
        return resultMap;
    }




    /**
     * 获取水情
     * @param reqDto
     * @return
     */
    public Map<String, Object> getWaterInfomation(RainPartitionDto reqDto){
        //获取河道站、水库站的水位跟流量值
        Map<String,Object> resultMap = new HashMap<>();
        Date startTime = reqDto.getStartTime();
        Date endTime = reqDto.getEndTime();
        String startTimeStr = DateUtil.dateToStringNormal(startTime);
        String endTimeStr = DateUtil.dateToStringNormal(endTime);

        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");
        String startStr = format.format(startTime);
        String endStr = format.format(endTime);
        //起始时间月日时
        Integer startYear = Integer.parseInt(startTimeStr.substring(0, 4));
        Integer endYear = Integer.parseInt(endTimeStr.substring(0, 4));

        Integer startMonth = Integer.parseInt(startTimeStr.substring(5, 7));
        Integer endMonth = Integer.parseInt(endTimeStr.substring(5, 7));

        Integer startDay = Integer.parseInt(startTimeStr.substring(8, 10));
        Integer endtDay = Integer.parseInt(endTimeStr.substring(8, 10));
        //起始时间月日时
        Integer startHour = Integer.parseInt(startTimeStr.substring(11, 13));
        Integer endHour = Integer.parseInt(endTimeStr.substring(11, 13));
        //水情
        String waterInfoFirst = startMonth + "月" + startDay + "日" + startHour + "时至" + endMonth + "月" + endtDay + "日" + endHour + "时，" +
                reqDto.getAreaName()+ "关键断面水位基本维持稳定/波动较大。";
        resultMap.put("waterFirstInfo",waterInfoFirst);

        List<String> areaIds = new ArrayList<>();
        if ("0".equals(reqDto.getAreaId())){
            areaIds.addAll(stStbprpPartitionDao.findAll().stream().map(StStbprpPartition::getId).collect(Collectors.toList()));
        }else {
            areaIds.add(reqDto.getAreaId());
        }
        List<String> sttps = new ArrayList<>();
        sttps.add("ZQ");
        sttps.add("ZZ");
        sttps.add("RR");//水库
        //筛选后的河道站监测站
        List<StStbprpB> allSTBB = stStbprpBDao.findByAreaIdAndSttp(areaIds, sttps);//todo 后面再改 findByAreaIdAndSttp(areaIds, sttps);
        //获取河道(非水库)
        List<StStbprpB> collectHD = allSTBB.stream().filter(t -> "ZQ".equals(t.getSttp()) || "ZZ".equals(t.getSttp())).collect(Collectors.toList());
        List<String> heDaoStcds = collectHD.stream().map(StStbprpB::getStcd).collect(Collectors.toList()); //stcds

        //河道站防洪指标表  指标
        List<TRvfcchB> tRvfcchBS = tRvfcchBDao.findAll();
        //获取河道站警戒信息
        Map<String, TRvfcchB> collectWarningHD = tRvfcchBS.stream().collect(Collectors.toMap(TRvfcchB::getStcd, Function.identity()));

        //组装表达信息
        List<String> hedaoAndSkStrings = new ArrayList<>();
        //组装表格信息
        List<Map> heDaoAndShuiKuList = new ArrayList<>(); //河道表格list

        List<Map<String, Object>> riverRLastData = new ArrayList<>();
        if (!CollectionUtils.isEmpty(heDaoStcds)) {
            //TODO 获取河道的数据
            List<TRiverR> minDateByDateAndStcds = tRiverRDao.findMinDateByDateAndStcds(heDaoStcds, startTime, endTime);
            Map<String, TRiverR> minHdMap = minDateByDateAndStcds.stream().collect(Collectors.toMap(TRiverR::getStcd, Function.identity(),(v1, v2) -> v1));

            List<TRiverR> maxDateByDateAndStcds = tRiverRDao.findMaxDateByDateAndStcds(heDaoStcds, startTime, endTime);
            Map<String, TRiverR> maxHdMap = maxDateByDateAndStcds.stream().collect(Collectors.toMap(TRiverR::getStcd, Function.identity(),(v1, v2) -> v1));

            //todo 获取河道时段最低水位等表格数据
            List<Map<String, Object>> waterLevelMinByTime = tRiverRDao.getWaterLevelMinByTime(heDaoStcds, startTime, endTime);
            Map<String, Map<String, Object>> minZMap = listToMap(waterLevelMinByTime); //stcd z

            List<TRiverR> waterMaxByTime = tRiverRDao.getWaterMaxByTime(heDaoStcds, startTime, endTime);
            Map<String, TRiverR> maxZMap = waterMaxByTime.stream().collect(Collectors.toMap(TRiverR::getStcd, Function.identity(),(v1, v2) -> v1));
            //当前平均值
            List<Map<String, Object>> avgValueByTime = tRiverRDao.getAvgValueByTime(heDaoStcds, startTime, endTime);
            Map<String, Map<String, Object>> avgValueMap = listToMap(avgValueByTime);
            //历史平均值

            List<Map<String, Object>> historyAvgValueByTime = tRiverRDao.getHistoryAvgValueByTime(heDaoStcds, startStr, endStr);
            Map<String, Map<String, Object>> historyAvgValueMap = listToMap(historyAvgValueByTime);
            //最大流量以及出现的时间
            List<TRiverR> maxQValueByTime = tRiverRDao.getMaxQValueByTime(heDaoStcds, startTime, endTime);
            Map<String, TRiverR> maxQMap = maxQValueByTime.stream().collect(Collectors.toMap(TRiverR::getStcd, Function.identity(),(v1, v2) -> v1));

            //获取河道水位数据 最大值
            riverRLastData = tRiverRDao.getWaterLevelMaxByTimeAndStcds(heDaoStcds,startTime,endTime); //between and

            for (StStbprpB stStbprpB : collectHD) {

                String stcd = stStbprpB.getStcd();
                String stnm = stStbprpB.getStnm();
                TRiverR minMap = minHdMap.get(stcd);
                TRiverR maxMap = maxHdMap.get(stcd);

                Map<String, Object> minZMapData = minZMap.get(stcd);//最小水位
                TRiverR maxZData = maxZMap.get(stcd); //最大水位以及时间
                Map<String, Object> avgZMapData = avgValueMap.get(stcd);//当前平均水位
                Map<String, Object> historyAvgValueData = historyAvgValueMap.get(stcd);//历史平均水位
                TRiverR maxQMapData = maxQMap.get(stcd); //最大流量 以及时间
                TRvfcchB tRvfcchStandard = collectWarningHD.get(stcd); //警戒水位

                if (minZMapData != null || maxZData != null || avgZMapData != null || historyAvgValueData != null || maxQMapData != null) {

                    Map<String, Object> dataMap = new HashMap();
                    dataMap.put("stnm", stnm);
                    dataMap.put("stcd", stcd);
                    String historyAvgValue = "-";  //历史平均值
                    String avgValue = "-"; //时段平均值
                    String maxzTime = "-";  //最大值出现时间
                    String maxzValue = "-"; //最大水位值
                    String minzValue = "-";
                    String jjswValue = "-";
                    String maxQvalue = "-";  //最大流量值
                    String maxQTime = "-";  //最大流量值时间
                    if (minZMapData != null && minZMapData.get("z") != null) {
                        minzValue = new BigDecimal(minZMapData.get("z") + "").setScale(2,BigDecimal.ROUND_HALF_UP).toString();
                    }
                    if (maxZData != null && maxZData.getZ() != null) {
                        maxzValue = new BigDecimal(maxZData.getZ() + "").setScale(2,BigDecimal.ROUND_HALF_UP).toString();
                        maxzTime = DateUtil.dateToStringNormal(maxZData.getTm());
                    }
                    if (avgZMapData != null && avgZMapData.get("value") != null) {
                        avgValue = new BigDecimal(avgZMapData.get("value") + "").setScale(2,BigDecimal.ROUND_HALF_UP).toString();
                    }
                    if (historyAvgValueData != null && historyAvgValueData.get("value") != null) {
                        historyAvgValue = new BigDecimal(historyAvgValueData.get("value") + "").setScale(2,BigDecimal.ROUND_HALF_UP).toString();
                    }
                    if (maxQMapData != null && maxQMapData.getQ() != null) {
                        maxQvalue = new BigDecimal(maxQMapData.getQ() + "").setScale(2,BigDecimal.ROUND_HALF_UP).toString();
                        maxQTime = DateUtil.dateToStringNormal(maxQMapData.getTm());
                    }
                    if (tRvfcchStandard != null && tRvfcchStandard.getWrz() != null) {
                        jjswValue = new BigDecimal(tRvfcchStandard.getWrz() + "").setScale(2,BigDecimal.ROUND_HALF_UP).toString();
                    }
                    dataMap.put("minzValue", minzValue); //最小水位
                    dataMap.put("maxzValue", maxzValue); //最大水位
                    dataMap.put("maxzTime", maxzTime); //最大水位时间
                    dataMap.put("jjswValue", jjswValue); //警戒水位
                    dataMap.put("avgValue", avgValue); //平均水位
                    dataMap.put("historyAvgValue", historyAvgValue); //历史平均水位
                    dataMap.put("maxQvalue", maxQvalue); //最大流量
                    dataMap.put("maxQTime", maxQTime); //最大流量时间
                    heDaoAndShuiKuList.add(dataMap); //组装表格数据
                }

                if (minMap != null || maxMap != null) {
                    /**
                     * 黄河洛口水文站3月1日8时水位26.78m，流量970m3/s；3月6日2时水位26.84m，流量1080m3/s。
                     */


                    String minz = minMap.getZ();
                    String minq = minMap.getQ();
                    String maxz = maxMap.getZ();
                    String maxq = maxMap.getQ();
                    if (minz == null && minq == null && maxz == null && maxq == null) {
                        continue;
                    }
                    String info = stnm + "水文站" + startMonth + "月" + startDay + "日" + startHour + "时";
                    if (minz != null) {
                        info += "水位" + new BigDecimal(minz).setScale(2,BigDecimal.ROUND_HALF_UP).toString() + "m,";
                    }
                    if (minq != null) {
                        info += "流量" + new BigDecimal(minq).setScale(2,BigDecimal.ROUND_HALF_UP).toString() + "m3/s;";
                    } else {
                        info = info.substring(0, info.length() - 1);
                        info += "。";
                    }
                    info += endMonth + "月" + endtDay + "月" + endHour + "时";
                    if (maxz != null) {
                        info += "水位" + new BigDecimal(maxz).setScale(2,BigDecimal.ROUND_HALF_UP).toString() + "m,";
                    }
                    if (maxq != null) {
                        info += "流量" + new BigDecimal(maxq).setScale(2,BigDecimal.ROUND_HALF_UP).toString() + "m3/s;";
                    } else {
                        info = info.substring(0, info.length() - 1);
                        info += "。";
                    }
                    hedaoAndSkStrings.add(info);

                }//min max的判断
            }//for 循环

        }
        //获取水库
        List<StStbprpB> collectSK = allSTBB.stream().filter(t -> "RR".equals(t.getSttp())).collect(Collectors.toList());
        List<String> skStcds = collectSK.stream().map(StStbprpB::getStcd).collect(Collectors.toList());
        //库（湖）站汛限水位表 查询主汛期为1的  指标
        List<TRsvrfsrB> tRsvrfsrBS = tRsvrfsrBDao.findByFstp("1");

        List<Map<String, Object>> rsvrLastData = new ArrayList<>();
        List<String> shuiKuXuShuiLiangs = new ArrayList<>(); //水库话描述
        List<Map> shuiKuXuShuiTable = new ArrayList<>(); //水库蓄水表格
        List<Map> shuiKuDDgz = new ArrayList<>(); //水库调度规则
        //获取水库警戒信息
        Map<String, TRsvrfsrB> collectWarningSK = tRsvrfsrBS.stream().collect(Collectors.toMap(TRsvrfsrB::getStcd, Function.identity()));
        if (!CollectionUtils.isEmpty(skStcds)) {
            List<TRsvrR> minDateByDateAndStcdsRsvrR = tRsvrRDao.findMinDateByDateAndStcds(skStcds, startTime, endTime);
            Map<String, TRsvrR> minSKMap = minDateByDateAndStcdsRsvrR.stream().collect(Collectors.toMap(TRsvrR::getStcd, Function.identity()));

            List<TRsvrR> maxDateByDateAndStcdsRsvrR = tRsvrRDao.findMaxDateByDateAndStcds(collectSK.stream().map(StStbprpB::getStcd).collect(Collectors.toList()), startTime, endTime);
            Map<String, TRsvrR> maxSKMap = maxDateByDateAndStcdsRsvrR.stream().collect(Collectors.toMap(TRsvrR::getStcd, Function.identity()));

            //todo 水库的水晴表格信息
            List<Map<String, Object>> skMinByTime = tRsvrRDao.getWaterLevelMinByTime(skStcds, startTime, endTime);
            Map<String, Map<String, Object>> skMinZMap = listToMap(skMinByTime); //stcd z

            List<TRsvrR> skMaxByTime = tRsvrRDao.getWaterMaxByTime(skStcds, startTime, endTime);
            Map<String, TRsvrR> skMaxZMap = skMaxByTime.stream().collect(Collectors.toMap(TRsvrR::getStcd, Function.identity(), (v1, v2) -> v1));

            List<Map<String, Object>> skAvgValueByTime = tRsvrRDao.getAvgValueByTime(skStcds, startTime, endTime);
            Map<String, Map<String, Object>> skAvgValueMap = listToMap(skAvgValueByTime);

            List<Map<String, Object>> skHistoryAvgValueByTime = tRsvrRDao.getHistoryAvgValueByTime(skStcds, startStr, endStr);
            Map<String, Map<String, Object>> skHistoryAvgValueMap = listToMap(skHistoryAvgValueByTime);

            List<TRsvrR> skMaxQValueByTime = tRsvrRDao.getMaxQValueByTime(skStcds, startTime, endTime);
            Map<String, TRsvrR> skMaxQMap = skMaxQValueByTime.stream().collect(Collectors.toMap(TRsvrR::getStcd, Function.identity(),(v1, v2) -> v1));

            //获取水库数据最大一条记录
            rsvrLastData = tRsvrRDao.getWaterLevelMaxByTimeAndStcds(skStcds,startTime, endTime);

            for (StStbprpB stStbprpB : collectSK) {
                String stcd = stStbprpB.getStcd();
                String stnm = stStbprpB.getStnm();

                TRsvrR minMap = minSKMap.get(stcd);
                TRsvrR maxMap = maxSKMap.get(stcd);

                Map<String, Object> skMinZMapData = skMinZMap.get(stcd);//最小水位
                TRsvrR skMaxZMapData = skMaxZMap.get(stcd); //最大水位以及时间  todo 根据此水位查 容量值
                Map<String, Object> skAvgValueMapData = skAvgValueMap.get(stcd);//当前平均水位
                Map<String, Object> skHistoryAvgValueMapData = skHistoryAvgValueMap.get(stcd);//历史平均水位
                TRsvrR skMaxQMapData = skMaxQMap.get(stcd); //最大流量 以及时间
                TRsvrfsrB tRsvrfsrB = collectWarningSK.get(stcd); //警戒水位
                if (skMinZMapData != null || skMaxZMapData != null || skAvgValueMapData != null || skHistoryAvgValueMapData != null || skMaxQMapData != null) {
                    Map<String, Object> dataMap = new HashMap();
                    dataMap.put("stcd", stcd);
                    dataMap.put("stnm", stnm);
                    String minzValue = "-";  //最小的水位值
                    String maxzValue = "-"; //最大水位值
                    String maxzTime = "-";  //最大值出现时间
                    String jjswValue = "-";
                    String avgValue = "-"; //时段平均值
                    String historyAvgValue = "-";  //历史平均值
                    String maxQvalue = "-";  //最大流量值
                    String maxQTime = "-";  //最大流量值时间
                    if (skMinZMapData != null && skMinZMapData.get("z") != null) {
                        minzValue = new BigDecimal(skMinZMapData.get("z") + "").setScale(2,BigDecimal.ROUND_HALF_UP).toString();
                    }
                    if (skMaxZMapData != null && skMaxZMapData.getRz() != null) {
                        maxzValue = new BigDecimal(skMaxZMapData.getRz() + "").setScale(2,BigDecimal.ROUND_HALF_UP).toString();
                        maxzTime = DateUtil.dateToStringNormal(skMaxZMapData.getTm());
                    }
                    if (skAvgValueMapData != null && skAvgValueMapData.get("value") != null) {
                        avgValue = new BigDecimal(skAvgValueMapData.get("value") + "").setScale(2,BigDecimal.ROUND_HALF_UP).toString();
                    }
                    if (skHistoryAvgValueMapData != null && skHistoryAvgValueMapData.get("value") != null) {
                        historyAvgValue = new BigDecimal(skHistoryAvgValueMapData.get("value") + "").setScale(2,BigDecimal.ROUND_HALF_UP).toString();
                    }
                    if (skMaxQMapData != null && skMaxQMapData.getInq() != null) {
                        maxQvalue = new BigDecimal(skMaxQMapData.getInq() + "").setScale(2,BigDecimal.ROUND_HALF_UP).toString();
                        maxQTime = DateUtil.dateToStringNormal(skMaxQMapData.getTm());
                    }
                    if (tRsvrfsrB != null && tRsvrfsrB.getFsltdz() != null) {
                        jjswValue = new BigDecimal(tRsvrfsrB.getFsltdz() + "").setScale(2,BigDecimal.ROUND_HALF_UP).toString();
                    }
                    dataMap.put("minzValue", minzValue); //最小水位
                    dataMap.put("maxzValue", maxzValue); //最大水位
                    dataMap.put("maxzTime", maxzTime); //最大水位时间
                    dataMap.put("jjswValue", jjswValue); //警戒水位
                    dataMap.put("avgValue", avgValue); //平均水位
                    dataMap.put("historyAvgValue", historyAvgValue); //历史平均水位
                    dataMap.put("maxQvalue", maxQvalue); //最大流量
                    dataMap.put("maxQTime", maxQTime); //最大流量时间
                    heDaoAndShuiKuList.add(dataMap); //组装水库表格数据
                }

                if (minMap != null || maxMap != null) { //todo 查水库容量
                    /**
                     * 黄河洛口水文站3月1日8时水位26.78m，流量970m3/s；3月6日2时水位26.84m，流量1080m3/s。
                     */
                    BigDecimal startZValue = null;
                    BigDecimal endZValue = null;
                    BigDecimal startValue = null;
                    BigDecimal endValue = null;

                    String startz = minMap.getRz();//起始水位
                    String startq = minMap.getInq();//起始流量
                    String endz = maxMap.getRz();
                    String endq = maxMap.getInq();
                    BigDecimal avgZHistoryValue = null;
                    BigDecimal avgHistoryValue = null;
                    if (skHistoryAvgValueMapData.get("value") != null){// 平均值{
                        avgZHistoryValue = new BigDecimal(skHistoryAvgValueMapData.get("value")+"").setScale(2,BigDecimal.ROUND_HALF_UP);
                    }
                    if (avgZHistoryValue == null ){
                        avgHistoryValue = new BigDecimal("0").setScale(2,BigDecimal.ROUND_HALF_UP);
                    }
                    if (startz == null && startq == null && endz == null && endq == null) {
                        continue;
                    }
                    String info = stnm + "水文站" + startMonth + "月" + startDay + "日" + startHour + "时";
                    if (startz != null) {
                        startZValue = new BigDecimal(startz+"").setScale(2,BigDecimal.ROUND_HALF_UP);
                        info += "水位" + new BigDecimal(startz).setScale(2,BigDecimal.ROUND_HALF_UP).toString() + "m,";
                    }
                    if (startq != null) {
                        info += "流量" + new BigDecimal(startq).setScale(2,BigDecimal.ROUND_HALF_UP).toString() + "m3/s;";
                    } else {
                        info = info.substring(0, info.length() - 1);
                        info += "。";
                    }
                    info += endMonth + "月" + endtDay + "月" + endHour + "时";
                    if (endz != null) {
                        endZValue = new BigDecimal(endz+"").setScale(2,BigDecimal.ROUND_HALF_UP);
                        info += "水位" + new BigDecimal(endz).setScale(2,BigDecimal.ROUND_HALF_UP).toString() + "m,";
                    }
                    if (endq != null) {
                        info += "流量" + new BigDecimal(endq).setScale(2,BigDecimal.ROUND_HALF_UP).toString() + "m3/s;";
                    } else {
                        info = info.substring(0, info.length() - 1);
                        info += "。";
                    }
                    hedaoAndSkStrings.add(info);

                    if(startZValue == null){
                        startZValue = new BigDecimal("0");
                    }
                    if (endZValue == null){
                        endZValue = new BigDecimal("0");
                    }
                    switch (stcd){

                        case "RSR_001"://y = 2.6198x2 - 867.14x + 71775

                            startValue = new BigDecimal("2.6198").multiply(startZValue.pow(2))
                                    .subtract(new BigDecimal("867.14").multiply(startZValue)).add(new BigDecimal("71775")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            endValue = new BigDecimal("2.6198").multiply(endZValue.pow(2))
                                    .subtract(new BigDecimal("867.14").multiply(endZValue)).add(new BigDecimal("71775")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            avgHistoryValue = new BigDecimal("2.6198").multiply(avgZHistoryValue.pow(2))
                                    .subtract(new BigDecimal("867.14").multiply(avgZHistoryValue)).add(new BigDecimal("71775")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            break;
                        case "RSR_002"://y = 8.9233x2 - 1206.3x + 40828

                            startValue = new BigDecimal("8.9233").multiply(startZValue.pow(2))
                                    .subtract(new BigDecimal("1206.3").multiply(startZValue)).add(new BigDecimal("40828")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            endValue = new BigDecimal("8.9233").multiply(endZValue.pow(2))
                                    .subtract(new BigDecimal("1206.3").multiply(endZValue)).add(new BigDecimal("40828")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            avgHistoryValue = new BigDecimal("8.9233").multiply(avgZHistoryValue.pow(2))
                                    .subtract(new BigDecimal("1206.3").multiply(avgZHistoryValue)).add(new BigDecimal("40828")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            break;
                        case "RSR_003"://y = 2.8675x2 - 142.23x + 1322.8

                            startValue = new BigDecimal("2.8675").multiply(startZValue.pow(2))
                                    .subtract(new BigDecimal("142.23").multiply(startZValue)).add(new BigDecimal("1322.8")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            endValue = new BigDecimal("2.8675").multiply(endZValue.pow(2))
                                    .subtract(new BigDecimal("142.23").multiply(endZValue)).add(new BigDecimal("1322.8")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            avgHistoryValue = new BigDecimal("2.8675").multiply(avgZHistoryValue.pow(2))
                                    .subtract(new BigDecimal("142.23").multiply(avgZHistoryValue)).add(new BigDecimal("1322.8")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            break;

                        case "RSR_004"://y = 1.3855x2 - 764.01x + 105316

                            startValue = new BigDecimal("1.3855").multiply(startZValue.pow(2))
                                    .subtract(new BigDecimal("764.01").multiply(startZValue)).add(new BigDecimal("105316")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            endValue = new BigDecimal("1.3855").multiply(endZValue.pow(2))
                                    .subtract(new BigDecimal("764.01").multiply(endZValue)).add(new BigDecimal("105316")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            avgHistoryValue = new BigDecimal("1.3855").multiply(avgZHistoryValue.pow(2))
                                    .subtract(new BigDecimal("764.01").multiply(avgZHistoryValue)).add(new BigDecimal("105316")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            break;
                        case "RSR_005"://y = 5.497x2 - 1020.7x + 47394

                            startValue = new BigDecimal("5.497").multiply(startZValue.pow(2))
                                    .subtract(new BigDecimal("1020.7").multiply(startZValue)).add(new BigDecimal("47394")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            endValue = new BigDecimal("5.497").multiply(endZValue.pow(2))
                                    .subtract(new BigDecimal("1020.7").multiply(endZValue)).add(new BigDecimal("47394")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            avgHistoryValue = new BigDecimal("5.497").multiply(avgZHistoryValue.pow(2))
                                    .subtract(new BigDecimal("1020.7").multiply(avgZHistoryValue)).add(new BigDecimal("47394")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            break;
                        case "RSR_006"://y = 1.5041x2 - 896.85x + 133839

                            startValue = new BigDecimal("1.5041").multiply(startZValue.pow(2))
                                    .subtract(new BigDecimal("896.85").multiply(startZValue)).add(new BigDecimal("133839")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            endValue = new BigDecimal("1.5041").multiply(endZValue.pow(2))
                                    .subtract(new BigDecimal("896.85").multiply(endZValue)).add(new BigDecimal("133839")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            avgHistoryValue = new BigDecimal("1.5041").multiply(avgZHistoryValue.pow(2))
                                    .subtract(new BigDecimal("896.85").multiply(avgZHistoryValue)).add(new BigDecimal("133839")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            break;
                        case "RSR_007"://y = 8.6731x2 - 5857.2x + 988887

                            startValue = new BigDecimal("8.6731").multiply(startZValue.pow(2))
                                    .subtract(new BigDecimal("5857.2").multiply(startZValue)).add(new BigDecimal("988887")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            endValue = new BigDecimal("8.6731").multiply(endZValue.pow(2))
                                    .subtract(new BigDecimal("5857.2").multiply(endZValue)).add(new BigDecimal("988887")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            avgHistoryValue = new BigDecimal("8.6731").multiply(avgZHistoryValue.pow(2))
                                    .subtract(new BigDecimal("5857.2").multiply(avgZHistoryValue)).add(new BigDecimal("988887")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            break;
                        case "RSR_008"://y = 9.8014x2 - 3900x + 388112

                            startValue = new BigDecimal("9.8014").multiply(startZValue.pow(2))
                                    .subtract(new BigDecimal("3900").multiply(startZValue)).add(new BigDecimal("388112")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            endValue = new BigDecimal("9.8014").multiply(endZValue.pow(2))
                                    .subtract(new BigDecimal("3900").multiply(endZValue)).add(new BigDecimal("388112")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            avgHistoryValue = new BigDecimal("9.8014").multiply(avgZHistoryValue.pow(2))
                                    .subtract(new BigDecimal("3900").multiply(avgZHistoryValue)).add(new BigDecimal("388112")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            break;
                        case "RSR_009"://y = 13.174x2 - 1685.4x + 53907  //得到的是万

                            startValue = new BigDecimal("13.174").multiply(startZValue.pow(2))
                                    .subtract(new BigDecimal("1685.4").multiply(startZValue)).add(new BigDecimal("53907")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            endValue = new BigDecimal("13.174").multiply(endZValue.pow(2))
                                    .subtract(new BigDecimal("1685.4").multiply(endZValue)).add(new BigDecimal("53907")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            avgHistoryValue = new BigDecimal("13.174").multiply(avgZHistoryValue.pow(2))
                                    .subtract(new BigDecimal("1685.4").multiply(avgZHistoryValue)).add(new BigDecimal("53907")).setScale(2,BigDecimal.ROUND_HALF_UP);
                            break;
                        default:
                    }//break方法
                    Map<String,String> skDdMap = new HashMap<>();
                    skDdMap.put("stcd",stcd);
                    skDdMap.put("stnm",stnm);
                    skDdMap.put("ddgz",SK_DD.get(stcd));
                    shuiKuDDgz.add(skDdMap);
                    BigDecimal compare = endValue.subtract(startValue);
                    String compareStr = "";
                    if (compare.compareTo(BigDecimal.ZERO) >= 0){
                        compareStr = "增加";
                    }else {
                        compareStr = "减少";
                    }
                    Map skKuRongMap = new HashMap();
                    skKuRongMap.put("stcd",stcd);
                    skKuRongMap.put("stnm",stnm);
                    skKuRongMap.put("startTime",startTimeStr);
                    skKuRongMap.put("startZ",startZValue);
                    skKuRongMap.put("startValue",startValue);
                    skKuRongMap.put("endTime",endTimeStr);
                    skKuRongMap.put("endZ",endZValue);
                    skKuRongMap.put("endValue",endValue);
                    skKuRongMap.put("avgZ",avgZHistoryValue);
                    skKuRongMap.put("avgValue",avgHistoryValue);
                    skKuRongMap.put("diffValue",compare.abs());
                    shuiKuXuShuiTable.add(skKuRongMap);
                    /**
                     杏林水库2021年3月1日8时水位231.93m，蓄水量13740万m3；2021年3月6日2时水位231.93m，蓄水量为13630万m3，时间段内增加蓄水量110万m3。
                     */
                    String shuiliangInfo = stnm + startYear + "年" +startMonth +"月"+startDay+"日"+startHour+"时水位"
                            + startZValue +"m,蓄水量为"+startValue+"万m3; "+endYear+ "年" +endMonth +"月"+endtDay+"日"+endHour+"时水位"
                            + endZValue + "m,蓄水量为" + endValue+"万m3, 时间段内"+compareStr+"蓄水量"+compare+"万m3。";
                    shuiKuXuShuiLiangs.add(shuiliangInfo);
                }//min max判断 if
            }//for循环
        }

        resultMap.put("waterBgZList",heDaoAndShuiKuList);
        resultMap.put("waterZList",hedaoAndSkStrings);
        resultMap.put("skKuRongStrList",shuiKuXuShuiLiangs);
        resultMap.put("skKuRongBgList",shuiKuXuShuiTable);
        resultMap.put("skDdgz",shuiKuDDgz);
        //超警戒水位个数
        Set<String> cjjSet = new HashSet<>();
        //保证水位
        Set<String> bzswSet = new HashSet<>();
        //历史最高水位
        Set<String> lszgswSet = new HashSet<>();

        for (Map<String, Object> map : riverRLastData) {
            try {
                String stcd = map.get("stcd").toString();
                //水位
                double z = Double.parseDouble(map.get("z").toString());
                TRvfcchB tRvfcchStandard = collectWarningHD.get(stcd);
                if (tRvfcchStandard == null) {
                    continue;
                }
                //警戒水位
                double wrz = Double.parseDouble(tRvfcchStandard.getWrz() == null ? "0" : tRvfcchStandard.getWrz());
                if (wrz < z) {
                    cjjSet.add(stcd);
                }
                //保证水位
                double grz = Double.parseDouble(tRvfcchStandard.getGrz()==null?"0":tRvfcchStandard.getGrz());
                //历史最高水位
                double obhtz = Double.parseDouble(tRvfcchStandard.getObhtz()==null?"0":tRvfcchStandard.getObhtz());
                if(obhtz<z){
                    lszgswSet.add(stcd);
                }
                if(grz<z){
                    bzswSet.add(stcd);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        //超警戒水库
        Set<String> cjjskSet = new HashSet<>();
        for (Map<String, Object> map : rsvrLastData) {
            try {
                String stcd = map.get("stcd").toString();
                //水位
                double z = Double.parseDouble(map.get("RZ").toString());
                TRsvrfsrB tRsvrfsrB = collectWarningSK.get(stcd);
                if (tRsvrfsrB == null) {
                    continue;
                }
                //汛限水位
                double fsltdz = Double.parseDouble(tRsvrfsrB.getFsltdz() == null ? "0" : tRsvrfsrB.getFsltdz());
                if (fsltdz < z) {
                    cjjskSet.add(stcd);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        String waterInfo = "";
        waterInfo += "河道站共记" + collectHD.size() + "个，其中超警戒水位断面共记" + cjjSet.size() + "个。" +
                "超保证水位断面"+ bzswSet.size() +"个,超历史最高水位断面"+lszgswSet.size()+"个.";
        waterInfo += "水库共记" + collectSK.size() + "个，其中超汛限水位水库共记" + cjjskSet.size() + "个。";
        resultMap.put("waterAfterInfo",waterInfo);
        return resultMap;
    }

    @Autowired
    StStbprpBDao stStbprpBDao;
    @Autowired
    TRvfcchBDao tRvfcchBDao; //河道站防洪指标表
    @Autowired
    TRsvrfsrBDao tRsvrfsrBDao; ////库（湖）站汛限水位表 查询主汛期为1的
    @Autowired
    private TRiverRODao tRiverRDao; //河道水情

    @Autowired
    private TRsvrRDao tRsvrRDao; //水库水情表


    private Map<String, Map<String, Object>> listToMap(List<Map<String, Object>> waterLevelMinByTime) {
        Map resultMap = new HashMap();
        for (Map<String, Object> map : waterLevelMinByTime){
            String stcd = map.get("stcd")+"";
            resultMap.put(stcd,map);
        }
        return resultMap;
    }


    private  Map<String,Object> getStcdMap(RainPartitionDto reqDto){
        //查询分区关系表  //TODO 并没有筛选雨量站，就看库里有没有数据
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

            List<String> stcds = new ArrayList<>();
            if (!"0".equals(reqDto.getAreaId())){
                stcds.addAll(stcdPartMap.get(reqDto.getAreaId()));
            }else{
                stcds.addAll(stPartList.stream().map(StStbprpPartRelate::getStcd).collect(Collectors.toList()));
            }
            Map resultMap = new HashMap();
            resultMap.put("stcds",stcds);
            resultMap.put("stcdMap",stcdMap);
            return resultMap;
        }

        /**
         * 获取历年的平均
         * @param reqDto
         * @return
         */
        private Double getBeforeValue(RainPartitionDto reqDto){
            DecimalFormat df = new DecimalFormat("0.00");
            Map<String, Object> dataMap = getStcdMap(reqDto);
            List<String> stcds =  (List<String>) dataMap.get("stcds");
            //Map<String, String> stcdMap = (Map<String, String>) dataMap.get("stcdMap");
            SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");
            Date startTime = reqDto.getStartTime();
            Date endTime = reqDto.getEndTime();
            String startStr = format.format(startTime);
            String endStr = format.format(endTime);
            List<Map<String, Object>> beforeRainByTime = stStbprpPartRelateDao.getBeforeRainByTime(stcds,startStr, endStr);
            Map<String,List<Map>> dataMapNew = new HashMap<>();
            for (Map<String,Object> map : beforeRainByTime){//stcd,TO_CHAR(t.tm,'yyyy') time,SUM(t.drp) value
                //String stcd = map.get("stcd")+"";
                String time = map.get("time")+"";
                //String value = map.get("value")+"";
                List<Map> maps = dataMapNew.get(time);
                if (CollectionUtils.isEmpty(maps)){
                    maps = new ArrayList<>();
                }
                maps.add(map);
                dataMapNew.put(time,maps);
            }
            Double sumDrp = 0.0;
            for (Map.Entry<String, List<Map>> entry : dataMapNew.entrySet()) {
                List<Map> datas = entry.getValue();
                Double oneYear = 0.0;
                for (Map<String, Object> map : datas) {
                    Double drp = Double.parseDouble(map.get("value") + "");
                    oneYear += drp;
                }
                sumDrp += Double.parseDouble(df.format(oneYear / datas.size()) + "");
            }
            if (!CollectionUtils.isEmpty(dataMapNew)){
                sumDrp = Double.parseDouble(df.format(sumDrp / dataMapNew.size()) + "");
            }
            return  sumDrp;
        }

        /**
         * 获取雨量
         * @param reqDto
         * @return
         */
         private String getPartNewRain(RainPartitionDto reqDto) {

            DecimalFormat df = new DecimalFormat("0.00");

            DecimalFormat df2 = new DecimalFormat("0.00%");
             Date startTime = reqDto.getStartTime();
             Date endTime = reqDto.getEndTime();
             Map<String, Object> dataMap = getStcdMap(reqDto);
             List<String> stcds =  (List<String>) dataMap.get("stcds");  //TODO 后续筛选下雨量站
             Map<String, String> stcdMap = (Map<String, String>) dataMap.get("stcdMap");

            RainPartitionDataDto rainPartitionDataDto = new RainPartitionDataDto();
            //查询测站降雨数据（按测站分组求和）todo 分区下每个监测站的降雨量  降序排序
            List<Map<String, Object>> stcdRainDataList = stStbprpPartRelateDao.getPartRainByTime(stcds, startTime, endTime);
            rainPartitionDataDto.setCount(stcdRainDataList.size());//设置次数
            //分区总雨量
            Double sumDrp = 0.0;
            //测站最大雨量
            Double maxDrp = 0.0;
            //测站第二大雨量
            Double secondDrp = 0.0;
            //测站最小雨量
            Double minDrp = 0.0;
            //最大雨量测站编码
            String maxStcd = "";
            //第二大雨量测站编码
            String secondStcd = "";
            //最小雨量测站编码
            String minStcd = "";


            if (!CollectionUtils.isEmpty(stcdRainDataList)){
                maxDrp = Double.parseDouble(df.format(Double.parseDouble(stcdRainDataList.get(0).get("DRP")+"")));
                maxStcd = stcdRainDataList.get(0).get("STCD")+"";
                if (stcdRainDataList.size() > 1){
                    secondDrp = Double.parseDouble(df.format(Double.parseDouble(stcdRainDataList.get(1).get("DRP")+"")));
                    secondStcd = stcdRainDataList.get(1).get("STCD")+"";
                }else {
                    secondDrp = Double.parseDouble(df.format(Double.parseDouble(stcdRainDataList.get(0).get("DRP")+"")));
                    secondStcd = stcdRainDataList.get(0).get("STCD")+"";
                }
                minDrp = Double.parseDouble(df.format(Double.parseDouble(stcdRainDataList.get(stcdRainDataList.size()-1).get("DRP")+"")));
                minStcd = stcdRainDataList.get(stcdRainDataList.size()-1).get("STCD")+"";

            }
            for (Map<String, Object> map : stcdRainDataList) {
                Double drp = Double.parseDouble(df.format(Double.parseDouble(map.get("DRP") + "")));
                sumDrp += drp;
            }
            //封装返回参数
            rainPartitionDataDto.setPartName(reqDto.getAreaName());
            try { //todo 求平均值 才能算分区降雨量
                rainPartitionDataDto.setPartDrp(Double.parseDouble(df.format(sumDrp / stcdRainDataList.size()) + ""));
            } catch (Exception e) {
                rainPartitionDataDto.setPartDrp(0.0);//设置为null
            }

            if (!"".equals(maxStcd)){
                rainPartitionDataDto.setMaxStcd(maxStcd);
                rainPartitionDataDto.setSecondStcd(secondStcd);
                rainPartitionDataDto.setMinStcd(minStcd); //设置最大其次最小

                rainPartitionDataDto.setMaxStnm(stcdMap.get(maxStcd));
                rainPartitionDataDto.setSecondStnm(stcdMap.get(secondStcd));
                rainPartitionDataDto.setMinStnm(stcdMap.get(minStcd));

                rainPartitionDataDto.setMaxDrp(maxDrp);
                rainPartitionDataDto.setSecondDrp(secondDrp);
                rainPartitionDataDto.setMinDrp(minDrp);
            }
            String startTimeStr = DateUtil.dateToStringNormal(startTime);
            String endTimeStr = DateUtil.dateToStringNormal(endTime);
            //起始时间月日时
            Integer startMonth = Integer.parseInt(startTimeStr.substring(5, 7));
            Integer startDay = Integer.parseInt(startTimeStr.substring(8, 10));
            Integer startHour = Integer.parseInt(startTimeStr.substring(11, 13));
            //起始时间月日时
            Integer endMonth = Integer.parseInt(endTimeStr.substring(5, 7));
            Integer endtDay = Integer.parseInt(endTimeStr.substring(8, 10));
            Integer endHour = Integer.parseInt(endTimeStr.substring(11, 13));
            //TODO 获取之前的年数据
            Double beforeValue = getBeforeValue(reqDto);

            Double compare = rainPartitionDataDto.getPartDrp() - beforeValue;
            String compareResult = compare > 0 ? "增加" : "减少";
            String precent = df2.format(Math.abs(compare)/beforeValue);
            if (CollectionUtils.isEmpty(stcdRainDataList)){
                String rainStr = startMonth + "月" + startDay + "日" + startHour + "时至" + endMonth + "月" + endtDay + "日" + endHour + "时，无详细降雨量信息";
                return rainStr;
            }
            String rainStr = startMonth + "月" + startDay + "日" + startHour + "时至" + endMonth + "月" + endtDay + "日" + endHour + "时，"
                    +reqDto.getAreaName()+"相关雨量站累计降雨量"+rainPartitionDataDto.getPartDrp()+"mm,较常年"+compareResult+precent+"。其中发生降雨的站点共"+rainPartitionDataDto.getCount()
                    +"个，最大点雨量为"+rainPartitionDataDto.getMaxStnm()+rainPartitionDataDto.getMaxDrp()+"mm,其次为"
                    + rainPartitionDataDto.getSecondStnm() + rainPartitionDataDto.getSecondDrp()+"mm,最小为"
                    + rainPartitionDataDto.getMinStnm()+rainPartitionDataDto.getMinDrp()+"mm。整体河系降雨分布情况见下图。";

            return rainStr;
    }

    /**
     * 获取分区列表
     * @return
     */
    @Override
    public Object getAreaList() {

        List<StStbprpPartition> all = stStbprpPartitionDao.findAll();
        StStbprpPartition one = new StStbprpPartition();
        one.setId("0");
        one.setPartiTionNM("小清河流域");
        all.add(one);
        return all;
    }

    /**
     * 保存报告
     * @param map
     * @return
     */
    @Autowired
    YwkReportDataFhfxDao ywkReportDataFhfxDao;
    @Autowired
    YwkReportDataFhfxInfoDao ywkReportDataFhfxInfoDao;
    @Autowired
    YwkReportDataFhfxSkDao ywkReportDataFhfxSkDao;

    @Transactional
    @Override
    public Object saveRainWaterCommonReport(Map map) {

        YwkRainWaterReport ywkRainWaterReport = new YwkRainWaterReport();
        if (map.get("reportId") == null){
            String reportId = StrUtil.getUUID();
            ywkRainWaterReport.setId(reportId);
        }else{
            ywkRainWaterReport.setId(map.get("reportId")+"");
        }
        ywkRainWaterReport.setReportStatus(map.get("reportStatus")+"");//0是草稿 1是历史
        ywkRainWaterReport.setCreateTime(DateUtil.getDateByStringNormal(map.get("createTime")+""));
        ywkRainWaterReport.setReportName(map.get("reportName")+"");
        ywkRainWaterReport.setReportType("2");//防洪预报
        ywkRainWaterReport.setReportStartTime(DateUtil.getDateByStringNormal(map.get("dataTime")+""));
        ywkRainWaterReport.setReportEndTime(DateUtil.getDateByStringNormal(map.get("endTime")+""));
        ywkRainWaterReport.setDescribeRainInfo(map.get("rainInfo")+"");
        ywkRainWaterReport.setDescribeWaterInfo(map.get("waterAfterInfo")+"");

        ywkRainWaterReport.setSign(map.get("sign")+"");
        ywkRainWaterReport.setEngagement(map.get("engagement")+"");
        ywkRainWaterReport.setVerification(map.get("darft")+"");
        ywkRainWaterReport.setDarft(map.get("darft")+"");
        ywkRainWaterReport.setcPartId(map.get("cPartId")+"");
        //保存
        ywkRainWaterReportDao.save(ywkRainWaterReport);

        List<Map<String,Object>> waterBgZList = (List<Map<String, Object>>) map.get("waterBgZList");
        if (waterBgZList != null){
            List<YwkReportDataFhfx>  inserts = new ArrayList<>();
            for (Map<String,Object> waterBgZMap : waterBgZList){
                YwkReportDataFhfx ywkReportDataFhfx = new YwkReportDataFhfx();
                ywkReportDataFhfx.setcId(StrUtil.getUUID());
                ywkReportDataFhfx.setcReportId(ywkRainWaterReport.getId());
                ywkReportDataFhfx.setcCreateTime(DateUtil.getCurrentTime());
                Double historyAvgValue = handleNullAndStr(waterBgZMap.get("historyAvgValue")+"") == null? null :Double.parseDouble(handleNullAndStr(waterBgZMap.get("historyAvgValue")+""));
                Double avgValue =handleNullAndStr(waterBgZMap.get("avgValue")+"") == null? null : Double.parseDouble(handleNullAndStr(waterBgZMap.get("avgValue")+""));
                Double maxQvalue =handleNullAndStr(waterBgZMap.get("maxQvalue")+"") == null? null : Double.parseDouble(handleNullAndStr(waterBgZMap.get("maxQvalue")+""));
                Date maxQTime =handleNullAndStr(waterBgZMap.get("maxQTime")+"") == null? null : DateUtil.getDateByStringNormal(handleNullAndStr(waterBgZMap.get("maxQTime")+""));
                Double maxzValue =handleNullAndStr(waterBgZMap.get("maxzValue")+"") == null? null : Double.parseDouble(handleNullAndStr(waterBgZMap.get("maxzValue")+""));
                Date maxzTime =handleNullAndStr(waterBgZMap.get("maxzTime")+"") == null? null : DateUtil.getDateByStringNormal(handleNullAndStr(waterBgZMap.get("maxzTime")+""));
                Double minzValue =handleNullAndStr(waterBgZMap.get("minzValue")+"") == null? null : Double.parseDouble(handleNullAndStr(waterBgZMap.get("minzValue")+""));
                String stcd =handleNullAndStr(waterBgZMap.get("stcd")+"") == null? null : handleNullAndStr(waterBgZMap.get("stcd")+"");
                String stnm =handleNullAndStr(waterBgZMap.get("stnm")+"") == null? null : handleNullAndStr(waterBgZMap.get("stnm")+"");
                Double jjswValue =handleNullAndStr(waterBgZMap.get("jjswValue")+"") == null? null : Double.parseDouble(handleNullAndStr(waterBgZMap.get("jjswValue")+""));

                ywkReportDataFhfx.setcAvgHistoryZ(historyAvgValue);
                ywkReportDataFhfx.setcAvgZ(avgValue);
                ywkReportDataFhfx.setcMaxQ(maxQvalue);
                ywkReportDataFhfx.setcMaxQTime(maxQTime);
                ywkReportDataFhfx.setcMaxZ(maxzValue);
                ywkReportDataFhfx.setcMaxZTime(maxzTime);
                ywkReportDataFhfx.setcMinZ(minzValue);
                ywkReportDataFhfx.setcStcd(stcd);
                ywkReportDataFhfx.setcName(stnm);
                ywkReportDataFhfx.setcWarnZ(jjswValue);
                inserts.add(ywkReportDataFhfx);
            }
            ywkReportDataFhfxDao.deleteByCReportId(ywkRainWaterReport.getId());
            if (!CollectionUtils.isEmpty(inserts)){
                ywkReportDataFhfxDao.saveAll(inserts);
            }
        }

        List<String> waterZQs = (List<String>) map.get("waterZList");//水情Z\Q显示

        if (waterZQs != null){
            List<YwkReportDataFhfxInfo> inserts = new ArrayList<>();
            for (String waterZQ : waterZQs){
                YwkReportDataFhfxInfo ywkReportDataFhfxInfo = new YwkReportDataFhfxInfo();
                ywkReportDataFhfxInfo.setcId(StrUtil.getUUID());
                ywkReportDataFhfxInfo.setcCreateTime(DateUtil.getCurrentTime());
                ywkReportDataFhfxInfo.setcReportId(ywkRainWaterReport.getId());
                ywkReportDataFhfxInfo.setcType("0");
                ywkReportDataFhfxInfo.setcZqInfo(waterZQ);
                inserts.add(ywkReportDataFhfxInfo);
            }
            ywkReportDataFhfxInfoDao.deleteByCReportIdAndCType(ywkRainWaterReport.getId(),"0");
            if (!CollectionUtils.isEmpty(inserts)){
                ywkReportDataFhfxInfoDao.saveAll(inserts);
            }
        }
        List<String> skKuRongStrList = (List<String>) map.get("skKuRongStrList");//水情容量
        if (skKuRongStrList != null){
            List<YwkReportDataFhfxInfo> inserts = new ArrayList();
            for (String skKuRongStr : skKuRongStrList ){
                YwkReportDataFhfxInfo ywkReportDataFhfxInfo = new YwkReportDataFhfxInfo();
                ywkReportDataFhfxInfo.setcId(StrUtil.getUUID());
                ywkReportDataFhfxInfo.setcCreateTime(DateUtil.getCurrentTime());
                ywkReportDataFhfxInfo.setcReportId(ywkRainWaterReport.getId());
                ywkReportDataFhfxInfo.setcType("1");
                ywkReportDataFhfxInfo.setcWaterQuantityInfo(skKuRongStr);
                inserts.add(ywkReportDataFhfxInfo);
            }
            ywkReportDataFhfxInfoDao.deleteByCReportIdAndCType(ywkRainWaterReport.getId(),"1");
            if (!CollectionUtils.isEmpty(inserts)){
                ywkReportDataFhfxInfoDao.saveAll(inserts);
            }
        }
        /**
         *   " "endZ": 4,
         *                     "avgValue": 32186.92,
         *                     "stcd": "RSR_001",
         *                     "endValue": 68348.36,
         *                     "startZ": 10,
         *                     "avgZ": 54.69,
         *                     "stnm": "狼猫山水库",
         *                     "diffValue": 4982.78,
         *                     "startTime": "2021-02-01 00:00:00",
         *                     "startValue": 63365.58,
         *                     "endTime": "2021-03-01 00:00:00"
         */
        List<Map<String,Object>> skKuRongBgList = (List<Map<String,Object>>) map.get("skKuRongBgList");//水情容量表格
        if ( skKuRongBgList != null){
            List<YwkReportDataFhfxSk> inserts = new ArrayList<>();
            for (Map<String,Object> skKuRongBgMap : skKuRongBgList){
                Double startZ = handleNullAndStr(skKuRongBgMap.get("startZ")) == null? null :Double.parseDouble(handleNullAndStr(skKuRongBgMap.get("startZ")));
                Double startValue = handleNullAndStr(skKuRongBgMap.get("startValue")) == null? null :Double.parseDouble(handleNullAndStr(skKuRongBgMap.get("startValue")));
                Double endZ = handleNullAndStr(skKuRongBgMap.get("endZ")) == null? null :Double.parseDouble(handleNullAndStr(skKuRongBgMap.get("endZ")));
                Double endValue = handleNullAndStr(skKuRongBgMap.get("endValue")) == null? null :Double.parseDouble(handleNullAndStr(skKuRongBgMap.get("endValue")));
                Double avgZ = handleNullAndStr(skKuRongBgMap.get("avgZ")) == null? null :Double.parseDouble(handleNullAndStr(skKuRongBgMap.get("avgZ")));
                Double avgValue = handleNullAndStr(skKuRongBgMap.get("avgValue")) == null? null :Double.parseDouble(handleNullAndStr(skKuRongBgMap.get("avgValue")));
                Double diffValue = handleNullAndStr(skKuRongBgMap.get("diffValue")) == null? null :Double.parseDouble(handleNullAndStr(skKuRongBgMap.get("diffValue")));

                Date startTime = handleNullAndStr(skKuRongBgMap.get("startTime")) == null? null :DateUtil.getDateByStringNormal(handleNullAndStr(skKuRongBgMap.get("startTime")));
                Date endTime = handleNullAndStr(skKuRongBgMap.get("endTime")) == null? null :DateUtil.getDateByStringNormal(handleNullAndStr(skKuRongBgMap.get("endTime")));
                YwkReportDataFhfxSk ywkReportDataFhfxSk = new YwkReportDataFhfxSk();
                ywkReportDataFhfxSk.setcId(StrUtil.getUUID());
                ywkReportDataFhfxSk.setcReportId(ywkRainWaterReport.getId());
                ywkReportDataFhfxSk.setcStcd(skKuRongBgMap.get("stcd")+"");
                ywkReportDataFhfxSk.setcStnm(skKuRongBgMap.get("stnm")+"");
                ywkReportDataFhfxSk.setcAvgHistoryZ(avgZ);
                ywkReportDataFhfxSk.setcWaterHistoryQuanTity(avgValue);
                ywkReportDataFhfxSk.setcStartZ(startZ);
                ywkReportDataFhfxSk.setcStartQuanTity(startValue);
                ywkReportDataFhfxSk.setcEndZ(endZ);
                ywkReportDataFhfxSk.setcEndQuanTity(endValue);
                ywkReportDataFhfxSk.setcDiffQuantity(diffValue);
                ywkReportDataFhfxSk.setcCreateTime(DateUtil.getCurrentTime());
                ywkReportDataFhfxSk.setcStartTime(startTime);
                ywkReportDataFhfxSk.setcEndTime(endTime);
                inserts.add(ywkReportDataFhfxSk);
            }
            ywkReportDataFhfxSkDao.deleteByCReportId(ywkRainWaterReport.getId());
            if (!CollectionUtils.isEmpty(inserts)){
                ywkReportDataFhfxSkDao.saveAll(inserts);
            }
        }
        return map;
    }

    private String handleNullAndStr(Object obj) {
        if (obj != null){
            if(!"-".equals(obj+"")){
                return obj+"";
            }
            return null;
        }
        return null;
    }

    @Override
    public Paginator getCommonReportList(PaginatorParam paginatorParam) {//查询列表 前端给stauts 判断是草稿还是历史
        List<Criterion> orders = paginatorParam.getOrders();
        if (orders == null) {
            orders = new ArrayList<>();
            paginatorParam.setOrders(orders);
        }
        Criterion criterion = new Criterion();
        criterion.setFieldName("createTime");
        criterion.setOperator(Criterion.DESC);
        orders.add(criterion);

        List<Criterion> conditions = paginatorParam.getConditions();
        if (conditions == null) {
            conditions = new ArrayList<>();
            paginatorParam.setConditions(conditions);
        }
        Criterion criterion2 = new Criterion();
        criterion2.setFieldName("reportType");
        criterion2.setOperator(Criterion.EQ);
        criterion2.setValue("2");//洪水预报的
        conditions.add(criterion2);
        return ywkRainWaterReportDao.findAll(paginatorParam);
    }


    @Transactional
    @Override
    public Object deleteCommonReportInfo(String reportId) {
        ywkRainWaterReportDao.deleteById(reportId);
        ywkReportDataFhfxDao.deleteByCReportId(reportId);
        ywkReportDataFhfxInfoDao.deleteByCReportId(reportId);
        ywkReportDataFhfxSkDao.deleteByCReportId(reportId);
        return reportId;
    }


    @Override
    public Object getCommonReportInfo(String reportId) {
        //查询简报信息
        YwkRainWaterReport report = ywkRainWaterReportDao.findOneById(reportId);
        if (report == null){
            return new Object();
        }
        Map resultMap = new HashMap();
        resultMap.put("reportId",reportId);
        resultMap.put("reportStatus",report.getReportStatus());
        //查询月份时间-数据时间
        resultMap.put("dataTime",DateUtil.dateToStringNormal(report.getReportStartTime()));
        resultMap.put("endTime",DateUtil.dateToStringNormal(report.getReportEndTime()));
        resultMap.put("createTime",DateUtil.dateToStringNormal(report.getCreateTime()));
        String createTime = DateUtil.dateToStringNormal(DateUtil.getCurrentTime());
        resultMap.put("createTimeStr",Integer.parseInt(createTime.substring(0, 4)) + "年" + Integer.parseInt(createTime.substring(5, 7)) + "月" + Integer.parseInt(createTime.substring(8, 10)) + "日" + Integer.parseInt(createTime.substring(11, 13)) + "时");

        resultMap.put("reportName",report.getReportName());
        //查时段分区雨量  3个分区的 todo 从这开始
        //获取水情。
        //Map<String,Object> waterInfo = getWaterInfomation(reqDto);
        resultMap.put("rainInfo",report.getDescribeRainInfo());
        resultMap.put("sign",report.getSign());
        resultMap.put("engagement",report.getEngagement());
        resultMap.put("darft",report.getDarft());
        resultMap.put("verification",report.getDarft());
        resultMap.put("cPartId",report.getcPartId());
        List<String> areaIds = new ArrayList<>();
        if ("0".equals(report.getcPartId())){
            areaIds.addAll(stStbprpPartitionDao.findAll().stream().map(StStbprpPartition::getId).collect(Collectors.toList()));
            resultMap.put("cPartName","小清河流域");
        }else {
            areaIds.add(report.getcPartId());
            List<StStbprpPartition> all = stStbprpPartitionDao.findAll();
            Map<String, String> stringMap = all.stream().collect(Collectors.toMap(StStbprpPartition::getId, StStbprpPartition::getPartiTionNM));
            resultMap.put("cPartName",stringMap.get(report.getcPartId()));
        }

       List<YwkReportDataFhfx> ywkReportDataFhfxs =  ywkReportDataFhfxDao.findByCReportId(reportId);
        /**
         *  {
         *                 "avgValue": "5.42",
         *                 "maxzValue": "10.00",
         *                 "maxzTime": "2021-02-01 00:00:00",
         *                 "maxQvalue": "58.50",
         *                 "stcd": "ST_005",
         *                 "jjswValue": "-",
         *                 "maxQTime": "2021-02-08 19:00:00",
         *                 "stnm": "岔河",
         *                 "historyAvgValue": "5.42",
         *                 "minzValue": "1.00"
         *             },
         */
        List<Map>  waterBgZList = new ArrayList<>();
        for (YwkReportDataFhfx fhfx : ywkReportDataFhfxs){
            Map waterBgZMap = new HashMap();
            waterBgZMap.put("avgValue",fhfx.getcAvgZ()==null?"-":fhfx.getcAvgZ());
            waterBgZMap.put("maxzValue",fhfx.getcMaxZ()==null?"-":fhfx.getcMaxZ());
            waterBgZMap.put("maxzTime",fhfx.getcMaxZTime()==null?"-":DateUtil.dateToStringNormal(fhfx.getcMaxZTime()));
            waterBgZMap.put("maxQvalue",fhfx.getcMaxQ()==null?"-":fhfx.getcMaxQ());
            waterBgZMap.put("stcd",fhfx.getcStcd());
            waterBgZMap.put("jjswValue",fhfx.getcWarnZ()==null?"-":fhfx.getcWarnZ());
            waterBgZMap.put("maxQTime",fhfx.getcMaxQTime()==null?"-":DateUtil.dateToStringNormal(fhfx.getcMaxQTime()));
            waterBgZMap.put("stnm",fhfx.getcName());
            waterBgZMap.put("historyAvgValue",fhfx.getcAvgHistoryZ()==null?"-":fhfx.getcAvgHistoryZ());
            waterBgZMap.put("minzValue",fhfx.getcMinZ()==null?"-":fhfx.getcMinZ());
            waterBgZList.add(waterBgZMap);
        }
        resultMap.put("waterBgZList",waterBgZList);

        resultMap.put("waterAfterInfo",report.getDescribeWaterInfo());

        List<YwkReportDataFhfxInfo> fhfxInfos =  ywkReportDataFhfxInfoDao.findByCReportId(reportId);


        List<YwkReportDataFhfxInfo> zqInfos = fhfxInfos.stream().filter( t ->"0".equals(t.getcType())).collect(Collectors.toList());
        List<String> zqInfoStr = new ArrayList<>();
        for (YwkReportDataFhfxInfo zqInfo : zqInfos){
            zqInfoStr.add(zqInfo.getcZqInfo());
        }
        List<YwkReportDataFhfxInfo> waterQuantityInfos = fhfxInfos.stream().filter( t ->"1".equals(t.getcType())).collect(Collectors.toList());
        List<String> wqInfos = new ArrayList<>();

        for (YwkReportDataFhfxInfo wqInfo : waterQuantityInfos){
            wqInfos.add(wqInfo.getcWaterQuantityInfo());
        }
        resultMap.put("waterZList",zqInfoStr);
        resultMap.put("skKuRongStrList",wqInfos);

        List<YwkReportDataFhfxSk> fhfxSks = ywkReportDataFhfxSkDao.findByCReportId(reportId);
        List<Map> skKuRongBgList = new ArrayList<>();
        for (YwkReportDataFhfxSk fhfxSk : fhfxSks){
            Map fffxSkMap = new HashMap();
            fffxSkMap.put("endZ",fhfxSk.getcEndZ()==null ? "-":fhfxSk.getcEndZ());
            fffxSkMap.put("avgValue",fhfxSk.getcWaterHistoryQuanTity() ==null?"-":fhfxSk.getcWaterHistoryQuanTity());
            fffxSkMap.put("stcd",fhfxSk.getcStcd());
            fffxSkMap.put("endValue",fhfxSk.getcEndQuanTity()==null?"-":fhfxSk.getcEndQuanTity());
            fffxSkMap.put("startZ",fhfxSk.getcStartZ()==null?"-":fhfxSk.getcStartZ());
            fffxSkMap.put("avgZ",fhfxSk.getcAvgHistoryZ()==null?"-":fhfxSk.getcAvgHistoryZ());
            fffxSkMap.put("stnm",fhfxSk.getcStnm());
            fffxSkMap.put("diffValue",fhfxSk.getcDiffQuantity()==null?"-":fhfxSk.getcDiffQuantity());
            fffxSkMap.put("startTime",fhfxSk.getcStartTime()==null?"-":DateUtil.dateToStringNormal(fhfxSk.getcStartTime()));
            fffxSkMap.put("startValue",fhfxSk.getcStartQuanTity()==null?"-":fhfxSk.getcStartQuanTity());
            fffxSkMap.put("endTime",fhfxSk.getcEndTime()==null?"-":DateUtil.dateToStringNormal(fhfxSk.getcEndTime()));
            skKuRongBgList.add(fffxSkMap);
        }

        resultMap.put("skKuRongBgList",skKuRongBgList);

        List<String> sttps = new ArrayList<>();
        sttps.add("RR");//水库

        //筛选后的河道站监测站
        List<StStbprpB> allSTBB = stStbprpBDao.findByAreaIdAndSttp(areaIds, sttps);//todo 后面再改 findByAreaIdAndSttp(areaIds, sttps);
        List<Map> shuiKuDDgz = new ArrayList<>();
        for (StStbprpB stStbprpB : allSTBB){
            Map<String,String> skDdMap = new HashMap<>();
            skDdMap.put("stcd",stStbprpB.getStcd());
            skDdMap.put("stnm",stStbprpB.getStnm());
            skDdMap.put("ddgz",SK_DD.get(stStbprpB.getStcd()));
            shuiKuDDgz.add(skDdMap);
        }
        resultMap.put("skDdgz",shuiKuDDgz);
        return resultMap;
    }


    @Override
    public Object getWaterRegimenMessage(Map mapVo) {
        Date startTime = DateUtil.getDateByStringNormal(mapVo.get("startTime")+"");
        Date endTime = DateUtil.getDateByStringNormal(mapVo.get("endTime")+"");

        WaterRegimenMessageDto waterRegimenMessageDto = new WaterRegimenMessageDto();

        int waterLevelHistoryHD = 0;
        int waterLevelGuaranteeHD = 0;
        int waterLevelWarningHD = 0;
        int waterLevelHistoryCZ = 0;
        int waterLevelGuaranteeCZ = 0;
        int waterLevelWarningCZ = 0;
        int waterLevelLineSK = 0;
        //测站编码表
        List<StStbprpB> stStbprpBS = stStbprpBDao.findAll();
        //河道站防洪指标表
        List<TRvfcchB> tRvfcchBS = tRvfcchBDao.findAll();
        //库（湖）站汛限水位表 查询主汛期为1的
        List<TRsvrfsrB> tRsvrfsrBS = tRsvrfsrBDao.findByFstp("1");
        //获取河道个数
        List<StStbprpB> collectHD = stStbprpBS.stream().filter(t -> "ZQ".equals(t.getSttp()) || "ZZ".equals(t.getSttp())).collect(Collectors.toList());
        //获取河道站，堰闸站，潮汐站警戒信息
        Map<String, TRvfcchB> collectWarningHD = tRvfcchBS.stream().collect(Collectors.toMap(TRvfcchB::getStcd, Function.identity()));
        //获取水库警戒信息
        Map<String, TRsvrfsrB> collectWarningSK = tRsvrfsrBS.stream().collect(Collectors.toMap(TRsvrfsrB::getStcd, Function.identity()));
        //获取河道水情信息
        List<Map<String, Object>> riverRLastData = tRiverRDao.getWaterLevelMaxByTime(startTime, endTime);//tRiverRDao.getRiverRLastData();//tRiverRDao.getRiverRLastData();

            for (Map<String, Object> map : riverRLastData) {
                try {
                    String stcd = map.get("stcd").toString();
                    //水位
                    double z = Double.parseDouble(map.get("z").toString());
                    TRvfcchB tRvfcchStandard = collectWarningHD.get(stcd);
                    if (tRvfcchStandard == null) {
                        continue;
                    }
                    //警戒水位
                    double wrz = Double.parseDouble(tRvfcchStandard.getWrz() == null ? "0" : tRvfcchStandard.getWrz());
                    //保证水位
                    double grz = Double.parseDouble(tRvfcchStandard.getGrz() == null ? "0" : tRvfcchStandard.getGrz());
                    //历史最高水位
                    double obhtz = Double.parseDouble(tRvfcchStandard.getObhtz() == null ? "0" : tRvfcchStandard.getObhtz());
                    if (obhtz < z) {
                        waterLevelHistoryHD++;
                    }
                    if (wrz < z) {
                        waterLevelWarningHD++;
                    }
                    if (grz < z) {
                        waterLevelGuaranteeHD++;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            //获取水库个数
            List<StStbprpB> collectSK = stStbprpBS.stream().filter(t -> "RR".equals(t.getSttp())).collect(Collectors.toList());
            //获取水库数据最新一条记录
            List<Map<String, Object>> rsvrLastData = tRsvrRDao.getWaterLevelMaxByTime(startTime, endTime);//tRsvrRDao.getRsvrLastData();
            for (Map<String, Object> map : rsvrLastData) {
                try {
                    String stcd = map.get("stcd").toString();
                    //水位
                    double z = Double.parseDouble(map.get("RZ").toString());
                    TRsvrfsrB tRsvrfsrB = collectWarningSK.get(stcd);
                    if (tRsvrfsrB == null) {
                        continue;
                    }
                    //汛险水位
                    double fsltdz = Double.parseDouble(tRsvrfsrB.getFsltdz() == null ? "0" : tRsvrfsrB.getFsltdz());
                    if (fsltdz < z) {
                        waterLevelLineSK++;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            //闸坝和潮汐个数
        List<StStbprpB> collectCZ = stStbprpBS.stream().filter(t -> "TT".equals(t.getSttp()) || "DD".equals(t.getSttp())).collect(Collectors.toList());
        List<Map<String, Object>> lastData = tWasRDao.getWaterLevelMaxByTime(startTime, endTime);//.getLastData();
        for (Map<String, Object> map : lastData) {
            try {
                String stcd = map.get("stcd").toString();
                //水位
                double z = Double.parseDouble(map.get("UPZ").toString());
                TRvfcchB tRvfcchStandard = collectWarningHD.get(stcd);
                if(tRvfcchStandard == null){
                    continue;
                }
                //警戒水位
                double wrz = Double.parseDouble(tRvfcchStandard.getWrz()==null?"0":tRvfcchStandard.getWrz());
                //保证水位
                double grz = Double.parseDouble(tRvfcchStandard.getGrz()==null?"0":tRvfcchStandard.getGrz());
                //历史最高水位
                double obhtz = Double.parseDouble(tRvfcchStandard.getObhtz()==null?"0":tRvfcchStandard.getObhtz());
                if(obhtz<z){
                    waterLevelHistoryCZ++;
                }
                if(wrz<z){
                    waterLevelWarningCZ++;
                }
                if(grz<z){
                    waterLevelGuaranteeCZ++;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        //潮汐
        List<Map<String, Object>> lastCData = tTideRDao.getWaterLevelMaxByTime(startTime, endTime);//.getLastData();//tTideRDao.getLastData();
        for (Map<String, Object> map : lastCData) {
            try {
                String stcd = map.get("stcd").toString();
                //水位
                double z = Double.parseDouble(map.get("TDZ").toString());
                TRvfcchB tRvfcchStandard = collectWarningHD.get(stcd);
                //警戒水位
                double wrz = Double.parseDouble(tRvfcchStandard.getWrz()==null?"0":tRvfcchStandard.getWrz());
                //保证水位
                double grz = Double.parseDouble(tRvfcchStandard.getGrz()==null?"0":tRvfcchStandard.getGrz());
                //历史最高水位
                double obhtz = Double.parseDouble(tRvfcchStandard.getObhtz()==null?"0":tRvfcchStandard.getObhtz());
                if(obhtz<z){
                    waterLevelHistoryCZ++;
                }
                if(wrz<z){
                    waterLevelWarningCZ++;
                }
                if(grz<z){
                    waterLevelGuaranteeCZ++;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        waterRegimenMessageDto.setCountHD(collectHD.size());
        waterRegimenMessageDto.setWaterLevelHistoryHD(waterLevelHistoryHD);
        waterRegimenMessageDto.setWaterLevelGuaranteeHD(waterLevelGuaranteeHD);
        waterRegimenMessageDto.setWaterLevelWarningHD(waterLevelWarningHD);
        waterRegimenMessageDto.setCountSK(collectSK.size());
        waterRegimenMessageDto.setWaterLevelLineSK(waterLevelLineSK);
        waterRegimenMessageDto.setCountCZ(collectCZ.size());
        waterRegimenMessageDto.setWaterLevelHistoryCZ(waterLevelHistoryCZ);
        waterRegimenMessageDto.setWaterLevelWarningCZ(waterLevelWarningCZ);
        waterRegimenMessageDto.setWaterLevelGuaranteeCZ(waterLevelGuaranteeCZ);

        return waterRegimenMessageDto;
    }


    @Override
    public Object geRiverWayDataOnTime(Map mapVo) {

        Date startTime = DateUtil.getDateByStringNormal(mapVo.get("startTime")+"");
        Date endTime = DateUtil.getDateByStringNormal(mapVo.get("endTime")+"");
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");
        String startStr = format.format(startTime);
        String endStr = format.format(endTime);
        DecimalFormat df = new DecimalFormat("0.00");
        List<Map<String,Object>> results = new ArrayList<>();
        //测站编码表
        List<StStbprpB> stStbprpBS = stStbprpBDao.findAll();
        //河道站防洪指标表
        List<TRvfcchB> tRvfcchBS = tRvfcchBDao.findAll();
        //获取河道站个数
        List<StStbprpB> collectHD = stStbprpBS.stream().filter(t -> "ZQ".equals(t.getSttp()) || "ZZ".equals(t.getSttp())).collect(Collectors.toList());
        //获取河道水情信息
        List<Map<String, Object>> riverRLastData = tRiverRDao.getAvgValueByTimeNew(startTime,endTime);
        List<Map<String, Object>> riverRLastHistoryData = tRiverRDao.getHistoryAvgValueByTimeNew(startStr,endStr);
        Map<String, Map<String, Object>> riverWayDataMap = riverRLastData.stream().collect(Collectors.toMap(t -> t.get("stcd").toString(), Function.identity()));
        Map<String, Map<String, Object>> riverWayDataHistoryMap = riverRLastHistoryData.stream().collect(Collectors.toMap(t -> t.get("stcd").toString(), Function.identity()));

        //获取河道站，堰闸站，潮汐站警戒信息
        Map<String, TRvfcchB> collectWarningHD = tRvfcchBS.stream().collect(Collectors.toMap(TRvfcchB::getStcd, Function.identity()));
        for (StStbprpB stStbprpB : collectHD) {
            Map avgMap = new HashMap();
            try {
                String historyWaterLevel = null;
                String historyFlow = null;
                RiverWayDataDto dataDto = new RiverWayDataDto();
                BeanUtils.copyProperties(stStbprpB,dataDto);
                String stcd = stStbprpB.getStcd();
                Map<String, Object> map = riverWayDataMap.get(stcd);
                Map<String, Object> historyMap = riverWayDataHistoryMap.get(stcd);
                if(map!=null && map.get("z") != null){
                    dataDto.setWaterLevel(Double.parseDouble(df.format(Double.parseDouble(map.get("z").toString()))));
                }
                if(map!=null && map.get("q") != null){
                    dataDto.setFlow(Double.parseDouble(df.format(Double.parseDouble(map.get("q").toString()))));
                }
                if (historyMap != null){
                    if (historyMap.get("z") != null){
                        historyWaterLevel = df.format(Double.parseDouble(historyMap.get("z").toString()));
                    }
                    if (historyMap.get("q") != null){
                        historyFlow = df.format(Double.parseDouble(historyMap.get("q").toString()));
                    }
                }
                avgMap.put("historyWaterLevel",historyWaterLevel);
                avgMap.put("historyFlow",historyFlow);

                TRvfcchB tRvfcchStandard = collectWarningHD.get(stcd);
                if(tRvfcchStandard != null && tRvfcchStandard.getWrz() != null){
                    dataDto.setWarningWaterLevel(Double.parseDouble(tRvfcchStandard.getWrz()));

                    //警戒水位
                    double wrz = Double.parseDouble(tRvfcchStandard.getWrz()==null?"0":tRvfcchStandard.getWrz());
                    //保证水位
                    double grz = Double.parseDouble(tRvfcchStandard.getGrz()==null?"0":tRvfcchStandard.getGrz());
                    //历史最高水位
                    double obhtz = Double.parseDouble(tRvfcchStandard.getObhtz()==null?"0":tRvfcchStandard.getObhtz());

                    for (Map<String, Object> riverRLastDatum : riverRLastData) {
                        if(riverRLastDatum.get("stcd") == tRvfcchStandard.getStcd()){

                            if(wrz < Double.parseDouble((String) riverRLastDatum.get("z"))){
                                dataDto.setIsThanWaterLevelWarning(1);
                            }

                            if(grz < Double.parseDouble((String) riverRLastDatum.get("z"))){
                                dataDto.setIsThanWaterLevelGuarantee(1);
                            }

                            if(obhtz < Double.parseDouble((String) riverRLastDatum.get("z"))){
                                dataDto.setIsThanWaterLevelHistory(1);
                            }

                            break;
                        }
                    }

                }
                Map result = JSON.parseObject(JSON.toJSONString(dataDto, SerializerFeature.WriteMapNullValue), Map.class);
                result.putAll(avgMap);
                results.add(result);
            } catch (BeansException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return results;
    }


    @Override
    public Object getReservoirDataOnTime(Map mapVo) {
        Date startTime = DateUtil.getDateByStringNormal(mapVo.get("startTime")+"");
        Date endTime = DateUtil.getDateByStringNormal(mapVo.get("endTime")+"");
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");
        String startStr = format.format(startTime);
        String endStr = format.format(endTime);
        DecimalFormat df = new DecimalFormat("0.00");
        List<Map<String,Object>> results = new ArrayList<>();
        //获取水库水文站个数
        List<StStbprpB> stStbprpBS = stStbprpBDao.findBySttp("RR");
        //库（湖）站汛限水位表 查询主汛期为1的
        List<TRsvrfsrB> tRsvrfsrBS = tRsvrfsrBDao.findByFstp("1");
        //获取水库警戒信息
        Map<String, TRsvrfsrB> collectWarningSK = tRsvrfsrBS.stream().collect(Collectors.toMap(TRsvrfsrB::getStcd, Function.identity()));
        List<Map<String, Object>> rsvrLastData = tRsvrRDao.getAvgValueByTimeNew(startTime,endTime);
        List<Map<String, Object>> rsvrLastHistroyData = tRsvrRDao.getHistoryAvgValueByTimeNew(startStr,endStr);
        Map<String, Map<String, Object>> rsvrLastDataMap = rsvrLastData.stream().collect(Collectors.toMap(t -> t.get("stcd").toString(), Function.identity()));
        Map<String, Map<String, Object>> rsvrLastDataHistoryMap = rsvrLastHistroyData.stream().collect(Collectors.toMap(t -> t.get("stcd").toString(), Function.identity()));

        for (StStbprpB stStbprpB : stStbprpBS) {

            try {
                Map newMap = new HashMap();
                ReservoirDataDto dataDto = new ReservoirDataDto();
                BeanUtils.copyProperties(stStbprpB,dataDto);
                String stcd = stStbprpB.getStcd();
                Map<String, Object> map = rsvrLastDataMap.get(stcd);
                Map<String, Object> historyMap = rsvrLastDataHistoryMap.get(stcd);
                String flow = null;
                String historyWaterLevel = null;
                String historyFlow = null;
                if(map!=null && map.get("RZ") != null){
                    dataDto.setWaterLevel(Double.parseDouble(df.format(Double.parseDouble(map.get("RZ").toString()))));
                }
                if(map!=null && map.get("INQ") != null){
                    flow = df.format(Double.parseDouble(map.get("INQ").toString()));
                }
                newMap.put("flow",flow);

                if (historyMap != null ){
                    if (historyMap.get("RZ") != null){
                        historyWaterLevel = df.format(Double.parseDouble(historyMap.get("RZ").toString()));
                    }
                    if (historyMap.get("INQ") != null){
                        historyFlow = df.format(Double.parseDouble(historyMap.get("INQ").toString()));
                    }
                }
                newMap.put("historyWaterLevel",historyWaterLevel);
                newMap.put("historyFlow",historyFlow);
                BigDecimal avgWaterLevel = dataDto.getWaterLevel() == null? new BigDecimal("0"):new BigDecimal(dataDto.getWaterLevel()+"");
                BigDecimal avgValue = null;
                switch (stcd){

                    case "RSR_001"://y = 2.6198x2 - 867.14x + 71775
                        avgValue = new BigDecimal("2.6198").multiply(avgWaterLevel.pow(2))
                                .subtract(new BigDecimal("867.14").multiply(avgWaterLevel)).add(new BigDecimal("71775")).setScale(2,BigDecimal.ROUND_HALF_UP);
                        break;
                    case "RSR_002"://y = 8.9233x2 - 1206.3x + 40828

                        avgValue = new BigDecimal("8.9233").multiply(avgWaterLevel.pow(2))
                                .subtract(new BigDecimal("1206.3").multiply(avgWaterLevel)).add(new BigDecimal("40828")).setScale(2,BigDecimal.ROUND_HALF_UP);
                        break;
                    case "RSR_003"://y = 2.8675x2 - 142.23x + 1322.8
                        avgValue = new BigDecimal("2.8675").multiply(avgWaterLevel.pow(2))
                                .subtract(new BigDecimal("142.23").multiply(avgWaterLevel)).add(new BigDecimal("1322.8")).setScale(2,BigDecimal.ROUND_HALF_UP);

                        break;

                    case "RSR_004"://y = 1.3855x2 - 764.01x + 105316

                        avgValue = new BigDecimal("1.3855").multiply(avgWaterLevel.pow(2))
                                .subtract(new BigDecimal("764.01").multiply(avgWaterLevel)).add(new BigDecimal("105316")).setScale(2,BigDecimal.ROUND_HALF_UP);
                        break;
                    case "RSR_005"://y = 5.497x2 - 1020.7x + 47394
                        avgValue = new BigDecimal("5.497").multiply(avgWaterLevel.pow(2))
                                .subtract(new BigDecimal("1020.7").multiply(avgWaterLevel)).add(new BigDecimal("47394")).setScale(2,BigDecimal.ROUND_HALF_UP);
                        break;
                    case "RSR_006"://y = 1.5041x2 - 896.85x + 133839

                        avgValue = new BigDecimal("1.5041").multiply(avgWaterLevel.pow(2))
                                .subtract(new BigDecimal("896.85").multiply(avgWaterLevel)).add(new BigDecimal("133839")).setScale(2,BigDecimal.ROUND_HALF_UP);
                        break;
                    case "RSR_007"://y = 8.6731x2 - 5857.2x + 988887

                        avgValue = new BigDecimal("8.6731").multiply(avgWaterLevel.pow(2))
                                .subtract(new BigDecimal("5857.2").multiply(avgWaterLevel)).add(new BigDecimal("988887")).setScale(2,BigDecimal.ROUND_HALF_UP);

                        break;
                    case "RSR_008"://y = 9.8014x2 - 3900x + 388112

                        avgValue = new BigDecimal("9.8014").multiply(avgWaterLevel.pow(2))
                                .subtract(new BigDecimal("3900").multiply(avgWaterLevel)).add(new BigDecimal("388112")).setScale(2,BigDecimal.ROUND_HALF_UP);

                        break;
                    case "RSR_009"://y = 13.174x2 - 1685.4x + 53907  //得到的是万

                        avgValue = new BigDecimal("13.174").multiply(avgWaterLevel.pow(2))
                                .subtract(new BigDecimal("1685.4").multiply(avgWaterLevel)).add(new BigDecimal("53907")).setScale(2,BigDecimal.ROUND_HALF_UP);
                        break;
                    default:
                }//break方法
                dataDto.setWaterStorage(avgValue.doubleValue());
                TRsvrfsrB tRsvrfsrB = collectWarningSK.get(stcd);
                if(tRsvrfsrB != null && tRsvrfsrB.getFsltdz() != null){
                    dataDto.setWaterLevelLine(Double.parseDouble(tRsvrfsrB.getFsltdz()));

                    //汛险水位
                    double fsltdz = Double.parseDouble(tRsvrfsrB.getFsltdz()==null?"0":tRsvrfsrB.getFsltdz());

                    for (Map<String, Object> rsvrLastDatum : rsvrLastData) {
                        if(rsvrLastDatum.get("stcd") == tRsvrfsrB.getStcd()) {
                            if (fsltdz < Double.parseDouble((String) rsvrLastDatum.get("RZ"))) {
                                dataDto.setIsThanWaterLevelLine(1);
                            }
                            break;
                        }
                    }

                }
                Map result = JSON.parseObject(JSON.toJSONString(dataDto, SerializerFeature.WriteMapNullValue), Map.class);
                result.putAll(newMap);
                results.add(result);
            } catch (BeansException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return results;
    }


    @Override
    public Object getRainDistributionList(QueryParamDto dto) {
        Date startTime = dto.getStartTime();
        Date endTime = dto.getEndTime();
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm:ss");
        String startStr = format.format(startTime);
        String endStr = format.format(endTime);
        DecimalFormat df = new DecimalFormat("0.00");
        List<Map<String, Object>> rainDistributionList = stStbprpBDao.getRainDistributionList(startTime,endTime, dto.getName());
        List<Map<String, Object>> historyRainDistributionList = stStbprpPartRelateDao.getBeforeHistoryRainByTime(startStr,endStr, dto.getName());
        Map<Object, Map<String, Object>> historyMap = historyRainDistributionList.stream().collect(Collectors.toMap(t -> t.get("STCD"), Function.identity()));
        List<Map<String, Object>> list = new ArrayList<>();

        //Oracle默认大写改为小写返回
        for (Map<String, Object> map : rainDistributionList) {
            Map<String, Object> hashMap = new HashMap<>();
            hashMap.put("stcd", map.get("STCD"));
            Map<String, Object> historyM = historyMap.get(map.get("STCD"));
            String  totalHistory = null;
            if (historyM != null && historyM.get("total") != null){
                totalHistory = df.format(Double.parseDouble(historyM.get("total")+""));
            }
            hashMap.put("historyTotal",totalHistory);
            hashMap.put("stnm", map.get("STNM"));
            hashMap.put("rvnm", map.get("RVNM"));
            hashMap.put("total", map.get("TOTAL"));
            hashMap.put("lgtd", map.get("LGTD"));
            hashMap.put("lttd", map.get("LTTD"));
            list.add(hashMap);
        }
        return list;
    }
}
