package com.essence.business.xqh.service.waterandrain;

import com.essence.business.xqh.api.rainfall.vo.RainPartitionDataDto;
import com.essence.business.xqh.api.rainfall.vo.RainPartitionDto;
import com.essence.business.xqh.api.rainfall.vo.RainWaterReportDto;
import com.essence.business.xqh.api.waterandrain.service.AnalysisOfFloodService;
import com.essence.business.xqh.api.waterandrain.service.RainPartitionService;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpPartitionDao;
import com.essence.business.xqh.dao.dao.fhybdd.YwkRainWaterReportDao;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpPartition;
import com.essence.business.xqh.dao.entity.fhybdd.YwkRainWaterReport;
import com.essence.framework.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
    @Override
    public Object getRainWaterCommonReport(RainPartitionDto reqDto) {
        RainWaterReportDto rainWaterReportDto = new RainWaterReportDto();
        DecimalFormat df = new DecimalFormat("0.00");
        //查询月份时间-数据时间
        Date startTime = reqDto.getStartTime();
        Date endTime = reqDto.getEndTime();
        rainWaterReportDto.setDataTime(startTime);
        rainWaterReportDto.setEndTime(endTime);
        //简报生成时间
        Date createTime = DateUtil.getCurrentTime();
        rainWaterReportDto.setCreateTime(createTime);
        String createTstr = DateUtil.dateToStringNormal(createTime);
        //年份
        int year = Integer.parseInt(createTstr.substring(0, 4));
        rainWaterReportDto.setCreateTimeStr(year + "年" + createTstr.substring(5, 7) + "月" + createTstr.substring(8, 10) + "日" + createTstr.substring(11, 13) + "时");
        //简报名称
        String startTimeStr = DateUtil.dateToStringNormal(startTime);
        String endTimeStr = DateUtil.dateToStringNormal(endTime);
        //数据年份
        int reportYear = Integer.parseInt(startTimeStr.substring(0, 4));
        rainWaterReportDto.setReportName(reqDto.getAreaName() + reportYear + "年" + startTimeStr.substring(5, 7) + "月水雨情综述");
        //数据年份
        rainWaterReportDto.setYear(reportYear);
        //第几期
        List<YwkRainWaterReport> reportsList = ywkRainWaterReportDao.findByYearAndReportTypeOrderBySerialNumberDesc(reportYear, "1");
        if (reportsList == null || reportsList.size() == 0) {
            rainWaterReportDto.setSerialNumber(1);
        } else {
            rainWaterReportDto.setSerialNumber(reportsList.get(0).getSerialNumber() + 1);
        }
        //查时段分区雨量  3个分区的
        List<RainPartitionDataDto> rainMonthList = rainPartitionService.getPartRain(new RainPartitionDto(startTime, endTime, "4"), true);
        String areaId = reqDto.getAreaId();
        String areaName = reqDto.getAreaName();
        if (!"0".equals(areaId)){
            Map<String, RainPartitionDataDto> rainPartitionDataDtoMap = rainMonthList.stream().collect(Collectors.toMap(RainPartitionDataDto::getPartName, Function.identity()));
            List<RainPartitionDataDto> list = new ArrayList<RainPartitionDataDto>();
            list.add(rainPartitionDataDtoMap.get(areaName));
            rainMonthList = list;
        }

        Double avgRain = 0.0;
        for (RainPartitionDataDto rainDto : rainMonthList) {
            avgRain += rainDto.getPartDrp();
        }
        try {
            avgRain = avgRain / rainMonthList.size();
        } catch (Exception e) {
        }
        //查询去年同期时段分区雨量
        List<RainPartitionDataDto> rainLastMonthList = rainPartitionService.getPartRain(new RainPartitionDto(DateUtil.getNextYear(startTime, -1), DateUtil.getNextYear(endTime, -1), "4"), false);
        if (!"0".equals(areaId)){
            Map<String, RainPartitionDataDto> rainPartitionDataDtoMap = rainLastMonthList.stream().collect(Collectors.toMap(RainPartitionDataDto::getPartName, Function.identity()));
            List<RainPartitionDataDto> list = new ArrayList<RainPartitionDataDto>();
            list.add(rainPartitionDataDtoMap.get(areaName));
            rainLastMonthList = list;
        }
        Double lastAvgRain = 0.0;
        for (RainPartitionDataDto rainDto : rainLastMonthList) {
            lastAvgRain += rainDto.getPartDrp();
        }
        try {
            lastAvgRain = lastAvgRain / rainLastMonthList.size();
        } catch (Exception e) {
        }
        Double thisLast = avgRain - lastAvgRain;
        String thisLastStr = thisLast > 0 ? "增加" : "减少";

        //起始时间月日时
        Integer startMonth = Integer.parseInt(startTimeStr.substring(5, 7));
        Integer startDay = Integer.parseInt(startTimeStr.substring(8, 10));
        Integer startHour = Integer.parseInt(startTimeStr.substring(11, 13));
        //起始时间月日时
        Integer endMonth = Integer.parseInt(endTimeStr.substring(5, 7));
        Integer endtDay = Integer.parseInt(endTimeStr.substring(8, 10));
        Integer endHour = Integer.parseInt(endTimeStr.substring(11, 13));
        //降雨量折合成水量
        Double rainToWater = (avgRain * XQH_AREA) / 1000 / 10000;
        //结束时间月日时
        String rainStr = startMonth + "月" + startDay + "日" + startHour + "时至" + endMonth + "月" + endtDay + "日" + endHour + "时，小清河流域面平均降水量";
        if (lastAvgRain == 0.0) {
            rainStr += df.format(avgRain) + "mm,折合水量" + df.format(rainToWater) + "万立方米,上一年同期无降水。";
        } else {
            rainStr += df.format(avgRain) + "mm,折合水量" + df.format(rainToWater) + "万立方米,上一年同期降水量为" + df.format(lastAvgRain) + "，同比" + thisLastStr + df.format(thisLast) + "mm。";
        }
        rainStr += "各流域分区降水量为：";
        for (RainPartitionDataDto rainDto : rainMonthList) {
            rainStr += rainDto.getPartName() + df.format(rainDto.getPartDrp()) + "mm,";
            if (rainDto.getMaxStnm() != null) {
                rainStr += "分区最大雨量站为：" + rainDto.getMaxStnm() + df.format(rainDto.getMaxDrp()) + "mm,";
            }
        }
        //今年雨量
        Date thisYearStartTime = DateUtil.getNextHour(DateUtil.getThisYear(), 8);
        Date thisYearEndTime = DateUtil.getCurrentTime();
        List<RainPartitionDataDto> thisYearList = rainPartitionService.getPartRain(new RainPartitionDto(thisYearStartTime, thisYearEndTime, "4"), true);
        if (!"0".equals(areaId)){
            Map<String, RainPartitionDataDto> rainPartitionDataDtoMap = thisYearList.stream().collect(Collectors.toMap(RainPartitionDataDto::getPartName, Function.identity()));
            List<RainPartitionDataDto> list = new ArrayList<RainPartitionDataDto>();
            list.add(rainPartitionDataDtoMap.get(areaName));
            thisYearList = list;
        }
        Double avgThisYearRain = 0.0;
        for (RainPartitionDataDto rainDto : thisYearList) {
            avgThisYearRain += rainDto.getPartDrp();
        }
        try {
            avgThisYearRain = avgThisYearRain / thisYearList.size();
        } catch (Exception e) {
        }
        rainStr += "今年以来（1月1日8时至" + endMonth + "月" + endtDay + "日" + endHour + "时）全流域平均降水量" + avgThisYearRain + "mm。";
        //如果汛期
        Date xqStartTm = DateUtil.getDateByStringNormal(reportYear + "-06-01 00:00:00");
        Date xqEndTm = DateUtil.getDateByStringNormal(reportYear + "-09-15 00:00:00");
        if (endTime.after(xqStartTm) && endTime.before(xqEndTm)) {
            //查询汛期雨量
            List<RainPartitionDataDto> xqDataList = rainPartitionService.getPartRain(new RainPartitionDto(xqStartTm, endTime, "4"), true);
            if (!"0".equals(areaId)){
                Map<String, RainPartitionDataDto> rainPartitionDataDtoMap = xqDataList.stream().collect(Collectors.toMap(RainPartitionDataDto::getPartName, Function.identity()));
                List<RainPartitionDataDto> list = new ArrayList<RainPartitionDataDto>();
                list.add(rainPartitionDataDtoMap.get(areaName));
                xqDataList = list;
            }
            Double xqRain = 0.0;
            for (RainPartitionDataDto rainDto : xqDataList) {
                xqRain += rainDto.getPartDrp();
            }
            try {
                xqRain = xqRain / xqDataList.size();
            } catch (Exception e) {
            }
            rainStr += "入汛以来（6月1日8时至" + endMonth + "月" + endtDay + "日" + endHour + "时）全流域平均降水量" + xqRain + "mm。";
        }
        rainWaterReportDto.setRainInfo(rainStr);
        //水情
        String waterInfo = startMonth + "月" + startDay + "日" + startHour + "时至" + endMonth + "月" + endtDay + "日" + endHour + "时，小清河流域";
        waterInfo = rainPartitionService.getWaterRegimenMessage(waterInfo, startTime, DateUtil.getNextMillis(endTime, -1));
        rainWaterReportDto.setWaterInfo(waterInfo);
        rainWaterReportDto.setSign(XQH_SIGN);
        rainWaterReportDto.setEngagement(XQH_ENGAGEMENT);
        rainWaterReportDto.setDarft(XQH_DARFT);
        rainWaterReportDto.setVerification(XQH_VERIFICATION);
        return rainWaterReportDto;
    }


    @Override
    public Object getAreaList() {

        List<StStbprpPartition> all = stStbprpPartitionDao.findAll();
        return all;
    }
}
