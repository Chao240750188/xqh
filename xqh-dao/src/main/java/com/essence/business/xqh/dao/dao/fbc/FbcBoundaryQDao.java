package com.essence.business.xqh.dao.dao.fbc;

import com.essence.business.xqh.dao.entity.fbc.FbcBoundaryQ;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FbcBoundaryQDao extends EssenceJpaRepository<FbcBoundaryQ, String> {

    /**
     * 根据方案id查询条件列表
     *
     * @param planId
     * @return
     */
    List<FbcBoundaryQ> findByNPlanidOrderByAbsoluteTime(String planId);
}
