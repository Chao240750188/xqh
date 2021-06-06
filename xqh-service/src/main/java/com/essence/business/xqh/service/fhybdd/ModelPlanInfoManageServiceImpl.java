package com.essence.business.xqh.service.fhybdd;

import com.essence.business.xqh.api.fhybdd.service.ModelPlanInfoManageService;
import com.essence.business.xqh.common.util.CacheUtil;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.common.util.FileUtil;
import com.essence.business.xqh.common.util.PropertiesUtil;
import com.essence.business.xqh.dao.dao.fhybdd.*;
import com.essence.business.xqh.dao.entity.fhybdd.*;
import com.essence.framework.jpa.Criterion;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ModelPlanInfoManageServiceImpl implements ModelPlanInfoManageService {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    YwkPlaninfoDao  ywkPlaninfoDao;

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

        Criterion criterion2 = new Criterion();//方案成功
        criterion2.setFieldName("nPlanstatus");
        criterion2.setOperator(Criterion.EQ);
        criterion2.setValue(2L);

        conditions.add(criterion1);
        conditions.add(criterion2);
        Paginator<YwkPlaninfo> all = ywkPlaninfoDao.findAll(paginatorParam);
        List<YwkPlaninfo> items = all.getItems();
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
        for (YwkPlaninfo planinfo : items){
            Long startTime = planinfo.getdCaculatestarttm().getTime();
            Long endTime = planinfo.getdCaculateendtm().getTime();
            Long total = endTime-startTime;///(60*60*1000);
            Long sum = total.longValue()/(60*60*1000);
            planinfo.setLeadTime(sum);
            planinfo.setIsWarnIng(1);
        }
        return all;
    }


    @Transactional
    @Override
    public void deleteByPlanId(YwkPlaninfo planInfo) {

       /* String catchMentAreaModelId = planInfo.getnModelid(); //集水区模型id   // 1是SCS  2是单位线
        String reachId = planInfo.getnSWModelid(); //河段模型id*/

        Integer dwxCount = ywkPlanCalibrationDwxDao.countByNPlanId(planInfo.getnPlanid());

        Integer zoneCount = ywkPlanCalibrationZoneDao.countByNPlanId(planInfo.getnPlanid());

        Integer zoneXajCount = ywkPlanCalibrationZoneXajDao.countByNPlanId(planInfo.getnPlanid());

        Integer zoneXGGXCount = ywkPlanCalibrationZoneXggxDao.countByNPlanId(planInfo.getnPlanid());

        if (dwxCount != 0L){
            ywkPlanCalibrationDwxDao.deleteByNPlanid(planInfo.getnPlanid());
        }
        if (zoneCount != 0L){
            ywkPlanCalibrationZoneDao.deleteByNPlanid(planInfo.getnPlanid());
        }

        if (zoneXajCount != 0L){
            ywkPlanCalibrationZoneXajDao.deleteByNPlanid(planInfo.getnPlanid());
        }

        if (zoneXGGXCount != 0L){
            ywkPlanCalibrationZoneXggxDao.deleteByNPlanid(planInfo.getnPlanid());
        }
        //预报断面也要干掉
        List<YwkPlanTriggerRcs> triggerRcs = ywkPlanTriggerRcsDao.findByNPlanid(planInfo.getnPlanid());
        if (!CollectionUtils.isEmpty(triggerRcs)){
            ywkPlanTriggerRcsDao.deleteByNPlanid(planInfo.getnPlanid());
            List<String> triggerIds = triggerRcs.stream().map(YwkPlanTriggerRcs::getId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(triggerIds)){
                ywkPlanTriggerRcsFlowDao.deleteByTriggerRcsIds(triggerIds);
            }
        }
        ywkPlaninfoDao.delete(planInfo.getnPlanid());

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
        }catch (Exception e){

        }
    }

    @Autowired
    WrpRcsBsinDao wrpRcsBsinDao;
    @Override
    public List<Map<String, Object>> getTriggerList(YwkPlaninfo planinfo) {

        List<Map<String, Object>>  results = new ArrayList<>();
        List<YwkPlanTriggerRcs> triggerRcs = ywkPlanTriggerRcsDao.findByNPlanid(planinfo.getnPlanid());
        if (CollectionUtils.isEmpty(triggerRcs)){
            return results;
        }
        Map<String, String> rcsMap = wrpRcsBsinDao.findAll().stream().collect(Collectors.toMap(WrpRcsBsin::getRvcrcrsccd, WrpRcsBsin::getRvcrcrscnm));
        List<String> triggerIds = triggerRcs.stream().map(YwkPlanTriggerRcs::getId).collect(Collectors.toList());
        List<YwkPlanTriggerRcsFlow> flowList = ywkPlanTriggerRcsFlowDao.findByTriggerRcsIdsOrderByTime(triggerIds);

        Map<String,List<YwkPlanTriggerRcsFlow>> flowMap = new HashMap<>();
        for (YwkPlanTriggerRcsFlow flow : flowList){
            String triggerRcsId = flow.getTriggerRcsId();
            List<YwkPlanTriggerRcsFlow> triggerRcsFlows = flowMap.get(triggerRcsId);
            if (CollectionUtils.isEmpty(triggerRcsFlows)){
                triggerRcsFlows = new ArrayList<>();
            }
            triggerRcsFlows.add(flow);
            flowMap.put(triggerRcsId,triggerRcsFlows);
        }
        for (YwkPlanTriggerRcs triggerRcs1 : triggerRcs){
            Map<String,Object>  resultMap = new HashMap<>();
            String id = triggerRcs1.getId();
            String rcsId = triggerRcs1.getRcsId();
            List<YwkPlanTriggerRcsFlow> triggerRcsFlows = flowMap.get(id);
            String name = rcsMap.get(rcsId);
            resultMap.put("name",name);
            resultMap.put("flow",triggerRcsFlows);
            results.add(resultMap);
        }
        return results;
    }


    @Override
    public void publishPlan(List<String> planIds,Integer tag) {

        List<YwkPlaninfo> planInfos = ywkPlaninfoDao.findAllById(planIds);
        if (!CollectionUtils.isEmpty(planInfos)) {
            for (YwkPlaninfo planinfo : planInfos) {
                if (tag == 1){
                    planinfo.setnPublish(1L);
                    planinfo.setnPublishTime(new Date());
                }else {
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

        if (rvcd != null && !"".equals(rvcd)){
            baseSql = baseSql+" WHERE b.RVCD = '" + rvcd+"' ";
            if (rcsName != null){
                baseSql = baseSql + "AND b.RVCRCRSCNM LIKE '%"+rcsName+"%'";
            }
        }else {
            if (rcsName != null && !"".equals(rcsName)){
                baseSql = baseSql + "WHERE b.RVCRCRSCNM LIKE '%"+rcsName+"%'";
            }
        }
        Query query = entityManager.createNativeQuery(baseSql);
        query.unwrap(NativeQueryImpl.class).setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        List<Map<String,Object>> warnIngWaterLevels = query.getResultList();

        //a.C_ID,a.RCS_ID,a.WARNING_WATER_LEVEL,b.RVCRCRSCNM,c.RVCD,c.RVNM
        //List<Map<String, Object>> warnIngWaterLevels = wrpWarningWaterLevelDao.getWarnIngWaterLevels();
        List<Map<String, Object>> results = new ArrayList<>();
        for (Map<String,Object> map : warnIngWaterLevels){
            Map<String,Object> resultMap = new HashMap<>();
            resultMap.put("cId",map.get("C_ID"));
            resultMap.put("rcsId",map.get("RCS_ID"));
            resultMap.put("warningWaterLevel",map.get("WARNING_WATER_LEVEL"));
            resultMap.put("rcsName",map.get("RVCRCRSCNM"));
            resultMap.put("riverId",map.get("RVCD"));
            resultMap.put("riverName",map.get("RVNM"));
            resultMap.put("flag",0);
            results.add(resultMap);
        }
        return results;
    }


    @Override
    public void upDateWarnIngWaterLevels(List<Map<String, Object>> datas) {
        if (CollectionUtils.isEmpty(datas)){
            return;
        }
        for (Map<String,Object> map : datas){
            WrpWarningWaterLevel model = new WrpWarningWaterLevel();
            model.setcId(map.get("cId")+"");
            model.setRcsId(map.get("rcsId")+"");
            model.setWarningWaterLevel(Double.parseDouble(map.get("warningWaterLevel")+""));
            wrpWarningWaterLevelDao.save(model);
        }
    }
}



