package com.essence.business.xqh.api.fhybdd.service;

import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;

import java.util.List;
import java.util.Map;

public interface ModelPlanInfoManageService {

    /**
     * 根据条件查询
     * @param paginatorParam
     * @return
     */
    Paginator getPlanList(PaginatorParam paginatorParam);


    /**
     * 根据方案id删除
     * @param planinfo
     */
    void deleteByPlanId(YwkPlaninfo planinfo);

    /**
     * 获取预报断面列表
     * @param planinfo
     * @return
     */
    List<Map<String, Object>> getTriggerList(YwkPlaninfo planinfo);

    /**
     *发布与撤销发布
     * @param planinfo
     */
    void publishPlan(List<String> planinfo,Integer tag);


    /**
     * 获取预警水位数据
     * @return
     */
    List<Map<String, Object>> getWarnIngWaterLevels(Map map);

    /**
     * 更新预警水位数据
     * @param datas
     */
    void upDateWarnIngWaterLevels(List<Map<String, Object>> datas);
}
