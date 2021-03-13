package com.essence.business.xqh.service.fhybdd;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.essence.business.xqh.api.fhybdd.dto.CalibrationMSJGAndScsVo;
import com.essence.business.xqh.api.fhybdd.dto.CalibrationXAJVo;
import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;
import com.essence.business.xqh.api.fhybdd.dto.ModelProperties;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import sun.net.www.content.audio.basic;

import javax.persistence.EntityManager;
import java.io.*;
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
    private EntityManager entityManager;


    @Override
    public String savePlan(ModelCallBySWDDVo vo) {


        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("XT_SWYB");

        Date startTime = vo.getStartTime(); //开始时间
        Date endTIme = vo.getEndTime();  //结束时间
        Date periodEndTime = vo.getPeriodEndTime();//预见结束时间
        if (periodEndTime != null) {
            endTIme = periodEndTime;
        }
        int step = vo.getStep();//以小时为单位

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
        ywkPlaninfo.setnOutputtm(step * 60L);//设置间隔分钟
        ywkPlaninfo.setnModelid(vo.getCatchmentAreaModelId());
        ywkPlaninfo.setnSWModelid(vo.getReachId());
        ywkPlaninfo.setdRainstarttime(startTime);
        ywkPlaninfo.setdRainendtime(endTIme);
        ywkPlaninfo.setdOpensourcestarttime(startTime);
        ywkPlaninfo.setdOpensourceendtime(endTIme);
        ywkPlaninfo.setnCreatetime(DateUtil.getCurrentTime());
        ywkPlaninfo.setRiverId(vo.getRvcd());
        YwkPlaninfo saveDbo = ywkPlaninfoDao.save(ywkPlaninfo);
        return saveDbo.getnPlanid();
    }


    @Override
    public List<Map<String,Object>> getRainfalls(String planId) {
        List<Map<String, Object>> results11 = (List<Map<String, Object>>) CacheUtil.get("rainfall", planId+"new");
        if (!CollectionUtils.isEmpty(results11)){  //TODO inport的时候更新了缓存
            return results11;
        }
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        if (planInfo == null){
            System.out.println("方案找不到。。。。。");
            return new ArrayList<>();
        }
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();

        String startTimeStr = format1.format(startTime);
        String endTimeStr = format1.format(endTime);
        Long step = planInfo.getnOutputtm() / 60;//步长

        Long count = ywkPlaninRainfallDao.countByPlanId(planId);
        List<Map<String, Object>> stPptnRWithSTCD = new ArrayList<>();
        if (count != 0){
            stPptnRWithSTCD = ywkPlaninRainfallDao.findStPptnRWithSTCD(startTimeStr,endTimeStr,planId);
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
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
        List<Map<String,Object>> results = new ArrayList<>();
        List<String> timeResults = new ArrayList();
        while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
            String hourStart = format.format(startTime);
            timeResults.add(hourStart);
            startTime = DateUtil.getNextHour(startTime, step.intValue());
        }
        Set<Map.Entry<String, List<Map<String, Object>>>> entries = handleMap.entrySet();

        for (Map.Entry<String, List<Map<String, Object>>> entry : entries) {
            List<Map<String, Object>> list = entry.getValue();
            Iterator<Map<String, Object>> iterator = list.iterator();
            if (CollectionUtils.isEmpty(list)){
                continue;
            }
            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("STCD",entry.getKey());//TODO 现在存在库里每个小时多个数据点
            resultMap.put("STNM",list.get(0).get("STNM"));
            resultMap.put("LGTD",list.get(0).get("LGTD"));
            resultMap.put("LTTD",list.get(0).get("LTTD"));

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
            List<Map<String,Object>> ll = new ArrayList<>();
            for (String time : timeResults){
                Map<String, Object> stringObjectMap = dataM.get(time);
                if (stringObjectMap == null){
                    stringObjectMap = new HashMap<>();
                    stringObjectMap.put("STCD",entry.getKey());
                    stringObjectMap.put("STNM",list.get(0).get("STNM"));
                    stringObjectMap.put("LGTD",list.get(0).get("LGTD"));
                    stringObjectMap.put("LTTD",list.get(0).get("LTTD"));
                    stringObjectMap.put("TM",time);
                    stringObjectMap.put("DRP",null);
                }
                ll.add(stringObjectMap);
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
        CacheUtil.saveOrUpdate("rainfall", planId+"new", results);
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
    public List<Map<String,Object>> getTriggerFlow(String planId, String rcsId) {//TODO 未从缓存里拿
        List<Map<String,Object>> results = new ArrayList<>();
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        YwkPlanTriggerRcs ywkPlanTriggerRcs = ywkPlanTriggerRcsDao.findByNPlanidAndRcsId(planId,rcsId);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");

        if (ywkPlanTriggerRcs == null){
            Date startTime = planInfo.getdCaculatestarttm();
            Date endTime = planInfo.getdCaculateendtm();
            Long step = planInfo.getnOutputtm() / 60;//步长
            while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
                Map<String,Object> map = new HashMap();
                String hourStart = format.format(startTime);
                map.put("date",hourStart);
                map.put("flow",null);
                results.add(map);
                startTime = DateUtil.getNextHour(startTime, step.intValue());
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
    public Workbook exportTriggerFlowTemplate(String planId) {//TODO 回写的没写

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
        Long step = planInfo.getnOutputtm() / 60;//步长
        int beginLine = 1;
        //封装数据
        while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
            XSSFRow row1 = sheet.createRow(beginLine);
            row1.createCell(0).setCellValue(DateUtil.dateToStringNormal3(startTime));
            beginLine++;
            startTime = DateUtil.getNextHour(startTime, step.intValue());
        }
        return workbook;
    }


    @Transactional
    @Override
    public List<Map<String,Object>> importTriggerFlowData(MultipartFile mutilpartFile, String planId,String rcsId) {
        List<Map<String,Object>> result = new ArrayList<>();
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        Long step = planInfo.getnOutputtm() / 60;//步长
        while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
            size++;
            startTime = DateUtil.getNextHour(startTime, step.intValue());
        }
        if (result.size() != size){//TODO 必须数量跟时间序列一致
            return new ArrayList<>();
        }
        //1、先插入
        ywkPlanTriggerRcsDao.deleteByNPlanidAndRcsId(planId, rcsId);

        YwkPlanTriggerRcs ywkPlanTriggerRcs = new YwkPlanTriggerRcs();
        ywkPlanTriggerRcs.setId(StrUtil.getUUID());
        ywkPlanTriggerRcs.setnPlanid(planId);
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
    public Workbook exportRainfallTemplate(String planId) {

        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        if (planInfo == null){
            return  new XSSFWorkbook();
        }
        //封装时间列
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        Long step = planInfo.getnOutputtm() / 60;//步长
       
        //List<StStbprpB> stations = stStbprpBDao.findAll();
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
            c.setCellValue(DateUtil.dateToStringNormal3(startTime));
            c.setCellStyle(style);
            startTime = DateUtil.getNextHour(startTime, step.intValue());
        }

        List<Map<String, Object>> rainfalls = getRainfalls(planId);
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
    public List<Map<String, Object>> importRainfallData(MultipartFile mutilpartFile, String planId) {
        List<StStbprpB> stbp = stStbprpBDao.findAll();
        Map<String, StStbprpB> collect = stbp.stream().collect(Collectors.toMap(StStbprpB::getStcd, Function.identity()));

        List<Map<String,Object>> result = new ArrayList<>();
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        Long step = planInfo.getnOutputtm() / 60;//步长
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
                startTime = DateUtil.getNextHour(startTime, step.intValue());
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
        CacheUtil.saveOrUpdate("rainfall", planId+"new", inSertList);
        return inSertList;
    }

    @Autowired
    ModelCallHandleDataService modelCallHandleDataService;
    @Transactional
    @Override
    public void saveRainfallsFromCacheToDb(String planId) {
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (planInfo == null){
            System.out.println("planId找不到方案信息");
            return;
        }
        List<Map<String,Object>> result = (List<Map<String,Object>>) CacheUtil.get("rainfall", planId+"new");

        if (CollectionUtils.isEmpty(result)){
            System.out.println("缓存里没有雨量信息");
            return;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
        List<String> timeResults = new ArrayList();
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        Long step = planInfo.getnOutputtm() / 60;//步长
        while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
            String hourStart = format.format(startTime);
            timeResults.add(hourStart);
            startTime = DateUtil.getNextHour(startTime, step.intValue());
        }
        List<YwkPlaninRainfall> insertList = new ArrayList<>();
        for (Map<String,Object> map : result){//
            String id = map.get("STCD") + "";//STCD<STNM<LGTD<LTTD<LIST
            List<Map<String,Object>> list = (List<Map<String,Object>>) map.get("LIST");
            int z = 0;
            if (CollectionUtils.isEmpty(list)){
                continue;
            }
            for (Map<String,Object> m : list){//TODO null值不存
                Object drp = m.get("DRP");
                String time = timeResults.get(z);
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
                ywkPlaninRainfall.setnPlanid(planId);
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

        ywkPlaninRainfallDao.deleteByNPlanid(planId);//先删除，后新增*/

       /* List<YwkPlaninRainfall> result = new ArrayList<>();

        for (int i=0;i<4000;i++){
            YwkPlaninRainfall y = new YwkPlaninRainfall();
            y.setcId(StrUtil.getUUID());
            y.setnPlanid("1111"+i);
            y.setnDrp(1.0+i);
            y.setdTime(new Date());
            y.setcStcd("111"+i);
            result.add(y);
        }*/
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


    @Override
    public Long modelCallHandleData(String planId) {


        //YwkPlaninfo planInfo = (YwkPlaninfo) CacheUtil.get("planInfo", planId);//方案基本信息
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        if(planInfo == null){
            System.out.println("计划信息为空，无法计算");
            return -1L;
        }

        //雨量信息表
       // List<Map<String, Object>> results = (List<Map<String, Object>>) CacheUtil.get("rainfall", planId+"new");
        Long aLong = ywkPlaninRainfallDao.countByPlanId(planId);
        if (aLong == 0){
            System.out.println("方案雨量表没有保存数据");
            return -1L;
        }
        List<Map<String, Object>> results = getRainfalls(planId);
        if (CollectionUtils.isEmpty(results)){
            System.out.println("雨量信息为空，无法计算");
            return -1L;
        }
        /*String catchMentAreaModelId = planInfo.getnModelid(); //集水区模型id   // 1是SCS  2是单位线
        String reachId = planInfo.getnSWModelid(); //河段模型id*/

        // modelPyId = modelProperties.getModel().get(modelid);

        //创建入参、出参
        String SWYB_PCP_HANDLE_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_BASE_NEW_PCP_HANDLE_MODEL_PATH");
        String SWYB_SHUIWEN_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_BASE_NEW_SHUIWEN_MODEL_PATH");
        String template = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");
        String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");
        String run = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_RUN");


        String PCP_HANDLE_MODEL_TEMPLATE = SWYB_PCP_HANDLE_MODEL_PATH + File.separator + template;

        String PCP_HANDLE_MODEL_TEMPLATE_INPUT = PCP_HANDLE_MODEL_TEMPLATE
                + File.separator + "INPUT" + File.separator + planId; //输入的地址
        String PCP_HANDLE_MODEL_TEMPLATE_OUTPUT = SWYB_PCP_HANDLE_MODEL_PATH + File.separator + out
                + File.separator + planId;//输出的地址

        String PCP_HANDLE_MODEL_RUN = SWYB_PCP_HANDLE_MODEL_PATH + File.separator + run;

        String PCP_HANDLE_MODEL_RUN_PLAN = PCP_HANDLE_MODEL_RUN + File.separator + planId;

        //另一个模型
        String SHUIWEN_MODEL_TEMPLATE = SWYB_SHUIWEN_MODEL_PATH + File.separator + template;
        String SHUIWEN_MODEL_TEMPLATE_INPUT = SHUIWEN_MODEL_TEMPLATE
                + File.separator + "INPUT" + File.separator + planId; //输入的地址
        String SHUIWEN_MODEL_TEMPLATE_OUTPUT = SWYB_SHUIWEN_MODEL_PATH + File.separator + out
                + File.separator + planId;//输出的地址
        //模型运行的config
        String SHUIWEN_MODEL_RUN = SWYB_SHUIWEN_MODEL_PATH + File.separator + run;

        String SHUIWEN_MODEL_RUN_PLAN = SHUIWEN_MODEL_RUN + File.separator + planId;

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
        int result0 = writeDataToInputPcpHRUCsv(PCP_HANDLE_MODEL_TEMPLATE_INPUT,PCP_HANDLE_MODEL_TEMPLATE, planInfo);
        if (result0 == 0 ){
            System.out.println("水文模型之PCP模型:写入pcp_HRU失败");
            return -1L;
        }
        //2,写入pcp_station.csv
        int result1 = writeDataToInputPcpStationCsv(PCP_HANDLE_MODEL_TEMPLATE_INPUT,results,planInfo);
        if (result1 == 0 ){
            System.out.println("水文模型之PCP模型:写入pcp_station失败");
            return -1L;
        }
        //3.复制cofig以及可执行文件
        int result2 = copyPCPExeFile(PCP_HANDLE_MODEL_RUN, PCP_HANDLE_MODEL_RUN_PLAN);
        if (result2 == 0) {
            System.out.println("水文模型之PCP模型:复制执行文件与config文件写入失败。。。");
            return -1L;
        }
        //4,修改config文件
        int result3 = writeDataToPcpConfig(PCP_HANDLE_MODEL_RUN_PLAN, PCP_HANDLE_MODEL_TEMPLATE_INPUT, PCP_HANDLE_MODEL_TEMPLATE_OUTPUT);
        if (result3 == 0){
            System.out.println("水文模型之PCP模型:修改config文件失败");
            return -1L;
        }

        //5.调用模型
        //调用模型计算
        System.out.println("水文模型之PCP模型:开始水文模型PCP模型计算。。。");
        System.out.println("水文模型之PCP模型:模型计算路径为。。。"+PCP_HANDLE_MODEL_RUN_PLAN + File.separator + "startUp.bat");
        runModelExe(PCP_HANDLE_MODEL_RUN_PLAN + File.separator + "startUp.bat");
        System.out.println("水文模型之PCP模型:模型计算结束。。。");

        //TODO 判断模型是否执行成功
        //判断是否执行成功，是否有error文件
        String pcp_result = PCP_HANDLE_MODEL_TEMPLATE_OUTPUT + File.separator + "hru_p_result.csv";
        File pcp_resultFile = new File(pcp_result);
        if (pcp_resultFile.exists()) {//存在表示执行成功
            System.out.println("水文模型之PCP模型:pcp模型执行成功hru_p_result.csv文件存在");
        } else {
            System.out.println("水文模型之PCP模型:pcp模型执行成功hru_p_result.csv文件不存在");
            return -1L;
        }
        //TODO 上面的入参条件没存库
        //TODO 第二个shuiwen模型
        //6，预报断面ChuFaDuanMian、ChuFaDuanMian_shuru.csv组装
        int result4 = writeDataToInputShuiWenChuFaDuanMianCsv(SHUIWEN_MODEL_TEMPLATE_INPUT, planInfo);

        if (result4 == 0){
            System.out.println("水文模型之水文模型:写入chufaduanmian跟chufaduanmian_shuru.csv失败");
            return -1L;
        }
        //7 cope pcp模型的输出文件到水文模型的输入文件里
        int result5 = copeFirstOutPutHruP(PCP_HANDLE_MODEL_TEMPLATE_OUTPUT,SHUIWEN_MODEL_TEMPLATE_INPUT);
        if (result5 == 0){
            System.out.println("水文模型之水文模型:copy数据处理模型PCP输出文件hru_p_result失败");
            return -1L;
        }
        //8 写入model_selection.csv 输入文件
        int result6 = writeDataToInputShuiWenModelSelectionCsv(SHUIWEN_MODEL_TEMPLATE_INPUT,planInfo);
        if (result6 == 0){
            System.out.println("水文模型之水文模型:写入model_selection.csv 输入文件失败");
            return -1L;
        }

        //9 copy剩下的率定csv输入文件
        int result7 = copyOtherShuiWenLvDingCsv(SHUIWEN_MODEL_TEMPLATE,SHUIWEN_MODEL_TEMPLATE_INPUT);
        if (result7 == 0){
            System.out.println("水文模型之水文模型: copy剩下的率定csv输入文件失败");
            return -1L;
        }
        //10 复制shuiwen cofig以及可执行文件
        int result8 = copyShuiWenExeFile(SHUIWEN_MODEL_RUN, SHUIWEN_MODEL_RUN_PLAN);
        if (result8 == 0) {
            System.out.println("水文模型之水文模型:复制执行文件与config文件写入失败。。。");
            return -1L;
        }
        //11,修改shuiwen config文件
        int result9 = writeDataToShuiWenConfig(SHUIWEN_MODEL_RUN_PLAN, SHUIWEN_MODEL_TEMPLATE_INPUT, SHUIWEN_MODEL_TEMPLATE_OUTPUT);
        if (result9 == 0){
            System.out.println("水文模型之水文模型:修改config文件失败");
            return -1L;
        }
        //12,
        //调用模型计算
        System.out.println("水文模型之水文模型:开始水文模型shuiwen模型计算。。。");
        System.out.println("水文模型之水文模型:模型计算路径为。。。"+SHUIWEN_MODEL_RUN_PLAN + File.separator + "startUp.bat");
        runModelExe(SHUIWEN_MODEL_RUN_PLAN + File.separator + "startUp.bat");
        System.out.println("水文模型之水文模型:模型计算结束。。。");

        //判断是否执行成功，是否有error文件
        String errorStr = SHUIWEN_MODEL_TEMPLATE_OUTPUT + File.separator + "error_log.txt";
        File errorFile = new File(errorStr);
        if (errorFile.exists()) {//存在表示执行失败
            System.out.println("水文模型之水文模型:模型计算失败。。存在error_log文件");
            return -1L;
        } else {
            System.out.println("水文模型之水文模型:模型计算成功。。不存在error_log文件");
            return 2L;
        }


    }
    @Override
    public Long ModelCallCalibration(String planId) {


        //YwkPlaninfo planInfo = (YwkPlaninfo) CacheUtil.get("planInfo", planId);//方案基本信息
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        if(planInfo == null){
            System.out.println("计划信息为空，无法计算");
            return -1L;
        }
        Long status = planInfo.getnPlanstatus();
        if (status.longValue() != 2){
            System.out.println("模型首次计算未成功，不能进行率定运算");
            return -1L;
        }


        //创建入参、出参
        String SWYB_SHUIWEN_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_BASE_NEW_SHUIWEN_MODEL_PATH");
        String template = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");
        String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");
        String run = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_RUN");


       //TODO 二次运行 pcp模型就不用在执行了

        //另一个模型
        String SHUIWEN_MODEL_TEMPLATE = SWYB_SHUIWEN_MODEL_PATH + File.separator + template;
        String SHUIWEN_MODEL_TEMPLATE_INPUT = SHUIWEN_MODEL_TEMPLATE
                + File.separator + "INPUT" + File.separator + planId; //输入的地址
        String SHUIWEN_MODEL_TEMPLATE_OUTPUT = SWYB_SHUIWEN_MODEL_PATH + File.separator + out
                + File.separator + planId+File.separator+"calibration";//率定的地址
        //模型运行的config
        String SHUIWEN_MODEL_RUN = SWYB_SHUIWEN_MODEL_PATH + File.separator + run;

        String SHUIWEN_MODEL_RUN_PLAN = SHUIWEN_MODEL_RUN + File.separator + planId;

        File outShuiWenPath = new File(SHUIWEN_MODEL_TEMPLATE_OUTPUT);

        outShuiWenPath.mkdir();

        String  catchModel = planInfo.getnModelid();
        String reachModel = planInfo.getnSWModelid();
        int isTag =0;
        switch (catchModel){
            case "MODEL_SWYB_CATCHMENT_SCS":
                //1 修改率定的SCS模型watershed.csv入参文件，watershed.csv
                int result0 = writeCalibrationWatershedCsv(SHUIWEN_MODEL_TEMPLATE_INPUT,planInfo);
                if (result0 == 0){
                    System.out.println("率定: 率定交互watershed.csv输入文件失败");
                    return -1L;
                }
                break;
            case "MODEL_SWYB_CATCHMENT_DWX":
                //2 修改率定的单位线unit.csv文件
                int result1 = writeCalibrationUnitCsv(SHUIWEN_MODEL_TEMPLATE_INPUT,planInfo);
                if (result1 == 0){
                    System.out.println("率定: 率定交互unit.csv输入文件失败");
                    return -1L;
                }
                break;
            case "MODEL_SWYB_CATCHMENT_XAJ":
                //3 修改率定的单位线xaj.csv文件
                int result2 = writeCalibrationXajCsv(SHUIWEN_MODEL_TEMPLATE_INPUT,planInfo);
                if (result2 == 0){
                    System.out.println("率定: 率定交互xaj.csv输入文件失败");
                    return -1L;
                }
                break;
            case "MODEL_SWYB_CATCHMENT_ZN": //什么都不用做
                isTag++;
                break;
            default:
                System.out.println("模型集水区编码错误");
                return -1L;
        }
        /**
         * 1：马斯京根法
         * 2：相关关系法
         * 3：智能方法
         */
        switch (reachModel){
            case "MODEL_SWYB_REACH_MSJG":
                //4 修改率定的单位线msgj.csv文件
                int result3 = writeCalibrationMsgjCsv(SHUIWEN_MODEL_TEMPLATE_INPUT,planInfo);
                if (result3 == 0){
                    System.out.println("率定: 率定交互msgj.csv输入文件失败");
                    return -1L;
                }
                break;
            case "MODEL_SWYB_REACH_XGGX"://什么都不用做
                isTag++;
                break;
            case "MODEL_SWYB_REACH_ZN":
                isTag++;
                break;
            default:
                System.out.println("模型河段编码错误");
                return -1L;
        }
        if (isTag == 2){
            return -1L;
        }

        //5,修改shuiwen config文件
        int result4 = writeDataToShuiWenConfig(SHUIWEN_MODEL_RUN_PLAN, SHUIWEN_MODEL_TEMPLATE_INPUT, SHUIWEN_MODEL_TEMPLATE_OUTPUT);
        if (result4 == 0){
            System.out.println("率定:修改config文件失败");
            return -1L;
        }
        //6,调用模型计算
        System.out.println("率定:开始水文模型shuiwen模型计算。。。");
        System.out.println("率定:模型计算路径为。。。"+SHUIWEN_MODEL_RUN_PLAN + File.separator + "startUp.bat");
        runModelExe(SHUIWEN_MODEL_RUN_PLAN + File.separator + "startUp.bat");
        System.out.println("率定:模型计算结束。。。");

        //判断是否执行成功，是否有error文件
        String errorStr = SHUIWEN_MODEL_TEMPLATE_OUTPUT + File.separator + "error_log.txt";
        File errorFile = new File(errorStr);
        if (errorFile.exists()) {//存在表示执行失败
            System.out.println("率定:模型计算失败。。存在error_log文件");
            return -1L;
        } else {
            System.out.println("率定:模型计算成功。。不存在error_log文件");
            return 2L;
        }


    }

    private int writeCalibrationMsgjCsv(String shuiwen_model_template_input, YwkPlaninfo planinfo) {

        String reachInput = shuiwen_model_template_input + File.separator + "Reach.csv";
        String reachXiangGuanShujvInput = shuiwen_model_template_input + File.separator + "reach_xiangguan_shujv.csv";

        String riverId = planinfo.getRiverId();
        List<WrpRiverZone> wrpRiverZones = wrpRiverZoneDao.findByRvcd(riverId);

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
            dataMap.put(rvcd+zoneStr,ywkPlanCalibrationZone);

        }
        if (dataMap.size() == 0){
            System.out.println("scs cn值未变");
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
            BufferedWriter bw = new BufferedWriter(new FileWriter(reachInput, false)); // 附加
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

           /* if (basic.getMsjgVote() == 1){//吸入相关系数csv，reachXiangGuanShujvInput
                List<YwkPlanCalibrationFlow> flows = ywkPlanCalibrationFlowDao.findByCalibrationId(basic.getcId());
                BufferedWriter bw1 = new BufferedWriter(new FileWriter(reachXiangGuanShujvInput, false)); // 附加
                bw1.write("dm1,dm2");
                bw1.newLine();
                for (YwkPlanCalibrationFlow flow : flows){
                    Double upFlow = flow.getUpFlow();
                    Double downFlow = flow.getDownFlow();
                    bw1.write(upFlow+","+downFlow);
                    bw1.newLine();
                }
                bw1.close();
                System.out.println("马斯京根相关数据:reachXiangGuanShujv.csv输入文件写入成功");
            }*/
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水文模型之水文模型-率定:水文模型reachInput.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }
    }

    private int writeCalibrationXajCsv(String shuiwen_model_template_input, YwkPlaninfo planinfo) {

        String xajInput = shuiwen_model_template_input + File.separator + "xaj.csv";

        YwkPlanCalibrationXaJBasic xajBasic = ywkPlanCalibrationXaJBasicDao.findByNPlanid(planinfo.getnPlanid());

        if (xajBasic == null){
            System.out.println("水文模型 新安江没有率定");
            return 1;
        }
        List<YwkPlanCalibrationXajEp> epList = ywkPlanCalibrationXajEpDao.findByXajBasicId(xajBasic.getcId());

        try {
            /**
             * K	0.95
             * B	0.3
             * C	0.14
             * WUM	15
             * WLM	85
             * WDM	20
             * WU0	0
             * WL0	2.2
             * WD0	20
             * ep	0.1
             */
            BufferedWriter bw = new BufferedWriter(new FileWriter(xajInput, false)); // 附加
            // 添加新的数据行
            String xajKStr = "K,"+ xajBasic.getXajK();
            String xajBStr = "B," + xajBasic.getXajB();
            String xajCStr = "C," + xajBasic.getXajC();
            String xajWumStr = "WUM," + xajBasic.getXajWum();
            String xajWlmStr = "WLM," + xajBasic.getXajWlm();
            String xajWdmStr = "WDM," + xajBasic.getXajWdm();
            String xajWU0Str = "WU0," + xajBasic.getXajWu0();
            String xajWl0Str = "WL0," + xajBasic.getXajWl0();
            String xajWd0Str = "WD0," + xajBasic.getXajWd0();
            String epStr = "";
            for ( YwkPlanCalibrationXajEp ep : epList){
                Double value = ep.getValue();
                epStr = epStr+value+",";
            }
            epStr = epStr.substring(0,epStr.length()-1);
            String xajEpStr = "ep,"+epStr;
            bw.write(xajKStr);
            bw.newLine();
            bw.write(xajBStr);
            bw.newLine();
            bw.write(xajCStr);
            bw.newLine();
            bw.write(xajWumStr);
            bw.newLine();
            bw.write(xajWlmStr);
            bw.newLine();
            bw.write(xajWdmStr);
            bw.newLine();
            bw.write(xajWU0Str);
            bw.newLine();
            bw.write(xajWl0Str);
            bw.newLine();
            bw.write(xajWd0Str);
            bw.newLine();
            bw.write(xajEpStr);
            bw.newLine();
            bw.close();
            System.out.println("水文模型之水文模型-率定:水文模型xaj.csv输入文件写入成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水文模型之水文模型-率定:水文模型xaj.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }
    }

    private int writeCalibrationUnitCsv(String shuiwen_model_template_input, YwkPlaninfo planinfo) {
        String unitInput = shuiwen_model_template_input + File.separator + "unit.csv";

        List<YwkPlanCalibrationDwx> dwxes = ywkPlanCalibrationDwxDao.findByNPlanid(planinfo.getnPlanid());
        if (CollectionUtils.isEmpty(dwxes)){
            System.out.println("dwx 没有率定交互");
            return 1;
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(unitInput, false)); // 附加
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

    private int writeCalibrationWatershedCsv(String shuiwen_model_template_input,YwkPlaninfo planinfo) {
        String watershedInput = shuiwen_model_template_input + File.separator + "Watershed.csv";
        String riverId = planinfo.getRiverId();
        List<WrpRiverZone> wrpRiverZones = wrpRiverZoneDao.findByRvcd(riverId);

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
            dataMap.put(rvcd+zoneStr,ywkPlanCalibrationZone.getScsCn());

        }
        if (dataMap.size() == 0){
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
            BufferedWriter bw = new BufferedWriter(new FileWriter(watershedInput, false)); // 附加
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
    private int writeDataToShuiWenConfig(String shuiwen_model_run_plan, String shuiwen_model_template_input, String shuiwen_model_template_output) {
        String configUrl = shuiwen_model_run_plan + File.separator + "config.txt";
        List<String> list = new ArrayList();
        String reachUrl = "Reach&&" + shuiwen_model_template_input + File.separator + "Reach.csv";
        String WatershedUrl = "Watershed&&" + shuiwen_model_template_input + File.separator + "Watershed.csv";
        String unitUrl = "unit&&" + shuiwen_model_template_input + File.separator + "unit.csv";
        String model_selectionUrl = "model_selection&&" + shuiwen_model_template_input + File.separator + "model_selection.csv";
        String shuiku_shuiwei_kurongUrl = "shuiku_shuiwei_kurong&&" + shuiwen_model_template_input + File.separator + "shuiku_shuiwei_kurong.csv";
        String shuiku_chushishujuUrl = "shuiku_chushishuju&&" + shuiwen_model_template_input + File.separator + "shuiku_chushishuju.csv";
        String chufayubaoUrl = "chufayubao&&" + shuiwen_model_template_input + File.separator + "chufaduanmian.csv";
        String chufa_shuruUrl = "chufa_shuru&&" + shuiwen_model_template_input + File.separator + "chufaduanmian_shuru.csv";
        String xaj_xinputUrl = "xaj_xinput&&" + shuiwen_model_template_input + File.separator + "xaj.csv";
        String hru_pUrl = "hru_p&&" + shuiwen_model_template_input + File.separator + "hru_p_result.csv";
        String model_functionUrl = "model_function&&" + shuiwen_model_template_input + File.separator + "model_function.csv";
        String hru_scaler_modelUrl = "hru_scaler_model&&" + shuiwen_model_template_input + File.separator + "bpscaler.model";
        String hru_BP_modelUrl = "hru_BP_model&&" + shuiwen_model_template_input + File.separator + "bp.h5";
        String reach_scaler_modelUrl = "reach_scaler_model&&" + shuiwen_model_template_input + File.separator + "bbpp1";
        String reach_BP_modelUrl = "reach_BP_model&&" + shuiwen_model_template_input + File.separator + "m3.h5";
        String reach_xiangguan_shujvUrl = "reach_xiangguan_shujv&&" + shuiwen_model_template_input + File.separator + "reach_xiangguan_shujv.csv";
        String resultUrl = "result&&" + shuiwen_model_template_output + File.separator + "result.txt";
        String shuiku_resultUrl = "shuiku_result&&" + shuiwen_model_template_output + File.separator + "shuiku_result.txt";
        String errorUrl = "error&&" + shuiwen_model_template_output + File.separator + "error_log.txt";

        list.add(reachUrl);
        list.add(WatershedUrl);
        list.add(unitUrl);
        list.add(model_selectionUrl);
        list.add(shuiku_shuiwei_kurongUrl);
        list.add(shuiku_chushishujuUrl);
        list.add(chufayubaoUrl);
        list.add(chufa_shuruUrl);
        list.add(xaj_xinputUrl);
        list.add(hru_pUrl);
        list.add(model_functionUrl);
        list.add(hru_scaler_modelUrl);
        list.add(hru_BP_modelUrl);
        list.add(reach_scaler_modelUrl);
        list.add(reach_BP_modelUrl);
        list.add(reach_xiangguan_shujvUrl);
        list.add(resultUrl);
        list.add(shuiku_resultUrl);
        list.add(errorUrl);
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

        String reachXiangGuanShujvRead = shuiwen_model_template + File.separator + "reach_xiangguan_shujv.csv";
        String reachXiangGuanShujvInput = shuiwen_model_template_input + File.separator + "reach_xiangguan_shujv.csv";

        String shuikuChushiShujuRead = shuiwen_model_template + File.separator + "shuiku_chushishuju.csv";
        String shuikuChushiShujuInput = shuiwen_model_template_input + File.separator + "shuiku_chushishuju.csv";

        String shuikuShuiweiKuRongRead = shuiwen_model_template + File.separator + "shuiku_shuiwei_kurong.csv";
        String shuikuShuiweiKuRongInput = shuiwen_model_template_input + File.separator + "shuiku_shuiwei_kurong.csv";

        String unitRead = shuiwen_model_template + File.separator + "unit.csv";
        String unitInput = shuiwen_model_template_input + File.separator + "unit.csv";

        String watershedRead = shuiwen_model_template + File.separator + "Watershed.csv";
        String watershedInput = shuiwen_model_template_input + File.separator + "Watershed.csv";

        String xajRead = shuiwen_model_template + File.separator + "xaj.csv";
        String xajInput = shuiwen_model_template_input + File.separator + "xaj.csv";

        String bpscalerModelRead = shuiwen_model_template + File.separator + "bpscaler.model";
        String bpscalerModelInput = shuiwen_model_template_input + File.separator + "bpscaler.model";

        String bpH5Read = shuiwen_model_template + File.separator + "bp.h5";
        String bpH5Input = shuiwen_model_template_input + File.separator + "bp.h5";

        String bbpp1Read = shuiwen_model_template + File.separator + "bbpp1";
        String bbpp1Input = shuiwen_model_template_input + File.separator + "bbpp1";

        String m3H5Read = shuiwen_model_template + File.separator + "m3.h5";
        String m3H5Input = shuiwen_model_template_input + File.separator + "m3.h5";

        try {
            FileUtil.copyFile(reachRead, reachInput, true);
            FileUtil.copyFile(reachXiangGuanShujvRead, reachXiangGuanShujvInput, true);
            FileUtil.copyFile(shuikuChushiShujuRead, shuikuChushiShujuInput, true);
            FileUtil.copyFile(shuikuShuiweiKuRongRead, shuikuShuiweiKuRongInput, true);
            FileUtil.copyFile(unitRead, unitInput, true);
            FileUtil.copyFile(watershedRead, watershedInput, true);
            FileUtil.copyFile(xajRead, xajInput, true);

            FileUtil.copyFile(bpscalerModelRead, bpscalerModelInput, true);
            FileUtil.copyFile(bpH5Read, bpH5Input, true);
            FileUtil.copyFile(bbpp1Read, bbpp1Input, true);
            FileUtil.copyFile(m3H5Read, m3H5Input, true);
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

        List<WrpRvrBsin> rivers = wrpRvrBsinDao.findAllParentIdIsNull();
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
                otherHru = otherHru +","+4;
                otherReach = otherReach + "," + 1;
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

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(configUrl, false)); // 附加
            // 写路径
            bw.write(pcp_HRUUrl);
            bw.newLine();
            bw.write(pcp_stationUrl);
            bw.newLine();
            bw.write(hru_p_resultUrl);
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
            Date endTime = planInfo.getdCaculateendtm();
            int size = 0;
            Long step = planInfo.getnOutputtm() / 60;//步长
            while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
                size++;
                startTime = DateUtil.getNextHour(startTime, step.intValue());
            }
            for (int i = 0 ;i < size;i++){
                bw.write("," + i);
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
            Date endTime = planInfo.getdCaculateendtm();
            int size = 0;
            Long step = planInfo.getnOutputtm() / 60;//步长
            while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
                size++;
                startTime = DateUtil.getNextHour(startTime, step.intValue());
            }
            for (int i = 0 ;i < size;i++){
                bw.write("," + i);
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
    public String getModelRunStatus(String planId,Integer tag) {

        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        if (planInfo == null){
            System.out.println("方案找不到。。。。。");
            return "1";
        }
        Long status;
        if (tag == 0){
            status  = planInfo.getnPlanstatus();
        }else {
            status  = planInfo.getnCalibrationStatus();
        }

        if ( status == 2L || status == -1L){
            return "1"; //1的话停止
        }else {
            return "0";
        }

    }

    @Override
    public Object getModelResultQ(String planId,Integer tag) {

        DecimalFormat df = new DecimalFormat("0.000");
        JSONArray list = new JSONArray();
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);

        Long step = planInfo.getnOutputtm() / 60;//步长(小时)

        String riverId = planInfo.getRiverId();

        String SWYB_SHUIWEN_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_BASE_NEW_SHUIWEN_MODEL_PATH");
        String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");

        String SHUIWEN_MODEL_TEMPLATE_OUTPUT ="";
        if (tag == 0){
            SHUIWEN_MODEL_TEMPLATE_OUTPUT = SWYB_SHUIWEN_MODEL_PATH + File.separator + out
                    + File.separator + planId;//输出的地址
        }else {
            SHUIWEN_MODEL_TEMPLATE_OUTPUT = SWYB_SHUIWEN_MODEL_PATH + File.separator + out
                    + File.separator + planId + File.separator +"calibration";//输出的地址
        }
        //解析河道断面
        Map<String, List<String>> finalResult = getModelResult(SHUIWEN_MODEL_TEMPLATE_OUTPUT+File.separator+"result.txt");
        //如果时小清河模型则解析水库断面
        Map<String, List<String>> shuikuResult = new HashMap<>();

         String SWYB_MODEL_OUTPUT_SHUIKU = SHUIWEN_MODEL_TEMPLATE_OUTPUT+File.separator+"shuiku_result.txt";//输出的地址
         shuikuResult = getModelResult(SWYB_MODEL_OUTPUT_SHUIKU);
         finalResult.putAll(shuikuResult);

        //找到河系关联的断面
        List<WrpRcsBsin> listByRiverId = wrpRcsBsinDao.findListByRiverId(riverId);
        List<String> sections = listByRiverId.stream().map(WrpRcsBsin::getRvcrcrsccd).collect(Collectors.toList());

        if(finalResult!=null && finalResult.size()>0){
            Date startTime = planInfo.getdCaculatestarttm();
            Date endTime = planInfo.getdCaculateendtm();
            for(String sectionId : sections){
                JSONObject valObj = new JSONObject();
                list.add(valObj);
                valObj.put("RCS_ID",sectionId);
                JSONArray valList = new JSONArray();
                valObj.put("values",valList);
                List<String> dataList = finalResult.get(sectionId);
                if(dataList!=null && dataList.size()>0){
                    int index = 0;
                    int count = 0;
                    for (Date time = startTime; time.before(endTime); time = DateUtil.getNextHour(startTime, count)) {
                        try{
                            JSONObject dataObj = new JSONObject();
                            dataObj.put("time",DateUtil.dateToStringNormal3(time));
                            dataObj.put("q",df.format(Double.parseDouble(dataList.get(index)+"")));
                            valList.add(dataObj);
                            count+=step;
                            index++;
                        }catch (Exception e){
                            break;
                        }
                    }
                }
            }
        }
        return list;
    }


    @Override
    public Object getModelResultQCalibration(String planId) {
        JSONArray modelResultQ = (JSONArray) getModelResultQ(planId, 0);
        JSONArray modelResultQCalibration = (JSONArray) getModelResultQ(planId, 1);
        List<Map> resultQ = JSON.parseArray(JSON.toJSONString(modelResultQ), Map.class);
        List<Map> resultQCalibration = JSON.parseArray(JSON.toJSONString(modelResultQCalibration), Map.class);
        Map handleMap = new HashMap();
        for (Map map : resultQ){
            Object rcs_id = map.get("RCS_ID");
            Object values = map.get("values");
            handleMap.put(rcs_id,values);
        }

        List<Map<String,Object>>  results = new ArrayList<>();

        for (Map m : resultQCalibration){
            Map<String,Object> resultMap = new HashMap();
            Object rcs_idNew = m.get("RCS_ID");
            resultMap.put("RCS_ID",rcs_idNew);

            List<Map<String,Object>>  valueList = new ArrayList<>();

            List<Map<String,Object>> valuesNew = (List<Map<String, Object>>) m.get("values");
            List<Map<String,Object>> values = (List<Map<String, Object>>) handleMap.get(rcs_idNew);
            Map<String,Object> valueMap = new HashMap<>();
            for (Map<String,Object> value : values){
                String time = value.get("time")+"";
                Object q = value.get("q");
                valueMap.put(time,q);
            }
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
            resultMap.put("RCS_ID",rcs_idNew);
            resultMap.put("values",valueList);
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
     * @param planId
     * @return
     */
    @Override
    public Object getCalibrationList(String planId) {

        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);

        List<WrpRiverZone> riverZones = wrpRiverZoneDao.findByRvcd( planInfo.getRiverId());//查找下面的分区
        Map<String, WrpRiverZone> riverZoneMap = riverZones.stream().collect(Collectors.toMap(WrpRiverZone::getcId, Function.identity()));
        List<String> zoneIds = riverZones.stream().map(WrpRiverZone::getcId).collect(Collectors.toList());

        //List<YwkPlanCalibrationZone> byZoneIds = ywkPlanCalibrationZoneDao.findByZoneIds(zoneIds);
        List<YwkPlanCalibrationZone> byNPlanid = ywkPlanCalibrationZoneDao.findByNPlanid(planId);
        Map<String, YwkPlanCalibrationZone> zoneMap = byNPlanid.stream().collect(Collectors.toMap(YwkPlanCalibrationZone::getZoneId, Function.identity()));

        String catchMentAreaModelId = planInfo.getnModelid(); //集水区模型id
        String reachId = planInfo.getnSWModelid(); //河段模型id

        Map<String,Object> resultMap = new HashMap<>();

        Map map = new HashMap();
        for (int i=1;i < 4 ;i++){
            map.put("msjgK"+i,null);
            map.put("msjgX"+i,null);
            map.put("zoneId"+i,null);
            map.put("zoneName"+i,null);
            map.put("cId"+i,null);
            map.put("scsCn"+i,null);
        }
        for (String zoneId :zoneIds ){
            YwkPlanCalibrationZone ywkPlanCalibrationZone = zoneMap.get(zoneId);
            WrpRiverZone wrpRiverZone = riverZoneMap.get(zoneId);
            Integer riverZoneZoneId = wrpRiverZone.getZoneId();
            map.put("zoneId"+riverZoneZoneId,riverZoneZoneId);
            map.put("zoneName"+riverZoneZoneId,wrpRiverZone.getZoneName());
            map.put("cId"+riverZoneZoneId,wrpRiverZone.getcId());
            if(ywkPlanCalibrationZone != null){
                map.put("msjgK"+riverZoneZoneId,ywkPlanCalibrationZone.getMsjgK());
                map.put("msjgX"+riverZoneZoneId,ywkPlanCalibrationZone.getMsjgX());
                map.put("scsCn"+riverZoneZoneId,ywkPlanCalibrationZone.getScsCn());
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
                resultMap.put("scs",map);
                catchNum = "1";
                break;
            case "MODEL_SWYB_CATCHMENT_DWX":

                List<YwkPlanCalibrationDwx> dwxes = ywkPlanCalibrationDwxDao.findByNPlanid(planId);
                if (CollectionUtils.isEmpty(dwxes)){
                    dwxes = null;
                }
                resultMap.put("dwx",dwxes);
                catchNum = "2";
                break;
            case "MODEL_SWYB_CATCHMENT_XAJ":
                catchNum = "3";
                YwkPlanCalibrationXaJBasic xaJBasic = ywkPlanCalibrationXaJBasicDao.findByNPlanid(planId);
                resultMap.put("xaj",xaJBasic);//基础信息
                List<YwkPlanCalibrationXajEp> xajEps = new ArrayList<>();
                if (xaJBasic != null){
                    xajEps = ywkPlanCalibrationXajEpDao.findByXajBasicId(xaJBasic.getcId());
                    if (CollectionUtils.isEmpty(xajEps)){
                        xajEps = null;
                    }
                }
                resultMap.put("ep",xajEps);

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
                resultMap.put("msjg",map);
                break;
            case "MODEL_SWYB_REACH_XGGX":
               /* List<Map<String,Double>> msjgresult = (List<Map<String,Double>>) CacheUtil.get("calibrationXAJ", planId);
                //List<YwkPlanCalibrationFlow> msjg = new ArrayList<>();
                Object msjg = new Object();
                if (CollectionUtils.isEmpty(msjgresult) && ywkPlanCalibrationBasic != null) {
                    List<YwkPlanCalibrationFlow> msjgFlow = ywkPlanCalibrationFlowDao.findByCalibrationId(ywkPlanCalibrationBasic.getcId());
                    if (CollectionUtils.isEmpty(msjgFlow)){
                        msjg = null;
                    }else {
                        msjg = msjgFlow;
                    }
                }else {
                    msjg = msjgresult;
                }*/

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
    public List<Map<String, Double>> importCalibrationWithDWX(MultipartFile mutilpartFile, String planId) {

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
        CacheUtil.saveOrUpdate("calibrationDWX", planId, results);
        return results;
    }


    /**
     * 新安江参数交互
     * @param mutilpartFile
     * @param planId
     * @return
     */
    @Override
    public List<Map<String, Object>> importCalibrationWithXAJ(MultipartFile mutilpartFile, String planId) {

        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);

        //解析ecxel数据 不包含第一行
        List<String[]> excelList = ExcelUtil.readFiles(mutilpartFile, 0);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        if (excelList == null || excelList.size() < 2) {
            return new ArrayList<>();
        }
        List<String> head = Arrays.asList(excelList.get(0));
        if (CollectionUtils.isEmpty(head) || head.size() != 2){
            System.out.println("新安江表格有问题。。。。");
            return new ArrayList<>();
        }

        List<Map<String,Object>> results = new ArrayList<>();
        try {
            excelList = excelList.subList(1,excelList.size());//干掉表头
                // 遍历每行数据（除了标题）
                for (int i = 0; i < excelList.size(); i++) {
                    Map<String, Object> dataMap = new HashMap<>();
                    String[] strings = excelList.get(i);
                    dataMap.put("relativeTime",i);
                    dataMap.put("absoluteTime",strings[0].trim());
                    df.parse( strings[0].trim());
                    if (strings != null && strings.length > 1) {
                        // 封装每列（每个指标项数据）
                        try {
                            dataMap.put("value",Double.parseDouble(strings[1].trim()));
                        }catch (Exception e){
                            dataMap.put("value",null);
                        }
                    }else {
                        dataMap.put("value",null);
                    }
                    results.add(dataMap);
                }

        }catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        int size = 0;
        Long step = planInfo.getnOutputtm() / 60;//步长
        while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
            size++;
            startTime = DateUtil.getNextHour(startTime, step.intValue());
        }
        if (results.size() != size){//TODO 必须数量跟时间序列一致
            return new ArrayList<>();
        }
        //TODO 修改雨量值并不修改基础表的数据，只修改缓存的的数据
        CacheUtil.saveOrUpdate("calibrationXAJ", planId, results);
        return results;
    }


    @Override
    public List<Map<String, Double>> importCalibrationWithMSJG(MultipartFile mutilpartFile, String planId) {

        //解析ecxel数据 不包含第一行
        List<String[]> excelList = ExcelUtil.readFiles(mutilpartFile, 0);

        if (excelList == null || excelList.size() < 2) {
            return new ArrayList<>();
        }
        List<String> head = Arrays.asList(excelList.get(0));
        if (CollectionUtils.isEmpty(head) || head.size() != 2){
            System.out.println("马斯京根表格有问题。。。。");
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
                for (int j =0;j < l.size();j++){
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
                        key = "upFlow";
                    }else if (j == 1){
                        key = "downFlow";
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
        CacheUtil.saveOrUpdate("calibrationMSJG", planId, results);
        return results;
    }

    @Autowired
    YwkPlanCalibrationXaJBasicDao ywkPlanCalibrationXaJBasicDao;

    @Autowired
    YwkPlanCalibrationDwxDao ywkPlanCalibrationDwxDao;

    @Transactional
    @Override
    public void saveCalibrationDwxToDB(String planId,List<Map<String,Double>> result) {

        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        if (planInfo.getnCalibrationStatus() == null || planInfo.getnCalibrationStatus() != 0L){
            planInfo.setnCalibrationStatus(0L);
            ywkPlaninfoDao.save(planInfo);
        }

        ywkPlanCalibrationDwxDao.deleteByNPlanid(planId);//删除
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
            ywkPlanCalibrationDwx.setnPlanid(planId);
            ywkPlanCalibrationDwx.setCreateTime(new Date());
            insert.add(ywkPlanCalibrationDwx);
        }
        ywkPlanCalibrationDwxDao.saveAll(insert);
    }

    @Autowired
    YwkPlanCalibrationXajEpDao ywkPlanCalibrationXajEpDao;

    @Transactional
    @Override
    public void saveCalibrationXAJToDB(String planId, List<Map<String, Object>> result, CalibrationXAJVo calibrationXAJVo) {

        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        if (planInfo.getnCalibrationStatus() == null || planInfo.getnCalibrationStatus() != 0L){
            planInfo.setnCalibrationStatus(0L);
            ywkPlaninfoDao.save(planInfo);
        }
        ywkPlanCalibrationXaJBasicDao.deleteByNPlanid(planId);
        YwkPlanCalibrationXaJBasic calibrationBasic = new YwkPlanCalibrationXaJBasic();
        calibrationBasic.setcId(StrUtil.getUUID());
        calibrationBasic.setnPlanid(planId);
        calibrationBasic.setCreateTime(new Date());
        calibrationBasic.setXajB(calibrationXAJVo.getXajB());
        calibrationBasic.setXajC(calibrationXAJVo.getXajC());
        calibrationBasic.setXajK(calibrationXAJVo.getXajK());
        calibrationBasic.setXajWd0(calibrationXAJVo.getXajWd0());
        calibrationBasic.setXajWdm(calibrationXAJVo.getXajWdm());
        calibrationBasic.setXajWl0(calibrationXAJVo.getXajWl0());
        calibrationBasic.setXajWlm(calibrationXAJVo.getXajWlm());
        calibrationBasic.setXajWu0(calibrationXAJVo.getXajWu0());
        calibrationBasic.setXajWum(calibrationXAJVo.getXajWum());
        ywkPlanCalibrationXaJBasicDao.save(calibrationBasic);

        ywkPlanCalibrationXajEpDao.deleteByXajBasicId(calibrationBasic.getcId());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<YwkPlanCalibrationXajEp> insert = new ArrayList<>();
        for (Map<String,Object> map : result){
            YwkPlanCalibrationXajEp ywkPlanCalibrationXajEp = new YwkPlanCalibrationXajEp();
            Long relativeTime = Long.parseLong(map.get("relativeTime")+"");
            try {
                Date absoluteTime = df.parse(map.get("absoluteTime")+"");
                ywkPlanCalibrationXajEp.setAbsoluteTime(absoluteTime);
            }catch (Exception e){
                e.printStackTrace();
            }
            Double ep = (Double) map.get("value");
            ywkPlanCalibrationXajEp.setRelativeTime(relativeTime);
            ywkPlanCalibrationXajEp.setValue(ep);
            ywkPlanCalibrationXajEp.setCreateTime(new Date());
            ywkPlanCalibrationXajEp.setcId(StrUtil.getUUID());
            ywkPlanCalibrationXajEp.setXajBasicId(calibrationBasic.getcId());
            insert.add(ywkPlanCalibrationXajEp);
        }
        ywkPlanCalibrationXajEpDao.saveAll(insert);

    }

    @Autowired
    YwkPlanCalibrationFlowDao ywkPlanCalibrationFlowDao;
    @Transactional
    @Override
    public void saveCalibrationMSJGOrScSToDB(String planId, CalibrationMSJGAndScsVo calibrationMSJGAndScsVo,Integer tag) {

        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        if (planInfo.getnCalibrationStatus() == null || planInfo.getnCalibrationStatus() != 0L){
            planInfo.setnCalibrationStatus(0L);
            ywkPlaninfoDao.save(planInfo);
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


        List<YwkPlanCalibrationZone> byNPlanidAndZoneId = ywkPlanCalibrationZoneDao.findByNPlanid(planId);
        Map<String, YwkPlanCalibrationZone> zoneMap = byNPlanidAndZoneId.stream().collect(Collectors.toMap(YwkPlanCalibrationZone::getZoneId, Function.identity()));

        for (String id : ids){
            YwkPlanCalibrationZone ywkPlanCalibrationZone = zoneMap.get(id);
            if (ywkPlanCalibrationZone == null){
                ywkPlanCalibrationZone  = new YwkPlanCalibrationZone();
                ywkPlanCalibrationZone.setcId(StrUtil.getUUID());
                ywkPlanCalibrationZone.setnPlanid(planId);
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


        /*if (calibrationMSJGVo.getMsjgVote()==1){相关稀释的
            ywkPlanCalibrationFlowDao.deleteByCalibrationId(calibrationBasic.getcId());
            List<YwkPlanCalibrationFlow> insert = new ArrayList<>();
            for (Map<String,Double> map : result){
                YwkPlanCalibrationFlow flow = new YwkPlanCalibrationFlow();
                Double d1 = map.get("upFlow");
                Double d2 = map.get("downFlow");
                flow.setcId(StrUtil.getUUID());
                flow.setCalibrationId(calibrationBasic.getcId());
                flow.setUpFlow(d1);
                flow.setDownFlow(d2);
                flow.setCreateTime(new Date());
                insert.add(flow);
            }
            ywkPlanCalibrationFlowDao.saveAll(insert);
        }
*/

    }



    @Override
    public Workbook exportCalibrationXAJTemplate(String planId) {
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
        XSSFSheet sheet = workbook.createSheet("预报断面流量数据导入模板");
        //填充表头
        //第一行
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue("时间");
        XSSFCell cell1 = row.createCell(1);
        cell1.setCellStyle(style);
        cell1.setCellValue("蒸发量");
        //设置自动列宽
        sheet.setColumnWidth(0, 5100);

        //封装时间列
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        Long step = planInfo.getnOutputtm() / 60;//步长
        int beginLine = 1;
        //封装数据
        while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
            XSSFRow row1 = sheet.createRow(beginLine);
            row1.createCell(0).setCellValue(DateUtil.dateToStringNormal3(startTime));
            row1.createCell(1).setCellValue(0.1);
            beginLine++;
            startTime = DateUtil.getNextHour(startTime, step.intValue());
        }
        return workbook;
    }
}
