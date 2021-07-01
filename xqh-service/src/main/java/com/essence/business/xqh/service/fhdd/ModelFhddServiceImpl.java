package com.essence.business.xqh.service.fhdd;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.essence.business.xqh.api.fhdd.ModelFhddService;
import com.essence.business.xqh.api.fhdd.vo.ModelFhddInputVo;
import com.essence.business.xqh.api.fhdd.vo.ModelFhddPlanVo;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("all")
public class ModelFhddServiceImpl implements ModelFhddService {

    @Autowired
    YwkPlaninfoDao ywkPlaninfoDao; //方案基本信息

    @Autowired
    StPptnRDao stPptnRDao; //雨量数据表

    @Autowired
    StStbprpBDao stStbprpBDao; //监测站

    @Autowired
    YwkModelDao ywkModelDao;

    @Autowired
    StRsvrfsrBDao stRsvrfsrBDao;  //汛限水位

    @Autowired
    StRsvrfcchBDao stRsvrfcchBDao; //防洪指标

    @Autowired
    YwkPlaninRainfallDao ywkPlaninRainfallDao; //方案雨量

    @Autowired
    YwkPlanInputZDao ywkPlanInputZDao; //方案起调水位和下泄流量

    @Autowired
    WrpRcsBsinDao wrpRcsBsinDao; //断面基本信息

    @Autowired
    YwkPlanTriggerRcsDao ywkPlanTriggerRcsDao;//预报断面

    @Autowired
    YwkPlanTriggerRcsFlowDao ywkPlanTriggerRcsFlowDao;//预报断面流量

    @Autowired
    WrpRvrBsinDao wrpRvrBsinDao;//河系

    @Autowired
    WrpRsrBsinDao wrpRsrBsinDao; //水库

    @Autowired
    YwkPlanCalibrationZoneXggxDao ywkPlanCalibrationZoneXggxDao;//相关关系

    @Override
    public Map<String, Object> getModelList() {
        String catchmentArea = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_MODEL_TYPE_CATCHMENT_AREA"); //集水区
        String reach = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_MODEL_TYPE_REACH"); //河段
        List<YwkModel> ywkModelByModelType = ywkModelDao.getYwkModelByModelType(catchmentArea);
        List<YwkModel> ywkModelByModelType1 = ywkModelDao.getYwkModelByModelType(reach);
        Map result = new HashMap();
        result.put("catchmentArea", ywkModelByModelType);
        result.put("reach", ywkModelByModelType1);
        return result;
    }

    @Override
    public Boolean searchPlanIsExits(String planName) {
        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("FHDD");
        List<YwkPlaninfo> isAll = ywkPlaninfoDao.findByCPlannameAndPlanSystem(planName, planSystem);
        return CollectionUtils.isEmpty(isAll)?false:true;
    }

    @Override
    public String savePlan(ModelFhddPlanVo vo) {
        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("FHDD");
        List<YwkPlaninfo> isAll = ywkPlaninfoDao.findByCPlannameAndPlanSystem(vo.getPlanName(), planSystem);
        if (!CollectionUtils.isEmpty(isAll)) {
            return "planNameExist";
        }

        Date startTime = vo.getStartTime(); //开始时间
        Date endTIme = vo.getEndTime();  //结束时间
        int step = vo.getStep();//以小时为单位
        int timeType = vo.getTimeType();
        //方案基本信息入库
        YwkPlaninfo ywkPlaninfo = new YwkPlaninfo();
        ywkPlaninfo.setnPlanid(UuidUtil.get32UUIDStr());
        ywkPlaninfo.setPlanSystem(planSystem);
        ywkPlaninfo.setcPlanname(vo.getPlanName());
        ywkPlaninfo.setnCreateuser("user");
        ywkPlaninfo.setnPlancurrenttime(new Date());
        ywkPlaninfo.setdCaculatestarttm(startTime);//方案计算开始时间
        ywkPlaninfo.setdCaculateendtm(endTIme);//方案计算结束时间
        ywkPlaninfo.setnPlanstatus(0l);//方案状态

        if (0 == timeType) {
            ywkPlaninfo.setnOutputtm(Long.parseLong(step + ""));//设置间隔分钟
        } else {
            ywkPlaninfo.setnOutputtm(step * 60L);//设置间隔分钟
        }
        ywkPlaninfo.setdRainstarttime(startTime);
        ywkPlaninfo.setdRainendtime(endTIme);
        ywkPlaninfo.setdOpensourcestarttime(startTime);
        ywkPlaninfo.setdOpensourceendtime(endTIme);
        ywkPlaninfo.setnCreatetime(DateUtil.getCurrentTime());
        ywkPlaninfo.setRiverId(vo.getRvcd());
        ywkPlaninfo.setnCalibrationStatus(0l);
        ywkPlaninfo.setnPublish(0L);
        ywkPlaninfo.setRscd(vo.getRscd()); //方案所属水文站编码
        YwkPlaninfo saveDbo = ywkPlaninfoDao.save(ywkPlaninfo);
        //保存数据到缓存
        CacheUtil.saveOrUpdate("planInfo", ywkPlaninfo.getnPlanid(), ywkPlaninfo);

        return saveDbo.getnPlanid();
    }

    /**
     * 获取方案信息
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
    public List<Map<String, Object>> getRainfallsInfo(YwkPlaninfo planInfo) throws ParseException {
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
        CacheUtil.saveOrUpdate("rainfall", planInfo.getnPlanid()+"new", results);
        return results;
    }

    public List<Map<String, Object>> getRainfalls(YwkPlaninfo planInfo) throws ParseException {
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

        return results;
    }

    @Autowired
    ModelCallHandleDataService modelCallHandleDataService;

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

    @Override
    public Workbook exportRainfallTemplate(YwkPlaninfo planinfo) throws Exception{
        //封装时间列
        Date startTime = planinfo.getdCaculatestarttm();
        Date endTime = planinfo.getdCaculateendtm();
        // Long step = planInfo.getnOutputtm() / 60;//步长
        Long step = planinfo.getnOutputtm();//步长
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

        List<Map<String, Object>> rainfalls = getRainfallsInfo(planinfo);
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
    public List<Map<String, Object>> importRainfallData(MultipartFile mutilpartFile, YwkPlaninfo planinfo) {
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
            System.out.println("雨量表头错误");
            return new ArrayList<>();
        }
        Date startTime = planinfo.getdCaculatestarttm();
        Date endTime = planinfo.getdCaculateendtm();
        int size = 0;
        Long step = planinfo.getnOutputtm();//步长
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
        CacheUtil.saveOrUpdate("rainfall", planinfo.getnPlanid() + "new", inSertList);
        return inSertList;
    }

    @Override
    public Boolean savePlanInputZ(ModelFhddInputVo vo) {
        YwkPlanInputZ inputByPlanid = ywkPlanInputZDao.findByNPlanid(vo.getPlanId());
        Boolean flag = false;
        if(inputByPlanid != null){
            ywkPlanInputZDao.delete(inputByPlanid.getCId());
        }
        //信息入库
        YwkPlanInputZ ywkPlanInputZ = new YwkPlanInputZ();
        ywkPlanInputZ.setCId(StrUtil.getUUID());
        ywkPlanInputZ.setNPlanid(vo.getPlanId());
        ywkPlanInputZ.setNZ(vo.getNumberZ());
        ywkPlanInputZ.setNQ(vo.getNumberQ());
        ywkPlanInputZ.setDCreateTime(DateUtil.getCurrentTime());
        YwkPlanInputZ resultInput = ywkPlanInputZDao.save(ywkPlanInputZ);
        if(resultInput!=null){
            flag = true;
        }
        return flag;
    }

    @Override
    public void modelPcpCall(YwkPlaninfo planinfo) {

    }

    @Override
    public Boolean modelHydrologyCall(YwkPlaninfo planinfo) {
        return null;
    }

    @Override
    @Async
    public void modelCall(YwkPlaninfo planInfo) {
        System.out.println("模型运算线程！" + Thread.currentThread().getName());

        Date originalStartTm = planInfo.getdCaculatestarttm();
        try{
            //雨量信息表
            long startTime = System.currentTimeMillis(); //获取开始时间

            Long aLong = ywkPlaninRainfallDao.countByPlanId(planInfo.getnPlanid());
            if (aLong == 0L) {
                System.out.println("方案雨量表没有保存数据");
                throw new RuntimeException("方案雨量表没有保存数据");
            }

            planInfo.setdCaculatestarttm(DateUtil.getNextHour(planInfo.getdCaculatestarttm(),-72));
            List<Map<String, Object>> before72results = getRainfalls(planInfo);
            if (CollectionUtils.isEmpty(before72results)) {
                System.out.println("雨量信息为空，无法计算");
                throw new RuntimeException("雨量信息为空，无法计算");
            }

            //创建入参、出参
            String FHDD_PCP_HANDLE_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("FHDD_PCP_HANDLE_MODEL_PATH");
            String FHDD_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("FHDD_MODEL_PATH");
            String template = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");
            String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");
            String run = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_RUN");

            //第一个模型
            String PCP_HANDLE_MODEL_TEMPLATE = FHDD_PCP_HANDLE_MODEL_PATH + File.separator + template;
            String PCP_HANDLE_MODEL_TEMPLATE_INPUT = PCP_HANDLE_MODEL_TEMPLATE
                    + File.separator + "INPUT" + File.separator + planInfo.getnPlanid(); //输入的地址
            String PCP_HANDLE_MODEL_TEMPLATE_OUTPUT = FHDD_PCP_HANDLE_MODEL_PATH + File.separator + out
                    + File.separator + planInfo.getnPlanid();//输出的地址
            String PCP_HANDLE_MODEL_RUN = FHDD_PCP_HANDLE_MODEL_PATH + File.separator + run;
            String PCP_HANDLE_MODEL_RUN_PLAN = PCP_HANDLE_MODEL_RUN + File.separator + planInfo.getnPlanid();

            //另一个模型
            String FHDD_MODEL_TEMPLATE = FHDD_MODEL_PATH + File.separator + template;
            String FHDD_MODEL_TEMPLATE_INPUT = FHDD_MODEL_TEMPLATE
                    + File.separator + "INPUT" + File.separator + planInfo.getnPlanid(); //输入的地址
            String FHDD_MODEL_TEMPLATE_OUTPUT = FHDD_MODEL_PATH + File.separator + out
                    + File.separator + planInfo.getnPlanid();//输出的地址

            //模型运行的config
            String FHDD_MODEL_RUN = FHDD_MODEL_PATH + File.separator + run;
            String FHDD_MODEL_RUN_PLAN = FHDD_MODEL_RUN + File.separator + planInfo.getnPlanid();

            File inputPcpPath = new File(PCP_HANDLE_MODEL_TEMPLATE_INPUT);
            File outPcpPath = new File(PCP_HANDLE_MODEL_TEMPLATE_OUTPUT);
            File runPcpPath = new File(PCP_HANDLE_MODEL_RUN_PLAN);

            File inputSkddPath = new File(FHDD_MODEL_TEMPLATE_INPUT);
            File outSkddPath = new File(FHDD_MODEL_TEMPLATE_OUTPUT);
            File runSkddPath = new File(FHDD_MODEL_RUN_PLAN);

            inputPcpPath.mkdir();
            outPcpPath.mkdir();
            runPcpPath.mkdir();
            inputSkddPath.mkdir();
            outSkddPath.mkdir();
            runSkddPath.mkdir();

            //TODO 模型先算第一步，数据处理模型pcp_model
            //1，写入pcp_HRU.csv
            int result0 = writeDataToInputPcpHRUCsv(PCP_HANDLE_MODEL_TEMPLATE_INPUT, PCP_HANDLE_MODEL_TEMPLATE, planInfo);
            if (result0 == 0) {
                System.out.println("防洪调度模型之PCP模型:写入pcp_HRU失败");
                throw new RuntimeException("防洪调度模型之PCP模型:写入pcp_HRU失败");
            }

            //2,写入pcp_station.csv
            int result1 = writeDataToInputPcpStationCsv(PCP_HANDLE_MODEL_TEMPLATE_INPUT, before72results, planInfo);
            if (result1 == 0) {
                System.out.println("防洪调度模型之PCP模型:写入pcp_station失败");
                throw new RuntimeException("防洪调度模型之PCP模型:写入pcp_station失败");
            }

            //3.复制config以及可执行文件
            int result2 = copyPCPExeFile(PCP_HANDLE_MODEL_RUN, PCP_HANDLE_MODEL_RUN_PLAN);
            if (result2 == 0) {
                System.out.println("防洪调度模型之PCP模型:复制执行文件与config文件写入失败。。。");
                throw new RuntimeException("防洪调度模型之PCP模型:复制执行文件与config文件写入失败。。。");

            }

            //4,修改config文件
            int result3 = writeDataToPcpConfig(PCP_HANDLE_MODEL_RUN_PLAN, PCP_HANDLE_MODEL_TEMPLATE_INPUT, PCP_HANDLE_MODEL_TEMPLATE_OUTPUT);
            if (result3 == 0) {
                System.out.println("防洪调度模型之PCP模型:修改config文件失败");
                throw new RuntimeException("防洪调度模型之PCP模型:修改config文件失败");

            }
            long endTime = System.currentTimeMillis();   //获取开始时间
            System.out.println("防洪调度模型之PCP模型:组装pcp模型所用的参数的时间为:" + (endTime - startTime) + "毫秒");

            //5.调用模型
            //调用模型计算
            startTime = System.currentTimeMillis();
            System.out.println("防洪调度模型之PCP模型:开始防洪调度模型PCP模型计算。。。");
            System.out.println("防洪调度模型之PCP模型:模型计算路径为。。。" + PCP_HANDLE_MODEL_RUN_PLAN + File.separator + "startUp.bat");
            runModelExe(PCP_HANDLE_MODEL_RUN_PLAN + File.separator + "startUp.bat");
            endTime = System.currentTimeMillis();
            System.out.println("防洪调度模型之PCP模型:模型计算结束。。。，所用时间为:" + (endTime - startTime) + "毫秒");
            startTime = System.currentTimeMillis();
            //TODO 判断模型是否执行成功
            //判断是否执行成功，是否有error文件
            String pcp_result = PCP_HANDLE_MODEL_TEMPLATE_OUTPUT + File.separator + "hru_p_result.csv";
            File pcp_resultFile = new File(pcp_result);
            if (pcp_resultFile.exists()) {//存在表示执行成功
                System.out.println("防洪调度模型之PCP模型:pcp模型执行成功hru_p_result.csv文件存在");
            } else {
                System.out.println("防洪调度模型之PCP模型:pcp模型执行成功hru_p_result.csv文件不存在");//todo 执行失败
                throw new RuntimeException("防洪调度模型之PCP模型:pcp模型执行成功hru_p_result.csv文件不存在");
            }

            //TODO 上面的入参条件没存库
            //TODO 第二个水文模型
            //6，预报断面ChuFaDuanMian、ChuFaDuanMian_shuru.csv组装
            int result4 = writeDataToInputShuiWenChuFaDuanMianCsv(FHDD_MODEL_TEMPLATE_INPUT, planInfo);
            if (result4 == 0) {
                System.out.println("防洪调度模型之防洪调度模型:写入chufaduanmian跟chufaduanmian_shuru.csv失败");
                throw new RuntimeException("防洪调度模型之防洪调度模型:写入chufaduanmian跟chufaduanmian_shuru.csv失败");

            }

            //7 copy pcp模型的输出文件到防洪调度模型的输入文件里
            int result5 = copeFirstOutPutHruP(PCP_HANDLE_MODEL_TEMPLATE_OUTPUT, FHDD_MODEL_TEMPLATE_INPUT);
            if (result5 == 0) {
                System.out.println("防洪调度模型之防洪调度模型:copy数据处理模型PCP输出文件hru_p_result失败");
                throw new RuntimeException("防洪调度模型之防洪调度模型:copy数据处理模型PCP输出文件hru_p_result失败");

            }

            //9 读出外部chushishuishuju.csv文件并修改后写入内部input
            int result7 = updateChuShiShuiShuJu(FHDD_MODEL_TEMPLATE, FHDD_MODEL_TEMPLATE_INPUT, planInfo);
            if (result7 == 0) {
                System.out.println("防洪调度模型之防洪调度模型: 修改chushishuishuju.csv文件失败");
                throw new RuntimeException("防洪调度模型之防洪调度模型: 修改chushishuishuju.csv文件失败");

            }

            //10 copy剩下的率定csv输入文件
            int result8 = copyOtherShuiWenLvDingCsv(FHDD_MODEL_TEMPLATE, FHDD_MODEL_TEMPLATE_INPUT);
            if (result8 == 0) {
                System.out.println("防洪调度模型之防洪调度模型: copy剩下的率定csv输入文件失败");
                throw new RuntimeException("防洪调度模型之防洪调度模型: copy剩下的率定csv输入文件失败");

            }

            //11 复制Skdd config以及可执行文件
            int result9 = copyShuiWenExeFile(FHDD_MODEL_RUN, FHDD_MODEL_RUN_PLAN);
            if (result9 == 0) {
                System.out.println("防洪调度模型之防洪调度模型:复制执行文件与config文件写入失败。。。");
                throw new RuntimeException("防洪调度模型之防洪调度模型:复制执行文件与config文件写入失败。。。");

            }

            //12 修改Skdd config文件
            int result10 = writeDataToShuiWenConfig(FHDD_MODEL_RUN_PLAN, FHDD_MODEL_TEMPLATE_INPUT, FHDD_MODEL_TEMPLATE_OUTPUT, 0, planInfo);
            if (result10 == 0) {
                System.out.println("防洪调度模型之防洪调度模型:修改config文件失败");
                throw new RuntimeException("防洪调度模型之防洪调度模型:修改config文件失败");

            }
            endTime = System.currentTimeMillis();
            System.out.println("防洪调度模型之PCP模型:组装Skdd模型所用的参数的时间为:" + (endTime - startTime) + "毫秒");

            //13,
            //调用模型计算
            startTime = System.currentTimeMillis();
            System.out.println("防洪调度模型之防洪调度模型:开始防洪调度模型Skdd模型计算。。。");
            System.out.println("防洪调度模型之防洪调度模型:模型计算路径为。。。" + FHDD_MODEL_RUN_PLAN + File.separator + "startUp.bat");
            runModelExe(FHDD_MODEL_RUN_PLAN + File.separator + "startUp.bat");
            endTime = System.currentTimeMillis();
            System.out.println("防洪调度模型之防洪调度模型:模型计算结束。。。所用时间为:" + (endTime - startTime) + "毫秒");

            //判断是否执行成功，是否有error文件
            String errorStr = FHDD_MODEL_TEMPLATE_OUTPUT + File.separator + "error_log.txt";
            File errorFile = new File(errorStr);
            planInfo.setdCaculatestarttm(originalStartTm);
            if (errorFile.exists()) {//存在表示执行失败
                System.out.println("防洪调度模型之防洪调度模型:模型计算失败。。存在error_log文件");
                planInfo.setnPlanstatus(-1L);
                ywkPlaninfoDao.save(planInfo);
                CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
                return;//todo 执行失败
            } else {
                System.out.println("防洪调度模型之防洪调度模型:模型计算成功。。不存在error_log文件");
                planInfo.setnPlanstatus(2L);
                ywkPlaninfoDao.save(planInfo);
                CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
                return;//todo  执行成功
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("模型执行失败了。。。。。。联系管理员" + e.getMessage());
            planInfo.setnPlanstatus(-1L);
            ywkPlaninfoDao.save(planInfo);
            CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
        }
    }

    @Override
    public String getModelRunStatus(YwkPlaninfo planInfo) {
        Long status = planInfo.getnPlanstatus();

        if (status == 2L || status == -1L) {  // 2L 执行成功  -1L 执行失败
            return "1"; //1的话停止
        } else {
            return "0";
        }
    }

    @Override
    public Object getModelResultQ(YwkPlaninfo planInfo) {
        JSONObject valObj = new JSONObject(); //定义返回对象
        DecimalFormat df = new DecimalFormat("0.000");
        Long step = planInfo.getnOutputtm();//步长(小时)

        String SKDD_XX_SKDD_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("FHDD_MODEL_PATH");
        String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");

        String SKDD_XX_MODEL_TEMPLATE_OUTPUT = "";

        SKDD_XX_MODEL_TEMPLATE_OUTPUT = SKDD_XX_SKDD_MODEL_PATH + File.separator + out
                + File.separator + planInfo.getnPlanid();//输出的地址

        //解析shuiku_result.txt
        Map<String, List<String>> finalResult = new HashMap<>();
        String SKDD_XX_MODEL_OUTPUT_SHUIKU = SKDD_XX_MODEL_TEMPLATE_OUTPUT + File.separator + "shuiku_result.txt";//输出的地址
        finalResult = getModelResult(SKDD_XX_MODEL_OUTPUT_SHUIKU);

        //解析result.txt
        Map<String, List<String>> result = new HashMap<>();
        String SKDD_XX_MODEL_OUTPUT_RESULT = SKDD_XX_MODEL_TEMPLATE_OUTPUT + File.separator + "result.txt";//输出的地址
        result = getModelResult(SKDD_XX_MODEL_OUTPUT_RESULT);

        //找到方案水库关联的断面
        WrpRcsBsin wrpRcsBsin = wrpRcsBsinDao.findById(planInfo.getRscd()).get();
        String wrpName = wrpRcsBsin.getRvcrcrscnm();

        if (finalResult != null && finalResult.size() > 0) {

            Date startTime = planInfo.getdCaculatestarttm();
            Date endTime = planInfo.getdCaculateendtm();
            valObj.put("RCS_ID", wrpRcsBsin.getRvcrcrsccd()); //河道断面编码
            valObj.put("RCS_NAME", wrpName); //河道断面名称
            String stcd = PropertiesUtil.read("/filePath.properties").getProperty("skddxx.RSCD_RSR." + planInfo.getRscd());
            valObj.put("fsltdz",stRsvrfsrBDao.findByStcd(stcd).getFsltdz()); //方案对应汛限水位
            valObj.put("damel",stRsvrfcchBDao.findByStcd(stcd).getDamel()); //方案对应水库坝顶高程
            JSONArray ZList = new JSONArray();
            valObj.put("zValues", ZList); // 水位
            JSONArray rainList = new JSONArray();
            valObj.put("rainValues", rainList); //雨量
            JSONArray valList = new JSONArray();
            valObj.put("qValues", valList); //出库流量

            List<String> needResult = finalResult.get(planInfo.getRscd());

            if(result !=null && result.size()>0){
                JSONArray iValList = new JSONArray();
                int rIndex = 0;
                valObj.put("iValues", iValList); //入库流量
                List<String> resultString = result.get(planInfo.getRscd());
                if(resultString !=null && resultString.size() > 0){
                    for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                        try {
                            JSONObject dataObj = new JSONObject();
                            dataObj.put("time", DateUtil.dateToStringNormal3(time));
                            dataObj.put("i", df.format(Double.parseDouble(resultString.get(rIndex) + "")));
                            iValList.add(dataObj);
                            rIndex++;
                        } catch (Exception e) {
                            break;
                        }
                    }

                    for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                        try {
                            JSONObject dataObjRain = new JSONObject();
                            dataObjRain.put("time", DateUtil.dateToStringNormal3(time));
                            dataObjRain.put("rain", df.format(Double.parseDouble(needResult.get(rIndex) + "")));
                            rainList.add(dataObjRain);
                            rIndex++;
                        } catch (Exception e) {
                            break;
                        }
                    }
                }
            }

            if (needResult != null && needResult.size() > 0) {
                int index = 0;
                for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                    try {
                        JSONObject dataObj = new JSONObject();
                        dataObj.put("time", DateUtil.dateToStringNormal3(time));
                        dataObj.put("q", df.format(Double.parseDouble(needResult.get(index) + "")));
                        valList.add(dataObj);
                        index++;
                    } catch (Exception e) {
                        break;
                    }
                }
                for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                    try {
                        JSONObject dataObjZ = new JSONObject();
                        dataObjZ.put("time", DateUtil.dateToStringNormal3(time));
                        dataObjZ.put("z", df.format(Double.parseDouble(needResult.get(index) + "")));
                        ZList.add(dataObjZ);
                        index++;
                    } catch (Exception e) {
                        break;
                    }
                }
//                valObj.put("hfQ", df.format(Double.parseDouble(needResult.get(index) + "")));
//                index++;
//                valObj.put("hfTime", DateUtil.getNextMinute(startTime, step.intValue() * Integer.parseInt(needResult.get(index) + "")));
//                index++;
//                valObj.put("hfTotal", df.format(Double.parseDouble(needResult.get(index) + "")));
//                index++;
//                for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextMinute(time, step.intValue())) {
//                    try {
//                        JSONObject dataObjRain = new JSONObject();
//                        dataObjRain.put("time", DateUtil.dateToStringNormal3(time));
//                        dataObjRain.put("rain", df.format(Double.parseDouble(needResult.get(index) + "")));
//                        rainList.add(dataObjRain);
//                        index++;
//                    } catch (Exception e) {
//                        break;
//                    }
//                }
            }

        }
        return valObj;
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
            System.err.println("水文模型之PCP模型：pcp_HRU.csv输入文件读取错误:read errors :" + e);
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
            System.out.println("水文模型之PCP模型:水文模型pcp_HRU.csv输入文件写入成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水文模型之PCP模型:水文模型pcp_HRU.csv输入文件写入失败");
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
//            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(pcpHRUInputUrl), "GBK" );
//            BufferedWriter bw = new BufferedWriter(out); // 附加
            BufferedWriter bw = new BufferedWriter(new FileWriter(pcpHRUInputUrl, false)); // 附加
            // 添加新的数据行
            bw.write("" + ",STNM,LGTD,LTTD"); //编写表头
            Date startTime = planInfo.getdCaculatestarttm();
            System.out.println(startTime);
            Date endTime = planInfo.getdCaculateendtm();
            System.out.println(endTime);
            int size = 0;
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
            for (Map<String, Object> map : results) {
                Object stcd = map.get("STCD");
                String stnm = map.get("STNM") == null ? "" : map.get("STNM") + "";
                String lgtd = map.get("LGTD") == null ? "" : map.get("LGTD") + "";
                Object lttd = map.get("LTTD") == null ? "" : map.get("LTTD") + "";
                List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("LIST");
                bw.write(stcd+","+stnm+","+lgtd+","+lttd);
                for (Map<String, Object> m : list) {
                    String value = m.get("DRP") == null ? "" : m.get("DRP") + "";
                    bw.write("," + value);
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
            System.err.println("水文模型之PCP模型：copy执行文件exe,bat文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("水文模型之PCP模型：copy执行文件exe,bat文件错误" + e.getMessage());
            return 0;
        }
    }

    /**
     * 修改水文模型的数据模型的config文件
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
     * 水文模型，chufaduanmian 跟chufaduanmian_shuru
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
            System.out.println("水文模型之水文模型:水文模型ChuFaDuanMian.csv输入文件写入成功");
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水文模型之水文模型:水文模型ChuFaDuanMian.csv输入文件写入失败");
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
            System.err.println("水文模型之水文模型：copy数据处理模型PCP输出文件hru_p_result文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("水文模型之水文模型：copy数据处理模型PCP输出文件hru_p_result文件失败" + e.getMessage());
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
     *
     * @param shuiwen_model_template
     * @param planinfo
     * @return
     */
    private int updateChuShiShuiShuJu(String shuiwen_model_template,String shuiwen_model_template_input, YwkPlaninfo planinfo){
        String shuikuChushiShujuReadCsv = shuiwen_model_template + File.separator + "shuiku_chushishuju.csv";
        String shuikuChushiShujuOutCsv = shuiwen_model_template_input + File.separator + "shuiku_chushishuju.csv";
        YwkPlanInputZ inputByPlanid = ywkPlanInputZDao.findByNPlanid(planinfo.getnPlanid());
        Double nZ = inputByPlanid.getNZ();
        Double nQ = inputByPlanid.getNQ();
        String plan_rscd_name = PropertiesUtil.read("/filePath.properties").getProperty("skdd.ID_NAME." + planinfo.getRscd()); //取水文站编码映射

        Long step = planinfo.getnOutputtm();
        DecimalFormat format = new DecimalFormat("0.00");
        Double hour = Double.parseDouble(format.format(step* 1.0 / 60 ));

        try {
            FileReader fileReader = new FileReader(shuikuChushiShujuReadCsv);
            if(fileReader!=null){
                BufferedReader reader = new BufferedReader(new FileReader(shuikuChushiShujuReadCsv));
                String line = null;
                List<List<String>> datas = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                    datas.add(Arrays.asList(item));
                }

                int index = 0;//修改参数的位置
                for(int i=0; i < datas.get(0).size(); i++){
                    if(plan_rscd_name.toLowerCase().equals(datas.get(0).get(i))){
                        index = i;
                    }
                }

                if(index != 0){
                    datas.get(1).set(index, hour.toString());
                    datas.get(2).set(index, nZ.toString());
                    datas.get(3).set(index, nQ.toString());
                }

//                FileOutputStream fos = new FileOutputStream(shuikuChushiShujuReadCsv,true); //为true 时会追加写
                FileOutputStream fos = new FileOutputStream(shuikuChushiShujuOutCsv,false); //false时覆盖
                OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
                BufferedWriter bw = new BufferedWriter(osw);
                for (int i=0; i < datas.size(); i++) {
                    for(int j=0; j < datas.get(i).toArray().length; j++){
                        bw.write(datas.get(i).toArray()[j] + ","); //csv文件以，分隔
                        if(j == datas.get(i).toArray().length-1) {
                            bw.write("\r\n");
                        }
                    }
                }

                //注意关闭的先后顺序，先打开的后关闭，后打开的先关闭
                bw.close();
                osw.close();
                fos.close();
                reader.close();
                fileReader.close();
            }
            System.err.println("水文模型之水文模型：修改chushishuishuju.csv文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("水文模型之水文模型：修改chushishuishuju.csv文件失败" + e.getMessage());
            return 0;
        }
    }

    /**
     * copy剩下的率定csv输入文件
     *
     * @param shuiwen_model_template
     * @param shuiwen_model_template_input
     * @return
     */
    private int copyOtherShuiWenLvDingCsv(String shuiwen_model_template, String shuiwen_model_template_input) {

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
            System.err.println("水文模型之水文模型：copy剩下的率定csv输入文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("水文模型之水文模型：copy剩下的率定csv输入文件失败" + e.getMessage());
            return 0;
        }
    }

    /**
     * cope 水文模型exe可执行文件
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
            System.err.println("水文模型之水文模型：copy执行文件exe,bat文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("水文模型之水文模型：copy执行文件exe,bat文件错误" + e.getMessage());
            return 0;
        }
    }

    /**
     * 修改水文模型config文件
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
            for (String s : list) {
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



}
