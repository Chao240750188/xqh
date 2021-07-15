package com.essence.business.xqh.service.csnl;

import com.essence.business.xqh.api.csnl.ModelCsnlPlanInfoService;
import com.essence.business.xqh.common.util.CacheUtil;
import com.essence.business.xqh.common.util.FileUtil;
import com.essence.business.xqh.common.util.PropertiesUtil;
import com.essence.business.xqh.dao.dao.fhybdd.*;
import com.essence.business.xqh.dao.dao.hsfxtk.YwkCsnlRoughnessDao;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.business.xqh.dao.entity.hsfxtk.YwkCsnlRoughness;
import com.essence.framework.jpa.Criterion;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class ModelCsnlPlanInfoServiceImpl implements ModelCsnlPlanInfoService {

    @Autowired
    YwkPlaninfoDao ywkPlaninfoDao;

    @Autowired
    YwkCsnlRoughnessDao ywkCsnlRoughnessDao;

    @Autowired
    YwkPlaninRainfallDao ywkPlaninRainfallDao; //方案雨量

    @Override
    public Paginator getPlanList(PaginatorParam paginatorParam) {
        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("XT_CSNL");
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
        List<String> planidList = new ArrayList<>();
        for (YwkPlaninfo item : all.getItems()) {
            planidList.add(item.getnPlanid());
        }
        List<YwkCsnlRoughness> ywkCsnlRoughnessList = ywkCsnlRoughnessDao.findByPlanIdList(planidList);

        for(YwkPlaninfo item : all.getItems()){
            for (YwkCsnlRoughness ywkCsnlRoughness : ywkCsnlRoughnessList) {
                if(ywkCsnlRoughness.getnPlanid().equals(item.getnPlanid())){
                    item.setRoughness(ywkCsnlRoughness.getRoughness());
                }
            }
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
    @Transactional
    public void deleteByPlanId(YwkPlaninfo planInfo) {
        ywkPlaninfoDao.delete(planInfo.getnPlanid()); //删除方案
        ywkPlaninRainfallDao.deleteByNPlanid(planInfo.getnPlanid()); //删除方案对应雨量
        ywkCsnlRoughnessDao.deleteByNPlanid(planInfo.getnPlanid()); //删除糙率

        //删除对应模型文件
        try {
            String CSNL_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("CSNL_MODEL");
            String template = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");
            String out = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT");
            String run = PropertiesUtil.read("/filePath.properties").getProperty("MODEL_RUN");

            //另一个模型
            String CSNL_MODEL_TEMPLATE = CSNL_MODEL_PATH + File.separator + template;
            //输入的地址
            String CSNL_MODEL_TEMPLATE_INPUT = CSNL_MODEL_TEMPLATE
                    + File.separator + "INPUT" + File.separator + planInfo.getnPlanid();
            //输出的地址
            String CSNL_MODEL_TEMPLATE_OUTPUT = CSNL_MODEL_PATH + File.separator + out
                    + File.separator + planInfo.getnPlanid();
            //模型运行的config
            String CSNL_MODEL_RUN = CSNL_MODEL_PATH + File.separator + run;
            String CSNL_MODEL_RUN_PLAN = CSNL_MODEL_RUN + File.separator + planInfo.getnPlanid();

            FileUtil.deleteFile(new File(CSNL_MODEL_TEMPLATE_INPUT));
            FileUtil.deleteFile(new File(CSNL_MODEL_TEMPLATE_OUTPUT));
            FileUtil.deleteFile(new File(CSNL_MODEL_RUN_PLAN));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
