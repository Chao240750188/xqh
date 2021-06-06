package com.essence.business.xqh.api.skdd;

import com.essence.business.xqh.api.skdd.vo.ModelSkddXxInputVo;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;

public interface ModelSkddXxPlanInfoManageService {

    /**
     * 获取模型执行成功的方案列表
     * @param paginatorParam
     * @return
     */
    Paginator getPlanList(PaginatorParam paginatorParam);

    /**
     * 获取方案详细信息
     * @param planId
     * @return
     */
    YwkPlaninfo getPlanInfoByPlanId(String planId);

    /**
     * 获取水库调度信息
     * @param planId
     * @return
     */
    ModelSkddXxInputVo getPlanInputInfo(String planId);


    /**
     * 删除方案基本信息
     * @param planinfo
     * @return
     */
    void deleteByPlanId(YwkPlaninfo planinfo);

}
