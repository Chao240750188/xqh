package com.essence.business.xqh.service.fhybdd;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.essence.business.xqh.api.fhybdd.dto.*;
import com.essence.business.xqh.api.fhybdd.service.ModelCallHandleDataService;
import com.essence.business.xqh.api.fhybdd.service.ModelPlanInfoManageService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.*;
import com.essence.business.xqh.dao.dao.fhybdd.*;
import com.essence.business.xqh.dao.entity.fhybdd.*;
import com.essence.framework.jpa.Criterion;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import com.essence.framework.util.StrUtil;
import javafx.beans.binding.ObjectExpression;
import jdk.internal.dynalink.linker.LinkerServices;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ModelPlanInfoManageServiceImpl implements ModelPlanInfoManageService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    YwkPlaninfoDao ywkPlaninfoDao;

    @Autowired
    YwkPlanCalibrationDwxDao ywkPlanCalibrationDwxDao;

    @Autowired
    YwkPlanCalibrationZoneDao ywkPlanCalibrationZoneDao;

    @Autowired
    YwkPlanCalibrationZoneXajDao ywkPlanCalibrationZoneXajDao;

    @Autowired
    YwkPlanCalibrationZoneXggxDao ywkPlanCalibrationZoneXggxDao;

    @Autowired
    YwkPlanOutputQDao ywkPlanOutputQDao;

    @Autowired
    YwkPlanTriggerRcsDao ywkPlanTriggerRcsDao;

    @Autowired
    YwkPlanTriggerRcsFlowDao ywkPlanTriggerRcsFlowDao;

    @Override
    public Paginator getPlanList(PaginatorParam paginatorParam) {
        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("XT_SWYB");

        List<Criterion> orders = paginatorParam.getOrders();
        if (orders == null) {
            orders = new ArrayList<>();
        }

        Criterion criterion = new Criterion();
        criterion.setFieldName("nCreatetime");
        criterion.setOperator(Criterion.DESC);
        orders.add(criterion);
        paginatorParam.setOrders(orders);

        List<Criterion> conditions = paginatorParam.getConditions();
        if (conditions == null) {
            conditions = new ArrayList<>();
            paginatorParam.setConditions(conditions);
        }
        Criterion criterion1 = new Criterion();
        criterion1.setFieldName("planSystem");
        criterion1.setOperator(Criterion.EQ);
        criterion1.setValue(planSystem);

        Criterion criterion2 = new Criterion();//方案成功
        criterion2.setFieldName("nPlanstatus");
        criterion2.setOperator(Criterion.EQ);
        criterion2.setValue(2L);

        conditions.add(criterion1);
        conditions.add(criterion2);
        Paginator<YwkPlaninfo> all = ywkPlaninfoDao.findAll(paginatorParam);
        List<YwkPlaninfo> items = all.getItems();
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
        for (YwkPlaninfo planinfo : items) {
            Long startTime = planinfo.getdCaculatestarttm().getTime();
            Long endTime = planinfo.getdCaculateendtm().getTime();
            Long total = endTime - startTime;///(60*60*1000);
            Long sum = total.longValue() / (60 * 60 * 1000);
            planinfo.setLeadTime(sum);
            planinfo.setIsWarnIng(1);
        }
        return all;
    }

    @Autowired
    YwkPlaninRainfallDao ywkPlaninRainfallDao;//方案雨量

    @Transactional
    @Override
    public void deleteByPlanId(YwkPlaninfo planInfo) {

       /* String catchMentAreaModelId = planInfo.getnModelid(); //集水区模型id   // 1是SCS  2是单位线
        String reachId = planInfo.getnSWModelid(); //河段模型id*/

        Integer dwxCount = ywkPlanCalibrationDwxDao.countByNPlanId(planInfo.getnPlanid());

        Integer zoneCount = ywkPlanCalibrationZoneDao.countByNPlanId(planInfo.getnPlanid());

        Integer zoneXajCount = ywkPlanCalibrationZoneXajDao.countByNPlanId(planInfo.getnPlanid());

        Integer zoneXGGXCount = ywkPlanCalibrationZoneXggxDao.countByNPlanId(planInfo.getnPlanid());

        if (dwxCount != 0L) {
            ywkPlanCalibrationDwxDao.deleteByNPlanid(planInfo.getnPlanid());
        }
        if (zoneCount != 0L) {
            ywkPlanCalibrationZoneDao.deleteByNPlanid(planInfo.getnPlanid());
        }

        if (zoneXajCount != 0L) {
            ywkPlanCalibrationZoneXajDao.deleteByNPlanid(planInfo.getnPlanid());
        }

        if (zoneXGGXCount != 0L) {
            ywkPlanCalibrationZoneXggxDao.deleteByNPlanid(planInfo.getnPlanid());
        }
        //预报断面也要干掉
        List<YwkPlanTriggerRcs> triggerRcs = ywkPlanTriggerRcsDao.findByNPlanid(planInfo.getnPlanid());
        if (!CollectionUtils.isEmpty(triggerRcs)) {
            ywkPlanTriggerRcsDao.deleteByNPlanid(planInfo.getnPlanid());
            List<String> triggerIds = triggerRcs.stream().map(YwkPlanTriggerRcs::getId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(triggerIds)) {
                ywkPlanTriggerRcsFlowDao.deleteByTriggerRcsIds(triggerIds);
            }
        }
        ywkPlaninfoDao.delete(planInfo.getnPlanid());
        ywkPlaninRainfallDao.deleteByNPlanid(planInfo.getnPlanid());
        ywkPlanOutputQDao.deleteByNPlanid(planInfo.getnPlanid());

        //删除对应模型文件
        try {
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
            //输入的地址
            String SHUIWEN_MODEL_TEMPLATE_INPUT = SHUIWEN_MODEL_TEMPLATE
                    + File.separator + "INPUT" + File.separator + planInfo.getnPlanid();
            //输出的地址
            String SHUIWEN_MODEL_TEMPLATE_OUTPUT = SWYB_SHUIWEN_MODEL_PATH + File.separator + out
                    + File.separator + planInfo.getnPlanid();
            //模型运行的config
            String SHUIWEN_MODEL_RUN = SWYB_SHUIWEN_MODEL_PATH + File.separator + run;
            String SHUIWEN_MODEL_RUN_PLAN = SHUIWEN_MODEL_RUN + File.separator + planInfo.getnPlanid();

            FileUtil.deleteFile(new File(PCP_HANDLE_MODEL_TEMPLATE_INPUT));
            FileUtil.deleteFile(new File(PCP_HANDLE_MODEL_TEMPLATE_OUTPUT));
            FileUtil.deleteFile(new File(PCP_HANDLE_MODEL_RUN_PLAN));
            FileUtil.deleteFile(new File(SHUIWEN_MODEL_TEMPLATE_INPUT));
            FileUtil.deleteFile(new File(SHUIWEN_MODEL_TEMPLATE_OUTPUT));
            FileUtil.deleteFile(new File(SHUIWEN_MODEL_RUN_PLAN));
        } catch (Exception e) {

        }
    }

    @Autowired
    WrpRcsBsinDao wrpRcsBsinDao;

    @Override
    public List<Map<String, Object>> getTriggerList(YwkPlaninfo planinfo) {

        List<Map<String, Object>> results = new ArrayList<>();
        List<YwkPlanTriggerRcs> triggerRcs = ywkPlanTriggerRcsDao.findByNPlanid(planinfo.getnPlanid());
        if (CollectionUtils.isEmpty(triggerRcs)) {
            return results;
        }
        Map<String, String> rcsMap = wrpRcsBsinDao.findAll().stream().collect(Collectors.toMap(WrpRcsBsin::getRvcrcrsccd, WrpRcsBsin::getRvcrcrscnm));
        List<String> triggerIds = triggerRcs.stream().map(YwkPlanTriggerRcs::getId).collect(Collectors.toList());
        List<YwkPlanTriggerRcsFlow> flowList = ywkPlanTriggerRcsFlowDao.findByTriggerRcsIdsOrderByTime(triggerIds);

        Map<String, List<YwkPlanTriggerRcsFlow>> flowMap = new HashMap<>();
        for (YwkPlanTriggerRcsFlow flow : flowList) {
            String triggerRcsId = flow.getTriggerRcsId();
            List<YwkPlanTriggerRcsFlow> triggerRcsFlows = flowMap.get(triggerRcsId);
            if (CollectionUtils.isEmpty(triggerRcsFlows)) {
                triggerRcsFlows = new ArrayList<>();
            }
            triggerRcsFlows.add(flow);
            flowMap.put(triggerRcsId, triggerRcsFlows);
        }
        for (YwkPlanTriggerRcs triggerRcs1 : triggerRcs) {
            Map<String, Object> resultMap = new HashMap<>();
            String id = triggerRcs1.getId();
            String rcsId = triggerRcs1.getRcsId();
            List<YwkPlanTriggerRcsFlow> triggerRcsFlows = flowMap.get(id);
            String name = rcsMap.get(rcsId);
            resultMap.put("name", name);
            resultMap.put("flow", triggerRcsFlows);
            results.add(resultMap);
        }
        return results;
    }


    @Override
    public void publishPlan(List<String> planIds, Integer tag) {

        List<YwkPlaninfo> planInfos = ywkPlaninfoDao.findAllById(planIds);
        if (!CollectionUtils.isEmpty(planInfos)) {
            for (YwkPlaninfo planinfo : planInfos) {
                if (tag == 1) {
                    planinfo.setnPublish(1L);
                    planinfo.setnPublishTime(new Date());
                } else {
                    planinfo.setnPublish(0L);
                    planinfo.setnPublishTime(null);
                }
                ywkPlaninfoDao.save(planinfo);
                CacheUtil.saveOrUpdate("planInfo", planinfo.getnPlanid(), planinfo);
            }
        }
    }


    @Autowired
    WrpWarningWaterLevelDao wrpWarningWaterLevelDao;

    @Override
    public List<Map<String, Object>> getWarnIngWaterLevels(Map m) {

        String rvcd = (String) m.get("riverId");
        String rcsName = (String) m.get("rcsName");
        String baseSql = "SELECT a.C_ID,a.RCS_ID,a.WARNING_WATER_LEVEL,b.RVCRCRSCNM,c.RVCD,c.RVNM FROM WRP_WARNING_WATER_LEVEL a INNER JOIN WRP_RCS_BSIN b on a.RCS_ID = b.RVCRCRSCCD INNER JOIN WRP_RVR_BSIN c on b.RVCD = c.RVCD ";

        if (rvcd != null && !"".equals(rvcd)) {
            baseSql = baseSql + " WHERE b.RVCD = '" + rvcd + "' ";
            if (rcsName != null) {
                baseSql = baseSql + "AND b.RVCRCRSCNM LIKE '%" + rcsName + "%'";
            }
        } else {
            if (rcsName != null && !"".equals(rcsName)) {
                baseSql = baseSql + "WHERE b.RVCRCRSCNM LIKE '%" + rcsName + "%'";
            }
        }
        Query query = entityManager.createNativeQuery(baseSql);
        query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map<String, Object>> warnIngWaterLevels = query.getResultList();

        //a.C_ID,a.RCS_ID,a.WARNING_WATER_LEVEL,b.RVCRCRSCNM,c.RVCD,c.RVNM
        //List<Map<String, Object>> warnIngWaterLevels = wrpWarningWaterLevelDao.getWarnIngWaterLevels();
        List<Map<String, Object>> results = new ArrayList<>();
        for (Map<String, Object> map : warnIngWaterLevels) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("cId", map.get("C_ID"));
            resultMap.put("rcsId", map.get("RCS_ID"));
            resultMap.put("warningWaterLevel", map.get("WARNING_WATER_LEVEL"));
            resultMap.put("rcsName", map.get("RVCRCRSCNM"));
            resultMap.put("riverId", map.get("RVCD"));
            resultMap.put("riverName", map.get("RVNM"));
            resultMap.put("flag", 0);
            results.add(resultMap);
        }
        return results;
    }


    @Override
    public void upDateWarnIngWaterLevels(List<Map<String, Object>> datas) {
        if (CollectionUtils.isEmpty(datas)) {
            return;
        }
        for (Map<String, Object> map : datas) {
            WrpWarningWaterLevel model = new WrpWarningWaterLevel();
            model.setcId(map.get("cId") + "");
            model.setRcsId(map.get("rcsId") + "");
            model.setWarningWaterLevel(Double.parseDouble(map.get("warningWaterLevel") + ""));
            wrpWarningWaterLevelDao.save(model);
        }
    }

    @Override
    public Object getAllRcsByRiver(String rvcd) {
        List<WrpRcsBsinDto> list = new ArrayList<>();
        List<WrpRcsBsin> rcsList = null;
        if("RVR_011".equals(rvcd)){ //如果是小清河，则查询所有断面
            rcsList = wrpRcsBsinDao.findAll();
        }else{
            rcsList = wrpRcsBsinDao.findListByRiverId(rvcd);
        }

        for (WrpRcsBsin wrpRcsBsin:rcsList) {
            WrpRcsBsinDto wrpRcsBsinDto = new WrpRcsBsinDto();
            BeanUtils.copyProperties(wrpRcsBsin,wrpRcsBsinDto);
            list.add(wrpRcsBsinDto);
        }
        return list;
    }

    @Override
    public Object getWaterLevelFlow(ModelPlanInfoManageDto modelPlanInfoManageDto) {
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOne(modelPlanInfoManageDto.getPlanId());
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        Long step = planInfo.getnOutputtm();//步长
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //目前并无断面对应的水位和流量数据，先封装成假数据
        Map<String, List<Map<String, String>>> resultMap = new HashMap<>();
        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        int index = 0;
        for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextMinute(time, step.intValue())) {
            map = new HashMap<>();
            map.put("time", sdf.format(time));
            //暂时无法获取，先全部设置为0
            map.put("y_true", 0+"");//实际水位
            map.put("s_true", 0+"");//实际流量
            result.add(map);
            index++;
        }

        for (String rvcrcrsccd : modelPlanInfoManageDto.getRvcrcrsccds()) {
            resultMap.put(rvcrcrsccd, result);
        }

        CacheUtil.saveOrUpdate("sectionWaterLevelFlow", modelPlanInfoManageDto.getPlanId(), resultMap);
        return resultMap;
    }

    @Override
    public Workbook exportTemplate(ModelPlanInfoManageDto modelPlanInfoManageDto) {
        Map<String, List<Map<String, String>>> cacheResult = (Map<String, List<Map<String, String>>>) CacheUtil.get("sectionWaterLevelFlow", modelPlanInfoManageDto.getPlanId());
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOne(modelPlanInfoManageDto.getPlanId());

        //封装时间列
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        Long step = planInfo.getnOutputtm();//步长
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
        XSSFSheet sheet = workbook.createSheet("断面水位流量信息模板");

        //填充表头
        //第一行
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue("时间");

        List<WrpRcsBsin> list = wrpRcsBsinDao.findListByIds(modelPlanInfoManageDto.getRvcrcrsccds());
        HashMap<String, String> map = (HashMap<String, String>)list.stream()
                .filter(t -> t.getRvcrcrsccd()!=null)
                .collect(Collectors.toMap(WrpRcsBsin::getRvcrcrsccd,WrpRcsBsin::getRvcrcrscnm,(k1,k2)->k2));
        int lineIndex = 0;
        for (String rvcrcrsccd : modelPlanInfoManageDto.getRvcrcrsccds()) {
            XSSFCell cell1 = row.createCell(++lineIndex);
            cell1.setCellStyle(style);
            cell1.setCellValue(map.get(rvcrcrsccd) + "实际水位(m)");
            XSSFCell cell2 = row.createCell(++lineIndex);
            cell2.setCellStyle(style);
            cell2.setCellValue(map.get(rvcrcrsccd) + "实际流量(m3/s)");
        }

        //设置自动列宽
        sheet.setColumnWidth(0, 2500);
        sheet.setColumnWidth(1, 3500);

        int beginLine = 1;

        for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextMinute(time, step.intValue())) {
            XSSFRow row1 = sheet.createRow(beginLine);

            row1.createCell(0).setCellValue(format.format(time)); //时间列

            int index = 1; //其它列
            for (int i = 0; i < modelPlanInfoManageDto.getRvcrcrsccds().size(); i++) {
                row1.createCell(index++).setCellValue(cacheResult.get(modelPlanInfoManageDto.getRvcrcrsccds().get(i)).get(i).get("y_true"));
                row1.createCell(index++).setCellValue(cacheResult.get(modelPlanInfoManageDto.getRvcrcrsccds().get(i)).get(i).get("s_true"));
            }
            beginLine++;
        }

        return workbook;
    }

    @Override
    public Map<String, List<Map<String, String>>> importWaterLevelFlow(MultipartFile mutilpartFile, String modelPlanInfoManageDto) {
        JSONObject jsonObject = JSON.parseObject(modelPlanInfoManageDto);
        String planId = (String) jsonObject.get("planId");
        List<String> rvcrcrsccdsList = (List<String>) jsonObject.get("rvcrcrsccds");

        //解析ecxel数据 不包含第一行
        List<String[]> excelList = ExcelUtil.readFiles(mutilpartFile, 1);

        Map<String, List<Map<String, String>>> resultMap = new HashMap<>();
        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        if (excelList != null && excelList.size() > 0) {
            for (int m = 0; m < rvcrcrsccdsList.size(); m++) {
                result = new ArrayList<>();
                resultMap.put(rvcrcrsccdsList.get(m), result);

                // 遍历每行数据（除了标题）
                for (int i = 0; i < excelList.size(); i++) {
                    map = new HashMap<>();
                    String[] strings = excelList.get(i);
                    if (strings != null && strings.length > 0) {
                        map.put("time", strings[0]);
                        map.put("y_true", strings[m+1]);
                        map.put("s_true", strings[m+2]);
                        // 封装每列（每个指标项数据）
                        result.add(map);
                    }
                }
            }
        }

        //修改缓存中的数据用于模型计算
        CacheUtil.saveOrUpdate("sectionWaterLevelFlow", planId, resultMap);
        return resultMap;
    }

    @Autowired
    ModelCallHandleDataService modelCallHandleDataService;

    /**
     * 精度评定模型运算
     *
     * @return
     */
    @Override
    public int modelCallJingDu(ModelPlanInfoManageDto modelPlanInfoManageDto) {
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOne(modelPlanInfoManageDto.getPlanId());
        if (planInfo == null) {
            System.out.println("计划planid没有找到记录");
            return 0;
        }

        Map<String, List<Map<String, String>>> waterLevelFlowMap = (Map<String, List<Map<String, String>>>) CacheUtil.get("sectionWaterLevelFlow", modelPlanInfoManageDto.getPlanId());

        if (CollectionUtils.isEmpty(waterLevelFlowMap)) {
            System.out.println("缓存里没有水位流量信息");
            return 0;
        }

        //创建入参、出参
        String JDPD_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("JDPD_MODEL");
        String template = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");
        String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");
        String run = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_RUN");


        Set<Map.Entry<String, List<Map<String, String>>>> entries = waterLevelFlowMap.entrySet();
        List<CompletableFuture<Integer>> futures = new ArrayList<>();

        for (Map.Entry<String, List<Map<String, String>>> entry : entries) {
            String rvcrcrsccd = entry.getKey();
            futures.add(modelCallHandleDataService.callOneMode(JDPD_MODEL_PATH, template, out, run, rvcrcrsccd, planInfo, waterLevelFlowMap));
        }

        System.out.println("futures.size"+futures.size());
        CompletableFuture[] completableFutures = new CompletableFuture[futures.size()];
        for (int j = 0;j < futures.size();j++){
            completableFutures[j] = futures.get(j);
        }
        System.out.println("等待多线程执行完毕。。。。");
        CompletableFuture.allOf(completableFutures).join();//全部执行完后 然后主线程结束
        System.out.println("多线程执行完毕，结束主线程。。。。");

        return 1;

    }

    @Override
    public Object getModelResultQ(ModelPlanInfoManageDto modelPlanInfoManageDto) {
        String JDPD_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("JDPD_MODEL");
        String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");

        Map<String, Object> resultMap = new HashMap<>();
        for (String rvcrcrsccd : modelPlanInfoManageDto.getRvcrcrsccds()) {
            String JDPD_MODEL_TEMPLATE_OUTPUT = JDPD_MODEL_PATH + File.separator + out
                    + File.separator + modelPlanInfoManageDto.getPlanId() + File.separator + rvcrcrsccd;//输出的地址
            String JINGDU_PINGDING_TXT = JDPD_MODEL_TEMPLATE_OUTPUT + File.separator + "jingdu_pingding.txt";//输出的地址
            File path = new File(JINGDU_PINGDING_TXT);
            if(!path.exists()){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("describe", "模型运行异常！");
                resultMap.put(rvcrcrsccd, jsonObject);
                return resultMap;
            }else{
                BufferedReader br = null;
                try {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
                    String lineTxt2 = br.readLine();
                    //if (lineTxt2 != null) {
                    String[] split = lineTxt2.split("\t");
                    JSONObject json = new JSONObject();
                    Map<String, String> map = new HashMap<>();
                    json.put("hsll",map);
                    map.put("pjxdwc", split[0]);
                    map.put("pjjdwc", split[1]);
                    map.put("nsxlxs", split[2]);

                    lineTxt2 = br.readLine();
                    split = lineTxt2.split("\t");
                    map = new HashMap<>();
                    json.put("hssw",map);
                    map.put("pjxdwc", split[0]);
                    map.put("pjjdwc", split[1]);
                    map.put("nsxlxs", split[2]);

                    lineTxt2 = br.readLine();
                    split = lineTxt2.split("\t");
                    map = new HashMap<>();
                    json.put("hl",map);
                    map.put("jdwc", split[0]);

                    lineTxt2 = br.readLine();
                    split = lineTxt2.split("\t");
                    map = new HashMap<>();
                    json.put("hsfz",map);
                    map.put("schf", split[0]);
                    map.put("ybhf", split[1]);
                    map.put("hfwc", split[2]);
                    map.put("hfxkwc", split[3]);
                    map.put("qualified", split[4]);

                    lineTxt2 = br.readLine();
                    split = lineTxt2.split("\t");
                    map = new HashMap<>();
                    json.put("fxsj",map);
                    map.put("scfxsj", split[0]);
                    map.put("ybfxsj", split[1]);
                    map.put("fxsjwc", split[2]);
                    map.put("fxsjxkwc", split[3]);
                    map.put("qualified", split[4]);
                    resultMap.put(rvcrcrsccd, json);
                } catch (Exception e) {
                    System.err.println("文件读取错误！" + e.getMessage());
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return resultMap;
    }

    private int writeDataToConfig(String jdpd_model_run_plan, String jdpd_model_template_input, String jdpd_model_template_output) {
        String configUrl = jdpd_model_run_plan + File.separator + "config.txt";
        String inputUrl = "input&&" + jdpd_model_template_input + File.separator + "input.csv";
        String resultUrl = "result&&" + jdpd_model_template_output + File.separator + "jingdu_pingding.txt";

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(configUrl, false)); // 附加
            // 写路径
            bw.write(resultUrl);
            bw.newLine();
            bw.write(inputUrl);
            bw.close();
            System.out.println("精度评定模型:写入config成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("精度评定模型:写入config失败");
            e.printStackTrace();
            return 0;
        }
    }

    private int copyExeFile(String jdpd_model_run, String jdpd_model_run_plan) {
        String exeUrl = jdpd_model_run + File.separator + "main.exe";
        String exeInputUrl = jdpd_model_run_plan + File.separator + "main.exe";
        String batUrl = jdpd_model_run + File.separator + "startUp.bat";
        String batInputUrl = jdpd_model_run_plan + File.separator + "startUp.bat";
        try {
            FileUtil.copyFile(exeUrl, exeInputUrl, true);
            FileUtil.copyFile(batUrl, batInputUrl, true);
            System.err.println("精度评定模型：copy执行文件exe,bat文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("精度评定模型：copy执行文件exe,bat文件错误" + e.getMessage());
            return 0;
        }
    }

    private int writeInputCsv(String JDPD_MODEL_TEMPLATE_INPUT, YwkPlaninfo planInfo,
                              String rvcrcrsccd, List<Map<String, String>> waterLevelFlowList) {
        //获取水文预报模型文件
        String SWYB_SHUIWEN_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_BASE_NEW_SHUIWEN_MODEL_PATH");
        String SHUIWEN_MODEL_TEMPLATE_OUTPUT = SWYB_SHUIWEN_MODEL_PATH + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                + File.separator + planInfo.getnPlanid();//输出的地址

        Map<String, List<String>> finalResult = getModelResult(SHUIWEN_MODEL_TEMPLATE_OUTPUT);
        Long step = planInfo.getnOutputtm();//步长(小时)
        if (finalResult != null && finalResult.size() > 0) {
            Date startTime = planInfo.getdCaculatestarttm();
            Date endTime = planInfo.getdCaculateendtm();
            List<String> dataList = finalResult.get(rvcrcrsccd); //根据断面ID获取此行数据

            ArrayList<String> qList = new ArrayList<>(); //流量预报值  -- y_pred流量预报值
            ArrayList<String> zList = new ArrayList<>(); //水位预报值  -- s_pred水位预报值
//            String s = (dataList != null && dataList.size() > 0)?"a":"b";
            if (dataList != null && dataList.size() > 0) {
                int index = 0;

                for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                    try {
                        qList.add(dataList.get(index));
                        index++;
                    } catch (Exception e) {
                        break;
                    }
                }

                for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                    try {
                        zList.add(dataList.get(index));
                        index++;
                    } catch (Exception e) {
                        break;
                    }
                }

                String inputOutCsv = JDPD_MODEL_TEMPLATE_INPUT + File.separator + "input.csv";

                try {
                    int lineIndex = 0;
                    BufferedWriter bw = new BufferedWriter(new FileWriter(inputOutCsv, false)); // 附加
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    // 添加新的数据行
                    bw.write("time,y_true,y_pred,s_true,s_pred"); //编写表头
                    for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                        bw.newLine();
                        bw.write(sdf.format(time) + "," + waterLevelFlowList.get(lineIndex).get("y_true") +
                                "," + qList.get(lineIndex) + "," + waterLevelFlowList.get(lineIndex).get("s_true") + "," + zList.get(lineIndex)); //填充数据
                        lineIndex++;
                    }
                    bw.close();
                    System.out.println("精度评定模型:精度评定模型input.csv输入文件写入成功");
                    return 1;
                } catch (Exception e) {
                    // File对象的创建过程中的异常捕获
                    System.out.println("精度评定模型:精度评定模型input.csv输入文件写入失败");
                    e.printStackTrace();
                    return 0;
                }
            }else{
                //没有对应预报数据时
                String inputOutCsv = JDPD_MODEL_TEMPLATE_INPUT + File.separator + "input.csv";

                try {
                    int lineIndex = 0;
                    BufferedWriter bw = new BufferedWriter(new FileWriter(inputOutCsv, false)); // 附加
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    // 添加新的数据行
                    bw.write("time,y_true,y_pred,s_true,s_pred"); //编写表头
                    for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                        bw.newLine();
                        bw.write(sdf.format(time) + "," + waterLevelFlowList.get(lineIndex).get("y_true") +
                                "," + 0 + "," + waterLevelFlowList.get(lineIndex).get("s_true") + "," + 0);
                        lineIndex++;
                    }
                    bw.close();
                    System.out.println("精度评定模型:精度评定模型input.csv输入文件写入成功");
                    return 1;
                } catch (Exception e) {
                    // File对象的创建过程中的异常捕获
                    System.out.println("精度评定模型:精度评定模型input.csv输入文件写入失败");
                    e.printStackTrace();
                    return 0;
                }
            }

        }
        return 0;
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
            String resultFilePath = model_template_output + "/result.txt";
            br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(resultFilePath)), "UTF-8"));
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

    @Override
    public Object getHistoryJingDuRcs(String planId) {
        String JDPD_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("JDPD_MODEL");
        String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");
        String JDPD_MODEL_TEMPLATE_OUTPUT = JDPD_MODEL_PATH + File.separator + out
                + File.separator + planId;//输出的地址

        List<String> rcsList = new ArrayList<>();
        File file =new File(JDPD_MODEL_TEMPLATE_OUTPUT);
        File[] files = file.listFiles();
        for (File file1 : files) {
            rcsList.add(file1.getName());
        }

        List<WrpRcsBsin> listByIds = wrpRcsBsinDao.findListByIds(rcsList);
        return listByIds;
    }


    @Override
    public Object getHistoryJingDuInfo(String planId, String rvcrcrsccd) {
        String JDPD_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("JDPD_MODEL");
        String template = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");

        String JDPD_MODEL_TEMPLATE = JDPD_MODEL_PATH + File.separator + template;
        String JDPD_MODEL_TEMPLATE_INPUT = JDPD_MODEL_TEMPLATE
                + File.separator + "INPUT" + File.separator + planId + File.separator + rvcrcrsccd; //输入的地址

        String inputCsv = JDPD_MODEL_TEMPLATE_INPUT + File.separator + "input.csv";

        YwkPlaninfo planInfo = ywkPlaninfoDao.findOne(planId);
        //封装时间列
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        Long step = planInfo.getnOutputtm();//步长
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        try {
            FileReader fileReader = new FileReader(inputCsv);
            if(fileReader!=null){
                BufferedReader reader = new BufferedReader(fileReader);
                String line = null;
                List<List<String>> datas = new ArrayList<>();
                while ((line = reader.readLine()) != null) {
                    String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                    datas.add(Arrays.asList(item));
                }

                int beginIndex = 1;
                for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                    map = new HashMap<>();
                    map.put("time", sdf.format(time));
                    map.put("y_true", datas.get(beginIndex).get(1));
                    map.put("y_pred", datas.get(beginIndex).get(2));
                    map.put("s_true", datas.get(beginIndex).get(3));
                    map.put("s_pred", datas.get(beginIndex).get(4));
                    result.add(map);
                    beginIndex++;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public Object getHistoryJingDuResult(String planId, String rvcrcrsccd) {
        String JDPD_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("JDPD_MODEL");
        String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");

        String JDPD_MODEL_TEMPLATE_OUTPUT = JDPD_MODEL_PATH + File.separator + out
                + File.separator + planId + File.separator + rvcrcrsccd;//输出的地址
        String JINGDU_PINGDING_TXT = JDPD_MODEL_TEMPLATE_OUTPUT + File.separator + "jingdu_pingding.txt";//输出的地址
        File path = new File(JINGDU_PINGDING_TXT);
        if(!path.exists()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("describe", "模型运行异常！");
            return jsonObject;
        }else{
            JSONArray result = new JSONArray();
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
                String lineTxt2 = br.readLine();
                //if (lineTxt2 != null) {
                String[] split = lineTxt2.split("\t");
                JSONObject json = new JSONObject();
                JSONArray hsllArray = new JSONArray();
                json.put("hsll",hsllArray);
                Map<String, String> map = new HashMap<>();
                map.put("pjxdwc", split[0]);
                map.put("pjjdwc", split[1]);
                map.put("nsxlxs", split[2]);
                hsllArray.add(map);
                result.add(json);

                lineTxt2 = br.readLine();
                split = lineTxt2.split("\t");
                json = new JSONObject();
                JSONArray hsswArray = new JSONArray();
                json.put("hssw",hsllArray);
                map = new HashMap<>();
                map.put("pjxdwc", split[0]);
                map.put("pjjdwc", split[1]);
                map.put("nsxlxs", split[2]);
                hsswArray.add(map);
                result.add(json);

                lineTxt2 = br.readLine();
                split = lineTxt2.split("\t");
                json = new JSONObject();
                JSONArray hlArray = new JSONArray();
                json.put("hl",hlArray);
                map = new HashMap<>();
                map.put("jdwc", split[0]);
                hlArray.add(map);
                result.add(json);

                lineTxt2 = br.readLine();
                split = lineTxt2.split("\t");
                json = new JSONObject();
                JSONArray hsfzArray = new JSONArray();
                json.put("hsfz",hsfzArray);
                map = new HashMap<>();
                map.put("schf", split[0]);
                map.put("ybhf", split[1]);
                map.put("hfwc", split[2]);
                map.put("hfxkwc", split[3]);
                map.put("qualified", split[4]);
                hsfzArray.add(map);
                result.add(json);

                lineTxt2 = br.readLine();
                split = lineTxt2.split("\t");
                json = new JSONObject();
                JSONArray fzsjArray = new JSONArray();
                json.put("fzsj",fzsjArray);
                map = new HashMap<>();
                map.put("scfzsj", split[0]);
                map.put("ybfzsj", split[1]);
                map.put("fzsjwc", split[2]);
                map.put("fzsjxk", split[3]);
                map.put("qualified", split[4]);
                fzsjArray.add(map);
                result.add(json);
                return result;
            } catch (Exception e) {
                System.err.println("文件读取错误！" + e.getMessage());
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public void deleteHistoryJingDu(String planId, String rvcrcrsccd) {
        String JDPD_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("JDPD_MODEL");
        String template = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");
        String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");
        String run = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_RUN");
        String JDPD_MODEL_TEMPLATE = JDPD_MODEL_PATH + File.separator + template;
        String JDPD_MODEL_TEMPLATE_INPUT = JDPD_MODEL_TEMPLATE
                + File.separator + "INPUT" + File.separator + planId + File.separator + rvcrcrsccd; //输入的地址
        String JDPD_MODEL_TEMPLATE_OUTPUT = JDPD_MODEL_PATH + File.separator + out
                + File.separator + planId + File.separator + rvcrcrsccd;//输出的地址

        //模型运行的config
        String JDPD_MODEL_RUN = JDPD_MODEL_PATH + File.separator + run;
        String JDPD_MODEL_RUN_PLAN = JDPD_MODEL_RUN + File.separator + planId + File.separator + rvcrcrsccd;

        FileUtil.deleteFile(new File(JDPD_MODEL_TEMPLATE_INPUT));
        FileUtil.deleteFile(new File(JDPD_MODEL_TEMPLATE_OUTPUT));
        FileUtil.deleteFile(new File(JDPD_MODEL_RUN_PLAN));

        String planIdPath = JDPD_MODEL_PATH + File.separator + out
                + File.separator + planId;//输出的地址

        File file =new File(planIdPath);
        File[] files = file.listFiles();
        if(files.length == 0){
            FileUtil.deleteFile(new File(planIdPath));
            FileUtil.deleteFile(new File(JDPD_MODEL_TEMPLATE
                    + File.separator + "INPUT" + File.separator + planId));
            FileUtil.deleteFile(new File(JDPD_MODEL_RUN + File.separator + planId));
        }
    }

    @Autowired
    YwkPlanBasicDwxParamDao ywkPlanBasicDwxParamDao;


    @Override
    public List<Map<String, Double>> importParamWithDWX(MultipartFile mutilpartFile) {
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
            System.out.println("error::::"+e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
        CacheUtil.saveOrUpdate("paramCache", "dwx", results);
        return results;
    }

    @Transactional
    @Override
    public void saveSwybDwxParamToDb(List<Map<String, Double>> result) {
        ywkPlanBasicDwxParamDao.deleteAll();//删除
        List<YwkPlanBasicDwxParam> insert = new ArrayList<>();
        for (Map<String,Double> map : result){
            YwkPlanBasicDwxParam ywkPlanBasicDwxParam = new YwkPlanBasicDwxParam();
            Double d1 = map.get("unitOne");
            Double d2 = map.get("unitTwo");
            Double d3 = map.get("unitThree");
            ywkPlanBasicDwxParam.setUnitOne(d1);
            ywkPlanBasicDwxParam.setUnitTwo(d2);
            ywkPlanBasicDwxParam.setUnitThree(d3);
            ywkPlanBasicDwxParam.setcId(StrUtil.getUUID());
            insert.add(ywkPlanBasicDwxParam);
        }
        ywkPlanBasicDwxParamDao.saveAll(insert);
    }

    @Autowired
    YwkPlanBasicXajParamDao ywkPlanBasicXajParamDao;

    @Transactional
    @Override
    public void saveSwybXajParamToDb(List<CalibrationXAJVo> calibrationXAJVos) {
        ywkPlanBasicXajParamDao.deleteAll();
        //todo 这个地方传的zoneid不是那个主键id  不是river_zone的主键id
        for (CalibrationXAJVo xajVo : calibrationXAJVos){
            YwkPlanBasicXajParam  ywkPlanBasicXajParam  = new YwkPlanBasicXajParam();
            ywkPlanBasicXajParam.setcId(StrUtil.getUUID());
            ywkPlanBasicXajParam.setZoneId(xajVo.getcId());
            ywkPlanBasicXajParam.setXajB(xajVo.getXajB());
            ywkPlanBasicXajParam.setXajC(xajVo.getXajC());
            ywkPlanBasicXajParam.setXajK(xajVo.getXajK());
            ywkPlanBasicXajParam.setXajWum(xajVo.getXajWum());
            ywkPlanBasicXajParam.setXajWdm(xajVo.getXajWdm());
            ywkPlanBasicXajParam.setXajWlm(xajVo.getXajWlm());
            ywkPlanBasicXajParam.setXajWu0(xajVo.getXajWu0());
            ywkPlanBasicXajParam.setXajWd0(xajVo.getXajWd0());
            ywkPlanBasicXajParam.setXajWl0(xajVo.getXajWl0());
            ywkPlanBasicXajParam.setXajEp(xajVo.getXajEp());
            ywkPlanBasicXajParamDao.save(ywkPlanBasicXajParam);//保存
        }
    }

    @Autowired
    YwkPlanBasicXggxParamDao ywkPlanBasicXggxParamDao;

    @Transactional
    @Override
    public void saveSwybXggxParamToDb(List<CalibrationXGGXVo> calibrationXGGXVos) {

        ywkPlanBasicXggxParamDao.deleteAll();

        for (CalibrationXGGXVo xggxVo : calibrationXGGXVos){
            YwkPlanBasicXggxParam ywkPlanBasicXggxParam  = new YwkPlanBasicXggxParam();
            ywkPlanBasicXggxParam.setcId(StrUtil.getUUID());
            ywkPlanBasicXggxParam.setZoneId(xggxVo.getcId());
            ywkPlanBasicXggxParam.setXggxA(xggxVo.getXggxA());
            ywkPlanBasicXggxParam.setXggxB(xggxVo.getXggxB());
            ywkPlanBasicXggxParamDao.save(ywkPlanBasicXggxParam);//保存
        }
    }

    @Autowired
    YwkPlanBasicScsmsjgParamDao ywkPlanBasicScsmsjgParamDao;
    @Transactional
    @Override
    public void saveSwybScsOrMsjgParamToDb(CalibrationMSJGAndScsVo calibrationMSJGAndScsVo, Integer tag) {

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

        List<YwkPlanBasicScsmsjgParam> ywkPlanBasicScsmsjgParams = ywkPlanBasicScsmsjgParamDao.findAll();
        Map<String, YwkPlanBasicScsmsjgParam> zoneMap = ywkPlanBasicScsmsjgParams.stream().collect(Collectors.toMap(YwkPlanBasicScsmsjgParam::getZoneId, Function.identity()));

        for (String id : ids){
            YwkPlanBasicScsmsjgParam ywkPlanBasicScsmsjgParam = zoneMap.get(id);
            if (ywkPlanBasicScsmsjgParam == null){
                ywkPlanBasicScsmsjgParam  = new YwkPlanBasicScsmsjgParam();
                ywkPlanBasicScsmsjgParam.setcId(StrUtil.getUUID());
                ywkPlanBasicScsmsjgParam.setZoneId(id);
            }
            CalibrationMSJGAndScsVo.MSJGAndScSVo msjgAndScSVo = keyMap.get(id);
            if (tag == 0){
                ywkPlanBasicScsmsjgParam.setMsjgK(msjgAndScSVo.getMsjgK());
                ywkPlanBasicScsmsjgParam.setMsjgX(msjgAndScSVo.getMsjgX());
            }else {
                ywkPlanBasicScsmsjgParam.setScsCn(msjgAndScSVo.getScsCn());
            }

            ywkPlanBasicScsmsjgParamDao.save(ywkPlanBasicScsmsjgParam);//保存
        }
    }

    @Override
    public List<YwkPlanBasicDwxParam> getSwybDwxParam() {
        List<YwkPlanBasicDwxParam> all = ywkPlanBasicDwxParamDao.findAll();
        return all;
    }

    @Override
    public List<YwkPlanBasicXajParam> getSwybXajParam() {
        List<YwkPlanBasicXajParam> all = ywkPlanBasicXajParamDao.findAll();
        return all;
    }

    @Override
    public List<YwkPlanBasicXggxParam> getSwybXggxParam() {
        List<YwkPlanBasicXggxParam> all = ywkPlanBasicXggxParamDao.findAll();
        return all;
    }

    @Override
    public List<YwkPlanBasicScsmsjgParam> getSwybScsOrMsjgParam() {
        List<YwkPlanBasicScsmsjgParam> all = ywkPlanBasicScsmsjgParamDao.findAll();
        return all;
    }
}



