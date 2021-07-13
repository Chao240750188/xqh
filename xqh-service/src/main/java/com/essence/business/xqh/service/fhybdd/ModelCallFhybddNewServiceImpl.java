package com.essence.business.xqh.service.fhybdd;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.essence.business.xqh.api.fhybdd.dto.*;
import com.essence.business.xqh.api.fhybdd.service.ModelCallFhybddNewService;
import com.essence.business.xqh.api.fhybdd.service.ModelCallHandleDataService;
import com.essence.business.xqh.common.util.*;
import com.essence.business.xqh.dao.dao.fhybdd.*;
import com.essence.business.xqh.dao.entity.fhybdd.*;
import com.essence.euauth.common.util.UuidUtil;
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
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ModelCallFhybddNewServiceImpl implements ModelCallFhybddNewService {


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


    /**
     * 根据方案id获取方案信息，并加入缓存
     * @param planId
     * @return
     */
    @Override
    public YwkPlaninfo getPlanInfoByPlanId(String planId){

        YwkPlaninfo planinfo = (YwkPlaninfo) CacheUtil.get("planInfo", planId);
        if (planinfo == null){
            planinfo = ywkPlaninfoDao.findOneById(planId);
            if (planinfo != null){
                CacheUtil.saveOrUpdate("planInfo", planId, planinfo);
            }
        }
        return planinfo;
    }
    @Override
    public String savePlan(ModelCallBySWDDVo vo) {

        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("XT_SWYB");
        List<YwkPlaninfo> isAll = ywkPlaninfoDao.findByCPlannameAndPlanSystem(vo.getcPlanname(), planSystem);
        if (!CollectionUtils.isEmpty(isAll)){
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
        if (0 == timeType){
            ywkPlaninfo.setnOutputtm(Long.parseLong(step+""));//设置间隔分钟
        }else {
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
    public List<Map<String,Object>> getRainfalls(YwkPlaninfo planInfo)throws Exception {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatHour = new SimpleDateFormat("yyyy-MM-dd HH:");
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();

        String startTimeStr = format1.format(startTime);
        String endTimeStr = format1.format(endTime);

        //Long step = planInfo.getnOutputtm() / 60;//步长
        Long step = planInfo.getnOutputtm();//分钟
        //switch (step)
        //Long count = ywkPlaninRainfallDao.countByPlanId(planInfo.getnPlanid());
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
                if(stringObjectMap.get("DRP") == null){//todo 改变pcp雨量模型的逻辑。如果站点有一个时间没数据，则把有数据的时间全部置为null
                    flag = true;
                }

                ll.add(stringObjectMap);
            }
            if (flag){ //todo pcp判断Drp置为null
               /* for (Map map : ll){
                    Map newMap = new HashMap(map);
                    if(newMap.get("DRP") != null ){
                        newMap.put("DRP",null);
                    }
                    map = newMap;
                    System.out.println("map:"+map.get("DRP"));
                }*/
               ll.clear();
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
        CacheUtil.saveOrUpdate("rainfall", planInfo.getnPlanid()+"new", results);
        return results;
    }


    @Override
    public Map<String, Object> getModelList() {
        String catchmentArea = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_MODEL_TYPE_CATCHMENT_AREA");
        String reach = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_MODEL_TYPE_REACH");
        List<YwkModel> ywkModelByModelType = ywkModelDao.getYwkModelByModelType(catchmentArea);
        List<YwkModel> ywkModelByModelType1 = ywkModelDao.getYwkModelByModelType(reach);
        Map result = new HashMap();
        result.put("catchmentArea",ywkModelByModelType);
        result.put("reach",ywkModelByModelType1);
        return result;
    }

    @Override
    public List<WrpRcsBsin> getRcsList() {

        List<WrpRcsBsin> all = wrpRcsBsinDao.findAll();
        return all;
    }

    @Override
    public List<Map<String,Object>> getTriggerFlow(YwkPlaninfo planInfo, String rcsId) {//
        List<Map<String,Object>> results = new ArrayList<>();
        YwkPlanTriggerRcs ywkPlanTriggerRcs = ywkPlanTriggerRcsDao.findByNPlanidAndRcsId(planInfo.getnPlanid(),rcsId);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (ywkPlanTriggerRcs == null){
            Date startTime = planInfo.getdCaculatestarttm();
            Date endTime = planInfo.getdCaculateendtm();
            //Long step = planInfo.getnOutputtm() / 60;//步长
            Long step = planInfo.getnOutputtm();//步长
            while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
                Map<String,Object> map = new HashMap();
                String hourStart = format.format(startTime);
                map.put("date",hourStart);
                map.put("flow",null);
                results.add(map);
                startTime = DateUtil.getNextMinute(startTime, step.intValue());
            }
        }else {
            List<YwkPlanTriggerRcsFlow> triggerRcsFlows = ywkPlanTriggerRcsFlowDao.findByTriggerRcsId(ywkPlanTriggerRcs.getId());
            for (YwkPlanTriggerRcsFlow flow : triggerRcsFlows){
                Map map = new HashMap();
                map.put("date",format.format(flow.getAbsoluteTime()));
                map.put("flow",flow.getFlow());
                results.add(map);
            }
        }
        return results;
    }


    @Override
    public Workbook exportTriggerFlowTemplate(YwkPlaninfo planInfo) {//TODO 回写的没写

        //封装边界模板数据
        XSSFWorkbook workbook = new XSSFWorkbook();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //设置样式
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);//字体高度
        font.setFontName("宋体");//字体
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        style.setWrapText(true);//自动换行
        XSSFSheet sheet = workbook.createSheet("预报断面流量数据导入模板");
        //填充表头
        //第一行
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue("时间");
        XSSFCell cell1 = row.createCell(1);
        cell1.setCellStyle(style);
        cell1.setCellValue("流量值");
        //设置自动列宽
        sheet.setColumnWidth(0, 5100);

        //封装时间列
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        //Long step = planInfo.getnOutputtm() / 60;//步长
        Long step = planInfo.getnOutputtm();//步长
        int beginLine = 1;
        //封装数据
        while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
            XSSFRow row1 = sheet.createRow(beginLine);
            row1.createCell(0).setCellValue(format.format(startTime));
            beginLine++;
            startTime = DateUtil.getNextMinute(startTime, step.intValue());
        }
        return workbook;
    }


    @Transactional
    @Override
    public List<Map<String,Object>> importTriggerFlowData(MultipartFile mutilpartFile, YwkPlaninfo planInfo,String rcsId) {
        List<Map<String,Object>> result = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        //解析ecxel数据 不包含第一行
        List<String[]> excelList = ExcelUtil.readFiles(mutilpartFile, 1);
        // 判断有无数据 时间-每个边界的值集合
        try {
            if (excelList != null && excelList.size() > 0) {
                // 遍历每行数据（除了标题）
                for (int i = 0; i < excelList.size(); i++) {
                    Map<String, Object> dataMap = new HashMap<>();
                    String[] strings = excelList.get(i);
                    dataMap.put("relativeTime",i);
                    dataMap.put("absoluteTime",strings[0].trim());//TODO 日期没处理
                    df.parse( strings[0].trim());
                    if (strings != null && strings.length > 1) {
                        // 封装每列（每个指标项数据）
                        try {
                            dataMap.put("flow",Double.parseDouble(strings[1].trim()));
                        }catch (Exception e){
                            dataMap.put("flow",null);
                        }
                    }else {
                        dataMap.put("flow",null);
                    }
                    result.add(dataMap);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        int size = 0;
        //Long step = planInfo.getnOutputtm() / 60;//步长
        Long step = planInfo.getnOutputtm();//步长
        while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
            size++;
            startTime = DateUtil.getNextMinute(startTime, step.intValue());
        }
        if (result.size() != size){//TODO 必须数量跟时间序列一致
            return new ArrayList<>();
        }
        //1、先插入
        ywkPlanTriggerRcsDao.deleteByNPlanidAndRcsId(planInfo.getnPlanid(), rcsId);

        YwkPlanTriggerRcs ywkPlanTriggerRcs = new YwkPlanTriggerRcs();
        ywkPlanTriggerRcs.setId(StrUtil.getUUID());
        ywkPlanTriggerRcs.setnPlanid(planInfo.getnPlanid());
        ywkPlanTriggerRcs.setRcsId(rcsId);
        ywkPlanTriggerRcs.setCreateTime(new Date());
        ywkPlanTriggerRcsDao.save(ywkPlanTriggerRcs);

        List<YwkPlanTriggerRcsFlow> insertFlow = new ArrayList<>();
        for (Map<String,Object> map : result){
            YwkPlanTriggerRcsFlow ywkPlanTriggerRcsFlow = new YwkPlanTriggerRcsFlow();
            ywkPlanTriggerRcsFlow.setRelativeTime(Long.parseLong(map.get("relativeTime")+""));
            try {
                ywkPlanTriggerRcsFlow.setAbsoluteTime(df.parse( map.get("absoluteTime")+""));
            }catch (Exception e){
                e.printStackTrace();
            }
            ywkPlanTriggerRcsFlow.setCreateTime(new Date());
            ywkPlanTriggerRcsFlow.setId(StrUtil.getUUID());
            ywkPlanTriggerRcsFlow.setFlow( map.get("flow")== null?null:(Double)map.get("flow"));
            ywkPlanTriggerRcsFlow.setTriggerRcsId(ywkPlanTriggerRcs.getId());
            insertFlow.add(ywkPlanTriggerRcsFlow);
        }
        ywkPlanTriggerRcsFlowDao.deleteByTriggerRcsId(ywkPlanTriggerRcs.getId());
        ywkPlanTriggerRcsFlowDao.saveAll(insertFlow);

        return result;
    }

    @Override
    public Workbook exportRainfallTemplate(YwkPlaninfo planInfo) throws Exception{

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
        while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
            beginLine++;
            sheet.setColumnWidth(beginLine, 5100);
            XSSFCell c = row.createCell(beginLine);
            c.setCellValue(format.format(startTime));
            c.setCellStyle(style);
            startTime = DateUtil.getNextMinute(startTime, step.intValue());
        }

        List<Map<String, Object>> rainfalls = getRainfalls(planInfo);
        int rowLine = 1;
        for (Map<String,Object> map :rainfalls ){
            //A.STCD,A.STNM,A.LGTD,A.LTTD,B.TM,B.DRP
            String stcd = map.get("STCD")+"";
            String stnm = map.get("STNM")+"";
            XSSFRow row1 = sheet.createRow(rowLine);
            XSSFCell c = row1.createCell(0);
            c.setCellValue(stcd);
            c.setCellStyle(style);
            XSSFCell c1 = row1.createCell(1);
            c1.setCellValue(stnm);
            c1.setCellStyle(style);
            rowLine++;
            int j = 2;
            List<Map<String,Object>> list = (List<Map<String, Object>>) map.get("LIST");
            if (CollectionUtils.isEmpty(list)){
                continue;
            }
            for (Map<String,Object> m : list){
                XSSFCell cell2 = row1.createCell(j);
                Object drp = m.get("DRP");
                j++;
                if (drp == null){
                    continue;
                }
                cell2.setCellValue(drp+"");
                cell2.setCellStyle(style);
            }

        }
        return workbook;
    }

    @Override
    public List<Map<String, Object>> importRainfallData(MultipartFile mutilpartFile, YwkPlaninfo planInfo) {
        List<StStbprpB> stbp = stStbprpBDao.findAll();
        Map<String, StStbprpB> collect = stbp.stream().collect(Collectors.toMap(StStbprpB::getStcd, Function.identity()));
        List<Map<String,Object>> result = new ArrayList<>();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //解析ecxel数据 不包含第一行
        List<String[]> excelList = ExcelUtil.readFiles(mutilpartFile, 0);

        if (excelList == null || excelList.size() < 2) {
            return new ArrayList<>();
        }
        List<String> head = Arrays.asList(excelList.get(0));
        List<String> timeHead = new ArrayList<>();
        if (!CollectionUtils.isEmpty(head) && head.size()>1){
            timeHead = head.subList(2,head.size());
        }else {
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
            while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
                String time = timeHead.get(size);
                String format = df.format(startTime);
                if (!time.equals(format)){
                    System.out.println("表头时间有问题");
                    return new ArrayList<>();
                }
                timeResults.add(format);
                size++;
                startTime = DateUtil.getNextMinute(startTime, step.intValue());
            }
        }catch (Exception e){
            System.out.println("时间序列不一致");
            return new ArrayList<>();
        }
        if (timeHead.size() != size){//TODO 必须数量跟时间序列一致
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
                if (!CollectionUtils.isEmpty(datas) && datas.size()>1){
                    dataMap.put("id",datas.get(0));
                    dataMap.put("name",datas.get(1));
                    dataMap.put("list",datas.subList(2,datas.size()));
                }else {
                    continue;
                }
                result.add(dataMap);
            }

        }catch (Exception e){
            System.out.println("解析csv文件出现问题");
            return new ArrayList<>();
        }
        List<Map<String,Object>> inSertList = new ArrayList();

        for (Map<String,Object> map : result){
            Map<String,Object> insert = new HashMap();
            String id = map.get("id") + "";
            String name = map.get("name") + "";
            StStbprpB stStbprpB = collect.get(id);
            if (stStbprpB == null){
                System.out.println("测站编码有问题，被修改过");
                continue;
            }

            insert.put("STCD",id);
            insert.put("STNM",name);
            insert.put("LGTD",stStbprpB.getLgtd());
            insert.put("LTTD",stStbprpB.getLttd());
            List<Map<String,Object>> list11 = new ArrayList<>();
            List<String> list = (List<String>) map.get("list");
            int z = 0;
            if (CollectionUtils.isEmpty(list)|| list.size() != timeHead.size()){
                list11 = new ArrayList<>();
                insert.put("LIST",list11);
                inSertList.add(insert);
                continue;
            }
            for (String drp : list){
                String time = timeResults.get(z);
                z++;
                Map<String,Object> m = new HashMap();
                m.put("STCD",id);
                m.put("STNM",name);
                m.put("TM",time);

                if (  drp == null || "".equals(drp.trim())){//drp为null 或者空串
                    m.put("DRP",null);
                }else {
                    drp = drp.trim();
                    try {
                        Double drpValue = Double.parseDouble(drp);
                        m.put("DRP",drpValue);
                    }catch (Exception e){
                        m.put("DRP",null);
                        System.out.println("雨量值不正确");
                        continue;
                    }
                }
                list11.add(m);

            }//里层for循环
            insert.put("LIST",list11);
            inSertList.add(insert);
        }

        //TODO 修改雨量值并不修改基础表的数据，只修改缓存的的数据
        CacheUtil.saveOrUpdate("rainfall", planInfo.getnPlanid()+"new"+format1.format(planInfo.getdCaculatestarttm()), inSertList);
        return inSertList;
    }

    @Autowired
    ModelCallHandleDataService modelCallHandleDataService;
    @Transactional
    @Override
    public void saveRainfallsFromCacheToDb(YwkPlaninfo planInfo,List<Map<String,Object>> results) {


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<String> timeResults = new ArrayList();
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        //Long step = planInfo.getnOutputtm() / 60;//步长
        Long step = planInfo.getnOutputtm();//分钟
        while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
            String hourStart = format.format(startTime);
            timeResults.add(hourStart);
            startTime = DateUtil.getNextMinute(startTime, step.intValue());
        }
        List<YwkPlaninRainfall> insertList = new ArrayList<>();
        for (Map<String,Object> map : results){//
            String id = map.get("STCD") + "";//STCD<STNM<LGTD<LTTD<LIST
            List<Map<String,Object>> list = (List<Map<String,Object>>) map.get("LIST");
            int z = 0;
            if (CollectionUtils.isEmpty(list)){
                continue;
            }
            for (Map<String,Object> m : list){//TODO null值不存
                Object drp = m.get("DRP");
                String time = timeResults.get(z);//TODO 为什么不用tm时间呢  防止表格被人 改 用程序的时间
                z++;
                if (drp == null){
                    continue;
                }
                String drpStr = (drp+"").trim();
                if ("".equals(drpStr)){
                    continue;
                }
                YwkPlaninRainfall ywkPlaninRainfall = new YwkPlaninRainfall();
                ywkPlaninRainfall.setcId(StrUtil.getUUID());
                ywkPlaninRainfall.setcStcd(id);
                ywkPlaninRainfall.setnPlanid(planInfo.getnPlanid());
                try {
                    ywkPlaninRainfall.setdTime(format.parse(time));
                }catch (Exception e){
                    System.out.println("时间出错");
                    e.printStackTrace();
                }
                try {
                    Double drpValue = Double.parseDouble(drpStr);
                    ywkPlaninRainfall.setnDrp(drpValue);
                }catch (Exception e){
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
        System.out.println("futures.size"+futures.size());
        CompletableFuture[] completableFutures = new CompletableFuture[futures.size()];
        for (int j = 0;j < futures.size();j++){
            completableFutures[j] = futures.get(j);
        }
        System.out.println("等待多线程执行完毕。。。。");
        CompletableFuture.allOf(completableFutures).join();//全部执行完后 然后主线程结束
        System.out.println("多线程执行完毕，结束主线程。。。。");
        return ;

    }
    @Async
    @Override
    public void modelCallPCP(YwkPlaninfo planInfo) {
        System.out.println("模型运算线程！"+Thread.currentThread().getName());
        try {
            //雨量信息表
            long startTime=System.currentTimeMillis();   //获取开始时间

            Long aLong = ywkPlaninRainfallDao.countByPlanId(planInfo.getnPlanid());
            if (aLong == 0L) {
                System.out.println("方案雨量表没有保存数据");
                throw new RuntimeException("方案雨量表没有保存数据");
            }
            YwkPlaninfo newPlan = new YwkPlaninfo();
            BeanUtils.copyProperties(planInfo,newPlan);
            newPlan.setdCaculatestarttm(DateUtil.getNextHour(planInfo.getdCaculatestarttm(),-72));
            newPlan.setdCaculateendtm(planInfo.getdCaculatestarttm());
            List<Map<String, Object>> before72results = getRainfalls(newPlan);//todo 这个地方先获取72小时之前的雨量，后获取现在的雨量。保证缓存的准去行

            List<Map<String, Object>> results = getRainfalls(planInfo);//todo 这个地方先获取72小时之前的雨量，后获取现在的雨量。保证缓存的准去行
            Map<Object, Map<String, Object>> resultMap = results.stream().collect(Collectors.toMap(t -> t.get("STCD"), Function.identity()));

            for (Map<String,Object> map : before72results){
                String stcd = map.get("STCD")+"";
                List<Map<String,Object>> beforeList = (List<Map<String, Object>>) map.get("LIST");
                List<Object> beforeDrpValues = beforeList.stream().map(key -> key.get("DRP")).collect(Collectors.toList());//todo new 7月8日
                Map<String, Object> thisMap = resultMap.get(stcd);
                List<Map<String,Object>> thisList = (List<Map<String, Object>>) thisMap.get("LIST");
                List<Object> drpValues = thisList.stream().map(key -> key.get("DRP")).collect(Collectors.toList());//todo new 7月8日
                if (beforeDrpValues.size()== 0 && drpValues.size() != 0){
                    thisMap.put("LIST",new ArrayList<>());
                    thisList = new ArrayList<>();
                }else if (beforeDrpValues.size() != 0 && drpValues.size() == 0){
                    map.put("LIST",new ArrayList<>());
                    beforeList = new ArrayList<>();
                }
                if (!CollectionUtils.isEmpty(beforeList)){
                    beforeList = beforeList.subList(0,beforeList.size()-1);
                }
                beforeList.addAll(thisList);
                map.put("LIST",beforeList);
            }

            //before72results.addAll(results);
            if (CollectionUtils.isEmpty(before72results)) {
                System.out.println("雨量信息为空，无法计算");
                throw new RuntimeException("雨量信息为空，无法计算");
            }
            //创建入参、出参
            String SWYB_PCP_HANDLE_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_BASE_NEW_PCP_HANDLE_MODEL_PATH");
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


            File inputPcpPath = new File(PCP_HANDLE_MODEL_TEMPLATE_INPUT);
            File outPcpPath = new File(PCP_HANDLE_MODEL_TEMPLATE_OUTPUT);
            File runPcpPath = new File(PCP_HANDLE_MODEL_RUN_PLAN);

            inputPcpPath.mkdir();
            outPcpPath.mkdir();
            runPcpPath.mkdir();

            //TODO 模型先算第一步，数据处理模型pcp_model
            //1，写入pcp_HRU.csv
            int result0 = writeDataToInputPcpHRUCsv(PCP_HANDLE_MODEL_TEMPLATE_INPUT, PCP_HANDLE_MODEL_TEMPLATE, planInfo);
            if (result0 == 0) {
                System.out.println("水文模型之PCP模型:写入pcp_HRU失败");
                throw new RuntimeException("水文模型之PCP模型:写入pcp_HRU失败");
            }
            //2,写入pcp_station.csv
            int result1 = writeDataToInputPcpStationCsv(PCP_HANDLE_MODEL_TEMPLATE_INPUT, before72results, planInfo);
            if (result1 == 0) {
                System.out.println("水文模型之PCP模型:写入pcp_station失败");
                throw new RuntimeException("水文模型之PCP模型:写入pcp_station失败");
            }
            //3.复制cofig以及可执行文件
            int result2 = copyPCPExeFile(PCP_HANDLE_MODEL_RUN, PCP_HANDLE_MODEL_RUN_PLAN);
            if (result2 == 0) {
                System.out.println("水文模型之PCP模型:复制执行文件与config文件写入失败。。。");
                throw new RuntimeException("水文模型之PCP模型:复制执行文件与config文件写入失败。。。");

            }
            //4,修改config文件
            int result3 = writeDataToPcpConfig(PCP_HANDLE_MODEL_RUN_PLAN, PCP_HANDLE_MODEL_TEMPLATE_INPUT, PCP_HANDLE_MODEL_TEMPLATE_OUTPUT);
            if (result3 == 0) {
                System.out.println("水文模型之PCP模型:修改config文件失败");
                throw new RuntimeException("水文模型之PCP模型:修改config文件失败");

            }
            long endTime =System.currentTimeMillis();   //获取开始时间
            System.out.println("水文模型之PCP模型:组装pcp模型所用的参数的时间为:"+(endTime-startTime) +"毫秒");
            //5.调用模型
            //调用模型计算
            startTime = System.currentTimeMillis();
            System.out.println("水文模型之PCP模型:开始水文模型PCP模型计算。。。");
            System.out.println("水文模型之PCP模型:模型计算路径为。。。" + PCP_HANDLE_MODEL_RUN_PLAN + File.separator + "startUp.bat");
            runModelExe(PCP_HANDLE_MODEL_RUN_PLAN + File.separator + "startUp.bat");
            endTime = System.currentTimeMillis();
            System.out.println("水文模型之PCP模型:模型计算结束。。。，所用时间为:"+(endTime-startTime) +"毫秒");
            //TODO 判断模型是否执行成功
            //判断是否执行成功，是否有error文件
            String pcp_result = PCP_HANDLE_MODEL_TEMPLATE_OUTPUT + File.separator + "hru_p_result.csv";
            File pcp_resultFile = new File(pcp_result);
            if (pcp_resultFile.exists()) {//存在表示执行成功
                System.out.println("水文模型之PCP模型:pcp模型执行成功hru_p_result.csv文件存在");
                planInfo.setnPlanstatus(3L);//todo 3L表示模型pcp成功 -1l是失败。2L是俩个模型都成功
                ywkPlaninfoDao.save(planInfo);
                CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
                return;//todo  执行成功
            } else {
                System.out.println("水文模型之PCP模型:pcp模型执行成功hru_p_result.csv文件不存在");//todo 执行失败
                throw new RuntimeException("水文模型之PCP模型:pcp模型执行成功hru_p_result.csv文件不存在");
            }

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("模型执行失败了。。。。。。联系管理员"+e.getMessage());
            planInfo.setnPlanstatus(-1L);
            ywkPlaninfoDao.save(planInfo);
            CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
        }
    }
    @Async
    @Override
    public void modelCall(YwkPlaninfo planInfo) {

        System.out.println("模型运算线程！"+Thread.currentThread().getName());
        try {

            //创建入参、出参
            String SWYB_PCP_HANDLE_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_BASE_NEW_PCP_HANDLE_MODEL_PATH");
            String SWYB_SHUIWEN_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_BASE_NEW_SHUIWEN_MODEL_PATH");
            String template = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");
            String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");
            String run = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_RUN");


            String PCP_HANDLE_MODEL_TEMPLATE_OUTPUT = SWYB_PCP_HANDLE_MODEL_PATH + File.separator + out
                    + File.separator + planInfo.getnPlanid();//输出的地址
            //TODO 判断模型是否执行成功  执行不成功不让执行
            //判断是否执行成功，是否有error文件
            String pcp_result = PCP_HANDLE_MODEL_TEMPLATE_OUTPUT + File.separator + "hru_p_result.csv";
            File pcp_resultFile = new File(pcp_result);
            if (pcp_resultFile.exists() && planInfo.getnPlanstatus() == 3L) {//存在表示执行成功 todo 3L表示pcp模型执行成功
                System.out.println("水文模型之PCP模型:pcp模型执行成功hru_p_result.csv文件存在");
            } else {
                System.out.println("水文模型之PCP模型:pcp模型执行成功hru_p_result.csv文件不存在");//todo 执行失败
                throw new RuntimeException("水文模型之PCP模型:pcp模型执行成功hru_p_result.csv文件不存在");
            }

            //另一个模型
            String SHUIWEN_MODEL_TEMPLATE = SWYB_SHUIWEN_MODEL_PATH + File.separator + template;
            String SHUIWEN_MODEL_TEMPLATE_INPUT = SHUIWEN_MODEL_TEMPLATE
                    + File.separator + "INPUT" + File.separator + planInfo.getnPlanid(); //输入的地址
            String SHUIWEN_MODEL_TEMPLATE_OUTPUT = SWYB_SHUIWEN_MODEL_PATH + File.separator + out
                    + File.separator + planInfo.getnPlanid();//输出的地址
            //模型运行的config
            String SHUIWEN_MODEL_RUN = SWYB_SHUIWEN_MODEL_PATH + File.separator + run;

            String SHUIWEN_MODEL_RUN_PLAN = SHUIWEN_MODEL_RUN + File.separator + planInfo.getnPlanid();


            File inputShuiWenPath = new File(SHUIWEN_MODEL_TEMPLATE_INPUT);
            File outShuiWenPath = new File(SHUIWEN_MODEL_TEMPLATE_OUTPUT);
            File runShuiWenPath = new File(SHUIWEN_MODEL_RUN_PLAN);

            inputShuiWenPath.mkdir();
            outShuiWenPath.mkdir();
            runShuiWenPath.mkdir();

            //TODO 上面的入参条件没存库
            //TODO 第二个shuiwen模型
            //6，预报断面ChuFaDuanMian、ChuFaDuanMian_shuru.csv组装
            int result4 = writeDataToInputShuiWenChuFaDuanMianCsv(SHUIWEN_MODEL_TEMPLATE_INPUT, planInfo);

            if (result4 == 0) {
                System.out.println("水文模型之水文模型:写入chufaduanmian跟chufaduanmian_shuru.csv失败");
                throw new RuntimeException("水文模型之水文模型:写入chufaduanmian跟chufaduanmian_shuru.csv失败");

            }
            //7 cope pcp模型的输出文件到水文模型的输入文件里
            int result5 = copeFirstOutPutHruP(PCP_HANDLE_MODEL_TEMPLATE_OUTPUT, SHUIWEN_MODEL_TEMPLATE_INPUT);
            if (result5 == 0) {
                System.out.println("水文模型之水文模型:copy数据处理模型PCP输出文件hru_p_result失败");
                throw new RuntimeException("水文模型之水文模型:copy数据处理模型PCP输出文件hru_p_result失败");

            }
            //8 写入model_selection.csv 输入文件
            int result6 = writeDataToInputShuiWenModelSelectionCsv(SHUIWEN_MODEL_TEMPLATE_INPUT, planInfo);
            if (result6 == 0) {
                System.out.println("水文模型之水文模型:写入model_selection.csv 输入文件失败");
                throw new RuntimeException("水文模型之水文模型:写入model_selection.csv 输入文件失败");

            }

            //9 copy剩下的率定csv输入文件
            int result7 = copyOtherShuiWenLvDingCsv(SHUIWEN_MODEL_TEMPLATE, SHUIWEN_MODEL_TEMPLATE_INPUT);
            if (result7 == 0) {
                System.out.println("水文模型之水文模型: copy剩下的率定csv输入文件失败");
                throw new RuntimeException("水文模型之水文模型: copy剩下的率定csv输入文件失败");

            }
            //10 复制shuiwen cofig以及可执行文件
            int result8 = copyShuiWenExeFile(SHUIWEN_MODEL_RUN, SHUIWEN_MODEL_RUN_PLAN);
            if (result8 == 0) {
                System.out.println("水文模型之水文模型:复制执行文件与config文件写入失败。。。");
                throw new RuntimeException("水文模型之水文模型:复制执行文件与config文件写入失败。。。");

            }
            //11,修改shuiwen config文件
            int result9 = writeDataToShuiWenConfig(SHUIWEN_MODEL_RUN_PLAN, SHUIWEN_MODEL_TEMPLATE_INPUT, SHUIWEN_MODEL_TEMPLATE_OUTPUT, 0, planInfo);
            if (result9 == 0) {
                System.out.println("水文模型之水文模型:修改config文件失败");
                throw new RuntimeException("水文模型之水文模型:修改config文件失败");

            }
            //12,
            //调用模型计算
            System.out.println("水文模型之水文模型:开始水文模型shuiwen模型计算。。。");
            System.out.println("水文模型之水文模型:模型计算路径为。。。" + SHUIWEN_MODEL_RUN_PLAN + File.separator + "startUp.bat");
            runModelExe(SHUIWEN_MODEL_RUN_PLAN + File.separator + "startUp.bat");

            //判断是否执行成功，是否有error文件
            String errorStr = SHUIWEN_MODEL_TEMPLATE_OUTPUT + File.separator + "error_log.txt";
            File errorFile = new File(errorStr);
            if (errorFile.exists()) {//存在表示执行失败
                System.out.println("水文模型之水文模型:模型计算失败。。存在error_log文件");
                planInfo.setnPlanstatus(-1L);
                ywkPlaninfoDao.save(planInfo);
                CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
                return;//todo 执行失败
            } else {
                System.out.println("水文模型之水文模型:模型计算成功。。不存在error_log文件");
                planInfo.setnPlanstatus(2L);
                ywkPlaninfoDao.save(planInfo);
                CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
                return;//todo  执行成功
            }

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("模型执行失败了。。。。。。联系管理员"+e.getMessage());
            planInfo.setnPlanstatus(-1L);
            ywkPlaninfoDao.save(planInfo);
            CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
        }


    }
    /*@Async
    @Override
    public void modelCall(YwkPlaninfo planInfo) {

        System.out.println("模型运算线程！"+Thread.currentThread().getName());
        try {
            //雨量信息表
            long startTime=System.currentTimeMillis();   //获取开始时间

            Long aLong = ywkPlaninRainfallDao.countByPlanId(planInfo.getnPlanid());
            if (aLong == 0L) {
                System.out.println("方案雨量表没有保存数据");
                throw new RuntimeException("方案雨量表没有保存数据");
            }
            List<Map<String, Object>> results = getRainfalls(planInfo);
            if (CollectionUtils.isEmpty(results)) {
                System.out.println("雨量信息为空，无法计算");
                throw new RuntimeException("雨量信息为空，无法计算");
            }

            //创建入参、出参
            String SWYB_PCP_HANDLE_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_BASE_NEW_PCP_HANDLE_MODEL_PATH");
            String SWYB_SHUIWEN_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_BASE_NEW_SHUIWEN_MODEL_PATH");
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
                System.out.println("水文模型之PCP模型:写入pcp_HRU失败");
                throw new RuntimeException("水文模型之PCP模型:写入pcp_HRU失败");
            }
            //2,写入pcp_station.csv
            int result1 = writeDataToInputPcpStationCsv(PCP_HANDLE_MODEL_TEMPLATE_INPUT, results, planInfo);
            if (result1 == 0) {
                System.out.println("水文模型之PCP模型:写入pcp_station失败");
                throw new RuntimeException("水文模型之PCP模型:写入pcp_station失败");
            }
            //3.复制cofig以及可执行文件
            int result2 = copyPCPExeFile(PCP_HANDLE_MODEL_RUN, PCP_HANDLE_MODEL_RUN_PLAN);
            if (result2 == 0) {
                System.out.println("水文模型之PCP模型:复制执行文件与config文件写入失败。。。");
                throw new RuntimeException("水文模型之PCP模型:复制执行文件与config文件写入失败。。。");

            }
            //4,修改config文件
            int result3 = writeDataToPcpConfig(PCP_HANDLE_MODEL_RUN_PLAN, PCP_HANDLE_MODEL_TEMPLATE_INPUT, PCP_HANDLE_MODEL_TEMPLATE_OUTPUT);
            if (result3 == 0) {
                System.out.println("水文模型之PCP模型:修改config文件失败");
                throw new RuntimeException("水文模型之PCP模型:修改config文件失败");

            }
            long endTime =System.currentTimeMillis();   //获取开始时间
            System.out.println("水文模型之PCP模型:组装pcp模型所用的参数的时间为:"+(endTime-startTime) +"毫秒");
            //5.调用模型
            //调用模型计算
            startTime = System.currentTimeMillis();
            System.out.println("水文模型之PCP模型:开始水文模型PCP模型计算。。。");
            System.out.println("水文模型之PCP模型:模型计算路径为。。。" + PCP_HANDLE_MODEL_RUN_PLAN + File.separator + "startUp.bat");
            runModelExe(PCP_HANDLE_MODEL_RUN_PLAN + File.separator + "startUp.bat");
            endTime = System.currentTimeMillis();
            System.out.println("水文模型之PCP模型:模型计算结束。。。，所用时间为:"+(endTime-startTime) +"毫秒");
            startTime = System.currentTimeMillis();
            //TODO 判断模型是否执行成功
            //判断是否执行成功，是否有error文件
            String pcp_result = PCP_HANDLE_MODEL_TEMPLATE_OUTPUT + File.separator + "hru_p_result.csv";
            File pcp_resultFile = new File(pcp_result);
            if (pcp_resultFile.exists()) {//存在表示执行成功
                System.out.println("水文模型之PCP模型:pcp模型执行成功hru_p_result.csv文件存在");
            } else {
                System.out.println("水文模型之PCP模型:pcp模型执行成功hru_p_result.csv文件不存在");//todo 执行失败
                throw new RuntimeException("水文模型之PCP模型:pcp模型执行成功hru_p_result.csv文件不存在");
            }
            //TODO 上面的入参条件没存库
            //TODO 第二个shuiwen模型
            //6，预报断面ChuFaDuanMian、ChuFaDuanMian_shuru.csv组装
            int result4 = writeDataToInputShuiWenChuFaDuanMianCsv(SHUIWEN_MODEL_TEMPLATE_INPUT, planInfo);

            if (result4 == 0) {
                System.out.println("水文模型之水文模型:写入chufaduanmian跟chufaduanmian_shuru.csv失败");
                throw new RuntimeException("水文模型之水文模型:写入chufaduanmian跟chufaduanmian_shuru.csv失败");

            }
            //7 cope pcp模型的输出文件到水文模型的输入文件里
            int result5 = copeFirstOutPutHruP(PCP_HANDLE_MODEL_TEMPLATE_OUTPUT, SHUIWEN_MODEL_TEMPLATE_INPUT);
            if (result5 == 0) {
                System.out.println("水文模型之水文模型:copy数据处理模型PCP输出文件hru_p_result失败");
                throw new RuntimeException("水文模型之水文模型:copy数据处理模型PCP输出文件hru_p_result失败");

            }
            //8 写入model_selection.csv 输入文件
            int result6 = writeDataToInputShuiWenModelSelectionCsv(SHUIWEN_MODEL_TEMPLATE_INPUT, planInfo);
            if (result6 == 0) {
                System.out.println("水文模型之水文模型:写入model_selection.csv 输入文件失败");
                throw new RuntimeException("水文模型之水文模型:写入model_selection.csv 输入文件失败");

            }

            //9 copy剩下的率定csv输入文件
            int result7 = copyOtherShuiWenLvDingCsv(SHUIWEN_MODEL_TEMPLATE, SHUIWEN_MODEL_TEMPLATE_INPUT);
            if (result7 == 0) {
                System.out.println("水文模型之水文模型: copy剩下的率定csv输入文件失败");
                throw new RuntimeException("水文模型之水文模型: copy剩下的率定csv输入文件失败");

            }
            //10 复制shuiwen cofig以及可执行文件
            int result8 = copyShuiWenExeFile(SHUIWEN_MODEL_RUN, SHUIWEN_MODEL_RUN_PLAN);
            if (result8 == 0) {
                System.out.println("水文模型之水文模型:复制执行文件与config文件写入失败。。。");
                throw new RuntimeException("水文模型之水文模型:复制执行文件与config文件写入失败。。。");

            }
            //11,修改shuiwen config文件
            int result9 = writeDataToShuiWenConfig(SHUIWEN_MODEL_RUN_PLAN, SHUIWEN_MODEL_TEMPLATE_INPUT, SHUIWEN_MODEL_TEMPLATE_OUTPUT, 0, planInfo);
            if (result9 == 0) {
                System.out.println("水文模型之水文模型:修改config文件失败");
                throw new RuntimeException("水文模型之水文模型:修改config文件失败");

            }
            endTime = System.currentTimeMillis();
            System.out.println("水文模型之PCP模型:组装shuiwen模型所用的参数的时间为:"+(endTime-startTime) +"毫秒");

            //12,
            //调用模型计算
            startTime = System.currentTimeMillis();
            System.out.println("水文模型之水文模型:开始水文模型shuiwen模型计算。。。");
            System.out.println("水文模型之水文模型:模型计算路径为。。。" + SHUIWEN_MODEL_RUN_PLAN + File.separator + "startUp.bat");
            runModelExe(SHUIWEN_MODEL_RUN_PLAN + File.separator + "startUp.bat");
            endTime = System.currentTimeMillis();
            System.out.println("水文模型之水文模型:模型计算结束。。。所用时间为:"+(endTime-startTime) +"毫秒");

            //判断是否执行成功，是否有error文件
            String errorStr = SHUIWEN_MODEL_TEMPLATE_OUTPUT + File.separator + "error_log.txt";
            File errorFile = new File(errorStr);
            if (errorFile.exists()) {//存在表示执行失败
                System.out.println("水文模型之水文模型:模型计算失败。。存在error_log文件");
                planInfo.setnPlanstatus(-1L);
                ywkPlaninfoDao.save(planInfo);
                CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
                return;//todo 执行失败
            } else {
                System.out.println("水文模型之水文模型:模型计算成功。。不存在error_log文件");
                planInfo.setnPlanstatus(2L);
                ywkPlaninfoDao.save(planInfo);
                CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
                return;//todo  执行成功
            }

        }catch (Exception e){
            e.printStackTrace();
            System.out.println("模型执行失败了。。。。。。联系管理员"+e.getMessage());
            planInfo.setnPlanstatus(-1L);
            ywkPlaninfoDao.save(planInfo);
            CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
        }


    }*/
    @Autowired
    YwkPlanCalibrationZoneXajDao ywkPlanCalibrationZoneXajDao;
    @Async
    @Override
    public void ModelCallCalibration(YwkPlaninfo planInfo) {

        System.out.println("率定的模型运算中的线程名字！"+Thread.currentThread().getName());

        try {

            //创建入参、出参
            String SWYB_SHUIWEN_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_BASE_NEW_SHUIWEN_MODEL_PATH");
            String template = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");
            String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");
            String run = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_RUN");

            //TODO 二次运行 pcp模型就不用在执行了

            //另一个模型
            String SHUIWEN_MODEL_TEMPLATE = SWYB_SHUIWEN_MODEL_PATH + File.separator + template;
            String SHUIWEN_MODEL_TEMPLATE_INPUT = SHUIWEN_MODEL_TEMPLATE
                    + File.separator + "INPUT" + File.separator + planInfo.getnPlanid();
            String SHUIWEN_MODEL_TEMPLATE_INPUT_CALIBRATION = SHUIWEN_MODEL_TEMPLATE
                    + File.separator + "INPUT" + File.separator + planInfo.getnPlanid()+File.separator+"calibration"; //输入的地址 //TODO 加一个calibration
            String SHUIWEN_MODEL_TEMPLATE_OUTPUT_CALIBRATION = SWYB_SHUIWEN_MODEL_PATH + File.separator + out
                    + File.separator + planInfo.getnPlanid()+File.separator+"calibration";//率定的地址
            //模型运行的config
            String SHUIWEN_MODEL_RUN = SWYB_SHUIWEN_MODEL_PATH + File.separator + run;

            String SHUIWEN_MODEL_RUN_PLAN = SHUIWEN_MODEL_RUN + File.separator + planInfo.getnPlanid();

            File outShuiWenPath = new File(SHUIWEN_MODEL_TEMPLATE_OUTPUT_CALIBRATION);
            outShuiWenPath.mkdir();
            File inputShuiwenPath = new File(SHUIWEN_MODEL_TEMPLATE_INPUT_CALIBRATION);
            inputShuiwenPath.mkdir();

            String  catchModel = planInfo.getnModelid();
            String reachModel = planInfo.getnSWModelid();

            switch (catchModel){
                case "MODEL_SWYB_CATCHMENT_SCS":
                    //1 修改率定的SCS模型watershed.csv入参文件，watershed.csv
                    int result0 = writeCalibrationWatershedCsv(SHUIWEN_MODEL_TEMPLATE_INPUT,SHUIWEN_MODEL_TEMPLATE_INPUT_CALIBRATION,planInfo);
                    if (result0 == 0){
                        System.out.println("率定: 率定交互watershed.csv输入文件失败");
                        throw new RuntimeException("率定: 率定交互watershed.csv输入文件失败");
                    }
                    break;
                case "MODEL_SWYB_CATCHMENT_DWX":
                    //2 修改率定的单位线unit.csv文件
                    int result1 = writeCalibrationUnitCsv(SHUIWEN_MODEL_TEMPLATE_INPUT,SHUIWEN_MODEL_TEMPLATE_INPUT_CALIBRATION,planInfo);
                    if (result1 == 0){
                        System.out.println("率定: 率定交互unit.csv输入文件失败");
                        throw new RuntimeException("率定: 率定交互unit.csv输入文件失败");
                    }
                    break;
                case "MODEL_SWYB_CATCHMENT_XAJ"://todo 并未进行测试
                    //3 修改率定的v文件
                    int result2 = writeCalibrationXajCsv(SHUIWEN_MODEL_TEMPLATE_INPUT,SHUIWEN_MODEL_TEMPLATE_INPUT_CALIBRATION,planInfo);
                    if (result2 == 0){
                        System.out.println("率定: 率定交互新安江watershed.csv输入文件失败");
                        throw new RuntimeException("率定: 率定交互新安江watershed.csv输入文件失败");

                    }
                    break;
                case "MODEL_SWYB_CATCHMENT_ZN": //什么都不用做
                    break;
                default:
                    System.out.println("模型集水区编码错误");
                    throw new RuntimeException("率定: 模型集水区编码错误");

            }
            /**
             * 1：马斯京根法
             * 2：相关关系法
             * 3：智能方法
             */
            switch (reachModel){
                case "MODEL_SWYB_REACH_MSJG":
                    //4 修改率定的单位线msgj.csv文件
                    int result3 = writeCalibrationMsgjCsv(SHUIWEN_MODEL_TEMPLATE_INPUT,SHUIWEN_MODEL_TEMPLATE_INPUT_CALIBRATION,planInfo);
                    if (result3 == 0){
                        System.out.println("率定: 率定交互msgj.csv输入文件失败");
                        throw new RuntimeException("率定: 率定交互msgj.csv输入文件失败");

                    }
                    break;
                case "MODEL_SWYB_REACH_XGGX"://todo 未接入
                    int result4 = writeCalibrationXGGXCsv(SHUIWEN_MODEL_TEMPLATE_INPUT,SHUIWEN_MODEL_TEMPLATE_INPUT_CALIBRATION,planInfo);
                    if (result4 == 0){
                        System.out.println("率定: 率定交互相关关系Reach.csv输入文件失败");
                        throw new RuntimeException("率定: 率定交互相关关系Reach.csv输入文件失败");

                    }
                    break;
                case "MODEL_SWYB_REACH_ZN":
                    break;
                default:
                    System.out.println("模型河段编码错误");
                    throw new RuntimeException("率定: 模型河段编码错误");

            }

            //5,修改shuiwen config文件
            int result4 = writeDataToShuiWenConfig(SHUIWEN_MODEL_RUN_PLAN, SHUIWEN_MODEL_TEMPLATE_INPUT, SHUIWEN_MODEL_TEMPLATE_OUTPUT_CALIBRATION,1,planInfo);
            if (result4 == 0){
                System.out.println("率定:修改config文件失败");
                throw new RuntimeException("率定:修改config文件失败");
            }
            //6,调用模型计算
            System.out.println("率定:开始水文模型shuiwen模型计算。。。");
            System.out.println("率定:模型计算路径为。。。"+SHUIWEN_MODEL_RUN_PLAN + File.separator + "startUp.bat");
            runModelExe(SHUIWEN_MODEL_RUN_PLAN + File.separator + "startUp.bat");
            System.out.println("率定:模型计算结束。。。");

            //判断是否执行成功，是否有error文件
            String errorStr = SHUIWEN_MODEL_TEMPLATE_OUTPUT_CALIBRATION + File.separator + "error_log.txt";
            File errorFile = new File(errorStr);
            if (errorFile.exists()) {//存在表示执行失败
                System.out.println("率定:模型计算失败。。存在error_log文件");
                throw new RuntimeException("率定:模型计算失败。。存在error_log文件");

            } else {
                System.out.println("率定:模型计算成功。。不存在error_log文件");
                planInfo.setnCalibrationStatus(2L);
                ywkPlaninfoDao.save(planInfo);
                CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
            }

        }catch (Exception e){
            System.out.println("率定:模型计算失败。。存在error_log文件"+e.getMessage());
            e.printStackTrace();
            planInfo.setnCalibrationStatus(-1L);
            ywkPlaninfoDao.save(planInfo);
            CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
        }


    }

    /**
     * 相关关系的Csv率定写入
     * @param shuiwen_model_template_input
     * @param shuiwen_model_template_input_calibration
     * @param planInfo
     * @return
     */
    private int writeCalibrationXGGXCsv(String shuiwen_model_template_input, String shuiwen_model_template_input_calibration, YwkPlaninfo planInfo) {
        String reachInput = shuiwen_model_template_input + File.separator + "Reach.csv";
        String reachInputUpdate = shuiwen_model_template_input_calibration + File.separator + "Reach.csv";
        //String reachXiangGuanShujvInput = shuiwen_model_template_input + File.separator + "reach_xiangguan_shujv.csv";

        String riverId = planInfo.getRiverId();
        List<WrpRiverZone> wrpRiverZones = wrpRiverZoneDao.findByRvcd(riverId);

        //找河系下的子河系
        List<WrpRvrBsin> allByParentIdRiver = wrpRvrBsinDao.findAllByParentId(planInfo.getRiverId());

        List<String> rvcds = allByParentIdRiver.stream().map(WrpRvrBsin::getRvcd).collect(Collectors.toList());// 子河系id

        List<YwkPlanCalibrationZoneXggx> zones = ywkPlanCalibrationZoneXggxDao.findByNPlanid(planInfo.getnPlanid());
        Iterator<YwkPlanCalibrationZoneXggx> iterator = zones.iterator();
        while (iterator.hasNext()){
            YwkPlanCalibrationZoneXggx next = iterator.next();
            if (next.getXggxA() == null){
                iterator.remove();
            }
        }
        Map<String, YwkPlanCalibrationZoneXggx> zoneMap = zones.stream().collect(Collectors.toMap(YwkPlanCalibrationZoneXggx::getZoneId, Function.identity()));
        Map<String,YwkPlanCalibrationZoneXggx> dataMap = new HashMap<>();
        for (WrpRiverZone riverZone : wrpRiverZones){
            String rvcd = riverZone.getRvcd();
            Integer zoneId = riverZone.getZoneId();
            String zoneStr = "";
            String cid = riverZone.getcId();
            YwkPlanCalibrationZoneXggx ywkPlanCalibrationZoneXggx = zoneMap.get(cid);
            if (ywkPlanCalibrationZoneXggx == null){
                continue;
            }
            switch (zoneId){
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
            if (!CollectionUtils.isEmpty(rvcds)){
                for (String rvcdStr : rvcds){
                    dataMap.put(rvcdStr+zoneStr,ywkPlanCalibrationZoneXggx);//河系id 加 分区id 确定一个
                }
            }
            dataMap.put(rvcd+zoneStr,ywkPlanCalibrationZoneXggx);

        }
        if (dataMap.size() == 0){ //TODO 如果即是相关关系又是msjg怎么办 不可能存在这种情况
            try {
                FileUtil.copyFile(reachInput, reachInputUpdate, true);
                System.out.println("copy xggx文件成功");
            }catch (Exception e){
                System.out.println("copy xggx文件失败");
                e.printStackTrace();
            }
            System.out.println("相关关系率定值a b 值未变");
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
            System.err.println("水文模型之水文模型-率定：reach.csv输入文件读取错误:read errors :" + e);
            return 0;
        } finally {
            try {
                br.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(reachInputUpdate, false)); // 附加
            // 添加新的数据行
            String head = "";
            List<String> strings1 = readDatas.get(0);
            for (String s :strings1){
                head = head + s+",";
            }
            head = head.substring(0,head.length()-1);
            bw.write(head);
            bw.newLine();

            for (int i = 1; i < readDatas.size(); i++) {
                List<String> strings = readDatas.get(i);
                YwkPlanCalibrationZoneXggx zone = dataMap.get(strings.get(4) + strings.get(5));
                if (zone != null){
                    strings.set(7,zone.getXggxA()+"");
                    strings.set(8,zone.getXggxB()+"");
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
            System.out.println("水文模型之水文模型-率定:水文模型reach.csv输入文件写入成功");

            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水文模型之水文模型-率定:水文模型reach.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }


    }

    private int writeCalibrationMsgjCsv(String shuiwen_model_template_input,String shuiwen_model_template_input_calibration, YwkPlaninfo planinfo) {

        String reachInput = shuiwen_model_template_input + File.separator + "Reach.csv";
        String reachInputUpdate = shuiwen_model_template_input_calibration + File.separator + "Reach.csv";
        //String reachXiangGuanShujvInput = shuiwen_model_template_input + File.separator + "reach_xiangguan_shujv.csv";
        String SWYB_SHUIWEN_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_BASE_NEW_SHUIWEN_MODEL_PATH");
        String template = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");

        String reachTemplateInput = SWYB_SHUIWEN_MODEL_PATH + File.separator +template+File.separator+ "ReachTemplate.csv";//todo 表格模板
        String riverId = planinfo.getRiverId();
        List<WrpRiverZone> wrpRiverZones = wrpRiverZoneDao.findByRvcd(riverId);

        //找河系下的子河系
        List<WrpRvrBsin> allByParentIdRiver = wrpRvrBsinDao.findAllByParentId(planinfo.getRiverId());

        List<String> rvcds = allByParentIdRiver.stream().map(WrpRvrBsin::getRvcd).collect(Collectors.toList());// 子河系id

        List<YwkPlanCalibrationZone> zones = ywkPlanCalibrationZoneDao.findByNPlanid(planinfo.getnPlanid());
        Iterator<YwkPlanCalibrationZone> iterator = zones.iterator();
        while (iterator.hasNext()){
            YwkPlanCalibrationZone next = iterator.next();
            if (next.getMsjgK() == null){
                iterator.remove();
            }
        }
        Map<String, YwkPlanCalibrationZone> zoneMap = zones.stream().collect(Collectors.toMap(YwkPlanCalibrationZone::getZoneId, Function.identity()));
        Map<String,YwkPlanCalibrationZone> dataMap = new HashMap<>();
        for (WrpRiverZone riverZone : wrpRiverZones){
            String rvcd = riverZone.getRvcd();
            Integer zoneId = riverZone.getZoneId();
            String zoneStr = "";
            String cid = riverZone.getcId();
            YwkPlanCalibrationZone ywkPlanCalibrationZone = zoneMap.get(cid);
            if (ywkPlanCalibrationZone == null){
                continue;
            }
            switch (zoneId){
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
            if (!CollectionUtils.isEmpty(rvcds)){
                for (String rvcdStr : rvcds){
                    dataMap.put(rvcdStr+zoneStr,ywkPlanCalibrationZone);//河系id 加 分区id 确定一个
                }
            }
            dataMap.put(rvcd+zoneStr,ywkPlanCalibrationZone);

        }
        if (dataMap.size() == 0){
            try {
                FileUtil.copyFile(reachInput, reachInputUpdate, true);
                System.out.println("copy msjg文件成功");
            }catch (Exception e){
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
            System.err.println("水文模型之水文模型-率定：reachInput.csv输入文件读取错误:read errors :" + e);
            return 0;
        } finally {
            try {
                br.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            //FileUtil.copyFile(reachTemplateInput, reachInputUpdate, true);
            BufferedWriter bw = new BufferedWriter(new FileWriter(reachInputUpdate, false)); // 附加 todo 追加在模板表头后数据
            // 添加新的数据行
            String head = "";
            List<String> strings1 = readDatas.get(0);
            for (String s :strings1){
                head = head + s+",";
            }
            head = head.substring(0,head.length()-1);
            bw.write(head);
            bw.newLine();

            for (int i = 1; i < readDatas.size(); i++) {
                List<String> strings = readDatas.get(i);
                YwkPlanCalibrationZone zone = dataMap.get(strings.get(4) + strings.get(5));
                if (zone != null){
                    strings.set(2,zone.getMsjgK()+"");
                    strings.set(3,zone.getMsjgX()+"");
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
            System.out.println("水文模型之水文模型-率定:水文模型reachInput.csv输入文件写入成功");

            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水文模型之水文模型-率定:水文模型reachInput.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }
    }

    private int writeCalibrationXajCsv(String shuiwen_model_template_input,String shuiwen_model_template_input_calibration, YwkPlaninfo planinfo) {

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
        while (iterator.hasNext()){
            YwkPlanCalibrationZoneXaj next = iterator.next();
            if (next.getXajK() == null){
                iterator.remove();
            }
        }
        Map<String, YwkPlanCalibrationZoneXaj> zoneMap = zones.stream().collect(Collectors.toMap(YwkPlanCalibrationZoneXaj::getZoneId, Function.identity()));
        Map<String,YwkPlanCalibrationZoneXaj> dataMap = new HashMap<>();
        for (WrpRiverZone riverZone : wrpRiverZones){
            String rvcd = riverZone.getRvcd();
            Integer zoneId = riverZone.getZoneId();
            String zoneStr = "";
            String cid = riverZone.getcId();
            YwkPlanCalibrationZoneXaj ywkPlanCalibrationZoneXaj = zoneMap.get(cid);
            if (ywkPlanCalibrationZoneXaj == null){
                continue;
            }
            switch (zoneId){
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
            if (!CollectionUtils.isEmpty(rvcds)){
                for (String rvcdStr : rvcds){
                    dataMap.put(rvcdStr+zoneStr,ywkPlanCalibrationZoneXaj);//河系id 加 分区id 确定一个
                }
            }
            dataMap.put(rvcd+zoneStr,ywkPlanCalibrationZoneXaj);

        }
        if (dataMap.size() == 0){ //值未变，copy一份进去

            try {
                FileUtil.copyFile(watershedInput, watershedInputUpdate, true);
                System.out.println("copy watershed文件成功");
            }catch (Exception e){
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
            System.err.println("水文模型之水文模型-率定：Watershed.csv输入文件读取错误:read errors :" + e);
            return 0;
        } finally {
            try {
                br.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(watershedInputUpdate, false)); // 附加
            // 添加新的数据行
            String head = "";
            List<String> strings1 = readDatas.get(0);
            for (String s :strings1){
                head = head + s+",";
            }
            head = head.substring(0,head.length()-1);
            bw.write(head);
            bw.newLine();

            for (int i = 1; i < readDatas.size(); i++) {
                List<String> strings = readDatas.get(i);
                YwkPlanCalibrationZoneXaj xaj = dataMap.get(strings.get(2) + strings.get(3));
                if (xaj != null){
                    strings.set(9,xaj.getXajK()+"");
                    strings.set(10,xaj.getXajB()+"");
                    strings.set(11,xaj.getXajC()+"");
                    strings.set(12,xaj.getXajWum()+"");
                    strings.set(13,xaj.getXajWlm()+"");
                    strings.set(14,xaj.getXajWdm()+"");
                    strings.set(15,xaj.getXajWu0()+"");
                    strings.set(16,xaj.getXajWl0()+"");
                    strings.set(17,xaj.getXajWd0()+"");
                    strings.set(18,xaj.getXajEp()+"");
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
            System.out.println("水文模型之水文模型-率定:水文模型Watershed.csv输入文件写入成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水文模型之水文模型-率定:水文模型Watershed.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }

    }

    private int writeCalibrationUnitCsv(String shuiwen_model_template_input,String shuiwen_model_template_input_calibration,YwkPlaninfo planinfo) {
        String unitInput = shuiwen_model_template_input + File.separator + "unit.csv";
        String unitInputUpdate = shuiwen_model_template_input_calibration + File.separator + "unit.csv";

        List<YwkPlanCalibrationDwx> dwxes = ywkPlanCalibrationDwxDao.findByNPlanid(planinfo.getnPlanid());
        if (CollectionUtils.isEmpty(dwxes)){
            try {
                FileUtil.copyFile(unitInput, unitInputUpdate, true);
                System.out.println("copy unit文件成功");
            }catch (Exception e){
                System.out.println("copy unit文件失败");
                e.printStackTrace();
            }
            System.out.println("dwx 没有率定交互");//copy一份进去
            return 1;
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(unitInputUpdate, false)); // 附加
            // 添加新的数据行
            String head = "unit_001,unit_002,unit_003";
            bw.write(head);
            bw.newLine();

            for (int i = 0; i < dwxes.size(); i++) {
                YwkPlanCalibrationDwx dwx = dwxes.get(i);
                String line = dwx.getUnitOne()+","+dwx.getUnitTwo()+","+dwx.getUnitThree();
                bw.write(line);
                bw.newLine();
            }
            bw.close();
            System.out.println("水文模型之水文模型-率定:水文模型unit.csv输入文件写入成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水文模型之水文模型-率定:水文模型unit.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }
    }
    //todo 这块首先要把子河系的分区都给改变掉
    private int writeCalibrationWatershedCsv(String shuiwen_model_template_input,String shuiwen_model_template_input_calibration,YwkPlaninfo planinfo) {
        String watershedInput = shuiwen_model_template_input + File.separator + "Watershed.csv";
        String watershedInputUpdate = shuiwen_model_template_input_calibration + File.separator + "Watershed.csv";
        String riverId = planinfo.getRiverId();
        List<WrpRiverZone> wrpRiverZones = wrpRiverZoneDao.findByRvcd(riverId);
        //找河系下的子河系
        List<WrpRvrBsin> allByParentIdRiver = wrpRvrBsinDao.findAllByParentId(planinfo.getRiverId());

        List<String> rvcds = allByParentIdRiver.stream().map(WrpRvrBsin::getRvcd).collect(Collectors.toList());// 子河系id


        List<YwkPlanCalibrationZone> zones = ywkPlanCalibrationZoneDao.findByNPlanid(planinfo.getnPlanid());
        Iterator<YwkPlanCalibrationZone> iterator = zones.iterator();
        while (iterator.hasNext()){
            YwkPlanCalibrationZone next = iterator.next();
            if (next.getScsCn() == null){
                iterator.remove();
            }
        }
        Map<String, YwkPlanCalibrationZone> zoneMap = zones.stream().collect(Collectors.toMap(YwkPlanCalibrationZone::getZoneId, Function.identity()));
        Map<String,Long> dataMap = new HashMap<>();
        for (WrpRiverZone riverZone : wrpRiverZones){
            String rvcd = riverZone.getRvcd();
            Integer zoneId = riverZone.getZoneId();
            String zoneStr = "";
            String cid = riverZone.getcId();
            YwkPlanCalibrationZone ywkPlanCalibrationZone = zoneMap.get(cid);
            if (ywkPlanCalibrationZone == null){
                continue;
            }
            switch (zoneId){
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
            if (!CollectionUtils.isEmpty(rvcds)){
                for (String rvcdStr : rvcds){
                    dataMap.put(rvcdStr+zoneStr,ywkPlanCalibrationZone.getScsCn());//河系id 加 分区id 确定一个ScsCn值
                }
            }
            dataMap.put(rvcd+zoneStr,ywkPlanCalibrationZone.getScsCn());//河系id 加 分区id 确定一个ScsCn值

        }
        if (dataMap.size() == 0){ //值未变，copy一份进去

            try {
                FileUtil.copyFile(watershedInput, watershedInputUpdate, true);
                System.out.println("copy watershed文件成功");
            }catch (Exception e){
                System.out.println("copy watershed文件失败");
                e.printStackTrace();
            }
            System.out.println("scs cn值未变");
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
            System.err.println("水文模型之水文模型-率定：Watershed.csv输入文件读取错误:read errors :" + e);
            return 0;
        } finally {
            try {
                br.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(watershedInputUpdate, false)); // 附加
            // 添加新的数据行
            String head = "";
            List<String> strings1 = readDatas.get(0);
            for (String s :strings1){
                head = head + s+",";
            }
            head = head.substring(0,head.length()-1);
            bw.write(head);
            bw.newLine();

            for (int i = 1; i < readDatas.size(); i++) {
                List<String> strings = readDatas.get(i);
                // strings.set(1,basic.getScsCn()+"");
                Long scs = dataMap.get(strings.get(2) + strings.get(3));
                if (scs != null){
                    strings.set(1,scs+"");

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
            System.out.println("水文模型之水文模型-率定:水文模型Watershed.csv输入文件写入成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水文模型之水文模型-率定:水文模型Watershed.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }

    }


    /**
     * 修改水文模型config文件
     * @param shuiwen_model_run_plan
     * @param shuiwen_model_template_input
     * @param shuiwen_model_template_output
     * @return
     */
    private int writeDataToShuiWenConfig(String shuiwen_model_run_plan, String shuiwen_model_template_input, String shuiwen_model_template_output,int tag,YwkPlaninfo planinfo) {
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
        //String model_functionUrl = "model_function&&" + shuiwen_model_template_input + File.separator + "model_function.csv";
        String hru_scaler_modelUrl = "hru_scaler_model&&" + shuiwen_model_template_input + File.separator + "bpscaler.model";
        String hru_BP_modelUrl = "hru_BP_model&&" + shuiwen_model_template_input + File.separator + "bp.h5";
        String reach_scaler_modelUrl = "reach_scaler_model&&" + shuiwen_model_template_input + File.separator + "bbpp1";
        String reach_BP_modelUrl = "reach_BP_model&&" + shuiwen_model_template_input + File.separator + "m3.h5";
        String resultUrl = "result&&" + shuiwen_model_template_output + File.separator + "result.txt";
        String shuiku_resultUrl = "shuiku_result&&" + shuiwen_model_template_output + File.separator + "shuiku_result.txt";
        String errorUrl = "error&&" + shuiwen_model_template_output + File.separator + "error_log.txt";
        //todo 新增的 输出文件
        String jinduUrl = "jindu&&"+ shuiwen_model_template_output + File.separator + "jindu.txt";
        String rugan_resultUrl = "rugan_result&&"+shuiwen_model_template_output + File.separator + "zhiliurugan.txt";

        /**
         * difangyujing&&E:\Xiaoqinghe\HY_1_13\deal2_4\input\difangyujing.csv
         * jindu&&E:\Xiaoqinghe\HY_1_13\deal2_4\output\jindu.txt
         * rugan_k&&E:\Xiaoqinghe\HY_1_13\deal2_4\input\rugan_k.csv
         * shuiku_jishuiqu&&E:\Xiaoqinghe\HY_1_13\deal2_4\input\shuiku_jishuiqu.csv
         * rugan_result&&E:\Xiaoqinghe\HY_1_13\deal2_4\output\zhiliurugan.txt
         */
        //todo 新增的输入文件
        String difangyujingUrl = "difangyujing&&"+shuiwen_model_template_input+File.separator+"difangyujing.csv";
        String rugan_kUrl = "rugan_k&&"+shuiwen_model_template_input+File.separator+"rugan_k.csv";
        String shuiku_jishuiquUrl = "shuiku_jishuiqu&&"+shuiwen_model_template_input+File.separator+"shuiku_jishuiqu.csv";

        if (tag == 1){
            String catchMentAreaModelId = planinfo.getnModelid(); //集水区模型id   // 1是SCS  2是单位线
            String reachId = planinfo.getnSWModelid(); //河段模型id

            /**
             * 1：SCS模型
             * 2：单位线模型
             * 3：新安江模型
             * 4：智能模型
             */
            switch (catchMentAreaModelId){
                case "MODEL_SWYB_CATCHMENT_SCS":
                    Watershed1Url = "Watershed1&&" + shuiwen_model_template_input + File.separator+"calibration" + File.separator + "Watershed.csv";
                    Watershed2Url = "Watershed2&&" + shuiwen_model_template_input + File.separator+"calibration" + File.separator + "Watershed.csv";
                    break;
                case "MODEL_SWYB_CATCHMENT_DWX":
                    unitUrl = "unit&&" + shuiwen_model_template_input + File.separator+"calibration" + File.separator + "unit.csv";
                    break;
                case "MODEL_SWYB_CATCHMENT_XAJ":
                    Watershed1Url = "Watershed1&&" + shuiwen_model_template_input + File.separator+"calibration" + File.separator + "Watershed.csv";
                    Watershed2Url = "Watershed2&&" + shuiwen_model_template_input + File.separator+"calibration" + File.separator + "Watershed.csv";
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
            switch (reachId){
                case "MODEL_SWYB_REACH_MSJG":
                    reach1Url = "Reach1&&" + shuiwen_model_template_input + File.separator+"calibration"+File.separator + "Reach.csv";
                    reach2Url = "Reach2&&" + shuiwen_model_template_input + File.separator+"calibration"+File.separator + "Reach.csv";
                    break;
                case "MODEL_SWYB_REACH_XGGX":
                    reach1Url = "Reach1&&" + shuiwen_model_template_input + File.separator+"calibration"+File.separator + "Reach.csv";
                    reach2Url = "Reach2&&" + shuiwen_model_template_input + File.separator+"calibration"+File.separator + "Reach.csv";
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
        //list.add(model_functionUrl);
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
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(configUrl, false)); // 附加
            // 写路径
            for (String s : list){
                bw.write(s);
                bw.newLine();
            }
            bw.close();
            System.out.println("水文模型之水文模型:写入水文模型config成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水文模型之水文模型:写入水文模型config失败");
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * cope 水文模型exe可执行文件
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
            System.err.println("水文模型之水文模型：copy执行文件exe,bat文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("水文模型之水文模型：copy执行文件exe,bat文件错误" + e.getMessage());
            return 0;
        }
    }

    /**
     *  cope剩下的率定csv输入文件
     * @param shuiwen_model_template
     * @param shuiwen_model_template_input
     * @return
     */
    private int copyOtherShuiWenLvDingCsv(String shuiwen_model_template, String shuiwen_model_template_input) {

        String reachRead = shuiwen_model_template + File.separator + "Reach.csv";
        String reachInput = shuiwen_model_template_input + File.separator + "Reach.csv";

        //String reachXiangGuanShujvRead = shuiwen_model_template + File.separator + "reach_xiangguan_shujv.csv";
        //String reachXiangGuanShujvInput = shuiwen_model_template_input + File.separator + "reach_xiangguan_shujv.csv";

        String shuikuChushiShujuRead = shuiwen_model_template + File.separator + "shuiku_chushishuju.csv";
        String shuikuChushiShujuInput = shuiwen_model_template_input + File.separator + "shuiku_chushishuju.csv";

        String shuikuShuiweiKuRongRead = shuiwen_model_template + File.separator + "shuiku_shuiwei_kurong.csv";
        String shuikuShuiweiKuRongInput = shuiwen_model_template_input + File.separator + "shuiku_shuiwei_kurong.csv";

        String duanmianShuiweiLiuLiangRead = shuiwen_model_template + File.separator + "duanmian_shuiweiliuliang.csv";
        String duanmianShuiweiLiuLiangInput = shuiwen_model_template_input + File.separator + "duanmian_shuiweiliuliang.csv";

        String jishuiquTongjiRead = shuiwen_model_template + File.separator + "jishuiqu_tongji.csv";
        String jishuiquTongjiInput = shuiwen_model_template_input + File.separator + "jishuiqu_tongji.csv";

        String unitRead = shuiwen_model_template + File.separator + "unit.csv";
        String unitInput = shuiwen_model_template_input + File.separator + "unit.csv";

        String watershedRead = shuiwen_model_template + File.separator + "Watershed.csv";
        String watershedInput = shuiwen_model_template_input + File.separator + "Watershed.csv";

        //String xajRead = shuiwen_model_template + File.separator + "xaj.csv";
        //String xajInput = shuiwen_model_template_input + File.separator + "xaj.csv";

        String bpscalerModelRead = shuiwen_model_template + File.separator + "bpscaler.model";
        String bpscalerModelInput = shuiwen_model_template_input + File.separator + "bpscaler.model";

        String bpH5Read = shuiwen_model_template + File.separator + "bp.h5";
        String bpH5Input = shuiwen_model_template_input + File.separator + "bp.h5";

        String bbpp1Read = shuiwen_model_template + File.separator + "bbpp1";
        String bbpp1Input = shuiwen_model_template_input + File.separator + "bbpp1";

        String m3H5Read = shuiwen_model_template + File.separator + "m3.h5";
        String m3H5Input = shuiwen_model_template_input + File.separator + "m3.h5";

        String difangyujingRead = shuiwen_model_template + File.separator + "difangyujing.csv";
        String difangyujingInput = shuiwen_model_template_input + File.separator + "difangyujing.csv";

        String rugan_kRead = shuiwen_model_template + File.separator + "rugan_k.csv";
        String rugan_kInput = shuiwen_model_template_input + File.separator + "rugan_k.csv";

        String shuiku_jishuiquRead = shuiwen_model_template + File.separator + "shuiku_jishuiqu.csv";
        String shuiku_jishuiquInput = shuiwen_model_template_input + File.separator + "shuiku_jishuiqu.csv";


        /**
         * difangyujing&&E:\Xiaoqinghe\HY_1_13\deal2_4\input\difangyujing.csv
         * jindu&&E:\Xiaoqinghe\HY_1_13\deal2_4\output\jindu.txt
         * rugan_k&&E:\Xiaoqinghe\HY_1_13\deal2_4\input\rugan_k.csv
         * shuiku_jishuiqu&&E:\Xiaoqinghe\HY_1_13\deal2_4\input\shuiku_jishuiqu.csv
         * rugan_result&&E:\Xiaoqinghe\HY_1_13\deal2_4\output\zhiliurugan.txt
         */

        try {
            FileUtil.copyFile(reachRead, reachInput, true);
            //FileUtil.copyFile(reachXiangGuanShujvRead, reachXiangGuanShujvInput, true);
            FileUtil.copyFile(duanmianShuiweiLiuLiangRead, duanmianShuiweiLiuLiangInput, true);
            FileUtil.copyFile(jishuiquTongjiRead, jishuiquTongjiInput, true);
            FileUtil.copyFile(shuikuChushiShujuRead, shuikuChushiShujuInput, true);
            FileUtil.copyFile(shuikuShuiweiKuRongRead, shuikuShuiweiKuRongInput, true);
            FileUtil.copyFile(unitRead, unitInput, true);
            FileUtil.copyFile(watershedRead, watershedInput, true);
            //FileUtil.copyFile(xajRead, xajInput, true);

            FileUtil.copyFile(bpscalerModelRead, bpscalerModelInput, true);
            FileUtil.copyFile(bpH5Read, bpH5Input, true);
            FileUtil.copyFile(bbpp1Read, bbpp1Input, true);
            FileUtil.copyFile(m3H5Read, m3H5Input, true);
            FileUtil.copyFile(difangyujingRead, difangyujingInput, true);
            FileUtil.copyFile(rugan_kRead, rugan_kInput, true);
            FileUtil.copyFile(shuiku_jishuiquRead, shuiku_jishuiquInput, true);
            System.err.println("水文模型之水文模型：copy剩下的率定csv输入文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("水文模型之水文模型：copy剩下的率定csv输入文件失败" + e.getMessage());
            return 0;
        }
    }

    /**
     * 写入输入文件MOdel_SelecTion
     * @param shuiwen_model_template_input
     * @param planInfo
     * @return
     */
    private int writeDataToInputShuiWenModelSelectionCsv(String shuiwen_model_template_input, YwkPlaninfo planInfo) {

        String shuiwenModelSelectionInputUrl = shuiwen_model_template_input + File.separator + "model_selection.csv";
        String riverId = planInfo.getRiverId() == null ? "RVR_011":planInfo.getRiverId();//TODO 后来改
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
        switch (catchMentAreaModelId){
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
        switch (reachId){
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
            for (String rvcd : riverIds){
                bw.write("" + "," + rvcd);
                otherHru = otherHru +","+catchNum;
                otherReach = otherReach + "," + reachNum;
                otherFunction = otherFunction +","+ 0 ;
            }
            bw.newLine();
            bw.write("HRU,"+catchNum+otherHru);
            bw.newLine();
            bw.write("REACH,"+reachNum+otherReach);
            bw.newLine();
            bw.write("function," + 1 + otherFunction);
            bw.newLine();
            bw.close();
            System.out.println("水文模型之水文模型:水文模型model_selection输入文件写入成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水文模型之水文模型:水文模型model_selection输入文件写入失败");
            e.printStackTrace();
            return 0;
        }

    }

    /**
     * copy 第一个模型的输出文件Hru_P_result
     * @param pcp_handle_model_template_output
     * @param shuiwen_model_template_input
     * @return
     */
    private int copeFirstOutPutHruP(String pcp_handle_model_template_output, String shuiwen_model_template_input) {

        String pcp_hru_p_result = pcp_handle_model_template_output + File.separator + "hru_p_result.csv";
        String shuiwen_hru_p_result_input = shuiwen_model_template_input + File.separator + "hru_p_result.csv";

        try {
            FileUtil.copyFile(pcp_hru_p_result, shuiwen_hru_p_result_input, true);
            System.err.println("水文模型之水文模型：copy数据处理模型PCP输出文件hru_p_result文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("水文模型之水文模型：copy数据处理模型PCP输出文件hru_p_result文件失败" + e.getMessage());
            return 0;
        }

    }

    /**
     * 水文模型，chufaduanmian 跟chufaduanmian_shuru
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
        List<Map<String,Object>> insertDuanMianList = new ArrayList<>();
        List<Map<String,Object>> insertDuanMianShuRuList = new ArrayList<>();
        List<YwkPlanTriggerRcs> triggerRcss = ywkPlanTriggerRcsDao.findByNPlanid(planInfo.getnPlanid());

        if (CollectionUtils.isEmpty(triggerRcss)){
            for (String s : rcsIds){
                Map map = new HashMap();
                map.put("rcd",s);
                map.put("num",1);
                insertDuanMianList.add(map);
                insertDuanMianShuRuList.add(map);
            }
        }else {
            List<String> rcdIds = triggerRcss.stream().map(YwkPlanTriggerRcs::getRcsId).collect(Collectors.toList());//断面id的集合
            for (String s : rcsIds){
                Map map = new HashMap();
                map.put("rcd",s);
                if (rcdIds.contains(s)){
                    map.put("num",0);
                    insertDuanMianList.add(map);
                    continue;
                }
                map.put("num",1);
                insertDuanMianList.add(map);
            }

            List<String> triggerRcssIds = triggerRcss.stream().map(YwkPlanTriggerRcs::getId).collect(Collectors.toList());//主键id的集合
            List<YwkPlanTriggerRcsFlow> triggerRcsFlows =  ywkPlanTriggerRcsFlowDao.findByTriggerRcsIdsOrderByTime(triggerRcssIds);
            Map<String,List<Double>> datas = new HashMap<>();
            for (YwkPlanTriggerRcsFlow rcsFlow : triggerRcsFlows){
                String triggerRcsId = rcsFlow.getTriggerRcsId();
                Double flow = rcsFlow.getFlow();
                List<Double> doubles = datas.get(triggerRcsId);
                if (CollectionUtils.isEmpty(doubles)){
                    doubles = new ArrayList<>();
                }
                doubles.add(flow);
                datas.put(triggerRcsId,doubles);
            }

            Map<String,String> rcsAndTriggerRcs = triggerRcss.stream().collect(Collectors.toMap(YwkPlanTriggerRcs::getRcsId,YwkPlanTriggerRcs::getId));

            for (String s : rcsIds){
                Map map = new HashMap();
                map.put("rcd",s);
                String triggerRcsId = rcsAndTriggerRcs.get(s);
                if (triggerRcsId != null){
                    List<Double> doubles = datas.get(triggerRcsId);
                    map.put("doubles",doubles);
                }
                insertDuanMianShuRuList.add(map);
            }

        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(shuiWenChuFaDuanMianInputUrl, false)); // 附加
            for (Map<String, Object> map : insertDuanMianList){
                String rcd = map.get("rcd")+"";
                String num = map.get("num")+"";
                bw.write(rcd+","+num);
                bw.newLine();
            }
            bw.close();
            System.out.println("水文模型之水文模型:水文模型ChuFaDuanMian.csv输入文件写入成功");
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水文模型之水文模型:水文模型ChuFaDuanMian.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(shuiWenChuFaDuanMianShuRuInputUrl, false)); // 附加
            for (Map<String, Object> map : insertDuanMianShuRuList){
                String rcd = map.get("rcd")+"";
                Object doubles = map.get("doubles");
                bw.write(rcd);
                if (doubles != null){
                    List<Double> list = (List) doubles;
                    for (Double d : list){
                        bw.write(","+d);
                    }
                }
                bw.newLine();
            }
            bw.close();
            System.out.println("水文模型之水文模型:水文模型ChuFaDuanMianShuRu.csv输入文件写入成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水文模型之水文模型:水文模型ChuFaDuanMianShuRu.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }


    }

    /**
     * cope pcp模型config文件以及可执行文件
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
            System.err.println("水文模型之PCP模型：copy执行文件exe,bat文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("水文模型之PCP模型：copy执行文件exe,bat文件错误" + e.getMessage());
            return 0;
        }
    }

    /**
     * 调用模型运行模型文件
     */
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
        } catch (Exception e) {
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

    /**
     * 修改水文模型的数据模型的config文件
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
            System.out.println("水文模型之PCP模型:写入水文模型config成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水文模型之PCP模型:写入水文模型config失败");
            e.printStackTrace();
            return 0;
        }

    }

    /**
     * 写入pcp模型的第二个输入文件pcp_station
     * @param pcp_handle_model_template_input
     * @param results
     * @return
     */
    private int writeDataToInputPcpStationCsv(String pcp_handle_model_template_input, List<Map<String, Object>> results,YwkPlaninfo planInfo) {

        String pcpHRUInputUrl = pcp_handle_model_template_input + File.separator + "pcp_station.csv";

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(pcpHRUInputUrl, false)); // 附加
            // 添加新的数据行
            bw.write("" + ",STNM,LGTD,LTTD"); //编写表头
            Date startTime = planInfo.getdCaculatestarttm();
            startTime = DateUtil.getNextHour(startTime,-72);
            Date endTime = planInfo.getdCaculateendtm();
            int size = 0;
            //Long step = planInfo.getnOutputtm() / 60;//步长
            Long step = planInfo.getnOutputtm();//步长
            DecimalFormat format = new DecimalFormat("0.00");
            Double hour = Double.parseDouble(format.format(step* 1.0 / 60 ));
            while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
                size++;
                startTime = DateUtil.getNextMinute(startTime, step.intValue());
            }
            for (int i = 0 ;i < size;i++){
                bw.write("," + i*hour);
            }
            bw.newLine();
            for (Map<String, Object> map : results){
                Object stcd = map.get("STCD");
                String stnm = map.get("STNM") == null ? "":map.get("STNM")+"";
                String lgtd = map.get("LGTD") == null ? "":map.get("LGTD")+"";
                Object lttd = map.get("LTTD") == null ? "":map.get("LTTD")+"";
                List<Map<String,Object>> list = (List<Map<String, Object>>) map.get("LIST");
                bw.write(stcd+","+stnm+","+lgtd+","+lttd);
                for (Map<String,Object> m : list){
                    String value = m.get("DRP") == null ? "": m.get("DRP")+"";
                    bw.write(","+value);
                }
                bw.newLine();
            }
            bw.close();
            System.out.println("水文模型之PCP模型:水文模型pcp_station.csv输入文件写入成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水文模型之PCP模型:水文模型pcp_station.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }

    }

    /**
     * 写入pcp模型的第一个输入文件pcp_hru
     * @param pcp_handle_model_template_input
     * @param planInfo
     * @return
     */
    private int writeDataToInputPcpHRUCsv(String pcp_handle_model_template_input,String pcp_handle_model_template, YwkPlaninfo planInfo) {

        String pcpHRUInputUrl = pcp_handle_model_template_input + File.separator + "pcp_HRU.csv";

        String pcpHRUReadUrl = pcp_handle_model_template +  File.separator + "pcp_HRU.csv";

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
            System.err.println("水文模型之PCP模型：pcp_HRU.csv输入文件读取错误:read errors :" + e);
            return 0;
        } finally {
            try {
                br.close();
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(pcpHRUInputUrl, false)); // 附加
            // 添加新的数据行
            bw.write("" + "," + "LGTD" + "," + "LTTD"); //编写表头

            Date startTime = planInfo.getdCaculatestarttm();
            startTime = DateUtil.getNextHour(startTime,-72);
            Date endTime = planInfo.getdCaculateendtm();
            int size = 0;
            //Long step = planInfo.getnOutputtm() / 60;//步长
            Long step = planInfo.getnOutputtm() ;//

            DecimalFormat format = new DecimalFormat("0.00");
            Double hour = Double.parseDouble(format.format(step*1.0 / 60));

            while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
                size++;//TODO 修改这个地方的时间序列值。
                startTime = DateUtil.getNextMinute(startTime, step.intValue());
            }
            for (int i = 0 ;i < size;i++){
                bw.write("," + i*hour);
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
            System.out.println("水文模型之PCP模型:水文模型pcp_HRU.csv输入文件写入成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水文模型之PCP模型:水文模型pcp_HRU.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }
    }


    @Override
    public Object getModelRunStatus(YwkPlaninfo planInfo,Integer tag) {

        /*Long status;
        if (tag == 0){
            status  = planInfo.getnPlanstatus();
        }else {
            status  = planInfo.getnCalibrationStatus();
        }

        if ( status == 2L || status == -1L){
            return "1"; //1的话停止
        }else {
            return "0";
        }*/
        Long status;
        if (tag == 0){
            status  = planInfo.getnPlanstatus();
        }else {
            status  = planInfo.getnCalibrationStatus();
        }
        JSONObject jsonObject = new JSONObject();
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

        String jinduPath = SHUIWEN_MODEL_TEMPLATE_OUTPUT + File.separator + "jindu.txt";
        File path = new File(jinduPath);
        if (!path.exists()) {
            if (status == -1L){
                //运行进度
                jsonObject.put("process", 0.0);
                //运行状态 1运行结束 0运行中
                jsonObject.put("runStatus", 1);
                //运行时间
                jsonObject.put("describ", "模型运行出现异常！");
                return jsonObject;
            }
            //运行进度
            jsonObject.put("process", 0.0);
            //运行状态 1运行结束 0运行中
            jsonObject.put("runStatus", 0);
            //运行时间
            jsonObject.put("describ", "模型运行准备中！");
            return jsonObject;
        } else {
            //运行状态 1运行结束 0运行中
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
                String lineTxt2 = br.readLine();
                //if (lineTxt2 != null) {
                String[] split = lineTxt2.split("&&");
                //运行进度
                double process = Double.parseDouble(split[1] + "");
                if (status == -1){
                    jsonObject.put("runStatus", 1);
                    jsonObject.put("describ", "模型运行出现异常！");
                    jsonObject.put("process", process * 1.0);
                    return jsonObject;
                }
                jsonObject.put("runStatus", 0);
                jsonObject.put("describ", "模型运行中！");
                jsonObject.put("process", process * 1.0);
                if (process == 100.0 && status == 2L){
                    jsonObject.put("runStatus", 1);
                    jsonObject.put("describ", "模型运行成功！");
                    jsonObject.put("process", process * 1.0);
                    return jsonObject;
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

    @Override
    public Object getModelRunPcPStatus(YwkPlaninfo planInfo) {
        Long status = planInfo.getnPlanstatus();

        /*if ( status == 3L || status == -1L){
            return "1"; //1的话停止
        }else {
            return "0";
        }*/
        JSONObject jsonObject = new JSONObject();

        String SWYB_PCP_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_BASE_NEW_PCP_HANDLE_MODEL_PATH");
        String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");
        String PCP_MODEL_TEMPLATE_OUTPUT = SWYB_PCP_MODEL_PATH + File.separator + out
                + File.separator + planInfo.getnPlanid();//输出的地址
        String jinduPath = PCP_MODEL_TEMPLATE_OUTPUT + File.separator + "jindu.txt";
        File path = new File(jinduPath);
        if (!path.exists()) {
            if (status == -1){
                return jsonObject;
            }
            //运行进度
            jsonObject.put("process", 0.0);
            //运行状态 1运行结束 0运行中
            jsonObject.put("runStatus", 0);
            //运行时间
            jsonObject.put("describ", "模型运行准备中！");
            return jsonObject;
        } else {
            //运行状态 1运行结束 0运行中
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
                String lineTxt2 = br.readLine();
                //if (lineTxt2 != null) {
                String[] split = lineTxt2.split("&&");
                //运行进度
                double process = Double.parseDouble(split[1] + "");
                if (status == -1){
                    jsonObject.put("runStatus", 1);
                    jsonObject.put("describ", "模型运行出现异常！");
                    jsonObject.put("process", process * 1.0);
                    return jsonObject;
                }
                jsonObject.put("runStatus", 0);
                jsonObject.put("describ", "模型运行中！");
                jsonObject.put("process", process * 1.0);
                if (process == 100.0 && status == 3L){
                    jsonObject.put("runStatus", 1);
                    jsonObject.put("describ", "模型运行成功！");
                    jsonObject.put("process", process * 1.0);
                    return jsonObject;
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

    @Override
    public Object getModelResultQ(YwkPlaninfo planInfo,Integer tag) {

        DecimalFormat df = new DecimalFormat("0.000");
        JSONArray list = new JSONArray();

        //Long step = planInfo.getnOutputtm() / 60;//步长(小时)
        Long step = planInfo.getnOutputtm();//步长(小时)

        String riverId = planInfo.getRiverId();

        List<String>  riverIds = new ArrayList<>();
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
        //如果时小清河模型则解析水库断面
        //Map<String, List<String>> shuikuResult = new HashMap<>();

        String SWYB_MODEL_OUTPUT_SHUIKU = SHUIWEN_MODEL_TEMPLATE_OUTPUT+File.separator+"shuiku_result.txt";//输出的地址
        //shuikuResult = getModelResult(SWYB_MODEL_OUTPUT_SHUIKU);todo 水库文件不要了 出库数据
        //finalResult.putAll(shuikuResult);

        //找到河系关联的断面
        //List<WrpRcsBsin> listByRiverId = wrpRcsBsinDao.findListByRiverId(riverId);
        List<WrpRcsBsin> listByRiverId = wrpRcsBsinDao.findListByRiverIds(riverIds);
        List<String> sections = listByRiverId.stream().map(WrpRcsBsin::getRvcrcrsccd).collect(Collectors.toList());
        Map<String, String> sectionName = listByRiverId.stream().collect(Collectors.toMap(WrpRcsBsin::getRvcrcrsccd, WrpRcsBsin::getRvcrcrscnm));
        if(finalResult!=null && finalResult.size()>0){
            Date startTime = planInfo.getdCaculatestarttm();
            Date endTime = planInfo.getdCaculateendtm();
            for(String sectionId : sections){
                String name = sectionName.get(sectionId);
                JSONObject valObj = new JSONObject();
                list.add(valObj);
                valObj.put("RCS_ID",sectionId);
                valObj.put("RCS_NAME",name);
                JSONArray valList = new JSONArray();
                valObj.put("values",valList);
                JSONArray ZList = new JSONArray();
                valObj.put("zValues",ZList);
                JSONArray rainList = new JSONArray();
                valObj.put("rainValues",rainList);
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
                    //todo 新增断面安全,type 0 是安全 1是满溢风险 2 是超警戒
                    String secretyType = dataList.get(index)+"";
                    index++;
                    String secretyTime = dataList.get(index)+"";
                    index++;
                    String secretyPosition = dataList.get(index)+"";
                    if ("anquan_00".equals(secretyType)){
                        valObj.put("type",0);
                        valObj.put("message","安全");
                        valObj.put("time",00);
                        valObj.put("position",00);

                    }else if ("manyi_01".equals(secretyType)){
                        valObj.put("type",1);
                        valObj.put("message","满溢风险");
                        valObj.put("time",DateUtil.dateToStringNormal3(DateUtil.getNextMinute(startTime,step.intValue()*Integer.parseInt(secretyTime))));
                        valObj.put("position",secretyPosition);
                    }else if ("yujing_02".equals(secretyType)){
                        valObj.put("type",2);
                        valObj.put("message","超警戒");
                        valObj.put("time",DateUtil.dateToStringNormal3(DateUtil.getNextMinute(startTime,step.intValue()*Integer.parseInt(secretyTime))));
                        valObj.put("position",secretyPosition);
                    }else {
                        System.out.println("解析错误");
                        valObj.put("type",0);
                        valObj.put("message","安全");
                        valObj.put("time",00);
                        valObj.put("position",00);
                    }
                }
            }
        }
        return list;
    }


    @Override
    public Object getModelResultQCalibration(YwkPlaninfo planInfo) {//TODO 这个地方也得需要优化一下
        JSONArray modelResultQ = (JSONArray) getModelResultQ(planInfo, 0);
        JSONArray modelResultQCalibration = (JSONArray) getModelResultQ(planInfo, 1);
        List<Map> resultQ = JSON.parseArray(JSON.toJSONString(modelResultQ), Map.class);
        List<Map> resultQCalibration = JSON.parseArray(JSON.toJSONString(modelResultQCalibration), Map.class);
        Map handleMap = new HashMap();
        for (Map map : resultQ){
            Object rcs_id = map.get("RCS_ID");
            Object values = map.get("values");
            handleMap.put(rcs_id,values);
        }

        Map<String, Map> recOldMap = resultQ.stream().collect(Collectors.toMap(t -> t.get("RCS_ID").toString(), Function.identity()));

        List<Map<String,Object>>  results = new ArrayList<>();


        for (Map m : resultQCalibration){
            Map<String,Object> resultMap = new HashMap();
            Object rcs_idNew = m.get("RCS_ID");
            Object rcs_nameNew = m.get("RCS_NAME");

            resultMap.put("RCS_ID",rcs_idNew);
            resultMap.put("RCS_NAME",rcs_nameNew);

            List<Map<String,Object>>  valueList = new ArrayList<>();
            List<Map<String,Object>>  rainList = new ArrayList<>();
            List<Map<String,Object>>  ZList = new ArrayList<>();

            List<Map<String,Object>> valuesNew = (List<Map<String, Object>>) m.get("values");
            List<Map<String,Object>> rainValuesNew = (List<Map<String, Object>>) m.get("rainValues");
            List<Map<String,Object>> zValuesNew = (List<Map<String, Object>>) m.get("zValues");
            String hfQNew =  m.get("hfQ")+"";
            String hfTimeNew =  DateUtil.dateToStringNormal(new Date(Long.parseLong(m.get("hfTime")+"")));
            String hfTotalNew =  m.get("hfTotal")+"";
            Integer typeNew = (Integer) m.get("type");
            String messageNew = m.get("message")+"";
            String timetNew = m.get("time")+"";
            String positionNew = m.get("position")+"";
            /*valObj.put("type",0);
            valObj.put("message","安全");
            valObj.put("time",00);
            valObj.put("position",00);*/
            //List<Map<String,Object>> values = (List<Map<String, Object>>) handleMap.get(rcs_idNew);
            Map rcsIdOldMap = recOldMap.get(rcs_idNew);
            List<Map<String,Object>> values = (List<Map<String, Object>>)rcsIdOldMap.get("values");
            List<Map<String,Object>> rainValues = (List<Map<String, Object>>)rcsIdOldMap.get("rainValues");
            List<Map<String,Object>> zValues = (List<Map<String, Object>>)rcsIdOldMap.get("zValues");
            String hfQ =  rcsIdOldMap.get("hfQ")+"";
            String hfTime =  DateUtil.dateToStringNormal(new Date(Long.parseLong(rcsIdOldMap.get("hfTime")+"")));
            String hfTotal =  rcsIdOldMap.get("hfTotal")+"";
            Integer type = (Integer) rcsIdOldMap.get("type");
            String message = rcsIdOldMap.get("message")+"";
            String timet = rcsIdOldMap.get("time")+"";
            String position = rcsIdOldMap.get("position")+"";
            Map<String,Object> valueMap = new HashMap<>();
            for (Map<String,Object> value : values){
                String time = value.get("time")+"";
                Object q = value.get("q");
                valueMap.put(time,q);
            }
            Map<String, Object> rainValueMap = rainValues.stream().collect(Collectors.toMap(t -> t.get("time").toString(), t -> t.get("rain").toString()));
            Map<String, Object> zValueMap = zValues.stream().collect(Collectors.toMap(t -> t.get("time").toString(), t -> t.get("z").toString()));

            for (Map<String,Object> valueNew: valuesNew){
                Map map = new HashMap();
                String time = valueNew.get("time")+"";
                Object qNew = valueNew.get("q");
                Object qOld = valueMap.get(time);
                map.put("time",time);
                map.put("qNew",qNew);
                map.put("q",qOld);
                valueList.add(map);
            }
            for (Map<String,Object> rainValueNew: rainValuesNew){
                Map map = new HashMap();
                String time = rainValueNew.get("time")+"";
                Object rainNew = rainValueNew.get("rain");
                Object rainOld = rainValueMap.get(time);
                map.put("time",time);
                map.put("rainNew",rainNew);
                map.put("rain",rainOld);
                rainList.add(map);
            }for (Map<String,Object> zValueNew: zValuesNew){
                Map map = new HashMap();
                String time = zValueNew.get("time")+"";
                Object zNew = zValueNew.get("z");
                Object zOld = zValueMap.get(time);
                map.put("time",time);
                map.put("zNew",zNew);
                map.put("z",zOld);
                ZList.add(map);
            }
            resultMap.put("values",valueList);
            resultMap.put("rainValues",rainList);
            resultMap.put("zValues",ZList);
            resultMap.put("hfQ",hfQ);
            resultMap.put("hfQNew",hfQNew);
            resultMap.put("hfTime",hfTime);
            resultMap.put("hfTimeNew",hfTimeNew);
            resultMap.put("hfTotal",hfTotal);
            resultMap.put("hfTotalNew",hfTotalNew);
            resultMap.put("type",type);
            resultMap.put("typeNew",typeNew);
            resultMap.put("timeNew",timetNew);
            resultMap.put("time",timet);
            resultMap.put("message",message);
            resultMap.put("messageNew",messageNew);
            resultMap.put("position",position);
            resultMap.put("positionNew",positionNew);
            results.add(resultMap);
        }
        return results;
    }

    /**
     * 解析模型输出结果文件成出库流量数据
     * @param model_template_output
     * @return
     */
    private  Map<String, List<String>> getModelResult(String model_template_output) {
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
            System.err.println("水文模型调用结果读取失败:read errors :" + e);
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

    @Autowired
    WrpRiverZoneDao wrpRiverZoneDao;

    @Autowired
    YwkPlanCalibrationZoneDao ywkPlanCalibrationZoneDao;
    /**
     * 获取率定参数交互列表
     * @param
     * @return
     */
    @Override
    public Object getCalibrationList(YwkPlaninfo planInfo) {


        List<WrpRiverZone> riverZones = wrpRiverZoneDao.findByRvcd( planInfo.getRiverId());//查找下面的分区
        Map<String, WrpRiverZone> riverZoneMap = riverZones.stream().collect(Collectors.toMap(WrpRiverZone::getcId, Function.identity()));
        List<String> zoneIds = riverZones.stream().map(WrpRiverZone::getcId).collect(Collectors.toList());

        //List<YwkPlanCalibrationZone> byZoneIds = ywkPlanCalibrationZoneDao.findByZoneIds(zoneIds);
        //msjg跟scs
        List<YwkPlanCalibrationZone> byNPlanid = ywkPlanCalibrationZoneDao.findByNPlanid(planInfo.getnPlanid());
        Map<String, YwkPlanCalibrationZone> zoneMap = byNPlanid.stream().collect(Collectors.toMap(YwkPlanCalibrationZone::getZoneId, Function.identity()));
        //xaj
        List<YwkPlanCalibrationZoneXaj> xajs = ywkPlanCalibrationZoneXajDao.findByNPlanid(planInfo.getnPlanid());
        Map<String, YwkPlanCalibrationZoneXaj> xajZoneMap = xajs.stream().collect(Collectors.toMap(YwkPlanCalibrationZoneXaj::getZoneId, Function.identity()));

        //xggx
        List<YwkPlanCalibrationZoneXggx> xggxs = ywkPlanCalibrationZoneXggxDao.findByNPlanid(planInfo.getnPlanid());
        Map<String, YwkPlanCalibrationZoneXggx> xggxZoneMap = xggxs.stream().collect(Collectors.toMap(YwkPlanCalibrationZoneXggx::getZoneId, Function.identity()));

        String catchMentAreaModelId = planInfo.getnModelid(); //集水区模型id
        String reachId = planInfo.getnSWModelid(); //河段模型id

        Map<String,Object> resultMap = new HashMap<>();

        Map msjgAndSCSmap = new HashMap();
        Map xajMap = new HashMap();
        Map xggxMap = new HashMap();
        for (int i=1;i < 4 ;i++){ //为什么这么组装，因为前端
            msjgAndSCSmap.put("msjgK"+i,null);
            msjgAndSCSmap.put("msjgX"+i,null);
            msjgAndSCSmap.put("zoneId"+i,null);
            msjgAndSCSmap.put("zoneName"+i,null);
            msjgAndSCSmap.put("cId"+i,null);
            msjgAndSCSmap.put("scsCn"+i,null);

            xajMap.put("cId"+i,null);
            xajMap.put("zoneId"+i,null);
            xajMap.put("zoneName"+i,null);
            xajMap.put("xajK"+i,null);
            xajMap.put("xajB"+i,null);
            xajMap.put("xajC"+i,null);
            xajMap.put("xajWum"+i,null);
            xajMap.put("xajWlm"+i,null);
            xajMap.put("xajWdm"+i,null);
            xajMap.put("xajWu0"+i,null);
            xajMap.put("xajWl0"+i,null);
            xajMap.put("xajWd0"+i,null);
            xajMap.put("xajEp"+i,null);

            xggxMap.put("cId"+i,null);
            xggxMap.put("zoneId"+i,null);
            xggxMap.put("zoneName"+i,null);
            xggxMap.put("XggxA"+i,null);
            xggxMap.put("XggxB"+i,null);
        }
        for (String zoneId :zoneIds ){
            YwkPlanCalibrationZone ywkPlanCalibrationZone = zoneMap.get(zoneId);//SCS msjg
            YwkPlanCalibrationZoneXaj ywkPlanCalibrationXajZone = xajZoneMap.get(zoneId);// xaj
            YwkPlanCalibrationZoneXggx ywkPlanCalibrationXggxZone = xggxZoneMap.get(zoneId); // xggx

            WrpRiverZone wrpRiverZone = riverZoneMap.get(zoneId);
            Integer riverZoneZoneId = wrpRiverZone.getZoneId();
            msjgAndSCSmap.put("zoneId"+riverZoneZoneId,riverZoneZoneId);
            msjgAndSCSmap.put("zoneName"+riverZoneZoneId,wrpRiverZone.getZoneName());
            msjgAndSCSmap.put("cId"+riverZoneZoneId,wrpRiverZone.getcId());

            xajMap.put("zoneId"+riverZoneZoneId,riverZoneZoneId);
            xajMap.put("zoneName"+riverZoneZoneId,wrpRiverZone.getZoneName());
            xajMap.put("cId"+riverZoneZoneId,wrpRiverZone.getcId());

            xggxMap.put("zoneId"+riverZoneZoneId,riverZoneZoneId);
            xggxMap.put("zoneName"+riverZoneZoneId,wrpRiverZone.getZoneName());
            xggxMap.put("cId"+riverZoneZoneId,wrpRiverZone.getcId());

            if(ywkPlanCalibrationZone != null){
                msjgAndSCSmap.put("msjgK"+riverZoneZoneId,ywkPlanCalibrationZone.getMsjgK());
                msjgAndSCSmap.put("msjgX"+riverZoneZoneId,ywkPlanCalibrationZone.getMsjgX());
                msjgAndSCSmap.put("scsCn"+riverZoneZoneId,ywkPlanCalibrationZone.getScsCn());
            }
            if (ywkPlanCalibrationXajZone != null){
                xajMap.put("xajK"+riverZoneZoneId,ywkPlanCalibrationXajZone.getXajK());
                xajMap.put("xajB"+riverZoneZoneId,ywkPlanCalibrationXajZone.getXajB());
                xajMap.put("xajC"+riverZoneZoneId,ywkPlanCalibrationXajZone.getXajC());
                xajMap.put("xajWum"+riverZoneZoneId,ywkPlanCalibrationXajZone.getXajWum());
                xajMap.put("xajWlm"+riverZoneZoneId,ywkPlanCalibrationXajZone.getXajWlm());
                xajMap.put("xajWdm"+riverZoneZoneId,ywkPlanCalibrationXajZone.getXajWdm());
                xajMap.put("xajWu0"+riverZoneZoneId,ywkPlanCalibrationXajZone.getXajWu0());
                xajMap.put("xajWl0"+riverZoneZoneId,ywkPlanCalibrationXajZone.getXajWl0());
                xajMap.put("xajWd0"+riverZoneZoneId,ywkPlanCalibrationXajZone.getXajWd0());
                xajMap.put("xajEp"+riverZoneZoneId,ywkPlanCalibrationXajZone.getXajEp());
            }
            if (ywkPlanCalibrationXggxZone != null){
                xggxMap.put("XggxA"+riverZoneZoneId,ywkPlanCalibrationXggxZone.getXggxA());
                xggxMap.put("XggxB"+riverZoneZoneId,ywkPlanCalibrationXggxZone.getXggxB());
            }

        }

        String catchNum = "";
        String reachNum = "";
        /**
         * 1：SCS模型
         * 2：单位线模型
         * 3：新安江模型
         * 4：智能模型
         */

        switch (catchMentAreaModelId){
            case "MODEL_SWYB_CATCHMENT_SCS":
                resultMap.put("scs",msjgAndSCSmap);
                catchNum = "1";
                break;
            case "MODEL_SWYB_CATCHMENT_DWX":

                List<YwkPlanCalibrationDwx> dwxes = ywkPlanCalibrationDwxDao.findByNPlanid(planInfo.getnPlanid());
                if (CollectionUtils.isEmpty(dwxes)){
                    dwxes = null;
                }
                resultMap.put("dwx",dwxes);
                catchNum = "2";
                break;
            case "MODEL_SWYB_CATCHMENT_XAJ":
                /*List<YwkPlanCalibrationZoneXaj> zoneXaj = ywkPlanCalibrationZoneXajDao.findByNPlanid(planInfo.getnPlanid());
                if (CollectionUtils.isEmpty(zoneXaj)){
                    zoneXaj = null;
                }*/
                resultMap.put("xaj",xajMap);
                catchNum = "3";
                break;
            case "MODEL_SWYB_CATCHMENT_ZN":
                catchNum = "4";
                break;
            default:
                System.out.println("模型集水区编码错误");
                return new ArrayList<>();
        }
        resultMap.put("catch",catchNum);

        /**
         * 1：马斯京根法
         * 2：相关关系法
         * 3：智能方法
         */
        switch (reachId){
            case "MODEL_SWYB_REACH_MSJG":
                reachNum = "1";
                resultMap.put("msjg",msjgAndSCSmap);
                break;
            case "MODEL_SWYB_REACH_XGGX":
                resultMap.put("xggx",xggxMap);
                reachNum = "2";
                break;
            case "MODEL_SWYB_REACH_ZN":
                reachNum = "3";
                break;
            default:
                System.out.println("模型河段编码错误");
                return new ArrayList<>();
        }
        resultMap.put("reach",reachNum);


        return resultMap;
    }

    //单位线模型参数交互
    @Override
    public List<Map<String, Double>> importCalibrationWithDWX(MultipartFile mutilpartFile, YwkPlaninfo planInfo) {

        //解析ecxel数据 不包含第一行
        List<String[]> excelList = ExcelUtil.readFiles(mutilpartFile, 0);

        if (excelList == null || excelList.size() < 2) {
            return new ArrayList<>();
        }
        List<String> head = Arrays.asList(excelList.get(0));
        if (CollectionUtils.isEmpty(head) || head.size() != 3){
            System.out.println("SCS单位线表格有问题。。。。");
            return new ArrayList<>();
        }
        List<Map<String,Double>> results = new ArrayList<>();
        try {
            // 遍历每行数据（除了标题）
            for (int i = 1; i < excelList.size(); i++) {
                String[] strings = excelList.get(i);
                List<String> l = Arrays.asList(strings);
                if (CollectionUtils.isEmpty(l)){
                    continue;
                }
                Map<String,Double> map = new HashMap();
                for (int j = 0;j < l.size();j++){
                    String data = l.get(j);
                    Double dataValue = null;
                    if (data != null && !"".equals(data.trim())){
                        try {
                            dataValue = Double.parseDouble(data.trim());
                        }catch (Exception e){
                            System.out.println("数据非数字，转换异常");
                            dataValue = null;
                            e.printStackTrace();
                        }
                    }
                    String key = "";
                    if (j == 0){
                        key = "unitOne";
                    }else if (j == 1){
                        key = "unitTwo";
                    }else if (j == 2){
                        key = "unitThree";
                    }
                    map.put(key,dataValue);
                }
                results.add(map);
            }

        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
        //TODO 修改雨量值并不修改基础表的数据，只修改缓存的的数据
        CacheUtil.saveOrUpdate("calibrationDWX", planInfo.getnPlanid(), results);
        return results;
    }




    @Autowired
    YwkPlanCalibrationDwxDao ywkPlanCalibrationDwxDao;

    @Transactional
    @Override
    public void saveCalibrationDwxToDB(YwkPlaninfo planInfo,List<Map<String,Double>> result) {

        if ( planInfo.getnCalibrationStatus() != 0L){
            planInfo.setnCalibrationStatus(0L);//将模型运算置换到0的状态
            ywkPlaninfoDao.save(planInfo);
            CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
        }

        ywkPlanCalibrationDwxDao.deleteByNPlanid(planInfo.getnPlanid());//删除
        List<YwkPlanCalibrationDwx> insert = new ArrayList<>();
        for (Map<String,Double> map : result){
            YwkPlanCalibrationDwx ywkPlanCalibrationDwx = new YwkPlanCalibrationDwx();
            Double d1 = map.get("unitOne");
            Double d2 = map.get("unitTwo");
            Double d3 = map.get("unitThree");
            ywkPlanCalibrationDwx.setUnitOne(d1);
            ywkPlanCalibrationDwx.setUnitTwo(d2);
            ywkPlanCalibrationDwx.setUnitThree(d3);
            ywkPlanCalibrationDwx.setcId(StrUtil.getUUID());
            ywkPlanCalibrationDwx.setnPlanid(planInfo.getnPlanid());
            ywkPlanCalibrationDwx.setCreateTime(new Date());
            insert.add(ywkPlanCalibrationDwx);
        }
        ywkPlanCalibrationDwxDao.saveAll(insert);
    }


    @Transactional
    @Override
    public void saveCalibrationXAJToDB(YwkPlaninfo planInfo, List<CalibrationXAJVo> calibrationXAJVos) {

        if (planInfo.getnCalibrationStatus() != 0L){
            planInfo.setnCalibrationStatus(0L);
            ywkPlaninfoDao.save(planInfo);
            CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
        }
        ywkPlanCalibrationZoneXajDao.deleteByNPlanid(planInfo.getnPlanid());


        for (CalibrationXAJVo xajVo : calibrationXAJVos){
            YwkPlanCalibrationZoneXaj  ywkPlanCalibrationZoneXaj  = new YwkPlanCalibrationZoneXaj();
            ywkPlanCalibrationZoneXaj.setcId(StrUtil.getUUID());
            ywkPlanCalibrationZoneXaj.setnPlanid(planInfo.getnPlanid());
            ywkPlanCalibrationZoneXaj.setZoneId(xajVo.getcId());
            ywkPlanCalibrationZoneXaj.setXajB(xajVo.getXajB());
            ywkPlanCalibrationZoneXaj.setXajC(xajVo.getXajC());
            ywkPlanCalibrationZoneXaj.setXajK(xajVo.getXajK());
            ywkPlanCalibrationZoneXaj.setXajWum(xajVo.getXajWum());
            ywkPlanCalibrationZoneXaj.setXajWdm(xajVo.getXajWdm());
            ywkPlanCalibrationZoneXaj.setXajWlm(xajVo.getXajWlm());
            ywkPlanCalibrationZoneXaj.setXajWu0(xajVo.getXajWu0());
            ywkPlanCalibrationZoneXaj.setXajWd0(xajVo.getXajWd0());
            ywkPlanCalibrationZoneXaj.setXajWl0(xajVo.getXajWl0());
            ywkPlanCalibrationZoneXaj.setXajEp(xajVo.getXajEp());
            ywkPlanCalibrationZoneXaj.setCreateTime(new Date());
            ywkPlanCalibrationZoneXajDao.save(ywkPlanCalibrationZoneXaj);//保存
        }

    }

    @Transactional
    @Override
    public void saveCalibrationXGGXToDB(YwkPlaninfo planInfo, List<CalibrationXGGXVo> calibrationXGGXVos) {
        if (planInfo.getnCalibrationStatus() != 0L){
            planInfo.setnCalibrationStatus(0L);
            ywkPlaninfoDao.save(planInfo);
            CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
        }
        ywkPlanCalibrationZoneXggxDao.deleteByNPlanid(planInfo.getnPlanid());

        for (CalibrationXGGXVo xggxVo : calibrationXGGXVos){
            YwkPlanCalibrationZoneXggx ywkPlanCalibrationZoneXggx  = new YwkPlanCalibrationZoneXggx();
            ywkPlanCalibrationZoneXggx.setcId(StrUtil.getUUID());
            ywkPlanCalibrationZoneXggx.setnPlanid(planInfo.getnPlanid());
            ywkPlanCalibrationZoneXggx.setZoneId(xggxVo.getcId());
            ywkPlanCalibrationZoneXggx.setXggxA(xggxVo.getXggxA());
            ywkPlanCalibrationZoneXggx.setXggxB(xggxVo.getXggxB());
            ywkPlanCalibrationZoneXggx.setCreateTime(new Date());
            ywkPlanCalibrationZoneXggxDao.save(ywkPlanCalibrationZoneXggx);//保存
        }
    }


    @Transactional
    @Override
    public void saveCalibrationMSJGOrScSToDB(YwkPlaninfo planInfo, CalibrationMSJGAndScsVo calibrationMSJGAndScsVo,Integer tag) {

        if (planInfo.getnCalibrationStatus() != 0L){
            planInfo.setnCalibrationStatus(0L);
            ywkPlaninfoDao.save(planInfo);
            CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
        }

        List<String> ids = new ArrayList<>();
        Map<String,CalibrationMSJGAndScsVo.MSJGAndScSVo> keyMap = new HashMap();
        if (calibrationMSJGAndScsVo.getMsjg1() != null){
            ids.add(calibrationMSJGAndScsVo.getMsjg1().getcId() );
            keyMap.put(calibrationMSJGAndScsVo.getMsjg1().getcId(),calibrationMSJGAndScsVo.getMsjg1());
        }
        if (calibrationMSJGAndScsVo.getMsjg2() != null){
            ids.add(calibrationMSJGAndScsVo.getMsjg2().getcId() );
            keyMap.put(calibrationMSJGAndScsVo.getMsjg2().getcId(),calibrationMSJGAndScsVo.getMsjg2());
        }
        if (calibrationMSJGAndScsVo.getMsjg3() != null){
            ids.add(calibrationMSJGAndScsVo.getMsjg3().getcId() );
            keyMap.put(calibrationMSJGAndScsVo.getMsjg3().getcId(),calibrationMSJGAndScsVo.getMsjg3());
        }

        List<YwkPlanCalibrationZone> byNPlanidAndZoneId = ywkPlanCalibrationZoneDao.findByNPlanid(planInfo.getnPlanid());
        Map<String, YwkPlanCalibrationZone> zoneMap = byNPlanidAndZoneId.stream().collect(Collectors.toMap(YwkPlanCalibrationZone::getZoneId, Function.identity()));

        for (String id : ids){
            YwkPlanCalibrationZone ywkPlanCalibrationZone = zoneMap.get(id);
            if (ywkPlanCalibrationZone == null){
                ywkPlanCalibrationZone  = new YwkPlanCalibrationZone();
                ywkPlanCalibrationZone.setcId(StrUtil.getUUID());
                ywkPlanCalibrationZone.setnPlanid(planInfo.getnPlanid());
                ywkPlanCalibrationZone.setZoneId(id);
            }
            CalibrationMSJGAndScsVo.MSJGAndScSVo msjgAndScSVo = keyMap.get(id);
            if (tag == 0){
                ywkPlanCalibrationZone.setMsjgK(msjgAndScSVo.getMsjgK());
                ywkPlanCalibrationZone.setMsjgX(msjgAndScSVo.getMsjgX());
            }else {
                ywkPlanCalibrationZone.setScsCn(msjgAndScSVo.getScsCn());
            }

            ywkPlanCalibrationZoneDao.save(ywkPlanCalibrationZone);//保存
        }

    }


    @Autowired
    YwkPlanOutputQDao ywkPlanOutputQDao;
    @Transactional
    @Override
    public void saveModelData(YwkPlaninfo planInfo) {
        //保存方案计算-降雨量条件
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //首先保存入库，
        JSONArray modelResultQ = (JSONArray) getModelResultQ(planInfo, 0);//因为经历过修改保存跟修改撤销后只有一个地方有result文件
        List<Map> resultQ = JSON.parseArray(JSON.toJSONString(modelResultQ), Map.class);
        List<YwkPlanOutputQ> insert = new ArrayList<>();
        for (Map<String,Object> map : resultQ){
            String rcs_id = map.get("RCS_ID")+"";
            List<Map<String,Object>> values = (List<Map<String, Object>>) map.get("values");
            for (Map<String,Object> value : values){
                YwkPlanOutputQ model = new YwkPlanOutputQ();
                String time = value.get("time")+"";
                Double q = Double.parseDouble(value.get("q")+"");
                model.setIdcId(StrUtil.getUUID());
                try {
                    model.setdTime(format.parse(time));
                }catch (Exception e){
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
        System.out.println("futures.size"+futures.size());
        CompletableFuture[] completableFutures = new CompletableFuture[futures.size()];
        for (int j = 0;j < futures.size();j++){
            completableFutures[j] = futures.get(j);
        }
        System.out.println("等待多线程执行完毕。。。。");//TODO 我认为不需要那个处理csv文件
        CompletableFuture.allOf(completableFutures).join();//全部执行完后 然后主线程结束
        System.out.println("多线程执行完毕，结束主线程。。。。");
        return ;
    }

    @Override
    public int saveOrDeleteResultCsv(YwkPlaninfo planInfo, Integer tag) {
        String SWYB_SHUIWEN_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_BASE_NEW_SHUIWEN_MODEL_PATH");
        String template = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");
        String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");
        String run = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_RUN");

        //另一个模型
        String SHUIWEN_MODEL_TEMPLATE = SWYB_SHUIWEN_MODEL_PATH + File.separator + template;
        String SHUIWEN_MODEL_TEMPLATE_INPUT = SHUIWEN_MODEL_TEMPLATE
                + File.separator + "INPUT" + File.separator + planInfo.getnPlanid();
        String SHUIWEN_MODEL_TEMPLATE_INPUT_CALIBRATION = SHUIWEN_MODEL_TEMPLATE
                + File.separator + "INPUT" + File.separator + planInfo.getnPlanid()+File.separator+"calibration"; //输入的地址 //TODO 加一个calibration

        String SHUIWEN_MODEL_TEMPLATE_OUTPUT = SWYB_SHUIWEN_MODEL_PATH + File.separator + out
                + File.separator + planInfo.getnPlanid();//率定的地址

        String SHUIWEN_MODEL_TEMPLATE_OUTPUT_CALIBRATION = SWYB_SHUIWEN_MODEL_PATH + File.separator + out
                + File.separator + planInfo.getnPlanid()+File.separator+"calibration";//率定的地址

        if (tag == 1){//保留旧的，删除新的  只是删除了文件 但是库里的数据没删除

            String catchMentAreaModelId = planInfo.getnModelid(); //集水区模型id   // 1是SCS  2是单位线
            String reachId = planInfo.getnSWModelid(); //河段模型id

            /**
             * 1：SCS模型
             * 2：单位线模型
             * 3：新安江模型
             * 4：智能模型
             */
            switch (catchMentAreaModelId){
                case "MODEL_SWYB_CATCHMENT_SCS":
                    try {
                        FileUtil.copyFile(SHUIWEN_MODEL_TEMPLATE_INPUT_CALIBRATION+File.separator+"Watershed.csv",
                                SHUIWEN_MODEL_TEMPLATE_INPUT+File.separator+"Watershed.csv",true);
                        System.out.println("copy SCS新版watershed.csv替换旧版watershed.scv文件成功");
                    }catch (Exception e){
                        System.out.println("copy SCS新版watershed.csv替换旧版watershed.scv文件失败");
                        e.printStackTrace();
                        return 0;
                    }
                    break;
                case "MODEL_SWYB_CATCHMENT_DWX":
                    try {
                        FileUtil.copyFile(SHUIWEN_MODEL_TEMPLATE_INPUT_CALIBRATION+File.separator+"unit.csv",
                                SHUIWEN_MODEL_TEMPLATE_INPUT+File.separator+"unit.csv",true);
                        System.out.println("copy 新版watershed.csv替换旧版unit.scv文件成功");
                    }catch (Exception e){
                        System.out.println("copy 新版watershed.csv替换旧版unit.scv文件失败");
                        e.printStackTrace();
                        return 0;
                    }
                    break;
                case "MODEL_SWYB_CATCHMENT_XAJ":
                    try {
                        FileUtil.copyFile(SHUIWEN_MODEL_TEMPLATE_INPUT_CALIBRATION+File.separator+"Watershed.csv",
                                SHUIWEN_MODEL_TEMPLATE_INPUT+File.separator+"Watershed.csv",true);
                        System.out.println("copy XAJ新版watershed.csv替换旧版watershed.scv文件成功");
                    }catch (Exception e){
                        System.out.println("copy XAJ新版watershed.csv替换旧版watershed.scv文件失败");
                        e.printStackTrace();
                        return 0;
                    }
                    break;
                case "MODEL_SWYB_CATCHMENT_ZN":
                    break;
                default:
                    System.out.println("模型集水区编码错误");
            }
            /**
             * 1：马斯京根法
             * 2：相关关系法
             * 3：智能方法
             */
            switch (reachId){
                case "MODEL_SWYB_REACH_MSJG":
                    try {
                        FileUtil.copyFile(SHUIWEN_MODEL_TEMPLATE_INPUT_CALIBRATION+File.separator+"Reach.csv",
                                SHUIWEN_MODEL_TEMPLATE_INPUT+File.separator+"Reach.csv",true);
                        System.out.println("copy MSJG新版watershed.csv替换旧版Reach.scv文件成功");
                    }catch (Exception e){
                        System.out.println("copy MSJG新版watershed.csv替换旧版Reach.scv文件失败");
                        e.printStackTrace();
                        return 0;
                    }
                    break;
                case "MODEL_SWYB_REACH_XGGX":
                    try {
                        FileUtil.copyFile(SHUIWEN_MODEL_TEMPLATE_INPUT_CALIBRATION+File.separator+"Reach.csv",
                                SHUIWEN_MODEL_TEMPLATE_INPUT+File.separator+"Reach.csv",true);
                        System.out.println("copy XGGX新版watershed.csv替换旧版Reach.scv文件成功");
                    }catch (Exception e){
                        System.out.println("copy XGGX新版watershed.csv替换旧版Reach.scv文件失败");
                        e.printStackTrace();
                        return 0;
                    }
                    break;
                case "MODEL_SWYB_REACH_ZN":
                    break;
                default:
                    System.out.println("模型河段编码错误");
            }

            //还需要将结果文件拷贝过来
            try {//tor + "result.txt";
                //String shuiku_resultUrl = "shuiku_result&&" + shuiwen_model_template_output + File.separator + "shuiku_result.txt";
                FileUtil.copyFile(SHUIWEN_MODEL_TEMPLATE_OUTPUT_CALIBRATION+File.separator+"result.txt",
                        SHUIWEN_MODEL_TEMPLATE_OUTPUT+File.separator+"result.txt",true);

                FileUtil.copyFile(SHUIWEN_MODEL_TEMPLATE_OUTPUT_CALIBRATION+File.separator+"shuiku_result.txt",
                        SHUIWEN_MODEL_TEMPLATE_OUTPUT+File.separator+"shuiku_result.txt",true);

                System.out.println("copy 新版watershed.csv替换旧版shuiku_result.txt、result.txt文件成功");
            }catch (Exception e){
                System.out.println("copy 新版watershed.csv替换旧版shuiku_result.txt、result.txt文件失败");
                e.printStackTrace();
                return 0;
            }
        }
        try {
            FileUtil.deleteFile(SHUIWEN_MODEL_TEMPLATE_INPUT_CALIBRATION); //删除输入文件 //TODO 这个地方率定之前的表里的数据没有了，只有率定之后的了，但是用户是修改保存了可咋办
            FileUtil.deleteFile(SHUIWEN_MODEL_TEMPLATE_OUTPUT_CALIBRATION);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }

        return 1;
    }

    @Autowired
    WrpRcsSwybDao wrpRcsSwybDao;
    /**
     * 获取每个河系下的上下游断面关系。
     * @param rvcd
     * @return
     */
    @Override
    public Object getRcsUpAndDownWithRiver(String rvcd) {
        List<WrpRcsSwyb> listByRvcd = wrpRcsSwybDao.findListByRvcd(rvcd);
        return listByRvcd;
    }


    @Override
    public Object calculationRcs(YwkPlaninfo planInfo,String oneRcs, String twoRcs,int tag) {
        DecimalFormat format = new DecimalFormat("0.00");
        JSONArray modelResultQ = (JSONArray) getModelResultQ(planInfo, tag);//todo 先按照tag为0来取
        JSONArray array = modelResultQ.stream().filter(item->{
            JSONObject object = (JSONObject) item;
            if (oneRcs.equals(object.get("RCS_ID"))||twoRcs.equals(object.get("RCS_ID"))){
                return true;
            }else {
                return false;
            }
        }).collect(Collectors.toCollection(JSONArray::new));
        JSONObject obj1 = (JSONObject) array.get(0);
        JSONObject obj2 = (JSONObject) array.get(1);
        Date time1 = (Date) obj1.get("hfTime");
        Date time2 = (Date) obj2.get("hfTime");
        int i = Math.abs(DateUtil.dValueOfTime(time1, time2));
        Double hour = Double.parseDouble(format.format(i*1.0 / 60));
        return hour;
    }

}
