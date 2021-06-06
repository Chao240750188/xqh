package com.essence.business.xqh.api.fhdd;

import com.essence.business.xqh.api.skdd.vo.ModelSkddXxInputVo;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;

public interface ModelFhddPlanInfoManageService {
    Paginator getPlanList(PaginatorParam paginatorParam);

    YwkPlaninfo getPlanInfoByPlanId(String planId);

    ModelSkddXxInputVo getPlanInputInfo(String planId);

    void deleteByPlanId(YwkPlaninfo planinfo);
}
