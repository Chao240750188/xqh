package com.essence.business.xqh.api.fbc;

import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;

import java.util.List;
import java.util.Map;

/**
 * 方案结果列表相关业务层
 */
public interface FbcPlanInfoManageService {

    /**
     * 获取方案列表信息
     * @return
     */
    Paginator getPlanList(PaginatorParam paginatorParam);


    /**
     * 根据方案id获取方案边界流量数据
     * @param planId
     * @return
     */
    List<Map> getAllBoundaryQByPlanId(String planId);


    /**
     * 根据方案id获取防洪保护区信息
     * @param planId
     * @return
     */
    Map<String, Object> getAllRoughnessByPlanId(String planId);

    /**
     * 根据方案id获取溃口列表
     * @param planId
     * @return
     */
    Map<String, Object> getAllBreakByPlanId(String planId);


    /**
     * 删除方案以及方案下关联点所有入参
     * @param planId
     * @return
     */
    void deleteAllInputByPlanId(String planId);
}
