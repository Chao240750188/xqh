package com.essence.business.xqh.service.fhdd;

import com.essence.business.xqh.api.fhdd.ModelFhddPlanInfoManageService;
import com.essence.business.xqh.api.skdd.vo.ModelSkddXxInputVo;
import com.essence.business.xqh.common.util.CacheUtil;
import com.essence.business.xqh.common.util.FileUtil;
import com.essence.business.xqh.common.util.PropertiesUtil;
import com.essence.business.xqh.dao.dao.fhybdd.*;
import com.essence.business.xqh.dao.entity.fhybdd.WrpRsrBsin;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlanInputZ;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlanTriggerRcs;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.framework.jpa.Criterion;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ModelFhddPlanInfoManageServiceImpl implements ModelFhddPlanInfoManageService {

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
    YwkPlanInputZDao ywkPlanInputZDao;

    @Autowired
    YwkPlanTriggerRcsDao ywkPlanTriggerRcsDao;

    @Autowired
    YwkPlanTriggerRcsFlowDao ywkPlanTriggerRcsFlowDao;

    @Autowired
    WrpRsrBsinDao wrpRsrBsinDao;

    @Autowired
    YwkPlaninRainfallDao ywkPlaninRainfallDao;//方案雨量

    @Override
    public Paginator getPlanList(PaginatorParam paginatorParam) {
        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("FHDD");
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

        List<WrpRsrBsin> allWRB = wrpRsrBsinDao.findAll();
        Map<String, String> map = new HashMap<>();
        for(WrpRsrBsin wrb: allWRB){
            map.put(wrb.getRscd(), wrb.getRsnm());
        }

        for (YwkPlaninfo planinfo : items){
            Long startTime = planinfo.getdCaculatestarttm().getTime();
            Long endTime = planinfo.getdCaculateendtm().getTime();
            Long total = endTime-startTime;///(60*60*1000);
            Long sum = total.longValue()/(60*60*1000);
            planinfo.setLeadTime(sum); //预见期
            planinfo.setIsWarnIng(1);
            planinfo.setRname(map.get(PropertiesUtil.read("/filePath.properties").getProperty("skddxx.RSCD_RSR." + planinfo.getRscd())));
        }

        return all;
    }

    @Override
    public YwkPlaninfo getPlanInfoByPlanId(String planId) {
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
    public ModelSkddXxInputVo getPlanInputInfo(String planId) {
        YwkPlanInputZ byNPlanid = ywkPlanInputZDao.findByNPlanid(planId);
        ModelSkddXxInputVo vo = new ModelSkddXxInputVo();
        if(byNPlanid != null){
            vo.setPlanId(planId);
            vo.setNumberZ(byNPlanid.getNZ());
            vo.setNumberQ(byNPlanid.getNQ());
        }
        return vo;
    }

    @Override
    @Transactional
    public void deleteByPlanId(YwkPlaninfo planInfo) {

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

        ywkPlanInputZDao.deleteByNPlanid(planInfo.getnPlanid());

        ywkPlaninRainfallDao.deleteByNPlanid(planInfo.getnPlanid()); //删除方案对应雨量

        //删除对应模型文件
        try {
            //创建入参、出参
            String FHDD_PCP_HANDLE_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("FHDD_PCP_HANDLE_MODEL_PATH");
            String FHDD_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("FHDD_MODEL_PATH");
            String template = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");
            String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");
            String run = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_RUN");



            String PCP_HANDLE_MODEL_TEMPLATE = FHDD_PCP_HANDLE_MODEL_PATH + File.separator + template;

            String PCP_HANDLE_MODEL_TEMPLATE_INPUT = PCP_HANDLE_MODEL_TEMPLATE
                    + File.separator + "INPUT" + File.separator + planInfo.getnPlanid(); //输入的地址
            String PCP_HANDLE_MODEL_TEMPLATE_OUTPUT = FHDD_PCP_HANDLE_MODEL_PATH + File.separator + out
                    + File.separator + planInfo.getnPlanid();//输出的地址

            String PCP_HANDLE_MODEL_RUN = FHDD_PCP_HANDLE_MODEL_PATH + File.separator + run;

            String PCP_HANDLE_MODEL_RUN_PLAN = PCP_HANDLE_MODEL_RUN + File.separator + planInfo.getnPlanid();

            //另一个模型
            String FHDD_MODEL_TEMPLATE = FHDD_MODEL_PATH + File.separator + template;
            //输入的地址
            String FHDD_MODEL_TEMPLATE_INPUT = FHDD_MODEL_TEMPLATE
                    + File.separator + "INPUT" + File.separator + planInfo.getnPlanid();
            //输出的地址
            String FHDD_MODEL_TEMPLATE_OUTPUT = FHDD_MODEL_PATH + File.separator + out
                    + File.separator + planInfo.getnPlanid();
            //模型运行的config
            String FHDD_MODEL_RUN = FHDD_MODEL_PATH + File.separator + run;
            String FHDD_XX_MODEL_RUN_PLAN = FHDD_MODEL_RUN + File.separator + planInfo.getnPlanid();

            FileUtil.deleteFile(new File(PCP_HANDLE_MODEL_TEMPLATE_INPUT));
            FileUtil.deleteFile(new File(PCP_HANDLE_MODEL_TEMPLATE_OUTPUT));
            FileUtil.deleteFile(new File(PCP_HANDLE_MODEL_RUN_PLAN));
            FileUtil.deleteFile(new File(FHDD_MODEL_TEMPLATE_INPUT));
            FileUtil.deleteFile(new File(FHDD_MODEL_TEMPLATE_OUTPUT));
            FileUtil.deleteFile(new File(FHDD_XX_MODEL_RUN_PLAN));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
