package com.essence.business.xqh.service.hsfxtk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.essence.business.xqh.api.hsfxtk.PlanInfoManageService;
import com.essence.business.xqh.dao.dao.fhybdd.YwkModelDao;
import com.essence.business.xqh.dao.dao.fhybdd.YwkPlaninfoDao;
import com.essence.business.xqh.dao.dao.hsfxtk.*;
import com.essence.business.xqh.dao.entity.fhybdd.YwkModel;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.business.xqh.dao.entity.hsfxtk.*;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 方案结果列表相关业务层实现
 */
@Service
public class PlanInfoManageServiceImpl implements PlanInfoManageService {

    @Autowired
    YwkPlaninfoDao ywkPlaninfoDao;

    @Autowired
    private YwkBoundaryBasicDao ywkBoundaryBasicDao;//13个基本信息

    @Autowired
    private YwkPlaninFloodRoughnessDao ywkPlaninFloodRoughnessDao;//糙率信息

    @Autowired
    private YwkPlaninRiverRoughnessDao ywkPlaninRiverRoughnessDao;//河道糙率信息

    @Autowired
    private YwkModelRoughnessParamDao ywkModelRoughnessParamDao; //糙率参数基本表
    @Autowired
    private YwkModelDao ywkModelDao; //YwkModelDao

    @Autowired
    private YwkPlaninFloodBoundaryDao ywkPlaninFloodBoundaryDao;

    @Autowired
    YwkPlaninFloodBreakDao ywkPlaninFloodBreakDao;//溃口方案表

    @Autowired
    YwkBreakBasicDao ywkBreakBasicDao;//溃口基本信息表

    @Override
    public Paginator<YwkPlaninfo> getPlanList(PaginatorParam paginatorParam) {
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
            if (CollectionUtils.isEmpty(datas)) {
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
            resultMap.put("list",maps);
            boundaryResults.add(resultMap);
        }
        List<Map> results = JSON.parseArray(JSON.toJSONString(boundaryResults, SerializerFeature.WriteDateUseDateFormat,SerializerFeature.WriteMapNullValue), Map.class);
        return results;
    }


    @Override
    public Map<String, Object> getAllRoughnessByPlanId(String planId) {

        List<YwkPlaninFloodRoughness> byPlanId = ywkPlaninFloodRoughnessDao.findByPlanId(planId);
        if (CollectionUtils.isEmpty(byPlanId)){
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
        if (!CollectionUtils.isEmpty(byPlanId)){
            ywkPlaninRiverRoughnessDao.deleteByPlanRoughnessId(byPlanId.get(0).getPlanRoughnessid());
        }
        ywkPlaninFloodRoughnessDao.deleteByPlanId(planId);

        //删除溃点
        ywkPlaninFloodBreakDao.deleteByNPlanid(planId);

        //删除方案基本信息
        ywkPlaninfoDao.deleteById(planId);

    }
}
