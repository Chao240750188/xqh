package com.essence.business.xqh.service.skdd;

import com.essence.business.xqh.api.skdd.ModelSkddXxPlanInfoManageService;
import com.essence.business.xqh.api.skdd.vo.ModelSkddXxInputVo;
import com.essence.business.xqh.common.util.CacheUtil;
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
import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("all")
public class ModelSkddXxPlanInfoManageServiceImpl implements ModelSkddXxPlanInfoManageService {

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
    YwkPlanInputZDao ywkPlanInputZDao;

    @Autowired
    YwkPlanTriggerRcsDao ywkPlanTriggerRcsDao;

    @Autowired
    YwkPlanTriggerRcsFlowDao ywkPlanTriggerRcsFlowDao;

    @Autowired
    WrpRsrBsinDao wrpRsrBsinDao;

    @Override
    public Paginator getPlanList(PaginatorParam paginatorParam) {
        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("SKDD_XX");
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
    }

}
