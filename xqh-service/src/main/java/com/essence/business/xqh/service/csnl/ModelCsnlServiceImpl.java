package com.essence.business.xqh.service.csnl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.essence.business.xqh.api.csnl.ModelCsnlService;
import com.essence.business.xqh.api.csnl.vo.PlanInfoCsnlVo;
import com.essence.business.xqh.api.fhybdd.service.ModelCallHandleDataService;
import com.essence.business.xqh.api.hsfxtk.dto.*;
import com.essence.business.xqh.api.modelResult.PlanProcessDataService;
import com.essence.business.xqh.common.util.*;
import com.essence.business.xqh.dao.dao.fhybdd.*;
import com.essence.business.xqh.dao.dao.hsfxtk.*;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.fhybdd.YwkModel;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninRainfall;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.business.xqh.dao.entity.hsfxtk.*;
import com.essence.framework.util.StrUtil;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ModelCsnlServiceImpl implements ModelCsnlService {

    @Autowired
    private YwkPlaninfoDao ywkPlaninfoDao;

    @Autowired
    YwkPlanOutputGridProcessDao ywkPlanOutputGridProcessDao;

    @Autowired
    StPptnRDao stPptnRDao; //雨量数据表

    @Autowired
    YwkPlaninRainfallDao ywkPlaninRainfallDao; //方案雨量

    @Autowired
    StStbprpBDao stStbprpBDao; //监测站

    @Autowired
    YwkCsnlRoughnessDao ywkCsnlRoughnessDao;//城市内涝糙率

    @Autowired
    PlanProcessDataService planProcessDataService;//模型结果解析

    /**
     * 根据方案名称查询对应方案是否已存在
     * @param planName
     * @return
     */
    @Override
    public Boolean searchPlanIsExits(String planName) {
        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("XT_CSNL");
        List<YwkPlaninfo> isAll = ywkPlaninfoDao.findByCPlannameAndPlanSystem(planName, planSystem);
        return CollectionUtils.isEmpty(isAll)?false:true;
    }

    /**
     * 保存方案基本信息
     * @param vo
     * @return
     */
    @Override
    public String savePlanToDb(PlanInfoCsnlVo vo) throws Exception {
        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("XT_CSNL");
        List<YwkPlaninfo> isAll = ywkPlaninfoDao.findByCPlannameAndPlanSystem(vo.getcPlanname(), planSystem);
        if (!CollectionUtils.isEmpty(isAll)) {
            return "planNameExist";
        }

        if (StrUtil.isEmpty(vo.getnPlanid())) {
            vo.setnPlanid(StrUtil.getUUID());
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
        Date startTime = format.parse(format.format(vo.getStartTime())); //开始时间
        Date endTime = format.parse(format.format(vo.getEndTime()));  //结束时间
        Long step = vo.getStep();//以分钟为单位

        //方案基本信息入库
        YwkPlaninfo ywkPlaninfo = new YwkPlaninfo();
        ywkPlaninfo.setnPlanid(vo.getnPlanid());
        ywkPlaninfo.setPlanSystem(planSystem);
        ywkPlaninfo.setcPlanname(vo.getcPlanname());
        ywkPlaninfo.setnCreateuser("user");
        ywkPlaninfo.setnPlancurrenttime(new Date());
        ywkPlaninfo.setdCaculatestarttm(startTime);//方案计算开始时间
        ywkPlaninfo.setdCaculateendtm(endTime);//方案计算结束时间
        ywkPlaninfo.setnPlanstatus(0l);//方案状态
        ywkPlaninfo.setnOutputtm(step);//设置间隔分钟
        ywkPlaninfo.setdRainstarttime(startTime);
        ywkPlaninfo.setdRainendtime(endTime);
        ywkPlaninfo.setdOpensourcestarttime(startTime);
        ywkPlaninfo.setdOpensourceendtime(endTime);
        ywkPlaninfo.setnCreatetime(DateUtil.getCurrentTime());
        YwkPlaninfo saveDbo = ywkPlaninfoDao.save(ywkPlaninfo);

        YwkCsnlRoughness ywkCsnlRoughness = new YwkCsnlRoughness();
        ywkCsnlRoughness.setnPlanid(vo.getnPlanid());
        ywkCsnlRoughness.setRoughness(vo.getRoughness());
        ywkCsnlRoughnessDao.save(ywkCsnlRoughness);

        //保存数据到缓存
        CacheUtil.saveOrUpdate("planInfo", ywkPlaninfo.getnPlanid(), ywkPlaninfo);
        return saveDbo.getnPlanid();
    }

    /**
     * 获取方案信息
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

    /**
     * 根据方案获取雨量信息
     *
     * @param planInfo
     * @return
     */
    @Override
    public List<Map<String, Object>> getRainfallsInfo(YwkPlaninfo planInfo) throws ParseException {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();

        String startTimeStr = format1.format(startTime);
        String endTimeStr = format1.format(endTime);
        List<String> timeResults = new ArrayList();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //Long step = planInfo.getnOutputtm() / 60;//步长
        Long step = planInfo.getnOutputtm();//分钟

        while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
            String hourStart = format.format(startTime);
            timeResults.add(hourStart);
            startTime = DateUtil.getNextMinute(startTime, step.intValue());//h获取分钟
        }

        Long count = ywkPlaninRainfallDao.countByPlanIdWithTime(planInfo.getnPlanid(),startTime,endTime);
        List<Map<String, Object>> stPptnRWithSTCD = new ArrayList<>();
        if (count != 0){//原来是小时  实时数据是小时  都先按照整点来
            stPptnRWithSTCD = ywkPlaninRainfallDao.findStPptnRWithSTCD(startTimeStr,endTimeStr,planInfo.getnPlanid());
        }
        else {
            //stPptnRWithSTCD = stPptnRDao.findStPptnRWithSTCD(startTimeStr, endTimeStr);
            Date startttt = format.parse(startTimeStr);
            startTimeStr = format.format(DateUtil.getNextMinute(startttt, -step.intValue()));
            if (step.intValue() == 30){ //todo 往前算30分   [ )
                stPptnRWithSTCD = stPptnRDao.findRainGroupStcdAndTimeWithMinutiue(startTimeStr, endTimeStr);
            }else {
                //往前算1小时。往前算2小时，3小时
                List<Map<String,Object>> feng = stPptnRDao.findRainGroupStcdAndTimeWithHour(startTimeStr, endTimeStr);
                if (step > 60 && !CollectionUtils.isEmpty(feng)){
                    for (String time : timeResults){
                        Date timeDate = format.parse(time);
                        List<String> timeList = new ArrayList<>();
                        for (int i = 0; i <step.intValue()/60;i++){
                            timeList.add(format.format(timeDate));
                            timeDate = DateUtil.getNextMinute(timeDate,-60);
                        }
                        //todo  stcd 跟drp
                        Map<String, Double> collect = feng.stream().filter(t -> timeList.contains(t.get("TM"))).
                                collect(Collectors.groupingBy(t -> (String)t.get("STCD"), Collectors.summingDouble(t -> ((BigDecimal) ((Map) t).get("DRP")).doubleValue())));
                        Set<Map.Entry<String, Double>> entries = collect.entrySet();
                        Iterator<Map.Entry<String, Double>> iterator = entries.iterator();
                        while (iterator.hasNext()){
                            Map dataMap = new HashMap();
                            Map.Entry<String, Double> next = iterator.next();
                            String stcd = next.getKey();
                            Double drp = next.getValue();
                            dataMap.put("STCD",stcd);
                            dataMap.put("TM",time);
                            dataMap.put("DRP",new BigDecimal(drp));
                            stPptnRWithSTCD.add(dataMap);
                        }
                    }
                }else {
                    stPptnRWithSTCD = feng;
                }

            }
        }
        Set<String> rainStcds = stPptnRWithSTCD.stream().map(t -> {
            String stcd = (String) t.get("STCD");
            return stcd;
        }).collect(Collectors.toSet());
        List<StStbprpB> usePPStation = stStbprpBDao.findUsePPStation();

        Map<String, StStbprpB> allRainStationMap = usePPStation.stream().collect(Collectors.toMap(StStbprpB::getStcd, Function.identity()));

        List<StStbprpB> nullList ;
        if (CollectionUtils.isEmpty(rainStcds)){
            nullList = usePPStation;
        }else {
            nullList = usePPStation.stream().filter(t->!rainStcds.contains(t.getStcd())).collect(Collectors.toList());
        }

        Map<String, Map<String, BigDecimal>> rainMap = stPptnRWithSTCD.stream().collect(Collectors.groupingBy(t -> (String) ((Map)t).get("STCD"),
                Collectors.toMap(tm->(String)((Map)tm).get("TM"),tm->(BigDecimal)((Map)tm).get("DRP"))
        ));
        List<Map<String,Object>> results = new ArrayList<>();

        Set<Map.Entry<String, Map<String, BigDecimal>>> entries = rainMap.entrySet();
        for (Map.Entry<String, Map<String, BigDecimal>> entry : entries) {
            String stcd = entry.getKey();
            Map<String, BigDecimal> tmMap = entry.getValue();
            StStbprpB stStbprpB = allRainStationMap.get(stcd);

            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("STCD",stcd);
            resultMap.put("STNM",stStbprpB.getStnm());
            resultMap.put("LGTD",stStbprpB.getLgtd());
            resultMap.put("LTTD",stStbprpB.getLttd());


            List<Map<String,Object>> llDatas = new ArrayList<>();
            for (String time : timeResults){

                BigDecimal drpValue = tmMap.get(time);
                if (drpValue == null){
                    drpValue = new BigDecimal(0d);
                }
                drpValue = drpValue.setScale(2,BigDecimal.ROUND_HALF_UP);
                Map<String, Object> rainDataMap = new HashMap<>();
                rainDataMap.put("TM",time);
                rainDataMap.put("DRP",drpValue);
                rainDataMap.put("STCD",stcd);
                rainDataMap.put("LGTD",stStbprpB.getLgtd());
                rainDataMap.put("LTTD",stStbprpB.getLttd());
                llDatas.add(rainDataMap);
            }
            resultMap.put("LIST",llDatas);
            results.add(resultMap);
        } //todo 都是null的站点不要显示了。。但是下载模板的里面得有
        for (StStbprpB nullStStbprpb : nullList){
            Map<String,Object> nullMap = new HashMap<>();
            nullMap.put("STCD",nullStStbprpb.getStcd());
            nullMap.put("STNM",nullStStbprpb.getStnm());
            nullMap.put("LGTD",nullStStbprpb.getLgtd());
            nullMap.put("LTTD",nullStStbprpb.getLttd());
            nullMap.put("LIST",new ArrayList<>());
            results.add(nullMap);

        }
        results.sort(Comparator.comparing(t -> {
            List list = (List) ((Map) t).get("LIST");
            return list.size();
        }).reversed());
        /*List<Map<String, Object>> list1 = results.stream().sorted(Comparator.comparing(t -> {
            List list = (List) ((Map) t).get("LIST");
            return list.size();
        }).reversed()).collect(Collectors.toList());*/
        //TODO 修改雨量值并不修改基础表的数据，只修改缓存的的数据
        CacheUtil.saveOrUpdate("rainfall", planInfo.getnPlanid()+"new", results);
        return results;
    }


    @Autowired
    ModelCallHandleDataService modelCallHandleDataService;

    /**
     * 从缓存里获取获取雨量信息并存库
     * @param planInfo results
     */
    @Transactional
    @Override
    public void saveRainfallsFromCacheToDb(YwkPlaninfo planInfo, List<Map<String, Object>> results) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<String> timeResults = new ArrayList();
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
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
    public Workbook exportRainfallTemplate(YwkPlaninfo planinfo)throws ParseException {
        //封装时间列
        Date startTime = planinfo.getdCaculatestarttm();
        Date endTime = planinfo.getdCaculateendtm();
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
    @Async
    public void callMode(String planId) {
        //调用模型计算
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOne(planId);
        if (planInfo == null) {
            System.out.println("方案不存在！");
            return;
        }
        try{
            Long aLong = ywkPlaninRainfallDao.countByPlanId(planInfo.getnPlanid());
            if (aLong == 0L) {
                System.out.println("方案雨量表没有保存数据");
                throw new RuntimeException("方案雨量表没有保存数据");
            }

            List<Map<String, Object>> results = getRainfallsInfo(planInfo);
            if (CollectionUtils.isEmpty(results)) {
                System.out.println("雨量信息为空，无法计算");
                throw new RuntimeException("雨量信息为空，无法计算");
            }

            String csnl_path = PropertiesUtil.read("/filePath.properties").getProperty("CSNL_MODEL");//模型文件夹

            String csnl_model_template = csnl_path +
                    File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");

            String csnl_model_template_input = csnl_path +
                    File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE")
                    + File.separator + "INPUT" + File.separator + planId; //输入文件地址

            String csnl_model_template_output = csnl_path +
                    File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                    + File.separator + planId; //输出文件的地址

            String csnl_model_template_run = csnl_path +
                    File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_RUN"); //模型地址

            String csnl_model_template_run_plan = csnl_model_template_run + File.separator + planId; //方案对应模型地址

            File inputCsnlPath = new File(csnl_model_template_input);
            File outCsnlPath = new File(csnl_model_template_output);
            File runPath = new File(csnl_model_template_run_plan);

            inputCsnlPath.mkdirs();
            outCsnlPath.mkdirs();
            runPath.mkdirs();

            //修改CT.csv中的计算结束时间
            int result1 = updateCtCsv(csnl_model_template, csnl_model_template_input, planInfo);
            if (result1 == 0) {
                System.out.println("城市内涝模型计算:修改CT.csv文件成功");
                return;
            }

            //修改Rain.csv为方案对应雨量数据
            int result2 = updateRainCsv(csnl_model_template_input, planInfo);
            if (result2 == 0) {
                System.out.println("城市内涝模型计算:修改Rain.csv文件成功");
                return;
            }

            //WG.csv
            int result4 = writeDataToInputWGCsv(csnl_model_template, csnl_model_template_input, planInfo);
            if (result4 == 0) {
                System.out.println("城市内涝模型计算:糙率WG.csv输入文件写入失败");
                return;
            }

            int result5 = copyOtherCsv(csnl_model_template, csnl_model_template_input);
            if (result5 == 0) {
                System.out.println("城市内涝模型计算:复制其他.csv输入文件写入失败");
                return;
            }

            int result6 = copyExeFile(csnl_model_template_run, csnl_model_template_run_plan);
            if (result6 == 0) {
                System.out.println("城市内涝模型计算:复制执行文件与config文件写入失败。。。");
                return;
            }

            int result7 = writeDataToConfig(csnl_model_template_run_plan, csnl_model_template_input, csnl_model_template_output);
            if (result7 == 0) {
                System.out.println("城市内涝模型计算:config文件写入失败。。。");
                return;
            }

            //调用模型计算
            System.out.println("城市内涝模型计算:开始城市内涝模型计算。。。");
            System.out.println("城市内涝模型计算路径为。。。"+csnl_model_template_run_plan + File.separator + "startUp.bat");
            runModelExe(csnl_model_template_run_plan + File.separator + "startUp.bat");
            System.out.println("城市内涝模型计算:城市内涝模型计算结束。。。");

            //判断是否执行成功，是否有error文件
            String errorStr = csnl_model_template_output + File.separator + "error.txt";
            File errorFile = new File(errorStr);
            if (errorFile.exists()) {//存在表示执行失败
                planInfo.setnPlanstatus(-1L);
            } else {
                planInfo.setnPlanstatus(2L);
            }
            ywkPlaninfoDao.save(planInfo);
            //保存数据到缓存
            CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);

            //解析模型结果调用GIS服务-生成图片 -存在表示执行失败
            if (!errorFile.exists()) {
                //如果模型运行成功-解析过程文件生成图片
                planProcessDataService.readCsnlDepthCsvFile(csnl_model_template_output, "process",  planId);
                //解析最大水深文件
                planProcessDataService.readCsnlDepthCsvFile(csnl_model_template_output, "maxDepth",  planId);
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("模型执行失败了。。。。。。联系管理员" + e.getMessage());
            planInfo.setnPlanstatus(-1L);
            ywkPlaninfoDao.save(planInfo);
            CacheUtil.saveOrUpdate("planInfo", planInfo.getnPlanid(), planInfo);
        }
    }

    private int updateRainCsv(String csnl_model_template_input, YwkPlaninfo planInfo) {
        String CtInputCsv = csnl_model_template_input + File.separator + "RAIN.csv";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH");
        try {
            List<Map<String, Object>> results = getRainfallsInfo(planInfo);
            BufferedWriter bw = new BufferedWriter(new FileWriter(CtInputCsv, false)); // 附加
            // 添加新的数据行
            bw.write("bianhao" + ",LGTD,LTTD"); //编写表头
            Date startTime = planInfo.getdCaculatestarttm();
            System.out.println(startTime);
            Date endTime = planInfo.getdCaculateendtm();
            System.out.println(endTime);
            Long step = planInfo.getnOutputtm();//步长
            DecimalFormat format = new DecimalFormat("0.00");
//            Double hour = Double.parseDouble(format.format(step* 1.0 / 60 ));

            for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                bw.write("," + sdf.format(time));
            }
            bw.newLine();
            int i = 1;
            for (Map<String, Object> map : results) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("LIST");
                Object bianhao = i;
                String lgtd = map.get("LGTD") == null ? "" : map.get("LGTD") + "";
                Object lttd = map.get("LTTD") == null ? "" : map.get("LTTD") + "";
                bw.write(bianhao+","+lgtd+","+lttd);
                for (Map<String, Object> m : list) {
                    String value = m.get("DRP") == null ? "" : m.get("DRP") + "";
                    bw.write("," + value);
                }
                i++;
                bw.newLine();

            }
            bw.close();
            System.out.println("城市内涝模型Rain.csv输入文件写入成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("城市内涝模型:水文模型Rain.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }

    }

    @Override
    public Object getModelRunStatus(YwkPlaninfo planInfo) {
        JSONObject jsonObject = new JSONObject();
        //运行进度
        jsonObject.put("process", 0.0);
        //运行状态 1运行结束 0运行中
        jsonObject.put("runStatus", 0);
        //运行时间
        jsonObject.put("time", 0);
        //描述
        jsonObject.put("describ", "模型运行准备中！");

        String CSNL_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("CSNL_MODEL");
        String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");
        String CSNL_MODEL_TEMPLATE_OUTPUT =  CSNL_MODEL_PATH + File.separator + out
                + File.separator + planInfo.getnPlanid();//输出的地址

        //判断是否有error文件
        String errorPath = CSNL_MODEL_TEMPLATE_OUTPUT + File.separator + "error.txt";
        String processPath = CSNL_MODEL_TEMPLATE_OUTPUT + File.separator + "jindu.txt";
        String picPath = CSNL_MODEL_TEMPLATE_OUTPUT + File.separator + "pic.txt";
        File picFile = new File(picPath);
        File jinduFile = new File(processPath);
        File errorFile = new File(errorPath);
        //存在表示执行失败
        if (errorFile.exists()) {
            return jsonObject;
        }

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

    private int updateCtCsv(String csnl_model_template, String csnl_model_template_input, YwkPlaninfo planInfo) {
        String CtReadCsv = csnl_model_template + File.separator + "CT.csv";
        String CtOutCsv = csnl_model_template_input + File.separator + "CT.csv";

        try {
            FileReader fileReader = new FileReader(CtReadCsv);
            if(fileReader!=null){
                BufferedReader reader = new BufferedReader(fileReader);
                String line = null;
                List<List<String>> datas = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                    datas.add(Arrays.asList(item));
                }
               Long diff = planInfo.getdCaculateendtm().getTime() - planInfo.getdCaculatestarttm().getTime();

                long nh = 1000*60*60;
                long result = diff/nh;
                datas.get(1).set(1, String.valueOf(result));

                FileOutputStream fos = new FileOutputStream(CtOutCsv,false); //false时覆盖
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
            System.err.println("城市内涝模型：修改CT.csv文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("城市内涝模型：修改CT.csv文件失败" + e.getMessage());
            return 0;
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
            while ((line = brError.readLine()) != null) {
                // 输出exe输出的信息以及错误信息
                System.out.println(line);
            }
            if (brError.readLine() != null){
                System.out.println("城市内涝调度模型调用失败！");
            }else {
                System.out.println("城市内涝调度模型调用成功！");
            }
        } catch (Exception e) {
            System.out.println("城市内涝调度模型调用失败！");
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

    private int writeDataToConfig(String csnl_model_template_run_plan, String csnl_model_template_input, String csnl_model_template_output) {

        List<String> finals = new ArrayList<>();
        //配置文件
        String configUrl = csnl_model_template_run_plan + File.separator + "config.txt";

        //输入文件
        String BDUrl = "BD&&" + csnl_model_template_input + File.separator +  "BD.csv";
        String RainUrl = "RAIN&&" + csnl_model_template_input + File.separator +  "RAIN.csv";
        String WGUrl = "WG&&" + csnl_model_template_input + File.separator +  "WG.csv";
        String CTUrl = "CT&&" + csnl_model_template_input + File.separator +  "CT.csv";
        String INUrl = "IN&&" + csnl_model_template_input + File.separator +  "IN.csv";
        String TDUrl = "TD&&" + csnl_model_template_input + File.separator +  "TD.csv";
        String JDUrl = "JD&&" + csnl_model_template_input + File.separator +  "JD.csv";

        //输出文件
        String resultUrl = "result&&" + csnl_model_template_output + File.separator + "result.csv";
        String processUrl = "process&&" + csnl_model_template_output + File.separator + "process.csv";
        String overflowUrl = "overflow&&" + csnl_model_template_output + File.separator + "overflow.csv";
        String kuikouUrl = "kuikou&&" + csnl_model_template_output + File.separator + "kuikou.csv";
        String jinduUrl = "jindu&&" + csnl_model_template_output + File.separator + "jindu.txt";
        String errorUrl = "error&&" + csnl_model_template_output + File.separator + "error.txt";


        finals.add(BDUrl);
        finals.add(RainUrl);
        finals.add(WGUrl);
        finals.add(CTUrl);
        finals.add(INUrl);
        finals.add(TDUrl);
        finals.add(JDUrl);
        finals.add(resultUrl);
        finals.add(processUrl);
        finals.add(overflowUrl);
        finals.add(kuikouUrl);
        finals.add(jinduUrl);
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
            System.out.println("写入城市内涝模型config成功");
            return 1;
        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            System.out.println("写入城市内涝模型config失败");
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            System.out.println("写入城市内涝模型config失败");
            e.printStackTrace();
            return 0;
        }

    }

    private int copyExeFile(String csnl_model_template_run, String csnl_model_template_run_plan) {

        String exeUrl = csnl_model_template_run + File.separator + "main.exe";
        String exeInputUrl = csnl_model_template_run_plan + File.separator + "main.exe";
        String batUrl = csnl_model_template_run + File.separator + "startUp.bat";
        String batInputUrl = csnl_model_template_run_plan + File.separator + "startUp.bat";
        try {
            FileUtil.copyFile(exeUrl, exeInputUrl, true);
            FileUtil.copyFile(batUrl, batInputUrl, true);
            System.err.println("城市内涝模型计算：copy执行文件exe,bat文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("城市内涝模型计算：copy执行文件exe,bat文件错误" + e.getMessage());
            return 0;
        }
    }

    private int copyOtherCsv(String hsfx_model_template, String hsfx_model_template_input) {
        String InCsvUrl = hsfx_model_template + File.separator + "IN.csv";
        String InCsvInputUrl = hsfx_model_template_input + File.separator + "IN.csv";

        String TdCsvUrl = hsfx_model_template + File.separator + "TD.csv";
        String TdCsvInputUrl = hsfx_model_template_input + File.separator + "TD.csv";

        String JdCsvUrl = hsfx_model_template + File.separator + "JD.csv";
        String JdCsvInputUrl = hsfx_model_template_input + File.separator + "JD.csv";

        String BdCsvUrl = hsfx_model_template + File.separator + "BD.csv";
        String BdCsvInputUrl = hsfx_model_template_input + File.separator + "BD.csv";

        try {
            FileUtil.copyFile(InCsvUrl, InCsvInputUrl, true);
            FileUtil.copyFile(TdCsvUrl, TdCsvInputUrl, true);
            FileUtil.copyFile(JdCsvUrl, JdCsvInputUrl, true);
            FileUtil.copyFile(BdCsvUrl, BdCsvInputUrl, true);
            System.err.println("城市内涝模型计算：copy输入文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("城市内涝模型计算：copy输入文件错误" + e.getMessage());
            return 0;
        }

    }

    private int writeDataToInputWGCsv(String csnl_model_template, String csnl_model_template_input, YwkPlaninfo planInfo) {

        String WGInputUrl = csnl_model_template_input + File.separator + File.separator + "WG.csv";
        String WGInputReadUrl = csnl_model_template + File.separator + File.separator + "WG.csv";

        Optional<YwkCsnlRoughness> byId = ywkCsnlRoughnessDao.findById(planInfo.getnPlanid());
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
            System.err.println("城市内涝模型计算：WG.csv输入文件读取错误:read errors :" + e);
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
                readDatas.get(i).set(4, byId.get().getRoughness() + "");
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
            System.out.println("城市内涝模型计算：WG.csv输入文件写入成功");
            return 1;
        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            System.out.println("城市内涝模型计算：WG.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            System.out.println("城市内涝模型计算：WG.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }

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
    public void previewFloodPic(HttpServletRequest request, HttpServletResponse response, String planId, String picId) {
        //图片路径
        String outputAbsolutePath = GisPathConfigurationUtil.getOutputPictureAbsolutePath() + "/MODEL_NLFX/" + planId;
        //图片路径
        String processOutputAbsolutePath = outputAbsolutePath + "/floodpic/";
        String filePath = null;
        filePath = processOutputAbsolutePath + picId + ".png";
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

    /**
     * 解析输出文件Grid process csv
     *
     * @param hsfx_model_template_output
     * @return
     */
    private List<YwkPlanOutputGridProcess> analysisOfGridProcessCSV(String hsfx_model_template_output, YwkPlaninfo planinfo) {
        List<YwkPlanOutputGridProcess> results = new ArrayList<>();
        Date startTime = planinfo.getdCaculatestarttm();//计算开始时间
        Long step = planinfo.getnOutputtm();
        String grid_process_csv = hsfx_model_template_output + File.separator + "erwei" + File.separator + "process.csv";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(grid_process_csv));//换成你的文件名
            reader.readLine();//第一行信息，为标题信息，不用，如果需要，注释掉
            String line = null;
            List<List<String>> datas = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                datas.add(Arrays.asList(item));
            }
            for (List<String> data : datas) {
                List<String> newList = data.subList(2, data.size());
                for (int i = 0; i < newList.size(); i++) {
                    YwkPlanOutputGridProcess ywkPlanOutputGridProcess = new YwkPlanOutputGridProcess();
                    ywkPlanOutputGridProcess.getPk().setnPlanid(planinfo.getnPlanid());
                    ywkPlanOutputGridProcess.getPk().setGridId(Long.parseLong(data.get(0)));
                    String str = newList.get(i);
                    Long stepNew = step * i;
                    Date newDate = DateUtil.getNextMinute(startTime, stepNew.intValue());
                    ywkPlanOutputGridProcess.setAbsoluteTime(new Timestamp(newDate.getTime()));
                    ywkPlanOutputGridProcess.getPk().setRelativeTime(stepNew);
                    ywkPlanOutputGridProcess.setGridDepth(Double.parseDouble(str));
                    results.add(ywkPlanOutputGridProcess);
                }
            }
        } catch (Exception e) {
            System.out.println("解析输出文件Grid process csv失败：" + e.getMessage());
            e.printStackTrace();
        }
        return results;

    }

    /**
     * 读取csv文件内容
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    private List<String> readCSV(String filePath) throws IOException {
        List dataList = new ArrayList();
        BufferedReader br = null;
        InputStreamReader isr = null;
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                isr = new InputStreamReader(new FileInputStream(file), "utf-8");
                br = new BufferedReader(isr);
                String lineTxt = null;
                while ((lineTxt = br.readLine()) != null) {
                    dataList.add(lineTxt);
                }
                return dataList;
            } else {
                System.out.println("文件不存在");
            }
        } catch (Exception e) {
            System.out.println("文件错误");
        } finally {
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
        }
        return dataList;
    }
}
