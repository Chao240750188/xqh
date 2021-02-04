package com.essence.business.xqh.api.hsfxtk;

import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;

import java.util.List;
import java.util.Map;

/**
 * 方案结果列表相关业务层
 */
public interface PlanInfoManageService {

    /**
     * 获取方案列表信息
     * @return
     */
    Paginator getPlanList(PaginatorParam paginatorParam);


    /**
     * 根据方案id获取边界信息
     * @param planId
     * @return
     */
    List<Map> getAllBoundaryByPlanId(String planId);


    /**
     * 根据方案id获取防洪保护区信息
     * @param planId
     * @return
     */
    Map<String, Object> getAllRoughnessByPlanId(String planId);
}
