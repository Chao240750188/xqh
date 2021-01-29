package com.essence.business.xqh.dao.dao.hsfxtk;


import com.essence.business.xqh.dao.entity.hsfxtk.YwkPlaninFloodBoundary;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YwkPlaninFloodBoundaryDao extends EssenceJpaRepository<YwkPlaninFloodBoundary, String> {

    /**
     * 根据方案id删除边界条件信息
     * @param planId
     */
    void deleteByPlanId(String planId);
}