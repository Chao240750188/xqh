package com.essence.business.xqh.service.hsfxtk;

import com.essence.business.xqh.api.hsfxtk.PlanInfoManageService;
import com.essence.business.xqh.dao.dao.fhybdd.YwkPlaninfoDao;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 方案结果列表相关业务层实现
 */
@Service
public class PlanInfoManageServiceImpl implements PlanInfoManageService {

    @Autowired
    YwkPlaninfoDao ywkPlaninfoDao;

    @Override
    public Paginator<YwkPlaninfo> getPlanList(PaginatorParam paginatorParam) {
        Paginator<YwkPlaninfo> all = ywkPlaninfoDao.findAll(paginatorParam);
        return all;
    }

}
