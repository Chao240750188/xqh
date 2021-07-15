package com.essence.business.xqh.service.hsfxtk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;
import com.essence.business.xqh.api.fhybdd.dto.ModelProperties;
import com.essence.business.xqh.api.fhybdd.service.ModelCallHandleDataService;
import com.essence.business.xqh.api.hsfxtk.ProjectJointDispatchService;
import com.essence.business.xqh.api.hsfxtk.dto.*;
import com.essence.business.xqh.api.modelResult.PlanProcessDataService;
import com.essence.business.xqh.common.util.*;
import com.essence.business.xqh.dao.dao.fhybdd.*;
import com.essence.business.xqh.dao.dao.hsfxtk.*;
import com.essence.business.xqh.dao.entity.fhybdd.*;
import com.essence.business.xqh.dao.entity.hsfxtk.*;
import com.essence.euauth.common.util.UuidUtil;
import com.essence.framework.jpa.Criterion;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import com.essence.framework.util.StrUtil;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProjectJointDispatchServiceImpl implements ProjectJointDispatchService {
    @Autowired
    YwkPlaninfoDao ywkPlaninfoDao;//方案基本信息

    @Autowired
    StPptnRDao stPptnRDao; //雨量数据表

    @Autowired
    StStbprpBDao stStbprpBDao;//监测站

    @Autowired
    ModelProperties modelProperties;

    @Autowired
    YwkModelDao ywkModelDao;

    @Autowired
    WrpRcsBsinDao wrpRcsBsinDao;//断面基本信息

    @Autowired
    YwkPlanTriggerRcsDao ywkPlanTriggerRcsDao;//预报断面

    @Autowired
    YwkPlanTriggerRcsFlowDao ywkPlanTriggerRcsFlowDao;//预报断面流量

    @Autowired
    WrpRvrBsinDao wrpRvrBsinDao;//河系

    @Autowired
    YwkPlaninRainfallDao ywkPlaninRainfallDao;//方案雨量

    @Autowired
    YwkPlanCalibrationZoneXggxDao ywkPlanCalibrationZoneXggxDao;//相关关系

    @Autowired
    WrpRiverZoneDao wrpRiverZoneDao;

    @Autowired
    YwkPlanCalibrationZoneDao ywkPlanCalibrationZoneDao;

    @Autowired
    YwkPlanCalibrationDwxDao ywkPlanCalibrationDwxDao;

    @Autowired
    YwkPlanOutputQDao ywkPlanOutputQDao;

    @Autowired
    ModelCallHandleDataService modelCallHandleDataService;

    @Autowired
    YwkPlanCalibrationZoneXajDao ywkPlanCalibrationZoneXajDao;

    @Autowired
    YwkModelBoundaryBasicRlDao ywkModelBoundaryBasicRlDao;

    @Autowired
    YwkBoundaryBasicDao ywkBoundaryBasicDao;

    @Autowired
    YwkPlaninFloodBoundaryDao ywkPlaninFloodBoundaryDao;

    @Autowired
    YwkPlaninRiverRoughnessDao ywkPlaninRiverRoughnessDao;

    @Autowired
    YwkRiverRoughnessParamDao ywkRiverRoughnessParamDao;

    @Autowired
    YwkPlaninFloodRoughnessDao ywkPlaninFloodRoughnessDao;

    @Autowired
    YwkModelRoughnessParamDao ywkModelRoughnessParamDao;

    @Autowired
    YwkPlaninFloodBreakDao ywkPlaninFloodBreakDao;

    @Autowired
    YwkBreakBasicDao ywkBreakBasicDao;

    @Autowired
    YwkFloodChannelFlowDao ywkFloodChannelFlowDao;

    @Autowired
    PlanProcessDataService planProcessDataService;

    @Autowired
    YwkFloodChannelBasicDao ywkFloodChannelBasicDao;

    /**
     * 根据方案名称校验方案是否存在
     *
     * @param planName
     */
    @Override
    public Integer getPlanInfoByName(String planName) {
        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("XT_LHDD");
        List<YwkPlaninfo> byCPlanname = ywkPlaninfoDao.findByCPlannameAndPlanSystem(planName, planSystem);
        return byCPlanname.size();
    }

    /**
     * 根据方案id获取方案信息，并加入缓存
     *
     * @param planId
     * @return
     */
    @Override
    public YwkPlaninfo getPlanInfoByPlanId(String planId) {
        YwkPlaninfo planinfo = (YwkPlaninfo) CacheUtil.get("planInfo", planId);
        if (planinfo == null) {
            planinfo = ywkPlaninfoDao.findOneById(planId);
            if (planinfo != null) {
                CacheUtil.saveOrUpdate("planInfo", planId, planinfo);
            }
        }
        return planinfo;
    }

    @Override
    public String savePlan(ModelCallBySWDDVo vo) {
        //工程联合调度默认调用小清河流域
        vo.setRvcd("RVR_011");
        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("XT_LHDD");
        List<YwkPlaninfo> isAll = ywkPlaninfoDao.findByCPlannameAndPlanSystem(vo.getcPlanname(), planSystem);
        if (!CollectionUtils.isEmpty(isAll)) {
            return "isExist";
        }
        Date startTime = vo.getStartTime(); //开始时间
        Date endTIme = vo.getEndTime();  //结束时间
        Date periodEndTime = vo.getPeriodEndTime();//预见结束时间
        if (periodEndTime != null) {
            endTIme = periodEndTime;
        }
        int step = vo.getStep();//以小时为单位
        int timeType = vo.getTimeType();
        //方案基本信息入库
        YwkPlaninfo ywkPlaninfo = new YwkPlaninfo();
        ywkPlaninfo.setnPlanid(UuidUtil.get32UUIDStr());
        ywkPlaninfo.setPlanSystem(planSystem);
        ywkPlaninfo.setcPlanname(vo.getcPlanname());
        ywkPlaninfo.setnCreateuser("user");
        ywkPlaninfo.setnPlancurrenttime(new Date());
        ywkPlaninfo.setdCaculatestarttm(startTime);//方案计算开始时间
        ywkPlaninfo.setdCaculateendtm(endTIme);//方案计算结束时间
        ywkPlaninfo.setnPlanstatus(0l);//方案状态
        if (0 == timeType) {
            ywkPlaninfo.setnOutputtm(Long.parseLong(step + ""));//设置间隔分钟
        } else {
            ywkPlaninfo.setnOutputtm(step * 60L);//设置间隔分钟\
        }
        ywkPlaninfo.setnModelid(vo.getCatchmentAreaModelId());
        ywkPlaninfo.setnSWModelid(vo.getReachId());
        ywkPlaninfo.setdRainstarttime(startTime);
        ywkPlaninfo.setdRainendtime(endTIme);
        ywkPlaninfo.setdOpensourcestarttime(startTime);
        ywkPlaninfo.setdOpensourceendtime(endTIme);
        ywkPlaninfo.setnCreatetime(DateUtil.getCurrentTime());
        ywkPlaninfo.setRiverId(vo.getRvcd());
        ywkPlaninfo.setnCalibrationStatus(0l);
        ywkPlaninfo.setnPublish(0L);
        YwkPlaninfo saveDbo = ywkPlaninfoDao.save(ywkPlaninfo);
        //保存数据到缓存
        CacheUtil.saveOrUpdate("planInfo", ywkPlaninfo.getnPlanid(), ywkPlaninfo);
        return saveDbo.getnPlanid();
    }


    @Override
    public List<Map<String, Object>> getRainfalls(YwkPlaninfo planInfo) throws Exception {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatHour = new SimpleDateFormat("yyyy-MM-dd HH:");
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();

        String startTimeStr = format1.format(startTime);
        String endTimeStr = format1.format(endTime);

        Long step = planInfo.getnOutputtm();//分钟
        Long count = ywkPlaninRainfallDao.countByPlanIdWithTime(planInfo.getnPlanid(),startTime,endTime);
        List<Map<String, Object>> stPptnRWithSTCD = new ArrayList<>();
        if (count != 0){//原来是小时  实时数据是小时  都先按照整点来
            stPptnRWithSTCD = ywkPlaninRainfallDao.findStPptnRWithSTCD(startTimeStr,endTimeStr,planInfo.getnPlanid());
        }
        else {
            stPptnRWithSTCD = stPptnRDao.findStPptnRWithSTCD(startTimeStr, endTimeStr);
        }
        Map<String,List<Map<String,Object>>> handleMap = new HashMap<>();
        List<Map<String,Object>> nullList = new ArrayList<>();
        for (Map<String,Object> datas : stPptnRWithSTCD){// A.STCD,B.TM,B.DRP
            Object tm = datas.get("TM");
            if (tm == null){
                nullList.add(datas);
                continue;
            }
            List<Map<String, Object>> handles = handleMap.get(datas.get("STCD")+"");
            if (CollectionUtils.isEmpty(handles)) {
                handles = new ArrayList<>();
            }
            handles.add(datas);
            handleMap.put(datas.get("STCD")+"", handles);//存在有时间但是drp为null的 后面被优化了
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<Map<String,Object>> results = new ArrayList<>();
        List<String> timeResults = new ArrayList();
        while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
            String hourStart = format.format(startTime);
            timeResults.add(hourStart);
            startTime = DateUtil.getNextMinute(startTime, step.intValue());//h获取分钟
        }
        Set<Map.Entry<String, List<Map<String, Object>>>> entries = handleMap.entrySet();

        for (Map.Entry<String, List<Map<String, Object>>> entry : entries) {
            List<Map<String, Object>> list = entry.getValue();
            Iterator<Map<String, Object>> iterator = list.iterator();
           /* if (CollectionUtils.isEmpty(list)){
                continue;
            }*/
            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("STCD",entry.getKey());//TODO 现在存在库里每个小时多个数据点
            String stnm = list.get(0).get("STNM")+"";
            String lgtd = list.get(0).get("LGTD")+"";
            String lttd = list.get(0).get("LTTD")+"";
            resultMap.put("STNM",stnm);
            resultMap.put("LGTD",lgtd);
            resultMap.put("LTTD",lttd);

            while (iterator.hasNext()){
                Map<String, Object> next = iterator.next();//为啥要remove呢。
                String tm = next.get("TM")+"";
                if (!timeResults.contains(tm)){
                    iterator.remove();
                }
            }

            Map<String,Map<String,Object>> dataM = new HashMap();
            for(Map<String, Object> m : list){
                String tm = m.get("TM")+"";
                dataM.put(tm,m);
            }
            boolean flag = false;
            List<Map<String,Object>> ll = new ArrayList<>();
            for (String time : timeResults){
                String newTime = "";
                if (dataM.get(time) == null && step.intValue() < 60){//9点 是8点到9点的降雨量  都是整点的 9：00 9:30   10:00 算5 10 30步长
                    //if (dataM.get(newTime) != null && step.intValue() < 30){ //实时数据30分钟一个点整点的时候，算5   10步长
                    //if (dataM.get(newTime) != null && step.intValue() < 10){ //实时数据10分钟一个点整点的时候，算步长
                    newTime = format.format(DateUtil.getNextMinute(formatHour.parse(formatHour.format(format.parse(time))),60));//TODO 一个小时的整点 算5 15 30
                    //newTime = format.format(DateUtil.getNextMinute(formatHour.parse(formatHour.format(format.parse(time))),30));//TODO 30分钟的整点数据，算5 15
                    //newTime = format.format(DateUtil.getNextMinute(formatHour.parse(formatHour.format(format.parse(time))),10));//TODO 10分钟的整点数据 算5
                }else{
                    newTime = time;
                }

                Map<String, Object> stringObjectMap = dataM.get(newTime);
                if (stringObjectMap == null){
                    stringObjectMap = new HashMap<>();
                    stringObjectMap.put("STCD",entry.getKey());
                    stringObjectMap.put("STNM",stnm);
                    stringObjectMap.put("LGTD",lgtd);
                    stringObjectMap.put("LTTD",lttd);
                    stringObjectMap.put("TM",time);

                    stringObjectMap.put("DRP",null);

                }else {
                    if (dataM.get(time) == null && step.intValue()<60){ //todo if (step.intValue()<30){ 更上面的一样
                        Long divise = 60L/step;
                        //Long divise = 30L/step; TODO
                        //Long divise = 10L/step;
                        Map mm = new HashMap(stringObjectMap);
                        mm.put("TM",time);
                        if (mm.get("DRP")!= null){
                            mm.put("DRP",new BigDecimal(mm.get("DRP")+"").divide(new BigDecimal(divise),2,BigDecimal.ROUND_HALF_UP));
                        }
                        stringObjectMap = mm;
                    }
                }
                if(stringObjectMap.get("DRP") != null){
                    flag = true;
                }
                ll.add(stringObjectMap);
            }
            if (flag){
                for (Map map : ll){
                    if(map.get("DRP") == null ){
                        map.put("DRP",0.5);
                    }

                }
            }

            resultMap.put("LIST",ll);
            results.add(resultMap);

        }
        for (Map<String,Object> nullMap : nullList){
            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("STCD",nullMap.get("STCD"));
            resultMap.put("STNM",nullMap.get("STNM"));
            resultMap.put("LGTD",nullMap.get("LGTD"));
            resultMap.put("LTTD",nullMap.get("LTTD"));
            resultMap.put("LIST",new ArrayList<>());
            results.add(resultMap);
        }
        //TODO 修改雨量值并不修改基础表的数据，只修改缓存的的数据
        CacheUtil.saveOrUpdate("rainfall", planInfo.getnPlanid() + "new", results);
        return results;
    }

    public List<Map<String, Object>> getRainsInfo(YwkPlaninfo planInfo) throws Exception {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatHour = new SimpleDateFormat("yyyy-MM-dd HH:");
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();

        String startTimeStr = format1.format(startTime);
        String endTimeStr = format1.format(endTime);

        Long step = planInfo.getnOutputtm();//分钟
        Long count = ywkPlaninRainfallDao.countByPlanIdWithTime(planInfo.getnPlanid(),startTime,endTime);
        List<Map<String, Object>> stPptnRWithSTCD = stPptnRDao.findStPptnRWithSTCD(startTimeStr, endTimeStr);
//        List<Map<String, Object>> stPptnRWithSTCD = new ArrayList<>();
//        if (count != 0){//原来是小时  实时数据是小时  都先按照整点来
//            stPptnRWithSTCD = ywkPlaninRainfallDao.findStPptnRWithSTCD(startTimeStr,endTimeStr,planInfo.getnPlanid());
//        }
//        else {
//            stPptnRWithSTCD = stPptnRDao.findStPptnRWithSTCD(startTimeStr, endTimeStr);
//        }
        Map<String,List<Map<String,Object>>> handleMap = new HashMap<>();
        List<Map<String,Object>> nullList = new ArrayList<>();
        for (Map<String,Object> datas : stPptnRWithSTCD){// A.STCD,B.TM,B.DRP
            Object tm = datas.get("TM");
            if (tm == null){
                nullList.add(datas);
                continue;
            }
            List<Map<String, Object>> handles = handleMap.get(datas.get("STCD")+"");
            if (CollectionUtils.isEmpty(handles)) {
                handles = new ArrayList<>();
            }
            handles.add(datas);
            handleMap.put(datas.get("STCD")+"", handles);//存在有时间但是drp为null的 后面被优化了
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<Map<String,Object>> results = new ArrayList<>();
        List<String> timeResults = new ArrayList();
        while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
            String hourStart = format.format(startTime);
            timeResults.add(hourStart);
            startTime = DateUtil.getNextMinute(startTime, step.intValue());//h获取分钟
        }
        Set<Map.Entry<String, List<Map<String, Object>>>> entries = handleMap.entrySet();

        for (Map.Entry<String, List<Map<String, Object>>> entry : entries) {
            List<Map<String, Object>> list = entry.getValue();
            Iterator<Map<String, Object>> iterator = list.iterator();
           /* if (CollectionUtils.isEmpty(list)){
                continue;
            }*/
            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("STCD",entry.getKey());//TODO 现在存在库里每个小时多个数据点
            String stnm = list.get(0).get("STNM")+"";
            String lgtd = list.get(0).get("LGTD")+"";
            String lttd = list.get(0).get("LTTD")+"";
            resultMap.put("STNM",stnm);
            resultMap.put("LGTD",lgtd);
            resultMap.put("LTTD",lttd);

            while (iterator.hasNext()){
                Map<String, Object> next = iterator.next();//为啥要remove呢。
                String tm = next.get("TM")+"";
                if (!timeResults.contains(tm)){
                    iterator.remove();
                }
            }

            Map<String,Map<String,Object>> dataM = new HashMap();
            for(Map<String, Object> m : list){
                String tm = m.get("TM")+"";
                dataM.put(tm,m);
            }
            boolean flag = false;
            List<Map<String,Object>> ll = new ArrayList<>();
            for (String time : timeResults){
                String newTime = "";
                if (dataM.get(time) == null && step.intValue() < 60){//9点 是8点到9点的降雨量  都是整点的 9：00 9:30   10:00 算5 10 30步长
                    //if (dataM.get(newTime) != null && step.intValue() < 30){ //实时数据30分钟一个点整点的时候，算5   10步长
                    //if (dataM.get(newTime) != null && step.intValue() < 10){ //实时数据10分钟一个点整点的时候，算步长
                    newTime = format.format(DateUtil.getNextMinute(formatHour.parse(formatHour.format(format.parse(time))),60));//TODO 一个小时的整点 算5 15 30
                    //newTime = format.format(DateUtil.getNextMinute(formatHour.parse(formatHour.format(format.parse(time))),30));//TODO 30分钟的整点数据，算5 15
                    //newTime = format.format(DateUtil.getNextMinute(formatHour.parse(formatHour.format(format.parse(time))),10));//TODO 10分钟的整点数据 算5
                }else{
                    newTime = time;
                }

                Map<String, Object> stringObjectMap = dataM.get(newTime);
                if (stringObjectMap == null){
                    stringObjectMap = new HashMap<>();
                    stringObjectMap.put("STCD",entry.getKey());
                    stringObjectMap.put("STNM",stnm);
                    stringObjectMap.put("LGTD",lgtd);
                    stringObjectMap.put("LTTD",lttd);
                    stringObjectMap.put("TM",time);

                    stringObjectMap.put("DRP",null);

                }else {
                    if (dataM.get(time) == null && step.intValue()<60){ //todo if (step.intValue()<30){ 更上面的一样
                        Long divise = 60L/step;
                        //Long divise = 30L/step; TODO
                        //Long divise = 10L/step;
                        Map mm = new HashMap(stringObjectMap);
                        mm.put("TM",time);
                        if (mm.get("DRP")!= null){
                            mm.put("DRP",new BigDecimal(mm.get("DRP")+"").divide(new BigDecimal(divise),2,BigDecimal.ROUND_HALF_UP));
                        }
                        stringObjectMap = mm;
                    }
                }
                if(stringObjectMap.get("DRP") != null){
                    flag = true;
                }
                ll.add(stringObjectMap);
            }
            if (flag){
                for (Map map : ll){
                    if(map.get("DRP") == null ){
                        map.put("DRP",0.5);
                    }

                }
            }

            resultMap.put("LIST",ll);
            results.add(resultMap);

        }
        for (Map<String,Object> nullMap : nullList){
            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("STCD",nullMap.get("STCD"));
            resultMap.put("STNM",nullMap.get("STNM"));
            resultMap.put("LGTD",nullMap.get("LGTD"));
            resultMap.put("LTTD",nullMap.get("LTTD"));
            resultMap.put("LIST",new ArrayList<>());
            results.add(resultMap);
        }

        return results;
    }

    @Override
    public Map<String, Object> getModelList() {
        String catchmentArea = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_MODEL_TYPE_CATCHMENT_AREA");
        String reach = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_MODEL_TYPE_REACH");
        List<YwkModel> ywkModelByModelType = ywkModelDao.getYwkModelByModelType(catchmentArea);
        List<YwkModel> ywkModelByModelType1 = ywkModelDao.getYwkModelByModelType(reach);
        Map result = new HashMap();
        result.put("catchmentArea", ywkModelByModelType);
        result.put("reach", ywkModelByModelType1);
        return result;
    }

    @Override
    public Workbook exportRainfallTemplate(YwkPlaninfo planInfo) throws Exception {
        //封装时间列
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        // Long step = planInfo.getnOutputtm() / 60;//步长
        Long step = planInfo.getnOutputtm();//步长
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //封装边界模板数据
        XSSFWorkbook workbook = new XSSFWorkbook();

        //设置样式
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);//字体高度
        font.setFontName("宋体");//字体
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        style.setWrapText(true);//自动换行
        XSSFSheet sheet = workbook.createSheet("监测站雨量数据导入模板");
        //填充表头
        //第一行
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue("编号");
        XSSFCell cell1 = row.createCell(1);
        cell1.setCellStyle(style);
        cell1.setCellValue("名称");
        //设置自动列宽
        sheet.setColumnWidth(0, 2500);
        sheet.setColumnWidth(1, 3500);

        int beginLine = 1;
        //封装数据
        while (startTime.before(DateUtil.getNextMillis(endTime, 1))) {
            beginLine++;
            sheet.setColumnWidth(beginLine, 5100);
            XSSFCell c = row.createCell(beginLine);
            c.setCellValue(format.format(startTime));
            c.setCellStyle(style);
            startTime = DateUtil.getNextMinute(startTime, step.intValue());
        }

        List<Map<String, Object>> rainfalls = getRainfalls(planInfo);
        int rowLine = 1;
        for (Map<String, Object> map : rainfalls) {
            //A.STCD,A.STNM,A.LGTD,A.LTTD,B.TM,B.DRP
            String stcd = map.get("STCD") + "";
            String stnm = map.get("STNM") + "";
            XSSFRow row1 = sheet.createRow(rowLine);
            XSSFCell c = row1.createCell(0);
            c.setCellValue(stcd);
            c.setCellStyle(style);
            XSSFCell c1 = row1.createCell(1);
            c1.setCellValue(stnm);
            c1.setCellStyle(style);
            rowLine++;
            int j = 2;
            List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("LIST");
            if (CollectionUtils.isEmpty(list)) {
                continue;
            }
            for (Map<String, Object> m : list) {
                XSSFCell cell2 = row1.createCell(j);
                Object drp = m.get("DRP");
                j++;
                if (drp == null) {
                    continue;
                }
                cell2.setCellValue(drp + "");
                cell2.setCellStyle(style);
            }

        }
        return workbook;
    }

    @Override
    public List<Map<String, Object>> importRainfallData(MultipartFile mutilpartFile, YwkPlaninfo planInfo) {
        List<StStbprpB> stbp = stStbprpBDao.findAll();
        Map<String, StStbprpB> collect = stbp.stream().collect(Collectors.toMap(StStbprpB::getStcd, Function.identity()));
        List<Map<String, Object>> result = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //解析ecxel数据 不包含第一行
        List<String[]> excelList = ExcelUtil.readFiles(mutilpartFile, 0);

        if (excelList == null || excelList.size() < 2) {
            return new ArrayList<>();
        }
        List<String> head = Arrays.asList(excelList.get(0));
        List<String> timeHead = new ArrayList<>();
        if (!CollectionUtils.isEmpty(head) && head.size() > 1) {
            timeHead = head.subList(2, head.size());
        } else {
            System.out.println("雨量表头有问题。。。。");
            return new ArrayList<>();
        }
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        int size = 0;
        //Long step = planInfo.getnOutputtm() / 60;//步长
        Long step = planInfo.getnOutputtm();//步长
        List<String> timeResults = new ArrayList();
        try {
            while (startTime.before(DateUtil.getNextMillis(endTime, 1))) {
                String time = timeHead.get(size);
                String format = df.format(startTime);
                if (!time.equals(format)) {
                    System.out.println("表头时间有问题");
                    return new ArrayList<>();
                }
                timeResults.add(format);
                size++;
                startTime = DateUtil.getNextMinute(startTime, step.intValue());
            }
        } catch (Exception e) {
            System.out.println("时间序列不一致");
            return new ArrayList<>();
        }
        if (timeHead.size() != size) {//TODO 必须数量跟时间序列一致
            System.out.println("时间序列不一致");
            return new ArrayList<>();
        }

        // 判断有无数据 时间-每个边界的值集合
        try {

            // 遍历每行数据（除了标题）
            for (int i = 1; i < excelList.size(); i++) {
                Map<String, Object> dataMap = new HashMap<>();
                String[] strings = excelList.get(i);
                List<String> datas = Arrays.asList(strings);
                if (!CollectionUtils.isEmpty(datas) && datas.size() > 1) {
                    dataMap.put("id", datas.get(0));
                    dataMap.put("name", datas.get(1));
                    dataMap.put("list", datas.subList(2, datas.size()));
                } else {
                    continue;
                }
                result.add(dataMap);
            }

        } catch (Exception e) {
            System.out.println("解析csv文件出现问题");
            return new ArrayList<>();
        }
        List<Map<String, Object>> inSertList = new ArrayList();

        for (Map<String, Object> map : result) {
            Map<String, Object> insert = new HashMap();
            String id = map.get("id") + "";
            String name = map.get("name") + "";
            StStbprpB stStbprpB = collect.get(id);
            if (stStbprpB == null) {
                System.out.println("测站编码有问题，被修改过");
                continue;
            }

            insert.put("STCD", id);
            insert.put("STNM", name);
            insert.put("LGTD", stStbprpB.getLgtd());
            insert.put("LTTD", stStbprpB.getLttd());
            List<Map<String, Object>> list11 = new ArrayList<>();
            List<String> list = (List<String>) map.get("list");
            int z = 0;
            if (CollectionUtils.isEmpty(list) || list.size() != timeHead.size()) {
                list11 = new ArrayList<>();
                insert.put("LIST", list11);
                inSertList.add(insert);
                continue;
            }
            for (String drp : list) {
                String time = timeResults.get(z);
                z++;
                Map<String, Object> m = new HashMap();
                m.put("STCD", id);
                m.put("STNM", name);
                m.put("TM", time);

                if (drp == null || "".equals(drp.trim())) {//drp为null 或者空串
                    m.put("DRP", null);
                } else {
                    drp = drp.trim();
                    try {
                        Double drpValue = Double.parseDouble(drp);
                        m.put("DRP", drpValue);
                    } catch (Exception e) {
                        m.put("DRP", null);
                        System.out.println("雨量值不正确");
                        continue;
                    }
                }
                list11.add(m);

            }//里层for循环
            insert.put("LIST", list11);
            inSertList.add(insert);
        }

        //TODO 修改雨量值并不修改基础表的数据，只修改缓存的的数据
        CacheUtil.saveOrUpdate("rainfall", planInfo.getnPlanid() + "new", inSertList);
        return inSertList;
    }

    @Transactional
    @Override
    public void saveRainfallsFromCacheToDb(YwkPlaninfo planInfo, List<Map<String, Object>> results) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<String> timeResults = new ArrayList();
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        //Long step = planInfo.getnOutputtm() / 60;//步长
        Long step = planInfo.getnOutputtm();//分钟
        while (startTime.before(DateUtil.getNextMillis(endTime, 1))) {
            String hourStart = format.format(startTime);
            timeResults.add(hourStart);
            startTime = DateUtil.getNextMinute(startTime, step.intValue());
        }
        List<YwkPlaninRainfall> insertList = new ArrayList<>();
        for (Map<String, Object> map : results) {//
            String id = map.get("STCD") + "";//STCD<STNM<LGTD<LTTD<LIST
            List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("LIST");
            int z = 0;
            if (CollectionUtils.isEmpty(list)) {
                continue;
            }
            for (Map<String, Object> m : list) {//TODO null值不存
                Object drp = m.get("DRP");
                String time = timeResults.get(z);//TODO 为什么不用tm时间呢  防止表格被人 改 用程序的时间
                z++;
                if (drp == null) {
                    continue;
                }
                String drpStr = (drp + "").trim();
                if ("".equals(drpStr)) {
                    continue;
                }
                YwkPlaninRainfall ywkPlaninRainfall = new YwkPlaninRainfall();
                ywkPlaninRainfall.setcId(StrUtil.getUUID());
                ywkPlaninRainfall.setcStcd(id);
                ywkPlaninRainfall.setnPlanid(planInfo.getnPlanid());
                try {
                    ywkPlaninRainfall.setdTime(format.parse(time));
                } catch (Exception e) {
                    System.out.println("时间出错");
                    e.printStackTrace();
                }
                try {
                    Double drpValue = Double.parseDouble(drpStr);
                    ywkPlaninRainfall.setnDrp(drpValue);
                } catch (Exception e) {
                    ywkPlaninRainfall.setnDrp(null);
                    System.out.println("雨量值不正确");
                    continue;
                }
                insertList.add(ywkPlaninRainfall);
            }//里层for循环

        }//外层for循环*/
        ywkPlaninRainfallDao.deleteByNPlanid(planInfo.getnPlanid());//先删除，后新增*/

        int insertLength = insertList.size();
        int i = 0;
        List<CompletableFuture<Integer>> futures = new ArrayList<>();
        while (insertLength > 400) {
            futures.add(modelCallHandleDataService.saveRainToDb(insertList.subList(i, i + 400)));
            i = i + 400;
            insertLength = insertLength - 400;
        }
        if (insertLength > 0) {
            futures.add(modelCallHandleDataService.saveRainToDb(insertList.subList(i, i + insertLength)));
        }
        System.out.println("futures.size" + futures.size());
        CompletableFuture[] completableFutures = new CompletableFuture[futures.size()];
        for (int j = 0; j < futures.size(); j++) {
            completableFutures[j] = futures.get(j);
        }
        System.out.println("等待多线程执行完毕。。。。");
        CompletableFuture.allOf(completableFutures).join();//全部执行完后 然后主线程结束
        System.out.println("多线程执行完毕，结束主线程。。。。");
        return;

    }

    @Async
    @Override
    public void modelCall(YwkPlaninfo planInfo) {

        System.out.println("模型运算线程！" + Thread.currentThread().getName());
        Date originalStartTm = planInfo.getdCaculatestarttm();
        try {
            //雨量信息表
            long startTime = System.currentTimeMillis();   //获取开始时间

            Long aLong = ywkPlaninRainfallDao.countByPlanId(planInfo.getnPlanid());
            if (aLong == 0L) {
                System.out.println("方案雨量表没有保存数据");
                throw new RuntimeException("方案雨量表没有保存数据");
            }

            planInfo.setdCaculatestarttm(DateUtil.getNextHour(planInfo.getdCaculatestarttm(),-72));
            List<Map<String, Object>> before72results = getRainsInfo(planInfo);
            if (CollectionUtils.isEmpty(before72results)) {
                System.out.println("雨量信息为空，无法计算");
                throw new RuntimeException("雨量信息为空，无法计算");
            }

            //创建入参、出参
            String SWYB_PCP_HANDLE_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("DFMY_PCP_HANDLE_MODEL_PATH");
            String SWYB_SHUIWEN_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("DFMY_MODEL_PATH");
            String template = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");
            String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");
            String run = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_RUN");

            String PCP_HANDLE_MODEL_TEMPLATE = SWYB_PCP_HANDLE_MODEL_PATH + File.separator + template;

            String PCP_HANDLE_MODEL_TEMPLATE_INPUT = PCP_HANDLE_MODEL_TEMPLATE
                    + File.separator + "INPUT" + File.separator + planInfo.getnPlanid(); //输入的地址
            String PCP_HANDLE_MODEL_TEMPLATE_OUTPUT = SWYB_PCP_HANDLE_MODEL_PATH + File.separator + out
                    + File.separator + planInfo.getnPlanid();//输出的地址

            String PCP_HANDLE_MODEL_RUN = SWYB_PCP_HANDLE_MODEL_PATH + File.separator + run;

            String PCP_HANDLE_MODEL_RUN_PLAN = PCP_HANDLE_MODEL_RUN + File.separator + planInfo.getnPlanid();

            //另一个模型
            String SHUIWEN_MODEL_TEMPLATE = SWYB_SHUIWEN_MODEL_PATH + File.separator + template;
            String SHUIWEN_MODEL_TEMPLATE_INPUT = SHUIWEN_MODEL_TEMPLATE
                    + File.separator + "INPUT" + File.separator + planInfo.getnPlanid(); //输入的地址
            String SHUIWEN_MODEL_TEMPLATE_OUTPUT = SWYB_SHUIWEN_MODEL_PATH + File.separator + out
                    + File.separator + planInfo.getnPlanid();//输出的地址
            //模型运行的config
            String SHUIWEN_MODEL_RUN = SWYB_SHUIWEN_MODEL_PATH + File.separator + run;

            String SHUIWEN_MODEL_RUN_PLAN = SHUIWEN_MODEL_RUN + File.separator + planInfo.getnPlanid();

            File inputPcpPath = new File(PCP_HANDLE_MODEL_TEMPLATE_INPUT);
            File outPcpPath = new File(PCP_HANDLE_MODEL_TEMPLATE_OUTPUT);
            File runPcpPath = new File(PCP_HANDLE_MODEL_RUN_PLAN);

            File inputShuiWenPath = new File(SHUIWEN_MODEL_TEMPLATE_INPUT);
            File outShuiWenPath = new File(SHUIWEN_MODEL_TEMPLATE_OUTPUT);
            File runShuiWenPath = new File(SHUIWEN_MODEL_RUN_PLAN);

            inputPcpPath.mkdir();
            outPcpPath.mkdir();
            runPcpPath.mkdir();
            inputShuiWenPath.mkdir();
            outShuiWenPath.mkdir();
            runShuiWenPath.mkdir();


            //TODO 模型先算第一步，数据处理模型pcp_model
            //1，写入pcp_HRU.csv
            int result0 = writeDataToInputPcpHRUCsv(PCP_HANDLE_MODEL_TEMPLATE_INPUT, PCP_HANDLE_MODEL_TEMPLATE, planInfo);
            if (result0 == 0) {
                System.out.println("堤防漫溢之PCP模型:写入pcp_HRU失败");
                throw new RuntimeException("堤防漫溢之PCP模型:写入pcp_HRU失败");
            }
            //2,写入pcp_station.csv
            int result1 = writeDataToInputPcpStationCsv(PCP_HANDLE_MODEL_TEMPLATE_INPUT, before72results, planInfo);
            if (result1 == 0) {
                System.out.println("堤防漫溢之PCP模型:写入pcp_station失败");
                throw new RuntimeException("堤防漫溢之PCP模型:写入pcp_station失败");
            }
            //3.复制config以及可执行文件
            int result2 = copyPCPExeFile(PCP_HANDLE_MODEL_RUN, PCP_HANDLE_MODEL_RUN_PLAN);
            if (result2 == 0) {
                System.out.println("堤防漫溢之PCP模型:复制执行文件与config文件写入失败。。。");
                throw new RuntimeException("堤防漫溢之PCP模型:复制执行文件与config文件写入失败。。。");

            }
            //4,修改config文件
            int result3 = writeDataToPcpConfig(PCP_HANDLE_MODEL_RUN_PLAN, PCP_HANDLE_MODEL_TEMPLATE_INPUT, PCP_HANDLE_MODEL_TEMPLATE_OUTPUT);
            if (result3 == 0) {
                System.out.println("堤防漫溢之PCP模型:修改config文件失败");
                throw new RuntimeException("堤防漫溢之PCP模型:修改config文件失败");

            }
            long endTime = System.currentTimeMillis();   //获取开始时间
            System.out.println("堤防漫溢之PCP模型:组装pcp模型所用的参数的时间为:" + (endTime - startTime) + "毫秒");
            //5.调用模型
            //调用模型计算
            startTime = System.currentTimeMillis();
            System.out.println("堤防漫溢之PCP模型:开始堤防漫溢PCP模型计算。。。");
            System.out.println("堤防漫溢之PCP模型:模型计算路径为。。。" + PCP_HANDLE_MODEL_RUN_PLAN + File.separator + "startUp.bat");
            runModelExe(PCP_HANDLE_MODEL_RUN_PLAN + File.separator + "startUp.bat");
            endTime = System.currentTimeMillis();
            System.out.println("堤防漫溢之PCP模型:模型计算结束。。。，所用时间为:" + (endTime - startTime) + "毫秒");
            startTime = System.currentTimeMillis();
            //TODO 判断模型是否执行成功
            //判断是否执行成功，是否有error文件
            String pcp_result = PCP_HANDLE_MODEL_TEMPLATE_OUTPUT + File.separator + "hru_p_result.csv";
            File pcp_resultFile = new File(pcp_result);
            if (pcp_resultFile.exists()) {//存在表示执行成功
                System.out.println("堤防漫溢之PCP模型:pcp模型执行成功hru_p_result.csv文件存在");
            } else {
                System.out.println("堤防漫溢之PCP模型:pcp模型执行成功hru_p_result.csv文件不存在");//todo 执行失败
                throw new RuntimeException("堤防漫溢之PCP模型:pcp模型执行成功hru_p_result.csv文件不存在");
            }
            //TODO 上面的入参条件没存库
            //TODO 第二个shuiwen模型
            //6，预报断面ChuFaDuanMian、ChuFaDuanMian_shuru.csv组装
            int result4 = writeDataToInputShuiWenChuFaDuanMianCsv(SHUIWEN_MODEL_TEMPLATE_INPUT, planInfo);

            if (result4 == 0) {
                System.out.println("堤防漫溢之堤防漫溢模型:写入chufaduanmian跟chufaduanmian_shuru.csv失败");
                throw new RuntimeException("堤防漫溢之堤防漫溢模型:写入chufaduanmian跟chufaduanmian_shuru.csv失败");

            }
            //7 cope pcp模型的输出文件到堤防漫溢的输入文件里
            int result5 = copeFirstOutPutHruP(PCP_HANDLE_MODEL_TEMPLATE_OUTPUT, SHUIWEN_MODEL_TEMPLATE_INPUT);
            if (result5 == 0) {
                System.out.println("堤防漫溢之堤防漫溢模型:copy数据处理模型PCP输出文件hru_p_result失败");
                throw new RuntimeException("堤防漫溢之堤防漫溢模型:copy数据处理模型PCP输出文件hru_p_result失败");

            }

            //9 copy剩下的率定csv输入文件
            int result7 = copyOtherShuiWenLvDingCsv(SHUIWEN_MODEL_TEMPLATE, SHUIWEN_MODEL_TEMPLATE_INPUT);
            if (result7 == 0) {
                System.out.println("堤防漫溢之堤防漫溢模型: copy剩下的率定csv输入文件失败");
                throw new RuntimeException("堤防漫溢之堤防漫溢模型: copy剩下的率定csv输入文件失败");

            }
            //10 复制shuiwen cofig以及可执行文件
            int result8 = copyShuiWenExeFile(SHUIWEN_MODEL_RUN, SHUIWEN_MODEL_RUN_PLAN);
            if (result8 == 0) {
                System.out.println("堤防漫溢之堤防漫溢模型:复制执行文件与config文件写入失败。。。");
                throw new RuntimeException("堤防漫溢之堤防漫溢模型:复制执行文件与config文件写入失败。。。");

            }
            //11,修改shuiwen config文件
            int result9 = writeDataToShuiWenConfig(SHUIWEN_MODEL_RUN_PLAN, SHUIWEN_MODEL_TEMPLATE_INPUT, SHUIWEN_MODEL_TEMPLATE_OUTPUT, 0, planInfo);
            if (result9 == 0) {
                System.out.println("堤防漫溢之堤防漫溢模型:修改config文件失败");
                throw new RuntimeException("堤防漫溢之堤防漫溢模型:修改config文件失败");

            }
            endTime = System.currentTimeMillis();
            System.out.println("堤防漫溢之PCP模型:组装shuiwen模型所用的参数的时间为:" + (endTime - startTime) + "毫秒");

            //12,
            //调用模型计算
            startTime = System.currentTimeMillis();
            System.out.println("堤防漫溢之堤防漫溢模型:开始堤防漫溢shuiwen模型计算。。。");
            System.out.println("堤防漫溢之堤防漫溢模型:模型计算路径为。。。" + SHUIWEN_MODEL_RUN_PLAN + File.separator + "startUp.bat");
            runModelExe(SHUIWEN_MODEL_RUN_PLAN + File.separator + "startUp.bat");
            endTime = System.currentTimeMillis();
            System.out.println("堤防漫溢之堤防漫溢模型:模型计算结束。。。所用时间为:" + (endTime - startTime) + "毫秒");

            //判断是否执行成功，是否有error文件
            String errorStr = SHUIWEN_MODEL_TEMPLATE_OUTPUT + File.separator + "error_log.txt";
            File errorFile = new File(errorStr);
            planInfo.setdCaculatestarttm(originalStartTm);
            if (errorFile.exists()) {//存在表示执行失败
                System.out.println("堤防漫溢之堤防漫溢模型:模型计算失败。。存在error_log文件");
                planInfo.setnPlanstatus(-1L);
                ywkPlaninfoDao.save(planInfo);
                CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
                return;//todo 执行失败
            } else {
                System.out.println("堤防漫溢之堤防漫溢模型:模型计算成功。。不存在error_log文件");
                planInfo.setnPlanstatus(2L);
                ywkPlaninfoDao.save(planInfo);
                CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
                return;//todo  执行成功
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("模型执行失败了。。。。。。联系管理员" + e.getMessage());
            planInfo.setnPlanstatus(-1L);
            ywkPlaninfoDao.save(planInfo);
            CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
        }


    }

    private int writeCalibrationMsgjCsv(String shuiwen_model_template_input, String shuiwen_model_template_input_calibration, YwkPlaninfo planinfo) {

        String reachInput = shuiwen_model_template_input + File.separator + "Reach.csv";
        String reachInputUpdate = shuiwen_model_template_input_calibration + File.separator + "Reach.csv";
        //String reachXiangGuanShujvInput = shuiwen_model_template_input + File.separator + "reach_xiangguan_shujv.csv";

        String riverId = planinfo.getRiverId();
        List<WrpRiverZone> wrpRiverZones = wrpRiverZoneDao.findByRvcd(riverId);

        //找河系下的子河系
        List<WrpRvrBsin> allByParentIdRiver = wrpRvrBsinDao.findAllByParentId(planinfo.getRiverId());

        List<String> rvcds = allByParentIdRiver.stream().map(WrpRvrBsin::getRvcd).collect(Collectors.toList());// 子河系id

        List<YwkPlanCalibrationZone> zones = ywkPlanCalibrationZoneDao.findByNPlanid(planinfo.getnPlanid());
        Iterator<YwkPlanCalibrationZone> iterator = zones.iterator();
        while (iterator.hasNext()) {
            YwkPlanCalibrationZone next = iterator.next();
            if (next.getMsjgK() == null) {
                iterator.remove();
            }
        }
        Map<String, YwkPlanCalibrationZone> zoneMap = zones.stream().collect(Collectors.toMap(YwkPlanCalibrationZone::getZoneId, Function.identity()));
        Map<String, YwkPlanCalibrationZone> dataMap = new HashMap<>();
        for (WrpRiverZone riverZone : wrpRiverZones) {
            String rvcd = riverZone.getRvcd();
            Integer zoneId = riverZone.getZoneId();
            String zoneStr = "";
            String cid = riverZone.getcId();
            YwkPlanCalibrationZone ywkPlanCalibrationZone = zoneMap.get(cid);
            if (ywkPlanCalibrationZone == null) {
                continue;
            }
            switch (zoneId) {
                case 1:
                    zoneStr = "RFQ_001";
                    break;
                case 2:
                    zoneStr = "RFQ_002";
                    break;
                case 3:
                    zoneStr = "RFQ_003";
                    break;
                default:
            }
            if (!CollectionUtils.isEmpty(rvcds)) {
                for (String rvcdStr : rvcds) {
                    dataMap.put(rvcdStr + zoneStr, ywkPlanCalibrationZone);//河系id 加 分区id 确定一个
                }
            }
            dataMap.put(rvcd + zoneStr, ywkPlanCalibrationZone);

        }
        if (dataMap.size() == 0) {
            try {
                FileUtil.copyFile(reachInput, reachInputUpdate, true);
                System.out.println("copy msjg文件成功");
            } catch (Exception e) {
                System.out.println("copy msjg文件失败");
                e.printStackTrace();
            }
            System.out.println("msjg率定值k x 值未变");
            return 1;
        }


        List<List<String>> readDatas = new ArrayList<>();
        /* 读取数据 */
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(reachInput)), "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                List<String> split = Arrays.asList(lineTxt.split(","));
                readDatas.add(split);
            }
        } catch (Exception e) {
            System.err.println("堤防漫溢之堤防漫溢模型-率定：reachInput.csv输入文件读取错误:read errors :" + e);
            return 0;
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(reachInputUpdate, false)); // 附加
            // 添加新的数据行
            String head = "";
            List<String> strings1 = readDatas.get(0);
            for (String s : strings1) {
                head = head + s + ",";
            }
            head = head.substring(0, head.length() - 1);
            bw.write(head);
            bw.newLine();

            for (int i = 1; i < readDatas.size(); i++) {
                List<String> strings = readDatas.get(i);
                YwkPlanCalibrationZone zone = dataMap.get(strings.get(4) + strings.get(5));
                if (zone != null) {
                    strings.set(2, zone.getMsjgK() + "");
                    strings.set(3, zone.getMsjgX() + "");
                }

                String line = "";
                for (String s : strings) {
                    line = line + s + ",";
                }
                line = line.substring(0, line.length() - 1);
                bw.write(line);
                bw.newLine();
            }
            bw.close();
            System.out.println("堤防漫溢之堤防漫溢模型-率定:堤防漫溢reachInput.csv输入文件写入成功");

            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("堤防漫溢之堤防漫溢模型-率定:堤防漫溢reachInput.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }
    }

    private int writeCalibrationXajCsv(String shuiwen_model_template_input, String shuiwen_model_template_input_calibration, YwkPlaninfo planinfo) {

        //String xajInput = shuiwen_model_template_input + File.separator + "xaj.csv";
        //String xajInputUpdate = shuiwen_model_template_input_calibration + File.separator + "xaj.csv";

        String watershedInput = shuiwen_model_template_input + File.separator + "Watershed.csv";
        String watershedInputUpdate = shuiwen_model_template_input_calibration + File.separator + "Watershed.csv";

        String riverId = planinfo.getRiverId();
        List<WrpRiverZone> wrpRiverZones = wrpRiverZoneDao.findByRvcd(riverId);


        //找河系下的子河系
        List<WrpRvrBsin> allByParentIdRiver = wrpRvrBsinDao.findAllByParentId(planinfo.getRiverId());

        List<String> rvcds = allByParentIdRiver.stream().map(WrpRvrBsin::getRvcd).collect(Collectors.toList());// 子河系id


        List<YwkPlanCalibrationZoneXaj> zones = ywkPlanCalibrationZoneXajDao.findByNPlanid(planinfo.getnPlanid());
        Iterator<YwkPlanCalibrationZoneXaj> iterator = zones.iterator();
        while (iterator.hasNext()) {
            YwkPlanCalibrationZoneXaj next = iterator.next();
            if (next.getXajK() == null) {
                iterator.remove();
            }
        }
        Map<String, YwkPlanCalibrationZoneXaj> zoneMap = zones.stream().collect(Collectors.toMap(YwkPlanCalibrationZoneXaj::getZoneId, Function.identity()));
        Map<String, YwkPlanCalibrationZoneXaj> dataMap = new HashMap<>();
        for (WrpRiverZone riverZone : wrpRiverZones) {
            String rvcd = riverZone.getRvcd();
            Integer zoneId = riverZone.getZoneId();
            String zoneStr = "";
            String cid = riverZone.getcId();
            YwkPlanCalibrationZoneXaj ywkPlanCalibrationZoneXaj = zoneMap.get(cid);
            if (ywkPlanCalibrationZoneXaj == null) {
                continue;
            }
            switch (zoneId) {
                case 1:
                    zoneStr = "RFQ_001";
                    break;
                case 2:
                    zoneStr = "RFQ_002";
                    break;
                case 3:
                    zoneStr = "RFQ_003";
                    break;
                default:
            }
            if (!CollectionUtils.isEmpty(rvcds)) {
                for (String rvcdStr : rvcds) {
                    dataMap.put(rvcdStr + zoneStr, ywkPlanCalibrationZoneXaj);//河系id 加 分区id 确定一个
                }
            }
            dataMap.put(rvcd + zoneStr, ywkPlanCalibrationZoneXaj);

        }
        if (dataMap.size() == 0) { //值未变，copy一份进去

            try {
                FileUtil.copyFile(watershedInput, watershedInputUpdate, true);
                System.out.println("copy watershed文件成功");
            } catch (Exception e) {
                System.out.println("copy watershed文件失败");
                e.printStackTrace();
            }
            System.out.println("新安江相关系数值未变");
            return 1;
        }
        List<List<String>> readDatas = new ArrayList();
        /* 读取数据 */
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(watershedInput)), "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                List<String> split = Arrays.asList(lineTxt.split(","));
                readDatas.add(split);
            }
        } catch (Exception e) {
            System.err.println("堤防漫溢之堤防漫溢模型-率定：Watershed.csv输入文件读取错误:read errors :" + e);
            return 0;
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(watershedInputUpdate, false)); // 附加
            // 添加新的数据行
            String head = "";
            List<String> strings1 = readDatas.get(0);
            for (String s : strings1) {
                head = head + s + ",";
            }
            head = head.substring(0, head.length() - 1);
            bw.write(head);
            bw.newLine();

            for (int i = 1; i < readDatas.size(); i++) {
                List<String> strings = readDatas.get(i);
                YwkPlanCalibrationZoneXaj xaj = dataMap.get(strings.get(2) + strings.get(3));
                if (xaj != null) {
                    strings.set(9, xaj.getXajK() + "");
                    strings.set(10, xaj.getXajB() + "");
                    strings.set(11, xaj.getXajC() + "");
                    strings.set(12, xaj.getXajWum() + "");
                    strings.set(13, xaj.getXajWlm() + "");
                    strings.set(14, xaj.getXajWdm() + "");
                    strings.set(15, xaj.getXajWu0() + "");
                    strings.set(16, xaj.getXajWl0() + "");
                    strings.set(17, xaj.getXajWd0() + "");
                    strings.set(18, xaj.getXajEp() + "");
                }
                String line = "";
                for (String s : strings) {
                    line = line + s + ",";
                }
                line = line.substring(0, line.length() - 1);
                bw.write(line);
                bw.newLine();
            }
            bw.close();
            System.out.println("堤防漫溢之堤防漫溢模型-率定:堤防漫溢Watershed.csv输入文件写入成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("堤防漫溢之堤防漫溢模型-率定:堤防漫溢Watershed.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }

    }

    /**
     * 修改堤防漫溢config文件
     *
     * @param shuiwen_model_run_plan
     * @param shuiwen_model_template_input
     * @param shuiwen_model_template_output
     * @return
     */
    private int writeDataToShuiWenConfig(String shuiwen_model_run_plan, String shuiwen_model_template_input, String shuiwen_model_template_output, int tag, YwkPlaninfo planinfo) {
        String configUrl = shuiwen_model_run_plan + File.separator + "config.txt";
        List<String> list = new ArrayList();
        String reach1Url = "Reach1&&" + shuiwen_model_template_input + File.separator + "Reach.csv";
        String reach2Url = "Reach2&&" + shuiwen_model_template_input + File.separator + "Reach.csv";
        String Watershed1Url = "Watershed1&&" + shuiwen_model_template_input + File.separator + "Watershed.csv";
        String Watershed2Url = "Watershed2&&" + shuiwen_model_template_input + File.separator + "Watershed.csv";
        String unitUrl = "unit&&" + shuiwen_model_template_input + File.separator + "unit.csv";
        String model_selectionUrl = "model_selection&&" + shuiwen_model_template_input + File.separator + "model_selection.csv";
        String shuiku_shuiwei_kurongUrl = "shuiku_shuiwei_kurong&&" + shuiwen_model_template_input + File.separator + "shuiku_shuiwei_kurong.csv";
        String shuiku_chushishujuUrl = "shuiku_chushishuju&&" + shuiwen_model_template_input + File.separator + "shuiku_chushishuju.csv";
        String chufayubaoUrl = "chufayubao&&" + shuiwen_model_template_input + File.separator + "chufaduanmian.csv";
        String chufa_shuruUrl = "chufa_shuru&&" + shuiwen_model_template_input + File.separator + "chufaduanmian_shuru.csv";

        String duanmian_shuiweiUrl = "duanmian_shuiwei&&" + shuiwen_model_template_input + File.separator + "duanmian_shuiweiliuliang.csv";
        String jishuiqu_tongjiUrl = "jishuiqu_tongji&&" + shuiwen_model_template_input + File.separator + "jishuiqu_tongji.csv";

        String hru_pUrl = "hru_p&&" + shuiwen_model_template_input + File.separator + "hru_p_result.csv";
        String hru_scaler_modelUrl = "hru_scaler_model&&" + shuiwen_model_template_input + File.separator + "bpscaler.model";
        String hru_BP_modelUrl = "hru_BP_model&&" + shuiwen_model_template_input + File.separator + "bp.h5";
        String reach_scaler_modelUrl = "reach_scaler_model&&" + shuiwen_model_template_input + File.separator + "bbpp1";
        String reach_BP_modelUrl = "reach_BP_model&&" + shuiwen_model_template_input + File.separator + "m3.h5";
        String resultUrl = "result&&" + shuiwen_model_template_output + File.separator + "result.txt";
        String shuiku_resultUrl = "shuiku_result&&" + shuiwen_model_template_output + File.separator + "shuiku_result.txt";
        String errorUrl = "error&&" + shuiwen_model_template_output + File.separator + "error_log.txt";

        String jinduUrl = "jindu&&" + shuiwen_model_template_output + File.separator + "jindu.txt";
        String rugan_resultUrl = "rugan_result&&" + shuiwen_model_template_output + File.separator + "zhiliurugan.txt";
        String difangyujingUrl = "difangyujing&&" + shuiwen_model_template_input + File.separator + "difangyujing.csv";
        String rugan_kUrl = "rugan_k&&" + shuiwen_model_template_input + File.separator + "rugan_k.csv";
        String shuiku_jishuiquUrl = "shuiku_jishuiqu&&" + shuiwen_model_template_input + File.separator + "shuiku_jishuiqu.csv";

        String manyi_resultUrl = "manyi_result&&" + shuiwen_model_template_output + File.separator + "manyi_result.txt";
        String xuzhihongquUrl = "xuzhihongqu&&" + shuiwen_model_template_output + File.separator + "xuzhihongqu.txt";

        if (tag == 1) {
            String catchMentAreaModelId = planinfo.getnModelid(); //集水区模型id   // 1是SCS  2是单位线
            String reachId = planinfo.getnSWModelid(); //河段模型id

            /**
             * 1：SCS模型
             * 2：单位线模型
             * 3：新安江模型
             * 4：智能模型
             */
            switch (catchMentAreaModelId) {
                case "MODEL_SWYB_CATCHMENT_SCS":
                    Watershed1Url = "Watershed1&&" + shuiwen_model_template_input + File.separator + "calibration" + File.separator + "Watershed.csv";
                    Watershed2Url = "Watershed2&&" + shuiwen_model_template_input + File.separator + "calibration" + File.separator + "Watershed.csv";
                    break;
                case "MODEL_SWYB_CATCHMENT_DWX":
                    unitUrl = "unit&&" + shuiwen_model_template_input + File.separator + "calibration" + File.separator + "unit.csv";
                    break;
                case "MODEL_SWYB_CATCHMENT_XAJ":
                    Watershed1Url = "Watershed1&&" + shuiwen_model_template_input + File.separator + "calibration" + File.separator + "Watershed.csv";
                    Watershed2Url = "Watershed2&&" + shuiwen_model_template_input + File.separator + "calibration" + File.separator + "Watershed.csv";
                    break;
                case "MODEL_SWYB_CATCHMENT_ZN":
                    break;
                default:
                    System.out.println("模型集水区编码错误");
                    return 0;
            }
            /**
             * 1：马斯京根法
             * 2：相关关系法
             * 3：智能方法
             */
            switch (reachId) {
                case "MODEL_SWYB_REACH_MSJG":
                    reach1Url = "Reach1&&" + shuiwen_model_template_input + File.separator + "calibration" + File.separator + "Reach.csv";
                    reach2Url = "Reach2&&" + shuiwen_model_template_input + File.separator + "calibration" + File.separator + "Reach.csv";
                    break;
                case "MODEL_SWYB_REACH_XGGX":
                    reach1Url = "Reach1&&" + shuiwen_model_template_input + File.separator + "calibration" + File.separator + "Reach.csv";
                    reach2Url = "Reach2&&" + shuiwen_model_template_input + File.separator + "calibration" + File.separator + "Reach.csv";
                    break;
                case "MODEL_SWYB_REACH_ZN":
                    break;
                default:
                    System.out.println("模型河段编码错误");
                    return 0;
            }
        }
        list.add(reach1Url);
        list.add(reach2Url);
        list.add(Watershed1Url);
        list.add(Watershed2Url);
        list.add(unitUrl);
        list.add(model_selectionUrl);
        list.add(shuiku_shuiwei_kurongUrl);
        list.add(shuiku_chushishujuUrl);
        list.add(chufayubaoUrl);
        list.add(chufa_shuruUrl);
        list.add(hru_pUrl);
        list.add(hru_scaler_modelUrl);
        list.add(hru_BP_modelUrl);
        list.add(reach_scaler_modelUrl);
        list.add(reach_BP_modelUrl);
        list.add(resultUrl);
        list.add(shuiku_resultUrl);
        list.add(errorUrl);
        list.add(duanmian_shuiweiUrl);
        list.add(jishuiqu_tongjiUrl);
        list.add(jinduUrl);
        list.add(rugan_resultUrl);
        list.add(difangyujingUrl);
        list.add(rugan_kUrl);
        list.add(shuiku_jishuiquUrl);
        list.add(manyi_resultUrl);
        list.add(xuzhihongquUrl);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(configUrl, false)); // 附加
            // 写路径
            for (String s : list) {
                bw.write(s);
                bw.newLine();
            }
            bw.close();
            System.out.println("堤防漫溢之堤防漫溢模型:写入堤防漫溢config成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("堤防漫溢之堤防漫溢模型:写入堤防漫溢config失败");
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * cope 堤防漫溢exe可执行文件
     *
     * @param shuiwen_model_run
     * @param shuiwen_model_run_plan
     * @return
     */
    private int copyShuiWenExeFile(String shuiwen_model_run, String shuiwen_model_run_plan) {

        String exeUrl = shuiwen_model_run + File.separator + "main.exe";
        String exeInputUrl = shuiwen_model_run_plan + File.separator + "main.exe";
        String batUrl = shuiwen_model_run + File.separator + "startUp.bat";
        String batInputUrl = shuiwen_model_run_plan + File.separator + "startUp.bat";
        try {
            FileUtil.copyFile(exeUrl, exeInputUrl, true);
            FileUtil.copyFile(batUrl, batInputUrl, true);
            System.err.println("堤防漫溢之堤防漫溢模型：copy执行文件exe,bat文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("堤防漫溢之堤防漫溢模型：copy执行文件exe,bat文件错误" + e.getMessage());
            return 0;
        }
    }

    /**
     * cope剩下的率定csv输入文件
     *
     * @param shuiwen_model_template
     * @param shuiwen_model_template_input
     * @return
     */
    private int copyOtherShuiWenLvDingCsv(String shuiwen_model_template, String shuiwen_model_template_input) {


        String shuikuChushiShujuRead = shuiwen_model_template + File.separator + "shuiku_chushishuju.csv";
        String shuikuChushiShujuInput = shuiwen_model_template_input + File.separator + "shuiku_chushishuju.csv";

        String bbppRead = shuiwen_model_template + File.separator + "bbpp";
        String bbppInput = shuiwen_model_template_input + File.separator + "bbpp";

        String bbpp01Read = shuiwen_model_template + File.separator + "bbpp_01.npy";
        String bbpp01Input = shuiwen_model_template_input + File.separator + "bbpp_01.npy";
        String bbpp02Read = shuiwen_model_template + File.separator + "bbpp_02.npy";
        String bbpp02Input = shuiwen_model_template_input + File.separator + "bbpp_02.npy";
        String bbpp03Read = shuiwen_model_template + File.separator + "bbpp_03.npy";
        String bbpp03Input = shuiwen_model_template_input + File.separator + "bbpp_03.npy";
        String bbpp04Read = shuiwen_model_template + File.separator + "bbpp_04.npy";
        String bbpp04Input = shuiwen_model_template_input + File.separator + "bbpp_04.npy";
        String bbpp05Read = shuiwen_model_template + File.separator + "bbpp_05.npy";
        String bbpp05Input = shuiwen_model_template_input + File.separator + "bbpp_05.npy";


        String bbpp1Read = shuiwen_model_template + File.separator + "bbpp1";
        String bbpp1Input = shuiwen_model_template_input + File.separator + "bbpp1";

        String bpH5Read = shuiwen_model_template + File.separator + "bp.h5";
        String bpH5Input = shuiwen_model_template_input + File.separator + "bp.h5";

        String bprain_qH5Read = shuiwen_model_template + File.separator + "bprain_q.h5";
        String bprain_qH5Input = shuiwen_model_template_input + File.separator + "bprain_q.h5";

        String bpscalerModelRead = shuiwen_model_template + File.separator + "bpscaler.model";
        String bpscalerModelInput = shuiwen_model_template_input + File.separator + "bpscaler.model";

        String difangyujingRead = shuiwen_model_template + File.separator + "difangyujing.csv";
        String difangyujingInput = shuiwen_model_template_input + File.separator + "difangyujing.csv";

        String duanmianShuiweiLiuLiangRead = shuiwen_model_template + File.separator + "duanmian_shuiweiliuliang.csv";
        String duanmianShuiweiLiuLiangInput = shuiwen_model_template_input + File.separator + "duanmian_shuiweiliuliang.csv";

        String jishuiquTongjiRead = shuiwen_model_template + File.separator + "jishuiqu_tongji.csv";
        String jishuiquTongjiInput = shuiwen_model_template_input + File.separator + "jishuiqu_tongji.csv";

        String m2H5Read = shuiwen_model_template + File.separator + "m2.h5";
        String m2H5Input = shuiwen_model_template_input + File.separator + "m2.h5";

        String m3H5Read = shuiwen_model_template + File.separator + "m3.h5";
        String m3H5Input = shuiwen_model_template_input + File.separator + "m3.h5";

        String shuiwenModelSelectionRead = shuiwen_model_template + File.separator + "model_selection.csv";
        String shuiwenModelSelectionInput = shuiwen_model_template_input + File.separator + "model_selection.csv";

        String reachRead = shuiwen_model_template + File.separator + "Reach.csv";
        String reachInput = shuiwen_model_template_input + File.separator + "Reach.csv";

        String rugan_kRead = shuiwen_model_template + File.separator + "rugan_k.csv";
        String rugan_kInput = shuiwen_model_template_input + File.separator + "rugan_k.csv";

        String scalerbpRead = shuiwen_model_template + File.separator + "scalerbp";
        String scalerbpInput = shuiwen_model_template_input + File.separator + "scalerbp";

        String shuiku_jishuiquRead = shuiwen_model_template + File.separator + "shuiku_jishuiqu.csv";
        String shuiku_jishuiquInput = shuiwen_model_template_input + File.separator + "shuiku_jishuiqu.csv";

        String shuikuShuiweiKuRongRead = shuiwen_model_template + File.separator + "shuiku_shuiwei_kurong.csv";
        String shuikuShuiweiKuRongInput = shuiwen_model_template_input + File.separator + "shuiku_shuiwei_kurong.csv";

        String unitRead = shuiwen_model_template + File.separator + "unit.csv";
        String unitInput = shuiwen_model_template_input + File.separator + "unit.csv";

        String watershedRead = shuiwen_model_template + File.separator + "Watershed.csv";
        String watershedInput = shuiwen_model_template_input + File.separator + "Watershed.csv";

        try {
            FileUtil.copyFile(shuikuChushiShujuRead, shuikuChushiShujuInput, true);
            FileUtil.copyFile(bbppRead, bbppInput, true);
            FileUtil.copyFile(bbpp01Read, bbpp01Input, true);
            FileUtil.copyFile(bbpp02Read, bbpp02Input, true);
            FileUtil.copyFile(bbpp03Read, bbpp03Input, true);
            FileUtil.copyFile(bbpp04Read, bbpp04Input, true);
            FileUtil.copyFile(bbpp05Read, bbpp05Input, true);
            FileUtil.copyFile(bbpp1Read, bbpp1Input, true);
            FileUtil.copyFile(bpH5Read, bpH5Input, true);
            FileUtil.copyFile(bprain_qH5Read, bprain_qH5Input, true);
            FileUtil.copyFile(bpscalerModelRead, bpscalerModelInput, true);
            FileUtil.copyFile(difangyujingRead, difangyujingInput, true);
            FileUtil.copyFile(duanmianShuiweiLiuLiangRead, duanmianShuiweiLiuLiangInput, true);
            FileUtil.copyFile(jishuiquTongjiRead, jishuiquTongjiInput, true);
            FileUtil.copyFile(m2H5Read, m2H5Input, true);
            FileUtil.copyFile(m3H5Read, m3H5Input, true);
            FileUtil.copyFile(shuiwenModelSelectionRead, shuiwenModelSelectionInput, true);
            FileUtil.copyFile(reachRead, reachInput, true);
            FileUtil.copyFile(rugan_kRead, rugan_kInput, true);
            FileUtil.copyFile(scalerbpRead, scalerbpInput, true);
            FileUtil.copyFile(shuiku_jishuiquRead, shuiku_jishuiquInput, true);
            FileUtil.copyFile(shuikuShuiweiKuRongRead, shuikuShuiweiKuRongInput, true);
            FileUtil.copyFile(unitRead, unitInput, true);
            FileUtil.copyFile(watershedRead, watershedInput, true);
            System.err.println("堤防漫溢之堤防漫溢模型：copy剩下的率定csv输入文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("堤防漫溢之堤防漫溢模型：copy剩下的率定csv输入文件失败" + e.getMessage());
            return 0;
        }
    }

    /**
     * 写入输入文件MOdel_SelecTion
     *
     * @param shuiwen_model_template_input
     * @param planInfo
     * @return
     */
    private int writeDataToInputShuiWenModelSelectionCsv(String shuiwen_model_template_input, YwkPlaninfo planInfo) {

        String shuiwenModelSelectionInputUrl = shuiwen_model_template_input + File.separator + "model_selection.csv";
        String riverId = planInfo.getRiverId() == null ? "RVR_011" : planInfo.getRiverId();//TODO 后来改
        String catchMentAreaModelId = planInfo.getnModelid(); //集水区模型id   // 1是SCS  2是单位线
        String reachId = planInfo.getnSWModelid(); //河段模型id
        String catchNum = "";
        String reachNum = "";
        /**
         * 1：SCS模型
         * 2：单位线模型
         * 3：新安江模型
         * 4：智能模型
         */
        switch (catchMentAreaModelId) {
            case "MODEL_SWYB_CATCHMENT_SCS":
                catchNum = "1";
                break;
            case "MODEL_SWYB_CATCHMENT_DWX":
                catchNum = "2";
                break;
            case "MODEL_SWYB_CATCHMENT_XAJ":
                catchNum = "3";
                break;
            case "MODEL_SWYB_CATCHMENT_ZN":
                catchNum = "4";
                break;
            default:
                System.out.println("模型集水区编码错误");
                return 0;
        }
        /**
         * 1：马斯京根法
         * 2：相关关系法
         * 3：智能方法
         */
        switch (reachId) {
            case "MODEL_SWYB_REACH_MSJG":
                reachNum = "1";
                break;
            case "MODEL_SWYB_REACH_XGGX":
                reachNum = "2";
                break;
            case "MODEL_SWYB_REACH_ZN":
                reachNum = "3";
                break;
            default:
                System.out.println("模型河段编码错误");
                return 0;
        }

        List<WrpRvrBsin> rivers = wrpRvrBsinDao.findAll();
        List<String> riverIds = rivers.stream().map(WrpRvrBsin::getRvcd).collect(Collectors.toList());
        riverIds.remove(riverId);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(shuiwenModelSelectionInputUrl, false)); // 附加
            bw.write("" + "," + riverId);
            String otherHru = "";
            String otherReach = "";
            String otherFunction = "";
            for (String rvcd : riverIds) {
                bw.write("" + "," + rvcd);
                otherHru = otherHru + "," + 4;
                otherReach = otherReach + "," + 1;
                otherFunction = otherFunction + "," + 0;
            }
            bw.newLine();
            bw.write("HRU," + catchNum + otherHru);
            bw.newLine();
            bw.write("REACH," + reachNum + otherReach);
            bw.newLine();
            bw.write("function," + 1 + otherFunction);
            bw.newLine();
            bw.close();
            System.out.println("堤防漫溢之堤防漫溢模型:堤防漫溢model_selection输入文件写入成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("堤防漫溢之堤防漫溢模型:堤防漫溢model_selection输入文件写入失败");
            e.printStackTrace();
            return 0;
        }

    }

    /**
     * copy 第一个模型的输出文件Hru_P_result
     *
     * @param pcp_handle_model_template_output
     * @param shuiwen_model_template_input
     * @return
     */
    private int copeFirstOutPutHruP(String pcp_handle_model_template_output, String shuiwen_model_template_input) {

        String pcp_hru_p_result = pcp_handle_model_template_output + File.separator + "hru_p_result.csv";
        String shuiwen_hru_p_result_input = shuiwen_model_template_input + File.separator + "hru_p_result.csv";

        try {
            FileUtil.copyFile(pcp_hru_p_result, shuiwen_hru_p_result_input, true);
            System.err.println("堤防漫溢之堤防漫溢模型：copy数据处理模型PCP输出文件hru_p_result文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("堤防漫溢之堤防漫溢模型：copy数据处理模型PCP输出文件hru_p_result文件失败" + e.getMessage());
            return 0;
        }

    }

    /**
     * 堤防漫溢，chufaduanmian 跟chufaduanmian_shuru
     *
     * @param shuiwen_model_template_input
     * @param
     * @param planInfo
     * @return
     */
    private int writeDataToInputShuiWenChuFaDuanMianCsv(String shuiwen_model_template_input, YwkPlaninfo planInfo) {
        String shuiWenChuFaDuanMianInputUrl = shuiwen_model_template_input + File.separator + "chufaduanmian.csv";
        String shuiWenChuFaDuanMianShuRuInputUrl = shuiwen_model_template_input + File.separator + "chufaduanmian_shuru.csv";

        //TODO 只要预报断面存在的情况，预报断面csv里面才会修改
        List<WrpRcsBsin> rcsAll = wrpRcsBsinDao.findAll();
        List<String> rcsIds = rcsAll.stream().map(WrpRcsBsin::getRvcrcrsccd).collect(Collectors.toList());//断面id集合
        List<Map<String, Object>> insertDuanMianList = new ArrayList<>();
        List<Map<String, Object>> insertDuanMianShuRuList = new ArrayList<>();
        List<YwkPlanTriggerRcs> triggerRcss = ywkPlanTriggerRcsDao.findByNPlanid(planInfo.getnPlanid());

        if (CollectionUtils.isEmpty(triggerRcss)) {
            for (String s : rcsIds) {
                Map map = new HashMap();
                map.put("rcd", s);
                map.put("num", 1);
                insertDuanMianList.add(map);
                insertDuanMianShuRuList.add(map);
            }
        } else {
            List<String> rcdIds = triggerRcss.stream().map(YwkPlanTriggerRcs::getRcsId).collect(Collectors.toList());//断面id的集合
            for (String s : rcsIds) {
                Map map = new HashMap();
                map.put("rcd", s);
                if (rcdIds.contains(s)) {
                    map.put("num", 0);
                    insertDuanMianList.add(map);
                    continue;
                }
                map.put("num", 1);
                insertDuanMianList.add(map);
            }

            List<String> triggerRcssIds = triggerRcss.stream().map(YwkPlanTriggerRcs::getId).collect(Collectors.toList());//主键id的集合
            List<YwkPlanTriggerRcsFlow> triggerRcsFlows = ywkPlanTriggerRcsFlowDao.findByTriggerRcsIdsOrderByTime(triggerRcssIds);
            Map<String, List<Double>> datas = new HashMap<>();
            for (YwkPlanTriggerRcsFlow rcsFlow : triggerRcsFlows) {
                String triggerRcsId = rcsFlow.getTriggerRcsId();
                Double flow = rcsFlow.getFlow();
                List<Double> doubles = datas.get(triggerRcsId);
                if (CollectionUtils.isEmpty(doubles)) {
                    doubles = new ArrayList<>();
                }
                doubles.add(flow);
                datas.put(triggerRcsId, doubles);
            }

            Map<String, String> rcsAndTriggerRcs = triggerRcss.stream().collect(Collectors.toMap(YwkPlanTriggerRcs::getRcsId, YwkPlanTriggerRcs::getId));

            for (String s : rcsIds) {
                Map map = new HashMap();
                map.put("rcd", s);
                String triggerRcsId = rcsAndTriggerRcs.get(s);
                if (triggerRcsId != null) {
                    List<Double> doubles = datas.get(triggerRcsId);
                    map.put("doubles", doubles);
                }
                insertDuanMianShuRuList.add(map);
            }

        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(shuiWenChuFaDuanMianInputUrl, false)); // 附加
            for (Map<String, Object> map : insertDuanMianList) {
                String rcd = map.get("rcd") + "";
                String num = map.get("num") + "";
                bw.write(rcd + "," + num);
                bw.newLine();
            }
            bw.close();
            System.out.println("堤防漫溢之堤防漫溢模型:堤防漫溢ChuFaDuanMian.csv输入文件写入成功");
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("堤防漫溢之堤防漫溢模型:堤防漫溢ChuFaDuanMian.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(shuiWenChuFaDuanMianShuRuInputUrl, false)); // 附加
            for (Map<String, Object> map : insertDuanMianShuRuList) {
                String rcd = map.get("rcd") + "";
                Object doubles = map.get("doubles");
                bw.write(rcd);
                if (doubles != null) {
                    List<Double> list = (List) doubles;
                    for (Double d : list) {
                        bw.write("," + d);
                    }
                }
                bw.newLine();
            }
            bw.close();
            System.out.println("堤防漫溢之堤防漫溢模型:堤防漫溢ChuFaDuanMianShuRu.csv输入文件写入成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("堤防漫溢之堤防漫溢模型:堤防漫溢ChuFaDuanMianShuRu.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }


    }

    /**
     * cope pcp模型config文件以及可执行文件
     *
     * @param pcp_handle_model_run
     * @param pcp_handle_model_run_plan
     * @return
     */
    private int copyPCPExeFile(String pcp_handle_model_run, String pcp_handle_model_run_plan) {
        String exeUrl = pcp_handle_model_run + File.separator + "pcp_handle.exe";
        String exeInputUrl = pcp_handle_model_run_plan + File.separator + "pcp_handle.exe";
        String batUrl = pcp_handle_model_run + File.separator + "startUp.bat";
        String batInputUrl = pcp_handle_model_run_plan + File.separator + "startUp.bat";
        try {
            FileUtil.copyFile(exeUrl, exeInputUrl, true);
            FileUtil.copyFile(batUrl, batInputUrl, true);
            System.err.println("堤防漫溢之PCP模型：copy执行文件exe,bat文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("堤防漫溢之PCP模型：copy执行文件exe,bat文件错误" + e.getMessage());
            return 0;
        }
    }

    /**
     * 修改堤防漫溢的数据模型的config文件
     *
     * @param pcp_handle_model_run_plan
     * @param pcp_handle_model_template_input
     * @param pcp_handle_model_template_output
     * @return
     */
    private int writeDataToPcpConfig(String pcp_handle_model_run_plan, String pcp_handle_model_template_input, String pcp_handle_model_template_output) {

        String configUrl = pcp_handle_model_run_plan + File.separator + "config.txt";
        String pcp_HRUUrl = "pcp_HRU&&" + pcp_handle_model_template_input + File.separator + "pcp_HRU.csv";
        String pcp_stationUrl = "pcp_station&&" + pcp_handle_model_template_input + File.separator + "pcp_station.csv";
        String hru_p_resultUrl = "hru_p_result&&" + pcp_handle_model_template_output + File.separator + "hru_p_result.csv";
        String jinduUrl = "jindu&&" + pcp_handle_model_template_output + File.separator + "jindu.txt";

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(configUrl, false)); // 附加
            // 写路径
            bw.write(pcp_HRUUrl);
            bw.newLine();
            bw.write(pcp_stationUrl);
            bw.newLine();
            bw.write(hru_p_resultUrl);
            bw.newLine();
            bw.write(jinduUrl);
            bw.newLine();
            bw.close();
            System.out.println("堤防漫溢之PCP模型:写入堤防漫溢config成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("堤防漫溢之PCP模型:写入堤防漫溢config失败");
            e.printStackTrace();
            return 0;
        }

    }

    /**
     * 写入pcp模型的第二个输入文件pcp_station
     *
     * @param pcp_handle_model_template_input
     * @param results
     * @return
     */
    private int writeDataToInputPcpStationCsv(String pcp_handle_model_template_input, List<Map<String, Object>> results, YwkPlaninfo planInfo) {

        String pcpHRUInputUrl = pcp_handle_model_template_input + File.separator + "pcp_station.csv";

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(pcpHRUInputUrl, false)); // 附加
            // 添加新的数据行
            bw.write("" + ",STNM,LGTD,LTTD"); //编写表头
            Date startTime = planInfo.getdCaculatestarttm();
            Date endTime = planInfo.getdCaculateendtm();
            int size = 0;
            //Long step = planInfo.getnOutputtm() / 60;//步长
            Long step = planInfo.getnOutputtm();//步长
            DecimalFormat format = new DecimalFormat("0.00");
            Double hour = Double.parseDouble(format.format(step * 1.0 / 60));
            while (startTime.before(DateUtil.getNextMillis(endTime, 1))) {
                size++;
                startTime = DateUtil.getNextMinute(startTime, step.intValue());
            }
            for (int i = 0; i < size; i++) {
                bw.write("," + i * hour);
            }
            bw.newLine();
            for (Map<String, Object> map : results) {
                Object stcd = map.get("STCD");
                String stnm = map.get("STNM") == null ? "" : map.get("STNM") + "";
                String lgtd = map.get("LGTD") == null ? "" : map.get("LGTD") + "";
                Object lttd = map.get("LTTD") == null ? "" : map.get("LTTD") + "";
                List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("LIST");
                bw.write(stcd + "," + stnm + "," + lgtd + "," + lttd);
                for (Map<String, Object> m : list) {
                    String value = m.get("DRP") == null ? "" : m.get("DRP") + "";
                    bw.write("," + value);
                }
                bw.newLine();
            }
            bw.close();
            System.out.println("堤防漫溢之PCP模型:堤防漫溢pcp_station.csv输入文件写入成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("堤防漫溢之PCP模型:堤防漫溢pcp_station.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }

    }

    /**
     * 写入pcp模型的第一个输入文件pcp_hru
     *
     * @param pcp_handle_model_template_input
     * @param planInfo
     * @return
     */
    private int writeDataToInputPcpHRUCsv(String pcp_handle_model_template_input, String pcp_handle_model_template, YwkPlaninfo planInfo) {

        String pcpHRUInputUrl = pcp_handle_model_template_input + File.separator + "pcp_HRU.csv";

        String pcpHRUReadUrl = pcp_handle_model_template + File.separator + "pcp_HRU.csv";

        List<List<String>> readDatas = new ArrayList();
        /* 读取数据 */
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(pcpHRUReadUrl)), "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                List<String> split = Arrays.asList(lineTxt.split(","));
                readDatas.add(split);
            }
        } catch (Exception e) {
            System.err.println("堤防漫溢之PCP模型：pcp_HRU.csv输入文件读取错误:read errors :" + e);
            return 0;
        } finally {
            try {
                br.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(pcpHRUInputUrl, false)); // 附加
            // 添加新的数据行
            bw.write("" + "," + "LGTD" + "," + "LTTD"); //编写表头

            Date startTime = planInfo.getdCaculatestarttm();
            Date endTime = planInfo.getdCaculateendtm();
            int size = 0;
            //Long step = planInfo.getnOutputtm() / 60;//步长
            Long step = planInfo.getnOutputtm();//

            DecimalFormat format = new DecimalFormat("0.00");
            Double hour = Double.parseDouble(format.format(step * 1.0 / 60));

            while (startTime.before(DateUtil.getNextMillis(endTime, 1))) {
                size++;//TODO 修改这个地方的时间序列值。
                startTime = DateUtil.getNextMinute(startTime, step.intValue());
            }
            for (int i = 0; i < size; i++) {
                bw.write("," + i * hour);
            }
            bw.newLine();
            for (int i = 1; i < readDatas.size(); i++) {
                List<String> strings = readDatas.get(i);
                String line = "";
                for (String s : strings) {
                    line = line + s + ",";
                }
                line = line.substring(0, line.length() - 1);
                bw.write(line);
                bw.newLine();
            }
            bw.close();
            System.out.println("堤防漫溢之PCP模型:堤防漫溢pcp_HRU.csv输入文件写入成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("堤防漫溢之PCP模型:堤防漫溢pcp_HRU.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }
    }


    @Override
    public String getModelRunStatus(YwkPlaninfo planInfo, Integer tag) {

        Long status;
        if (tag == 0) {
            status = planInfo.getnPlanstatus();
        } else {
            status = planInfo.getnCalibrationStatus();
        }

        if (status == 2L || status == -1L) {
            return "1"; //1的话停止
        } else {
            return "0";
        }

    }

    @Override
    public Object getModelResultQ(YwkPlaninfo planInfo, Integer tag) {

        DecimalFormat df = new DecimalFormat("0.000");
        JSONObject resultObj = new JSONObject();

        Long step = planInfo.getnOutputtm();//步长(小时)

        String riverId = planInfo.getRiverId();

        List<String> riverIds = new ArrayList<>();
        List<WrpRvrBsin> allByParentId = wrpRvrBsinDao.findAllByParentId(riverId);
        if (!CollectionUtils.isEmpty(allByParentId)){
            riverIds = allByParentId.stream().map(WrpRvrBsin::getRvcd).collect(Collectors.toList());
        }
        riverIds.add(riverId);

        String SWYB_SHUIWEN_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_BASE_NEW_SHUIWEN_MODEL_PATH");
        String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");

        String SHUIWEN_MODEL_TEMPLATE_OUTPUT ="";
        if (tag == 0){
            SHUIWEN_MODEL_TEMPLATE_OUTPUT = SWYB_SHUIWEN_MODEL_PATH + File.separator + out
                    + File.separator + planInfo.getnPlanid();//输出的地址
        }else {
            SHUIWEN_MODEL_TEMPLATE_OUTPUT = SWYB_SHUIWEN_MODEL_PATH + File.separator + out
                    + File.separator + planInfo.getnPlanid() + File.separator +"calibration";//输出的地址
        }
        //解析河道断面
        Map<String, List<String>> finalResult = getModelResult(SHUIWEN_MODEL_TEMPLATE_OUTPUT+File.separator+"result.txt");

        Map<String, List<String>> manYiResult = getModelResult(SHUIWEN_MODEL_TEMPLATE_OUTPUT+File.separator+"manyi_result.txt");

        String rcs014 = "RCS_014"; //在result结果文件中判断是否开启分洪道

        //找到河系关联的断面
        List<WrpRcsBsin> listByRiverId = wrpRcsBsinDao.findListByRiverIds(riverIds);
        List<String> sections = listByRiverId.stream().map(WrpRcsBsin::getRvcrcrsccd).collect(Collectors.toList());
        Map<String, String> sectionName = listByRiverId.stream().collect(Collectors.toMap(WrpRcsBsin::getRvcrcrsccd, WrpRcsBsin::getRvcrcrscnm));

        JSONArray xuZhiJsonL = new JSONArray();
        resultObj.put("xzhq",xuZhiJsonL);
        List<String> xzList = new ArrayList<>();
        xzList.add("shanghuashanwa");
        xzList.add("baiyunhu");
        xzList.add("yazhuanghu");
        xzList.add("madahu");
        Map<String, List<String>> xuzhihongquResult = getModelResult(SHUIWEN_MODEL_TEMPLATE_OUTPUT+File.separator+"xuzhihongqu.txt");
        for (String xz : xzList) {
            JSONObject valObj = new JSONObject();
            int isXuZhi = 0;
            List<String> strings = xuzhihongquResult.get(xz);
            for (String string : strings) {
                if(!"0.0".equals(string)){
                    isXuZhi = 1;
                    break;
                }
            }
            valObj.put(xz,isXuZhi);
            xuZhiJsonL.add(valObj);
        }

        if(finalResult!=null && finalResult.size()>0){
            Date startTime = planInfo.getdCaculatestarttm();
            Date endTime = planInfo.getdCaculateendtm();

            //判断是否开启分洪道
            List<String> rcs014Data = finalResult.get(rcs014);
            int isFhd = 0;
            if(rcs014Data.size()>0 && rcs014Data!=null){
                int rcsIndex = 0;
                for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime,1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                    if(Double.parseDouble(rcs014Data.get(rcsIndex))!=0.0){
                        isFhd = 1;
                        break;
                    }
                    rcsIndex++;
                }
            }

            resultObj.put("isFhd",isFhd);

            JSONArray sectionJsonL = new JSONArray();
            resultObj.put("section",sectionJsonL);
            for(String sectionId : sections){
                String name = sectionName.get(sectionId);
                JSONObject valObj = new JSONObject();
                valObj.put("RCS_ID",sectionId);
                valObj.put("RCS_NAME",name);
                JSONArray valList = new JSONArray();
                valObj.put("values",valList);
                JSONArray ZList = new JSONArray();
                valObj.put("zValues",ZList);
                JSONArray rainList = new JSONArray();
                valObj.put("rainValues",rainList);
                JSONArray manyiList = new JSONArray();
                valObj.put("manyiValues",manyiList);
                sectionJsonL.add(valObj);
                List<String> manyiResultL = manYiResult.get(sectionId);
                if(manyiResultL!=null && manyiResultL.size()>0){
                    int myIndex = 0;
                    for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime,1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                        try{
                            JSONObject dataObj = new JSONObject();
                            dataObj.put("time",DateUtil.dateToStringNormal3(time));
                            dataObj.put("manyi",Double.parseDouble(manyiResultL.get(myIndex)) == 0.0 ? 0:1);
                            manyiList.add(dataObj);
                            //count+=step;
                            myIndex++;
                        }catch (Exception e){
                            break;
                        }
                    }
                }


                List<String> dataList = finalResult.get(sectionId);
                if(dataList!=null && dataList.size()>0){
                    int index = 0;
                    //int count = 0;
                    for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime,1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                        try{
                            JSONObject dataObj = new JSONObject();
                            dataObj.put("time",DateUtil.dateToStringNormal3(time));
                            dataObj.put("q",df.format(Double.parseDouble(dataList.get(index)+"")));
                            valList.add(dataObj);
                            //count+=step;
                            index++;
                        }catch (Exception e){
                            break;
                        }
                    }
                    for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime,1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                        try{
                            JSONObject dataObjZ = new JSONObject();
                            dataObjZ.put("time",DateUtil.dateToStringNormal3(time));
                            dataObjZ.put("z",df.format(Double.parseDouble(dataList.get(index)+"")));
                            ZList.add(dataObjZ);
                            //count+=step;
                            index++;
                        }catch (Exception e){
                            break;
                        }
                    }
                    valObj.put("hfQ",df.format(Double.parseDouble(dataList.get(index)+"")));
                    index++;
                    valObj.put("hfTime",DateUtil.getNextMinute(startTime,step.intValue()*Integer.parseInt(dataList.get(index)+"")));
                    index++;
                    valObj.put("hfTotal",df.format(Double.parseDouble(dataList.get(index)+"")));
                    index++;
                    for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime,1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                        try{
                            JSONObject dataObjRain = new JSONObject();
                            dataObjRain.put("time",DateUtil.dateToStringNormal3(time));
                            dataObjRain.put("rain",df.format(Double.parseDouble(dataList.get(index)+"")));
                            rainList.add(dataObjRain);
                            //count+=step;
                            index++;
                        }catch (Exception e){
                            break;
                        }
                    }

                }
            }
        }
        return resultObj;
    }

    /**
     * 解析模型输出结果文件成出库流量数据
     *
     * @param model_template_output
     * @return
     */
    private Map<String, List<String>> getModelResult(String model_template_output) {
        Map<String, List<String>> resultMap = new HashMap<>();
        List<String> datas = new ArrayList<>();

        /* 读取数据 */
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(model_template_output)), "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                datas.add(lineTxt);
            }

            for (String s : datas) {
                List<String> split = Arrays.asList(s.split("\t"));
                resultMap.put(split.get(0), new ArrayList<>(split.subList(1, split.size())));
            }
        } catch (Exception e) {
            System.err.println("堤防漫溢调用结果读取失败:read errors :" + e);
            return new HashMap<>();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultMap;
    }


    @Transactional
    @Override
    public void saveModelData(YwkPlaninfo planInfo) {
        //保存方案计算-降雨量条件
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //首先保存入库，
        JSONArray modelResultQ = (JSONArray) getModelResultQ(planInfo, 0);//因为经历过修改保存跟修改撤销后只有一个地方有result文件
        List<Map> resultQ = JSON.parseArray(JSON.toJSONString(modelResultQ), Map.class);
        List<YwkPlanOutputQ> insert = new ArrayList<>();
        for (Map<String, Object> map : resultQ) {
            String rcs_id = map.get("RCS_ID") + "";
            List<Map<String, Object>> values = (List<Map<String, Object>>) map.get("values");
            for (Map<String, Object> value : values) {
                YwkPlanOutputQ model = new YwkPlanOutputQ();
                String time = value.get("time") + "";
                Double q = Double.parseDouble(value.get("q") + "");
                model.setIdcId(StrUtil.getUUID());
                try {
                    model.setdTime(format.parse(time));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                model.setnQ(q);
                model.setnPlanid(planInfo.getnPlanid());
                model.setRvcrcrsccd(rcs_id);
                insert.add(model);
            }
        }

        ywkPlanOutputQDao.deleteByNPlanid(planInfo.getnPlanid());//先删除，后新增*/
        //modelCallHandleDataService.handleCsvAndResult(tag,planInfo);//开启个线程处理csv
        int insertLength = insert.size();
        int i = 0;
        List<CompletableFuture<Integer>> futures = new ArrayList<>();
        while (insertLength > 400) {
            futures.add(modelCallHandleDataService.savePlanOut(insert.subList(i, i + 400)));
            i = i + 400;
            insertLength = insertLength - 400;
        }
        if (insertLength > 0) {
            futures.add(modelCallHandleDataService.savePlanOut(insert.subList(i, i + insertLength)));
        }
        System.out.println("futures.size" + futures.size());
        CompletableFuture[] completableFutures = new CompletableFuture[futures.size()];
        for (int j = 0; j < futures.size(); j++) {
            completableFutures[j] = futures.get(j);
        }
        System.out.println("等待多线程执行完毕。。。。");//TODO 我认为不需要那个处理csv文件
        CompletableFuture.allOf(completableFutures).join();//全部执行完后 然后主线程结束
        System.out.println("多线程执行完毕，结束主线程。。。。");
        return;
    }


    /**
     * 查询方案边界条件列表数据
     *
     * @param modelParamVo
     * @return
     */
    @Override
    public Object getSwModelBoundaryBasicData(ModelParamVo modelParamVo) {
        //先从缓存获取
        Object dataCache = CacheUtil.get("modelBoundaryData", modelParamVo.getnPlanid() + "sw");
        if (dataCache != null)
            return dataCache;

        //如果没有数据再从文件封装
        JSONObject resultObj = new JSONObject();
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(modelParamVo.getnPlanid());
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        DecimalFormat df = new DecimalFormat("0.000");
        Long step = planInfo.getnOutputtm();//步长(小时)
        //读取堤防漫溢输出路径
        String SWYB_SHUIWEN_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("DFMY_MODEL_PATH");
        String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");

        String SHUIWEN_MODEL_TEMPLATE_OUTPUT = SWYB_SHUIWEN_MODEL_PATH + File.separator + out
                + File.separator + planInfo.getnPlanid();//输出的地址

        //解析河道断面数据
        Map<String, List<String>> finalResult = getModelResult(SHUIWEN_MODEL_TEMPLATE_OUTPUT + File.separator + "result.txt");

        JSONArray xuZhiJsonL = new JSONArray();
        resultObj.put("xzhq",xuZhiJsonL);
        List<String> xzList = new ArrayList<>();
        xzList.add("shanghuashanwa");
        xzList.add("baiyunhu");
        xzList.add("yazhuanghu");
        xzList.add("madahu");
        Map<String, List<String>> xuzhihongquResult = getModelResult(SHUIWEN_MODEL_TEMPLATE_OUTPUT+File.separator+"xuzhihongqu.txt");
        for (String xz : xzList) {
            JSONObject valObj = new JSONObject();
            int isXuZhi = 0;
            List<String> strings = xuzhihongquResult.get(xz);
            for (String string : strings) {
                if(!"0.0".equals(string)){
                    isXuZhi = 1;
                    break;
                }
            }
            valObj.put(xz,isXuZhi);
            valObj.put("LGTD","");
            valObj.put("LTTD","");
            xuZhiJsonL.add(valObj);
        }

        Map<String, List<String>> manYiResult = getModelResult(SHUIWEN_MODEL_TEMPLATE_OUTPUT+File.separator+"manyi_result.txt");
        //找到河系关联的断面
        String riverId = planInfo.getRiverId();
        List<String> riverIds = new ArrayList<>();
        List<WrpRvrBsin> allByParentId = wrpRvrBsinDao.findAllByParentId(riverId);
        if (!CollectionUtils.isEmpty(allByParentId)){
            riverIds = allByParentId.stream().map(WrpRvrBsin::getRvcd).collect(Collectors.toList());
        }
        riverIds.add(riverId);
        List<WrpRcsBsin> listByRiverId = wrpRcsBsinDao.findListByRiverIds(riverIds);
        List<String> sections = listByRiverId.stream().map(WrpRcsBsin::getRvcrcrsccd).collect(Collectors.toList());
        Map<String, String> sectionName = listByRiverId.stream().collect(Collectors.toMap(WrpRcsBsin::getRvcrcrsccd, WrpRcsBsin::getRvcrcrscnm));
        Map<String, Double> lgtds = listByRiverId.stream().collect(Collectors.toMap(WrpRcsBsin::getRvcrcrsccd, WrpRcsBsin::getLgtd));
        Map<String, Double> lttds = listByRiverId.stream().collect(Collectors.toMap(WrpRcsBsin::getRvcrcrsccd, WrpRcsBsin::getLttd));

        if(manYiResult != null && manYiResult.size() > 0){
            JSONArray manyiList = new JSONArray();
            resultObj.put("manyiData",manyiList);
            for (String sectionId : sections) {
                Double lgtd = lgtds.get(sectionId);
                Double lttd = lttds.get(sectionId);
                String name = sectionName.get(sectionId);
                List<String> manyiResultL = manYiResult.get(sectionId);
                int myIndex = 0;
                for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime,1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                    try{
                        if(Double.parseDouble(manyiResultL.get(myIndex))!=0.0){
                            JSONObject valObj = new JSONObject();
                            valObj.put("RCS_ID",sectionId);
                            valObj.put("RCS_NAME",name);
                            valObj.put("LGTD",lgtd);
                            valObj.put("LTTD",lttd);
                            valObj.put("time",DateUtil.dateToStringNormal3(time));
                            valObj.put("manyi",Double.parseDouble(manyiResultL.get(myIndex)) == 0.0 ? 0:1);
                            manyiList.add(valObj);
                        }
                        myIndex++;
                    }catch (Exception e){
                        break;
                    }
                }
            }
        }

        //判断是否开启分洪道
        String rcs014 = "RCS_014"; //在result结果文件中判断是否开启分洪道
        List<String> rcs014Data = finalResult.get(rcs014);
        int isOpen = 0;
        JSONObject fhdResult = new JSONObject();
        if(rcs014Data.size()>0 && rcs014Data!=null){
            int rcsIndex = 0;
            for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime,1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                if(Double.parseDouble(rcs014Data.get(rcsIndex))!=0.0){
                    isOpen = 1;
                    break;
                }
                rcsIndex++;
            }
        }
        fhdResult.put("isOpen",isOpen);
        fhdResult.put("LGTD","");
        fhdResult.put("LTTD","");
        resultObj.put("fhdData", fhdResult);

        //查询水动力边界数据
        List<YwkBoundaryBasic> boundaryBasicList = ywkBoundaryBasicDao.findByRcsIdNotNull();
        //找到河系关联的断面
        if (finalResult != null && finalResult.size() > 0) {

            JSONArray sectionJsonL = new JSONArray();
            resultObj.put("boundaryData",sectionJsonL);
            for (YwkBoundaryBasic ywkBoundaryBasic : boundaryBasicList) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("boundary", ywkBoundaryBasic);
                List<Object> dataList = new ArrayList<>();

                List<String> qList = finalResult.get(ywkBoundaryBasic.getRcsId());
                int index = 0;
                for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                    JSONObject dataJsonObj = new JSONObject();
                    dataJsonObj.put("time", DateUtil.dateToStringNormal3(time));
                    try {
                        dataJsonObj.put("boundaryData", df.format(Double.parseDouble(qList.get(index) + "")));
                    } catch (Exception e) {
                        dataJsonObj.put("boundaryData", "0.00");
                    }
                    dataList.add(dataJsonObj);
                    index++;
                }
                jsonObject.put("dataList", dataList);
                sectionJsonL.add(jsonObject);
            }
        }
        //放入缓存
        CacheUtil.saveOrUpdate("modelBoundaryData", modelParamVo.getnPlanid() + "sw", resultObj);
        return resultObj;
    }

    @Override
    public List<YwkPlanInfoBoundaryDto> saveSwModelBoundaryBasicData(List<YwkPlanInfoBoundaryDto> boundaryDtoList, String planId) {
        //放入缓存
        CacheUtil.saveOrUpdate("modelBoundaryData", planId + "sw", boundaryDtoList);
        return boundaryDtoList;
    }

    /**
     * 防洪保护区设置获取模型列表
     *
     * @return
     */
    @Override
    public List<Object> getHsfxModelList() {
        List<Object> list = new ArrayList<>();
        List<YwkModel> modelList = ywkModelDao.getYwkModelByModelType("HSFX");
        for (YwkModel ywkModel : modelList) {
            list.add(ywkModel);
        }
        return list;
    }

    /**
     * 根据模型获取河道糙率设置参数
     *
     * @param modelId
     * @return
     */
    @Override
    public List<Object> getModelRiverRoughness(String modelId) {
        List<Object> list = new ArrayList<>();
        //根据模型id获取模型糙率设置
        List<YwkModelRoughnessParam> modelRoughnessList = ywkModelRoughnessParamDao.findByIdmodelId(modelId);
        //查询糙率参数
        List<YwkRiverRoughnessParam> paramList = ywkRiverRoughnessParamDao.findAll();
        Map<String, List<YwkRiverRoughnessParam>> paramMap = paramList.stream().collect(Collectors.groupingBy(YwkRiverRoughnessParam::getRoughnessParamid));
        //封装参数
        for (YwkModelRoughnessParam roughnessParam : modelRoughnessList) {
            roughnessParam.setParamList(paramMap.get(roughnessParam.getRoughnessParamid()));
            list.add(roughnessParam);
        }
        return list;
    }

    @Override
    @Transactional
    public ModelParamVo saveModelRiverRoughness(YwkModelRoughnessParam modelRoughness, String planId, String modelId) {
        //修改方案计算模型
        YwkPlaninfo ywkPlaninfo = ywkPlaninfoDao.findOneById(planId);
        ywkPlaninfo.setnModelid(modelId);
        ywkPlaninfoDao.save(ywkPlaninfo);
        //保存方案计算模型糙率参数
        //先删除再新增
        List<YwkPlaninFloodRoughness> planFloodRoughnessList = ywkPlaninFloodRoughnessDao.findByPlanId(planId);
        for (YwkPlaninFloodRoughness floodRoughness : planFloodRoughnessList) {
            ywkPlaninRiverRoughnessDao.deleteByPlanRoughnessId(floodRoughness.getPlanRoughnessid());
        }
        ywkPlaninFloodRoughnessDao.deleteByPlanId(planId);
        //插入最新设定数据
        //方案模型糙率
        YwkPlaninFloodRoughness ywkPlaninFloodRoughness = new YwkPlaninFloodRoughness();
        String ywkPlaninFloodRoughnessId = StrUtil.getUUID();
        ywkPlaninFloodRoughness.setPlanRoughnessid(ywkPlaninFloodRoughnessId);
        ywkPlaninFloodRoughness.setPlanId(planId);
        ywkPlaninFloodRoughness.setRoughnessParamnm(modelRoughness.getRoughnessParamnm());
        ywkPlaninFloodRoughness.setRoughnessParamid(modelRoughness.getRoughnessParamid());
        ywkPlaninFloodRoughness.setGridSynthesizeRoughness(modelRoughness.getGridSynthesizeRoughness());
        //方案河道糙率
        List<YwkRiverRoughnessParam> ywkRiverRougParamsList = modelRoughness.getParamList();
        List<YwkPlaninRiverRoughness> planRiverRoughnessList = new ArrayList<>();
        for (YwkRiverRoughnessParam ywkRiverRoughnessParam : ywkRiverRougParamsList) {
            YwkPlaninRiverRoughness ywkPlaninRiverRoughness = new YwkPlaninRiverRoughness();
            ywkPlaninRiverRoughness.setId(StrUtil.getUUID());
            ywkPlaninRiverRoughness.setPlanRoughnessId(ywkPlaninFloodRoughnessId);
            ywkPlaninRiverRoughness.setRoughness(ywkRiverRoughnessParam.getRoughness());
            ywkPlaninRiverRoughness.setMileage(ywkRiverRoughnessParam.getMileage());
            ywkPlaninRiverRoughness.setIsFix(ywkRiverRoughnessParam.getIsFix());
            planRiverRoughnessList.add(ywkPlaninRiverRoughness);
        }
        //保存方案模型糙率
        ywkPlaninFloodRoughnessDao.save(ywkPlaninFloodRoughness);
        //保存方案河道糙率
        if (planRiverRoughnessList.size() > 0) {
            ywkPlaninRiverRoughnessDao.saveAll(planRiverRoughnessList);
        }
        return new ModelParamVo(planId, modelId, modelRoughness.getRoughnessParamid());
    }


    /**
     * 查询水动力边界条件列表数据
     *
     * @param modelParamVo
     * @return
     */
    @Override
    public List<Object> getModelBoundaryBasic(ModelParamVo modelParamVo) {
        //先从缓存获取
        List<Object> dataCacheList = (List<Object>) CacheUtil.get("modelBoundaryData", modelParamVo.getnPlanid());
        if (dataCacheList != null)
            return dataCacheList;
        //如果缓存没有从数据库获取
        List<Object> list = new ArrayList<>();
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(modelParamVo.getnPlanid());
        //封装时间列
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        //查询模型边界关联表
        List<YwkModelBoundaryBasicRl> modelBoundaryList = ywkModelBoundaryBasicRlDao.findByIdmodelId(modelParamVo.getIdmodelId());
        //查询边界详细数据表
        List<String> stcdList = new ArrayList<>();
        stcdList.add(StrUtil.getUUID());
        for (YwkModelBoundaryBasicRl modelboundary : modelBoundaryList) {
            stcdList.add(modelboundary.getStcd());
        }
        List<YwkBoundaryBasic> boundaryBasicList = ywkBoundaryBasicDao.findByStcdInOrderByStcd(stcdList);

        //封装边界流量数据
        for (YwkBoundaryBasic ywkBoundaryBasic : boundaryBasicList) {
            if (ywkBoundaryBasic.getRcsId() != null)
                continue;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("boundary", ywkBoundaryBasic);
            List<Object> dataList = new ArrayList<>();
            int count = 0;
            for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextHour(startTime, count)) {
                JSONObject dataJsonObj = new JSONObject();
                dataJsonObj.put("time", DateUtil.dateToStringNormal3(time));
                dataJsonObj.put("boundaryData", 0.0);
                dataList.add(dataJsonObj);
                count++;
            }
            jsonObject.put("dataList", dataList);
            list.add(jsonObject);
        }
        return list;
    }

    /**
     * 下载水动力边界数据模板
     *
     * @param planId
     * @param modelId
     * @return
     */
    @Override
    public Workbook exportDutyTemplate(String planId, String modelId) {
        //查询模型边界关联表
        List<YwkModelBoundaryBasicRl> modelBoundaryList = ywkModelBoundaryBasicRlDao.findByIdmodelId(modelId);
        //查询边界详细数据表
        List<String> stcdList = new ArrayList<>();
        stcdList.add(StrUtil.getUUID());
        for (YwkModelBoundaryBasicRl modelboundary : modelBoundaryList) {
            stcdList.add(modelboundary.getStcd());
        }
        List<YwkBoundaryBasic> boundaryBasicList = ywkBoundaryBasicDao.findByStcdInOrderByStcd(stcdList);
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        //封装边界模板数据
        XSSFWorkbook workbook = new XSSFWorkbook();

        //设置样式
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);//字体高度
        font.setFontName("宋体");//字体
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        style.setWrapText(true);//自动换行
        XSSFSheet sheet = workbook.createSheet("边界数据导入模板");
        //填充表头
        //第一行
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue("时间/边界值");
        //设置自动列宽
        sheet.setColumnWidth(0, 5100);

        int cellCount = 1;
        for (int i = 0; i < boundaryBasicList.size(); i++) {
            YwkBoundaryBasic ywkBoundaryBasic = boundaryBasicList.get(i);
            if (ywkBoundaryBasic.getRcsId() != null)
                continue;
            sheet.setColumnWidth(cellCount, 4000);
            String dataType = "0".equals(ywkBoundaryBasic.getBoundaryDataType()) ? "水位" : "流量";
            XSSFCell cells = row.createCell(cellCount);
            cells.setCellStyle(style);
            cells.setCellValue(ywkBoundaryBasic.getBoundarynm() + "(" + dataType + ")");
            cellCount++;
        }
        //封装时间列
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        int beginLine = 1;
        //封装数据
        int count = 0;
        for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextHour(startTime, count)) {
            XSSFRow row1 = sheet.createRow(beginLine);
            row1.createCell(0).setCellValue(DateUtil.dateToStringNormal3(time));
            count++;
            beginLine++;
        }
        return workbook;
    }

    @Override
    public List<Object> importBoundaryData(MultipartFile mutilpartFile, String planId, String modelId) throws IOException {
        List<Object> boundaryDataList = new ArrayList<>();
        //查询模型边界关联表
        List<YwkModelBoundaryBasicRl> modelBoundaryList = ywkModelBoundaryBasicRlDao.findByIdmodelId(modelId);
        //查询边界详细数据表
        List<String> stcdList = new ArrayList<>();
        stcdList.add(StrUtil.getUUID());
        for (YwkModelBoundaryBasicRl modelboundary : modelBoundaryList) {
            stcdList.add(modelboundary.getStcd());
        }
        List<YwkBoundaryBasic> boundaryBasicList = ywkBoundaryBasicDao.findByStcdInOrderByStcd(stcdList);

        //解析ecxel数据 不包含第一行
        List<String[]> excelList = ExcelUtil.readFiles(mutilpartFile, 1);
        // 判断有无数据 时间-每个边界的值集合
        Map<String, List<String>> dataMap = new HashMap<>();
        if (excelList != null && excelList.size() > 0) {
            // 遍历每行数据（除了标题）
            for (int i = 0; i < excelList.size(); i++) {
                String[] strings = excelList.get(i);
                if (strings != null && strings.length > 0) {
                    // 封装每列（每个指标项数据）
                    List<String> dataList = new ArrayList<>(Arrays.asList(strings));
                    dataMap.put((strings[0] + "").trim(), dataList.subList(0, dataList.size()));
                }
            }
        }
        //封装边界流量数据
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        //封装时间列
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        int dataCount = 1;
        for (int i = 0; i < boundaryBasicList.size(); i++) {
            YwkBoundaryBasic boundaryBasic = boundaryBasicList.get(i);
            if (boundaryBasic.getRcsId() != null)
                continue;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("boundary", boundaryBasicList.get(i));
            //封装时间数据
            List<Object> dataList = new ArrayList<>();
            int count = 0;
            for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextHour(startTime, count)) {
                String timeStr = DateUtil.dateToStringNormal3(time);
                List<String> strings = dataMap.get(timeStr);
                JSONObject dataTimeObj = new JSONObject();
                dataTimeObj.put("time", timeStr);
                try {
                    dataTimeObj.put("boundaryData", Double.parseDouble(strings.get(dataCount)));
                } catch (Exception e) {
                    dataTimeObj.put("boundaryData", 0.0);
                }
                dataList.add(dataTimeObj);
                count++;
            }
            dataCount++;
            jsonObject.put("dataList", dataList);
            boundaryDataList.add(jsonObject);
        }
        //存入缓存
        CacheUtil.saveOrUpdate("modelBoundaryData", planId, boundaryDataList);

        return boundaryDataList;
    }

    @Override
    @javax.transaction.Transactional
    public List<YwkPlanInfoBoundaryDto> savePlanBoundaryData(List<YwkPlanInfoBoundaryDto> ywkPlanInfoBoundaryDtoList, String planId) {
        //获取水文边界
        List<YwkPlanInfoBoundaryDto> dataCacheList = (List<YwkPlanInfoBoundaryDto>) CacheUtil.get("modelBoundaryData", planId + "sw");
        if (dataCacheList != null) {
            ywkPlanInfoBoundaryDtoList.addAll(dataCacheList);
        }
        //根据方案id删除边界条件信息
        ywkPlaninFloodBoundaryDao.deleteByPlanId(planId);
        //封装新数据
        YwkPlaninfo planinfo = ywkPlaninfoDao.findOneById(planId);
        //输出步长
        Date startTime = planinfo.getdCaculatestarttm();
        Long step = planinfo.getnOutputtm();
        //封装边界条件数据
        List<YwkPlaninFloodBoundary> planBoundaryList = new ArrayList<>();
        for (YwkPlanInfoBoundaryDto ywkPlanInfoBoundaryDto : ywkPlanInfoBoundaryDtoList) {
            YwkBoundaryBasicDto boundary = ywkPlanInfoBoundaryDto.getBoundary();
            List<YwkBoundaryDataDto> dataList = ywkPlanInfoBoundaryDto.getDataList();
            for (YwkBoundaryDataDto ywkBoundaryDataDto : dataList) {
                Date time = ywkBoundaryDataDto.getTime();
                YwkPlaninFloodBoundary ywkPlaninFloodBoundary = new YwkPlaninFloodBoundary();
                ywkPlaninFloodBoundary.setId(StrUtil.getUUID());
                ywkPlaninFloodBoundary.setPlanId(planId);
                ywkPlaninFloodBoundary.setStcd(boundary.getStcd());
                ywkPlaninFloodBoundary.setAbsoluteTime(time);
                int i = DateUtil.dValueOfTime(startTime, time);
                ywkPlaninFloodBoundary.setRelativeTime(Long.parseLong(i + ""));
                if ("0".equals(boundary.getBoundaryDataType())) {
                    ywkPlaninFloodBoundary.setZ(ywkBoundaryDataDto.getBoundaryData());
                } else {
                    ywkPlaninFloodBoundary.setQ(ywkBoundaryDataDto.getBoundaryData());
                }
                planBoundaryList.add(ywkPlaninFloodBoundary);
            }
        }
        ywkPlaninFloodBoundaryDao.saveAll(planBoundaryList);
        //更新缓存
        //存入缓存
        CacheUtil.saveOrUpdate("modelBoundaryData", planId, ywkPlanInfoBoundaryDtoList);

        return ywkPlanInfoBoundaryDtoList;
    }

    @Override
    public List<YwkBreakBasicDto> getBreakList(String modelId) {

        List<YwkBreakBasic> ywkBreakBasics = ywkBreakBasicDao.findsByModelId(modelId);
        List<YwkBreakBasicDto> results = new ArrayList<>();
        for (YwkBreakBasic source : ywkBreakBasics) {
            YwkBreakBasicDto target = new YwkBreakBasicDto();
            BeanUtils.copyProperties(source, target);
            results.add(target);
        }
        return results;
    }


    @Override
    @javax.transaction.Transactional
    public BreakVo savePlanBreak(BreakVo breakDto) {
        //根据方案id删除旧数据
        ywkPlaninFloodBreakDao.deleteByNPlanid(breakDto.getnPlanid());
        //保存记录
        YwkPlaninFloodBreak target = new YwkPlaninFloodBreak();
        BeanUtils.copyProperties(breakDto, target);
        target.setId(StrUtil.getUUID());
        ywkPlaninFloodBreakDao.save(target);
        return breakDto;
    }


    @Override
    public void modelCallHsfx(String planId) {
        //调用模型计算
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOne(planId);
        if (planInfo == null) {
            System.out.println("计划planid没有找到记录");
            return;
        }
        String modelid = planInfo.getnModelid();

        String hsfx_path = PropertiesUtil.read("/filePath.properties").getProperty("HSFX_MODEL");

        String hsfx_model_template_output = hsfx_path +
                File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                + File.separator + planId; //输出的地址


        String hsfx_model_template = hsfx_path +
                File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");//默认文件对位置

        if ("MODEL_HSFX_01".equals(modelid)) {
            hsfx_model_template = hsfx_model_template + File.separator + "FHBHQ1";
        } else if ("MODEL_HSFX_02".equals(modelid)) {
            hsfx_model_template = hsfx_model_template + File.separator + "FHBHQ2";
        } else {
            System.out.println("水动力模型的模型id值不对");
            return;
        }

        String hsfx_model_template_input = hsfx_path +
                File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE")
                + File.separator + "INPUT" + File.separator + planId; //输入的地址

        String hsfx_model_template_run = hsfx_path +
                File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_RUN");

        String hsfx_model_template_run_plan = hsfx_model_template_run + File.separator + planId;
        //List<YwkPlanOutputGridProcess> results = analysisOfGridProcessCSV(hsfx_model_template_output,planinfo);

        File inputyiweiPath = new File(hsfx_model_template_input + File.separator + "yiwei");
        File inputerweiPath = new File(hsfx_model_template_input + File.separator + "erwei");

        File outyiweiPath = new File(hsfx_model_template_output + File.separator + "yiwei");
        File outerweiPath = new File(hsfx_model_template_output + File.separator + "erwei");
        File runPath = new File(hsfx_model_template_run_plan);
        inputyiweiPath.mkdirs();
        inputerweiPath.mkdirs();
        outyiweiPath.mkdirs();
        outerweiPath.mkdirs();
        runPath.mkdirs();

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        String startTimeStr = format1.format(startTime);
        String endTimeStr = format1.format(endTime);


        //获取模型边界基本信息表对所有数据
        List<Map<String, Object>> BndDatas = new ArrayList<>();//表头13个的里程
        List<String> BndList = new ArrayList<>();//点位数据
//        List<Map<String, Object>> breakList = new ArrayList<>();//洪道开始结束俩个点的里程
//        Map<String, List<Map<String, Object>>> channels = new HashMap<>();//点位数据
//        getBndCsvChanelDatas(breakList, channels, planId);//获取分洪道数据
//        getBndCsvBoundaryDatas(BndDatas, BndList, planInfo, breakList, channels);//获取边界数据
        getBndCsvBoundaryDatas(BndDatas, BndList, planInfo);//获取边界数据

        //写入边界条件成功
        int result0 = writeDataToInputBNDCsv(hsfx_model_template_input, BndDatas, BndList);
        if (result0 == 0) {
            System.out.println("水动力模型计算:边界BND.csv输入文件写入成功。。。");
            return;
        }

        List<YwkPlaninRiverRoughness> ctrCsvDatas = getCTRCsvDatas(planId);
        int result1 = writeDataToInputCTRCsv(hsfx_model_template, hsfx_model_template_input, ctrCsvDatas, planInfo, BndList.size());

        if (result1 == 0) {
            System.out.println("水动力模型计算:溃口CTR.csv输入文件写入失败。。。");
            return;
        }
        int result2 = writeDataToInputBDCsv(hsfx_model_template, hsfx_model_template_input, planInfo, BndList.size());

        if (result2 == 0) {
            System.out.println("水动力模型计算:溃口通道BD.csv输入文件写入失败。。。");
            return;
        }

        int result3 = writeDataToInputWGCsv(hsfx_model_template, hsfx_model_template_input, planInfo);

        if (result3 == 0) {
            System.out.println("水动力模型计算:糙率WG.csv输入文件写入失败。。。");
            return;
        }

        int result4 = copyOtherCsv(hsfx_model_template, hsfx_model_template_input);
        if (result4 == 0) {
            System.out.println("水动力模型计算:复制其他.csv输入文件写入失败。。。");
            return;
        }
        int result5 = copyExeFile(hsfx_model_template_run, hsfx_model_template_run_plan);
        if (result5 == 0) {
            System.out.println("水动力模型计算:复制执行文件与config文件写入失败。。。");
            return;
        }

        int result6 = writeDataToConfig(hsfx_model_template_run_plan, hsfx_model_template_input, hsfx_model_template_output);
        if (result6 == 0) {
            System.out.println("水动力模型计算:config文件写入失败。。。");
            return;
        }
        //调用模型计算
        System.out.println("水动力模型计算:开始水动力模型计算。。。");
        System.out.println("水动力模型计算路径为。。。" + hsfx_model_template_run_plan + File.separator + "startUp.bat");
        runModelExe(hsfx_model_template_run_plan + File.separator + "startUp.bat");
        System.out.println("水动力模型计算:水动力模型计算结束。。。");

        //判断是否执行成功，是否有error文件
        String errorStr = hsfx_model_template_output + File.separator + "error.txt";
        File errorFile = new File(errorStr);
        if (errorFile.exists()) {//存在表示执行失败
            planInfo.setnPlanstatus(-1L);
        } else {
            planInfo.setnPlanstatus(2L);
        }
        ywkPlaninfoDao.save(planInfo);

        //解析模型结果调用GIS服务-生成图片 -存在表示执行失败
        if (!errorFile.exists()) {
            try {
                //解析淹没过程文件数据入库
                // saveGridProcessToDb(planId);
                //解析最大水深文件数据入库
                //saveGridMaxToDb(planId);

                //如果模型运行成功-解析过程文件生成图片
                planProcessDataService.readDepthCsvFile(hsfx_model_template_output, "process", planInfo.getnModelid(), planId);
                //解析最大水深文件
                planProcessDataService.readDepthCsvFile(hsfx_model_template_output, "maxDepth", planInfo.getnModelid(), planId);
            } catch (Exception e) {
                System.out.println("模型结果解析失败！");
            }

        }


    }

    private void runModelExe(String modelRunPath) {
        BufferedReader br = null;
        BufferedReader brError = null;
        try {
            // 执行exe cmd可以为字符串(exe存放路径)也可为数组，调用exe时需要传入参数时，可以传数组调用(参数有顺序要求)
            Process p = Runtime.getRuntime().exec(modelRunPath);
            String line = null;
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            brError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            // while ((line = br.readLine()) != null || (line = brError.readLine()) != null)
            // {
            while ((line = brError.readLine()) != null) {
                // 输出exe输出的信息以及错误信息
                System.out.println(line);
            }
            if (brError.readLine() != null) {
                System.out.println("模型调用失败！");
            } else {
                System.out.println("模型调用成功！");
            }
        } catch (Exception e) {
            System.out.println("模型调用失败！");
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private int copyExeFile(String hsfx_model_template_run, String hsfx_model_template_run_plan) {

        String exeUrl = hsfx_model_template_run + File.separator + "HSFXYB_MODEL.exe";
        String exeInputUrl = hsfx_model_template_run_plan + File.separator + "HSFXYB_MODEL.exe";
        String batUrl = hsfx_model_template_run + File.separator + "startUp.bat";
        String batInputUrl = hsfx_model_template_run_plan + File.separator + "startUp.bat";
        try {
            FileUtil.copyFile(exeUrl, exeInputUrl, true);
            FileUtil.copyFile(batUrl, batInputUrl, true);
            System.err.println("水动力模型计算：copy执行文件exe,bat文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("水动力模型计算：copy执行文件exe,bat文件错误" + e.getMessage());
            return 0;
        }
    }

    private int writeDataToConfig(String hsfx_model_template_run_plan, String hsfx_model_template_input, String hsfx_model_template_output) {
        List<String> finals = new ArrayList<>();

        //String configUrl = "/Users/xiongchao/小清河/洪水风险调控/yierwei0128提交版/database/Xqh1_Guojia_50的副本"+File.separator+"config.txt";

        String configUrl = hsfx_model_template_run_plan + File.separator + "config.txt";

        String erweiInputBDUrl = "BD&&" + hsfx_model_template_input + File.separator + "erwei" + File.separator + "BD.csv";
        String erweiInputINUrl = "IN&&" + hsfx_model_template_input + File.separator + "erwei" + File.separator + "IN.csv";
        String erweiInputWGUrl = "WG&&" + hsfx_model_template_input + File.separator + "erwei" + File.separator + "WG.csv";
        String erweiInputTDUrl = "TD&&" + hsfx_model_template_input + File.separator + "erwei" + File.separator + "TD.csv";
        String erweiInputJDUrl = "JD&&" + hsfx_model_template_input + File.separator + "erwei" + File.separator + "JD.csv";
        String erweiInputResultUrl = "result&&" + hsfx_model_template_output + File.separator + "erwei" + File.separator + "result.csv";//输出
        String erweiInputProcessUrl = "process&&" + hsfx_model_template_output + File.separator + "erwei" + File.separator + "process.csv";//输出
        String erweiInputOverflowUrl = "overflow&&" + hsfx_model_template_output + File.separator + "erwei" + File.separator + "overflow.csv";//输出
        String erweiInputKuikouUrl = "kuikou&&" + hsfx_model_template_output + File.separator + "erwei" + File.separator + "kuikou.csv";//输出
        String yiweiInputBNDUrl = "BND&&" + hsfx_model_template_input + File.separator + "yiwei" + File.separator + "BND.csv";
        String yiweiInputINIUrl = "INI&&" + hsfx_model_template_input + File.separator + "yiwei" + File.separator + "INI.csv";
        String yiweiInputSECUrl = "SEC&&" + hsfx_model_template_input + File.separator + "yiwei" + File.separator + "SEC.csv";
        String yiweiInputSEC0Url = "SEC0&&" + hsfx_model_template_input + File.separator + "yiwei" + File.separator + "SEC0.csv";
        String yiweiInputSTRUrl = "STR&&" + hsfx_model_template_input + File.separator + "yiwei" + File.separator + "STR.csv";
        String yiweiInputFHDUrl = "FHD&&" + hsfx_model_template_input + File.separator + "yiwei" + File.separator + "FHD.csv";
        String yiweiInputCTRUrl = "CTR&&" + hsfx_model_template_input + File.separator + "yiwei" + File.separator + "CTR.csv";
        String yiweiInputDischargeUrl = "Discharge&&" + hsfx_model_template_output + File.separator + "yiwei" + File.separator + "Discharge.csv";//输出
        String yiweiInputDischarge0Url = "Discharge0&&" + hsfx_model_template_output + File.separator + "yiwei" + File.separator + "Discharge0.csv";//输出
        String yiweiInputWaterlevelUrl = "Waterlevel&&" + hsfx_model_template_output + File.separator + "yiwei" + File.separator + "Waterlevel.csv";//输出
        String yiweiInputWaterlevel0Url = "Waterlevel0&&" + hsfx_model_template_output + File.separator + "yiwei" + File.separator + "Waterlevel0.csv";//输出
        String yiweiInputWaterdepthUrl = "Waterdepth&&" + hsfx_model_template_output + File.separator + "yiwei" + File.separator + "Waterdepth.csv";//输出
        String yiweiInputWaterdepth0Url = "Waterdepth0&&" + hsfx_model_template_output + File.separator + "yiwei" + File.separator + "Waterdepth0.csv";//输出
        String JinduUrl = "jindu&&" + hsfx_model_template_output + File.separator + "jindu.txt";//输出
        String errorUrl = "error&&" + hsfx_model_template_output + File.separator + "error.txt";//输出

        finals.add(erweiInputBDUrl);
        finals.add(erweiInputINUrl);
        finals.add(erweiInputWGUrl);
        finals.add(erweiInputTDUrl);
        finals.add(erweiInputJDUrl);
        finals.add(erweiInputResultUrl);
        finals.add(erweiInputProcessUrl);
        finals.add(erweiInputOverflowUrl);
        finals.add(erweiInputKuikouUrl);
        finals.add(yiweiInputBNDUrl);
        finals.add(yiweiInputINIUrl);
        finals.add(yiweiInputSECUrl);
        finals.add(yiweiInputSEC0Url);
        finals.add(yiweiInputSTRUrl);
        finals.add(yiweiInputFHDUrl);
        finals.add(yiweiInputCTRUrl);
        finals.add(yiweiInputDischargeUrl);
        finals.add(yiweiInputDischarge0Url);
        finals.add(yiweiInputWaterlevelUrl);
        finals.add(yiweiInputWaterlevel0Url);
        finals.add(yiweiInputWaterdepthUrl);
        finals.add(yiweiInputWaterdepth0Url);
        finals.add(JinduUrl);
        finals.add(errorUrl);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(configUrl, false)); // 附加
            // 添加新的数据行
            for (int i = 0; i < finals.size(); i++) {
                String s = finals.get(i);
                if (i == finals.size() - 1) {
                    bw.write(s);
                } else {
                    bw.write(s);
                    bw.newLine();
                }
            }
            bw.close();
            System.out.println("写入水动力模型config成功");
            return 1;
        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            System.out.println("写入水动力模型config失败");
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            System.out.println("写入水动力模型config失败");
            e.printStackTrace();
            return 0;
        }

    }

    private int copyOtherCsv(String hsfx_model_template, String hsfx_model_template_input) {
        String INIUrl = hsfx_model_template + File.separator + "yiwei" + File.separator + "INI.csv";
        String INIInputUrl = hsfx_model_template_input + File.separator + "yiwei" + File.separator + "INI.csv";

        String SecUrl = hsfx_model_template + File.separator + "yiwei" + File.separator + "SEC.csv";
        String SecInputUrl = hsfx_model_template_input + File.separator + "yiwei" + File.separator + "SEC.csv";

        String Sec0Url = hsfx_model_template + File.separator + "yiwei" + File.separator + "SEC0.csv";
        String Sec0InputUrl = hsfx_model_template_input + File.separator + "yiwei" + File.separator + "SEC0.csv";

        String StrUrl = hsfx_model_template + File.separator + "yiwei" + File.separator + "STR.csv";
        String StrInputUrl = hsfx_model_template_input + File.separator + "yiwei" + File.separator + "STR.csv";

        String FhdUrl = hsfx_model_template + File.separator + "yiwei" + File.separator + "FHD.csv";
        String FhdInputUrl = hsfx_model_template_input + File.separator + "yiwei" + File.separator + "FHD.csv";

        String shujuUrl = hsfx_model_template + File.separator + "erwei" + File.separator + "数据.xls";
        String shujuInputUrl = hsfx_model_template_input + File.separator + "erwei" + File.separator + "数据.xls";

        String InUrl = hsfx_model_template + File.separator + "erwei" + File.separator + "IN.csv";
        String InInputUrl = hsfx_model_template_input + File.separator + "erwei" + File.separator + "IN.csv";

        String JDUrl = hsfx_model_template + File.separator + "erwei" + File.separator + "JD.csv";
        String JDInputUrl = hsfx_model_template_input + File.separator + "erwei" + File.separator + "JD.csv";

        String TDUrl = hsfx_model_template + File.separator + "erwei" + File.separator + "TD.csv";
        String TDInputUrl = hsfx_model_template_input + File.separator + "erwei" + File.separator + "TD.csv";

        try {
            FileUtil.copyFile(INIUrl, INIInputUrl, true); //一维的
            FileUtil.copyFile(SecUrl, SecInputUrl, true); //一维的
            FileUtil.copyFile(Sec0Url, Sec0InputUrl, true); //一维的
            FileUtil.copyFile(StrUrl, StrInputUrl, true); //一维的
            FileUtil.copyFile(FhdUrl, FhdInputUrl, true); //一维的
            FileUtil.copyFile(shujuUrl, shujuInputUrl, true); //二维的
            FileUtil.copyFile(InUrl, InInputUrl, true); //二维的
            FileUtil.copyFile(JDUrl, JDInputUrl, true); //二维的
            FileUtil.copyFile(TDUrl, TDInputUrl, true); //二维的
            System.err.println("水动力模型计算：copy输入文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("水动力模型计算：copy输入文件错误" + e.getMessage());
            return 0;
        }

    }

    private int writeDataToInputWGCsv(String hsfx_model_template, String hsfx_model_template_input, YwkPlaninfo planInfo) {

        List<YwkPlaninFloodRoughness> byPlanId = ywkPlaninFloodRoughnessDao.findByPlanId(planInfo.getnPlanid());
        //String WGInputUrl = "/Users/xiongchao/小清河/洪水风险调控/yierwei0128提交版/database/Xqh1_Guojia_50的副本"+File.separator+"WG.csv";
        String WGInputUrl = hsfx_model_template_input + File.separator + "erwei" + File.separator + "WG.csv";
        //String WGInputReadUrl = "/Users/xiongchao/小清河/洪水风险调控/yierwei0128提交版/database/Xqh2_Xinhecun_50/erwei" + File.separator+"WG.csv";
        String WGInputReadUrl = hsfx_model_template + File.separator + "erwei" + File.separator + "WG.csv";

        List<List<String>> readDatas = new ArrayList();
        /* 读取数据 */
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(WGInputReadUrl)), "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                List<String> split = Arrays.asList(lineTxt.split(","));
                readDatas.add(split);
            }
        } catch (Exception e) {
            System.err.println("水动力模型计算：WG.csv输入文件读取错误:read errors :" + e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            //BufferedWriter bw = new BufferedWriter(new FileWriter(WGInputUrl, false)); // 附加
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(WGInputUrl, false), "UTF-8"));
            for (int i = 1; i < readDatas.size(); i++) {
                readDatas.get(i).set(4, byPlanId.get(0).getGridSynthesizeRoughness() + "");
            }
            for (int i = 0; i < readDatas.size(); i++) {
                List<String> strings = readDatas.get(i);
                String line = "";
                for (String s : strings) {
                    line = line + s + ",";
                }
                line = line.substring(0, line.length() - 1);
                bw.write(line);
                bw.newLine();
            }
            bw.close();
            System.out.println("水动力模型计算：WG.csv输入文件写入成功");
            return 1;
        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水动力模型计算：WG.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            System.out.println("水动力模型计算：WG.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }

    }

    private int writeDataToInputBDCsv(String hsfx_model_template, String hsfx_model_template_input, YwkPlaninfo planInfo, int size) {

        //获取溃口入参数据
        YwkPlaninFloodBreak floodBreak = ywkPlaninFloodBreakDao.findByNPlanid(planInfo.getnPlanid());
        //溃口基本信息表
        YwkBreakBasic breakBasic = ywkBreakBasicDao.findById(floodBreak.getBreakId()).get();

        //String BDInputUrl = "/Users/xiongchao/小清河/洪水风险调控/yierwei0128提交版/database/Xqh1_Guojia_50的副本"+File.separator+"BD.csv";
        String BDInputUrl = hsfx_model_template_input + File.separator + "erwei" + File.separator + "BD.csv";
        //String BDInputReadUrl = "/Users/xiongchao/小清河/洪水风险调控/yierwei0128提交版/database/Xqh2_Xinhecun_50/erwei" + File.separator+"BD.csv";
        String BDInputReadUrl = hsfx_model_template + File.separator + "erwei" + File.separator + "BD.csv";

        List<List<String>> readDatas = new ArrayList();
        /* 读取数据 */
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(BDInputReadUrl)), "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                List<String> split = Arrays.asList(lineTxt.split(","));
                readDatas.add(split);
            }
        } catch (Exception e) {
            System.err.println("水动力模型计算：BD.csv输入文件读取错误:read errors :" + e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            //BufferedWriter bw = new BufferedWriter(new FileWriter(BDInputUrl, false)); // 附加
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(BDInputUrl, false), "UTF-8"));
            readDatas.get(1).set(3, breakBasic.getBreakNo() + "");//设置溃口通道编号
            readDatas.get(3).set(3, size + "");//设置计算结束时间
            for (int i = 0; i < readDatas.size(); i++) {
                List<String> strings = readDatas.get(i);
                int sizeList = strings.size();
                if (sizeList < 4) {//TODO 写死4列 不能多不能少
                    List<String> newList = new ArrayList<>();
                    newList.addAll(strings);
                    for (int j = sizeList; j < 4; j++) {
                        newList.add("");
                    }
                    readDatas.set(i, newList);
                }
            }

            for (int i = 0; i < readDatas.size(); i++) {

                List<String> strings = readDatas.get(i);
                String line = "";
                for (String s : strings) {
                    line = line + s + ",";
                }
                line = line.substring(0, line.length() - 1);
                bw.write(line);
                bw.newLine();
            }
            bw.close();
            System.out.println("水动力模型计算：BD.csv输入文件写入成功");
            return 1;
        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水动力模型计算：BD.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            System.out.println("水动力模型计算：BD.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }

    }

    private List<YwkPlaninRiverRoughness> getCTRCsvDatas(String planId) {
        List<YwkPlaninFloodRoughness> byPlanId = ywkPlaninFloodRoughnessDao.findByPlanId(planId);
        YwkPlaninFloodRoughness ywkPlaninFloodRoughness = byPlanId.get(0);
        //查出结果集
        List<YwkPlaninRiverRoughness> byPlanRoughnessId = ywkPlaninRiverRoughnessDao.findByPlanRoughnessIdOrderByMileageAsc(ywkPlaninFloodRoughness.getPlanRoughnessid());

        return byPlanRoughnessId;
    }

    private int writeDataToInputCTRCsv(String hsfx_model_template, String hsfx_model_template_input, List<YwkPlaninRiverRoughness> ctrCsvDatas, YwkPlaninfo planInfo, int size) {

        String CTRInputUrl = hsfx_model_template_input + File.separator + "yiwei" + File.separator + "CTR.csv";
        //String CTRInputUrl = "/Users/xiongchao/小清河/洪水风险调控/yierwei0128提交版/database/Xqh1_Guojia_50的副本"+File.separator+"CTR.csv";
        String CTRInputReadUrl = hsfx_model_template + File.separator + "yiwei" + File.separator + "CTR.csv";
        //String CTRInputReadUrl = "/Users/xiongchao/小清河/洪水风险调控/yierwei0128提交版/database/Xqh2_Xinhecun_50/yiwei" + File.separator+"CTR.csv";

        List<List<String>> readDatas = new ArrayList();
        /* 读取数据 */
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(CTRInputReadUrl)), "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                List<String> split = Arrays.asList(lineTxt.split(","));
                readDatas.add(split);
            }
        } catch (Exception e) {
            System.err.println("水动力模型计算：CTR.csv输入文件读取错误:read errors :" + e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            //获取溃口入参数据
            YwkPlaninFloodBreak floodBreak = ywkPlaninFloodBreakDao.findByNPlanid(planInfo.getnPlanid());
            //溃口基本信息表
            YwkBreakBasic breakBasic = ywkBreakBasicDao.findById(floodBreak.getBreakId()).get();

            //BufferedWriter bw = new BufferedWriter(new FileWriter(CTRInputUrl, false)); // 附加
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(CTRInputUrl, false), "UTF-8"));
            //数据小于等于excel的个数
            if (ctrCsvDatas.size() <= readDatas.size() - 1) {//9个数据，7个excel 1个表头 TODO 这个if已经测试了 但是else没有测试呢

                for (int i = 0; i < ctrCsvDatas.size(); i++) {
                    List<String> strings = readDatas.get(i + 1);
                    YwkPlaninRiverRoughness ywkPlaninRiverRoughness = ctrCsvDatas.get(i);//数据是从0开始
                    try {
                        strings.set(2, ywkPlaninRiverRoughness.getMileage() + "");
                        strings.set(3, ywkPlaninRiverRoughness.getRoughness() + "");
                    } catch (Exception e) {
                        List<String> newStrings = new ArrayList<>();
                        newStrings.addAll(strings);
                        newStrings.add(2, ywkPlaninRiverRoughness.getMileage() + "");
                        newStrings.add(3, ywkPlaninRiverRoughness.getRoughness() + "");
                        readDatas.add(i + 1, newStrings);
                    }
                }
                for (int i = 1; i < readDatas.size(); i++) {
                    List<String> strings = readDatas.get(i);
                    if (i == 2) {
                        strings.set(1, size - 1 + "");//模拟时间
                    } else if (i == 4) {
                        strings.set(1, planInfo.getnOutputtm() + "");//输出时间步长
                    } else if (i == 8) { //溃口里程
                        strings.set(1, breakBasic.getBreakMileage() + "");
                    } else if (i == 9) {//溃口底高程
                        strings.set(1, floodBreak.getBreakBottomElevation() + "");
                    } else if (i == 10) {//溃口宽度
                        strings.set(1, floodBreak.getBreakWidth() + "");
                    } else if (i == 12) {//起溃水位
                        strings.set(1, floodBreak.getStartZ() + "");
                    }
                }
                //还要去除原来的csv里面的里程数据
                for (int i = ctrCsvDatas.size() + 1; i < readDatas.size(); i++) {
                    List<String> strings = readDatas.get(i);
                    if (strings.size() == 4) {
                        strings.set(2, "");
                        strings.set(3, "");
                    }
                }
            } else {//数据大于excel的个数
                for (int i = 1; i < readDatas.size(); i++) {
                    YwkPlaninRiverRoughness ywkPlaninRiverRoughness = ctrCsvDatas.get(i - 1);//数据是从0开始
                    List<String> strings = readDatas.get(i);
                    strings.set(2, ywkPlaninRiverRoughness.getMileage() + "");
                    strings.set(3, ywkPlaninRiverRoughness.getRoughness() + "");
                    if (i == 2) {
                        strings.set(1, size - 1 + "");//模拟时间 比数据的个数少1
                    } else if (i == 4) {
                        strings.set(1, planInfo.getnOutputtm() + "");//输出时间步长
                    } else if (i == 8) { //溃口里程
                        strings.set(1, breakBasic.getBreakMileage() + "");
                    } else if (i == 9) {//溃口底高程
                        strings.set(1, floodBreak.getBreakBottomElevation() + "");
                    } else if (i == 10) {//溃口宽度
                        strings.set(1, floodBreak.getBreakWidth() + "");
                    } else if (i == 12) {//起溃水位
                        strings.set(1, floodBreak.getStartZ() + "");
                    }
                }
                for (int i = readDatas.size() - 1; i < ctrCsvDatas.size(); i++) {
                    List list = new ArrayList();
                    list.add("");
                    list.add("");
                    YwkPlaninRiverRoughness ywkPlaninRiverRoughness = ctrCsvDatas.get(i);//数据是从0开始
                    list.set(2, ywkPlaninRiverRoughness.getMileage() + "");
                    list.set(3, ywkPlaninRiverRoughness.getRoughness() + "");
                    readDatas.add(list);
                }
            }//组装完数据

            for (int i = 0; i < readDatas.size(); i++) {
                List<String> strings = readDatas.get(i);
                String line = "";
                for (String s : strings) {
                    line = line + s + ",";
                }
                line = line.substring(0, line.length() - 1);
                bw.write(line);
                bw.newLine();
            }
            bw.close();
            System.out.println("水动力模型计算：溃口CTR.csv输入文件写入成功");
            return 1;
        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水动力模型计算：溃口CTR.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            System.out.println("水动力模型计算：溃口CTR.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }


    }

    private void getBndCsvChanelDatas(List<Map<String, Object>> breakList, Map<String, List<Map<String, Object>>> channels, String planId) {

        //一个计划id关联俩个，入点跟出点。一个入点对应数据有多条
        YwkPlaninFloodBreak floodBreak = ywkPlaninFloodBreakDao.findByNPlanid(planId);

        //通过breakId 查出起始点跟结束点数据，1是入流，-1是出流。里程
        List<YwkFloodChannelBasic> byBreakIdOrderByOutflowAndInflowType = ywkFloodChannelBasicDao.findByBreakIdOrderByOutflowAndInflowTypeDesc(floodBreak.getBreakId());
        Map<String, Object> entranceMap = new HashMap();
        entranceMap.put("stcd", "entrance");
        entranceMap.put("mileage", byBreakIdOrderByOutflowAndInflowType.get(0).getMileage());
        Map<String, Object> exportMap = new HashMap<>();
        exportMap.put("stcd", "export");
        exportMap.put("mileage", byBreakIdOrderByOutflowAndInflowType.get(1).getMileage());
        breakList.add(entranceMap);
        breakList.add(exportMap);

        List<String> breakIds = byBreakIdOrderByOutflowAndInflowType.stream().map(YwkFloodChannelBasic::getFloodChannelId).collect(Collectors.toList());
        //通过起始点跟结束点查某个区间范围点数据
        List<YwkFloodChannelFlow> byChannelBasicIds = ywkFloodChannelFlowDao.findByChannelBasicIds(breakIds);
        //按照channelBasicIds分组
        Map<String, List<YwkFloodChannelFlow>> channelCollect = byChannelBasicIds.stream().collect(Collectors.groupingBy(YwkFloodChannelFlow::getFloodChannelId));
        List<Map<String, Object>> entrance = poToListMap(channelCollect.get(byBreakIdOrderByOutflowAndInflowType.get(0).getFloodChannelId()));
        List<Map<String, Object>> export = poToListMap(channelCollect.get(byBreakIdOrderByOutflowAndInflowType.get(1).getFloodChannelId()));
        channels.put("entrance", entrance);
        channels.put("export", export);
    }

    public <T> List<Map<String, Object>> poToListMap(List<T> source) {
        List<Map<String, Object>> target = new ArrayList<>();
        if (CollectionUtils.isEmpty(source)) {
            return target;
        }
        for (Object s : source) {
            Map map = JSON.parseObject(JSON.toJSONString(s), Map.class);
            target.add(map);
        }
        return target;
    }

    //, List<Map<String, Object>> breakIds, Map<String, List<Map<String, Object>>> channels
    private void getBndCsvBoundaryDatas(List<Map<String, Object>> bndDatas, List<String> bndList, YwkPlaninfo planInfo) {

        List<YwkModelBoundaryBasicRl> modelBoundaryList = ywkModelBoundaryBasicRlDao.findByIdmodelId(planInfo.getnModelid());//基本信息中间表
        List<String> collectStcd = modelBoundaryList.stream().map(YwkModelBoundaryBasicRl::getStcd).collect(Collectors.toList());//中间表的stcd集合
        //上游、下游，根据stcd集合获取上游下游的列表
        List<YwkBoundaryBasic> boundaryBasicList = ywkBoundaryBasicDao.findByStcdInOrderByBoundaryType(collectStcd);
        Map upperMap = new HashMap(); //上游
        upperMap.put("stcd", boundaryBasicList.get(0).getStcd());
        upperMap.put("mileage", 1);
        Map downMap = new HashMap();//下游
        downMap.put("stcd", boundaryBasicList.get(1).getStcd());
        downMap.put("mileage", 0);
        bndDatas.add(upperMap);
        bndDatas.add(downMap);
        //截取非上下游的其他数据
        List<YwkBoundaryBasic> newBoundaryBasicList = new ArrayList(boundaryBasicList.subList(2, boundaryBasicList.size()));
        List<Map<String, Object>> newBoundaryBasics = poToListMap(newBoundaryBasicList);

        //按照历程排序从小到大
        Collections.sort(newBoundaryBasics, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Double mileage1 = Double.parseDouble(o1.get("mileage") + "");
                Double mileage2 = Double.parseDouble(o2.get("mileage") + "");
                return mileage1.compareTo(mileage2);//降序
            }
        });

        bndDatas.addAll(newBoundaryBasics);

        //TODO 获取里程的入参信息
        List<YwkPlaninFloodBoundary> byPlanIds = ywkPlaninFloodBoundaryDao.findByPlanId(planInfo.getnPlanid());
        //按照stcd分组。
        Map<String, List<YwkPlaninFloodBoundary>> groupCollect = byPlanIds.stream().collect(Collectors.groupingBy(YwkPlaninFloodBoundary::getStcd));
        for (Map.Entry<String, List<YwkPlaninFloodBoundary>> entry : groupCollect.entrySet()) {
            List<YwkPlaninFloodBoundary> value = entry.getValue();
            for (int i = 0; i < value.size(); i++) {
                bndList.add(""); //初始化list
            }
            break;
        }
        Map<String, List<Map<String, Object>>> planinFloodBoundaryMap = new HashMap<>();
        for (Map.Entry<String, List<YwkPlaninFloodBoundary>> entry : groupCollect.entrySet()) {
            String key = entry.getKey();
            List<YwkPlaninFloodBoundary> value = entry.getValue();
            List<Map<String, Object>> list = poToListMap(value);
            planinFloodBoundaryMap.put(key, list);
        }
        for (int i = 0; i < bndDatas.size(); i++) {
            Map<String, Object> map = bndDatas.get(i);
            String stcd = map.get("stcd") + "";//下游水文，其他流量
            //TODO 这个地方个数必须正确
            List<Map<String, Object>> listMap = planinFloodBoundaryMap.get(stcd);
            //按照时间排序
            Collections.sort(listMap, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Long relativeTime = Long.parseLong(o1.get("relativeTime") + "");
                    Long relativeTime1 = Long.parseLong(o2.get("relativeTime") + "");
                    return relativeTime.compareTo(relativeTime1);
                }
            });//TODO 这个地方目前是根据上游点时间点个数写点
            int size = planinFloodBoundaryMap.get(bndDatas.get(1).get("stcd")).size();
            if (listMap.size() < size) {
                for (int z = 0; z < size - listMap.size(); z++) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("q", listMap.get(listMap.size() - 1).get("q"));
                    listMap.add(m);
                }
            } else {
                listMap = listMap.subList(0, size);
            }
            //TODO 这个地方如果是24个点的话，不够24个的以最后一个点的值补够24个点

            for (int j = 0; j < listMap.size(); j++) {
                Map<String, Object> boundryMap = listMap.get(j);
                String s = bndList.get(j);
                Double q;
                if (i == 1) {///下游水文，其他流量
                    q = Double.parseDouble(boundryMap.get("z") + "");
                } else {
                    q = Double.parseDouble(boundryMap.get("q") + "");
                }
                if (StringUtils.isEmpty(s)) {// bw.write("\"time\"" + "," + "\"pcp\"");
                    s = j + "," + q + "";
                } else {
                    s = s + "," + j + "," + q;
                }
                bndList.set(j, s);
            }
        }
    }

    private int writeDataToInputBNDCsv(String hsfx_model_template_input, List<Map<String, Object>> datas, List<String> list) {
        String BndInputUrl = hsfx_model_template_input + File.separator + "yiwei" + File.separator + "BND.csv";

        try {
            //BufferedWriter bw = new BufferedWriter(new FileWriter(BndInputUrl, false)); // 附加
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(BndInputUrl, false), "UTF-8"));

            //BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/xiongchao/小清河/洪水风险调控/yierwei0128提交版/database/Xqh1_Guojia_50的副本/yiwei/BND.csv", false)); // 附加
            // 添加新的数据行

            String head = "\"上边界条件\"" + "," + "\"\"" + ",";
            head = head + "\"下边界条件\"" + "," + "\"\"";

            Map<String, Object> map1 = datas.get(0);
            Map<String, Object> map2 = datas.get(1);
            int size = list.size();
            String flood_boundary = size + "," + map1.get("mileage") + "," + size + "," + map2.get("mileage");
            for (int i = 2; i < datas.size(); i++) {
                Map map = datas.get(i);
                String stcd = map.get("stcd") + "";
//                if ("entrance".equals(stcd)) {
//                    head = head + "," + "\"分洪道入流\"" + "," + "\"\"";
//                } else if ("export".equals(stcd)) {
//                    head = head + "," + "\"分洪道出流\"" + "," + "\"\"";
//                } else {
//                    head = head + "," + "\"侧向集中入流条件\"" + "," + "\"\"";
//                }
                head = head + "," + "\"侧向集中入流条件\"" + "," + "\"\"";
                flood_boundary = flood_boundary + "," + size + "," + datas.get(i).get("mileage");
            }
            bw.write(head);
            bw.newLine();
            bw.write(flood_boundary);
            bw.newLine();
            for (int i = 0; i < list.size(); i++) {
                String str = list.get(i);
                bw.write(str);
                bw.newLine();
            }
            bw.close();
            System.out.println("水动力模型边界条件输入文件写入成功");
            return 1;
        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水动力模型边界条件输入文件写入失败");
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            System.out.println("水动力模型边界条件输入文件写入失败");
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取方案模型计算进度
     *
     * @param planId
     * @return
     */
    @Override
    public Object getHsfxModelRunStatus(String planId) {
        JSONObject jsonObject = new JSONObject();
        //运行进度
        jsonObject.put("process", 0.0);
        //运行状态 1运行结束 0运行中
        jsonObject.put("runStatus", 0);
        //运行时间
        jsonObject.put("time", 0);
        //描述
        jsonObject.put("describ", "模型运行准备中！");

        String hsfx_path = PropertiesUtil.read("/filePath.properties").getProperty("HSFX_MODEL");
        String hsfx_model_template_output = hsfx_path +
                File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                + File.separator + planId; //输出的地址
        //判断是否有error文件
        String errorPath = hsfx_model_template_output + File.separator + "error.txt";
        String processPath = hsfx_model_template_output + File.separator + "jindu.txt";
        String picPath = hsfx_model_template_output + File.separator + "pic.txt";
        File picFile = new File(picPath);
        File errorFile = new File(errorPath);
        //存在表示执行失败
        if (errorFile.exists()) {
            return jsonObject;
        }
        File jinduFile = new File(processPath);
        if (!jinduFile.exists()) {
            //运行进度
            jsonObject.put("process", 0.0);
            //运行状态 1运行结束 0运行中
            jsonObject.put("runStatus", 0);
            //运行时间
            jsonObject.put("time", 0);
            jsonObject.put("describ", "模型运行准备中！");
            return jsonObject;
        } else {
            //运行状态 1运行结束 0运行中
            jsonObject.put("runStatus", 0);
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(processPath))));
                String lineTxt = br.readLine();
                if (lineTxt != null) {
                    String[] split = lineTxt.split("&&");
                    //运行时间
                    jsonObject.put("time", Double.parseDouble(split[1] + ""));
                }
                String lineTxt2 = br.readLine();
                if (lineTxt2 != null) {
                    String[] split = lineTxt2.split("&&");
                    //运行进度
                    double process = Double.parseDouble(split[1] + "");
                    jsonObject.put("process", process * 0.94);
                    if (process == 100.0) {
                        jsonObject.put("describ", "水深过程渲染效果图生成中！");
                    } else {
                        jsonObject.put("describ", "模型运行中！");
                    }
                    if (picFile.exists()) {
                        jsonObject.put("process", 100.0);
                        jsonObject.put("runStatus", 1);
                        jsonObject.put("describ", "模型运行结束！");
                    }
                }
                return jsonObject;
            } catch (Exception e) {
                System.err.println("进度读取错误！" + e.getMessage());
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObject;
    }

    /**
     * 模型输出淹没历程-及最大水深图片列表
     *
     * @param planId
     * @return
     */
    @Override
    public Object getModelProcessPicList(String planId) {
        List<String> processList = new ArrayList<>();
        //方案基本信息
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        if (planInfo != null) {
            Date startTime = planInfo.getdCaculatestarttm();
            Date endTime = planInfo.getdCaculateendtm();
            Long aLong = planInfo.getnOutputtm();
            int count = 1;
            for (Date time = startTime; time.before(endTime); time = DateUtil.getNextHour(startTime, count)) {
                count++;
            }
            for (int i = 0; i < count; i++) {
                processList.add((i + 1) + "");
            }
        }
        return processList;
    }

    @Override
    public void previewPicFile(HttpServletRequest request, HttpServletResponse response, String planId, String picId) {
        YwkPlaninfo planinfo = ywkPlaninfoDao.findOneById(planId);
        //图片路径
        String outputAbsolutePath = GisPathConfigurationUtil.getOutputPictureAbsolutePath() + "/" + planinfo.getnModelid() + "/" + planId;
        //图片路径
        String processOutputAbsolutePath = outputAbsolutePath + "/process/";
        String filePath = null;
        if ("maxDepth".equals(picId)) {
            filePath = outputAbsolutePath + "/maxDepth.png";
        } else {
            filePath = processOutputAbsolutePath + picId + ".png";
        }
        try {
            File file = new File(filePath);
            if (file != null && file.exists()) {
                int length = Integer.MAX_VALUE;
                if (file.length() < length) {
                    length = (int) file.length();
                }
                response.setContentLength(length);
                String fileName = file.getName();
                FileUtil.openFilebBreakpoint(request, response, file, fileName);
            }
        } catch (Exception e) {
        }
    }


    //工程联合调度方案管理

    @Override
    public Paginator<YwkPlaninfo> getPlanList(PaginatorParam paginatorParam) {
        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("XT_LHDD");

        List<Criterion> orders = paginatorParam.getOrders();
        if(orders==null){
            orders = new ArrayList<>();
        }

        Criterion criterion = new Criterion();
        criterion.setFieldName("nCreatetime");
        criterion.setOperator(Criterion.DESC);
        orders.add(criterion);
        paginatorParam.setOrders(orders);

        List<Criterion> conditions = paginatorParam.getConditions();
        if(conditions==null) {
            conditions = new ArrayList<>();
            paginatorParam.setConditions(conditions);
        }
        Criterion criterion1 = new Criterion();
        criterion1.setFieldName("planSystem");
        criterion1.setOperator(Criterion.EQ);
        criterion1.setValue(planSystem);
        conditions.add(criterion1);
        Paginator<YwkPlaninfo> all = ywkPlaninfoDao.findAll(paginatorParam);
        return all;
    }


    @Override
    public List<Map> getAllBoundaryByPlanId(String planId) {

        //获取防洪保护区设置入参列表
        List<Map<String, Object>> boundaryByPlanIds = ywkBoundaryBasicDao.findBoundaryByPlanId(planId);

        Map<String, List<Map<String, Object>>> boundaryDataMap = new HashMap<>();
        List<Map<String, Object>> boundaryResults = new ArrayList<>();
        Set<String> stcds = new TreeSet<>();
        for (Map<String, Object> boundaryByPlanIdMap : boundaryByPlanIds) {
            String stcd = boundaryByPlanIdMap.get("STCD") + "";
            stcds.add(stcd);
        }
        for (Map<String, Object> boundaryByPlanIdMap : boundaryByPlanIds) {
            String stcd = boundaryByPlanIdMap.get("STCD") + "";
            List datas = boundaryDataMap.get(stcd);
            if (org.apache.commons.collections.CollectionUtils.isEmpty(datas)) {
                datas = new ArrayList();
            }
            datas.add(boundaryByPlanIdMap);
            boundaryDataMap.put(stcd,datas);
        }
        List<String> list = new ArrayList(stcds);
        for (String s : list){
            Map<String,Object> resultMap = new HashMap();
            List<Map<String, Object>> maps = boundaryDataMap.get(s);
            resultMap.put("stcd",maps.get(0).get("STCD"));
            resultMap.put("name",maps.get(0).get("BOUNDARYNM"));
            resultMap.put("mileage",maps.get(0).get("MILEAGE"));
            resultMap.put("boundaryType",maps.get(0).get("BOUNDARY_TYPE"));
            Collections.sort(maps, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Integer relative_time1 = Integer.parseInt(o1.get("RELATIVE_TIME")+"");
                    Integer relative_time2 = Integer.parseInt(o2.get("RELATIVE_TIME")+"");
                    return relative_time1-relative_time2;
                }
            });
            resultMap.put("list",maps);
            boundaryResults.add(resultMap);
        }
        List<Map> results = JSON.parseArray(JSON.toJSONString(boundaryResults, SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue), Map.class);
        return results;
    }


    @Override
    public Map<String, Object> getAllRoughnessByPlanId(String planId) {

        List<YwkPlaninFloodRoughness> byPlanId = ywkPlaninFloodRoughnessDao.findByPlanId(planId);
        if (org.apache.commons.collections.CollectionUtils.isEmpty(byPlanId)){
            return null;
        }
        YwkPlaninFloodRoughness floodRoughness = byPlanId.get(0);

        List<YwkPlaninRiverRoughness> byPlanRoughnessIdOrderByMileageAsc = ywkPlaninRiverRoughnessDao.findByPlanRoughnessIdOrderByMileageAsc(floodRoughness.getPlanRoughnessid());

        YwkModelRoughnessParam roughnessParam = ywkModelRoughnessParamDao.findById(floodRoughness.getRoughnessParamid()).get();

        YwkModel model = ywkModelDao.findById(roughnessParam.getIdmodelId()).get();

        Map resultMap = new HashMap();
        resultMap.put("ywkPlaninFloodRoughness",floodRoughness);
        resultMap.put("ywkPlaninRiverRoughness",byPlanRoughnessIdOrderByMileageAsc);
        resultMap.put("model",model);
        return resultMap;
    }


    @Override
    public Map<String, Object> getAllBreakByPlanId(String planId) {

        Map resultMap = new HashMap();

        YwkPlaninFloodBreak ywkPlaninFloodBreak = ywkPlaninFloodBreakDao.findByNPlanid(planId);
        if (ywkPlaninFloodBreak == null){
            return new HashMap<>();
        }
        resultMap.put("ywkPlaninFloodBreak",ywkPlaninFloodBreak);
        YwkBreakBasic ywkBreakBasic = ywkBreakBasicDao.findById(ywkPlaninFloodBreak.getBreakId()).get();

        resultMap.put("ywkBreakBasic",ywkBreakBasic);

        return resultMap;
    }


    @Transactional
    @Override
    public void deleteAllInputByPlanId(String planId) {

        //删除边界条件
        ywkPlaninFloodBoundaryDao.deleteByPlanId(planId);
        //删除糙率
        List<YwkPlaninFloodRoughness> byPlanId = ywkPlaninFloodRoughnessDao.findByPlanId(planId);
        if (!org.apache.commons.collections.CollectionUtils.isEmpty(byPlanId)){
            ywkPlaninRiverRoughnessDao.deleteByPlanRoughnessId(byPlanId.get(0).getPlanRoughnessid());
        }
        ywkPlaninFloodRoughnessDao.deleteByPlanId(planId);

        //删除溃点
        ywkPlaninFloodBreakDao.deleteByNPlanid(planId);

        //删除方案基本信息
        ywkPlaninfoDao.deleteById(planId);

        //删除模型相关文件

    }
}
