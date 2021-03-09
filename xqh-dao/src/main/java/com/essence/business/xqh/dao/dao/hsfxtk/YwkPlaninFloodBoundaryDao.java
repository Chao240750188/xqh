package com.essence.business.xqh.dao.dao.hsfxtk;


import com.essence.business.xqh.dao.entity.hsfxtk.YwkPlaninFloodBoundary;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkPlaninFloodBoundaryDao extends EssenceJpaRepository<YwkPlaninFloodBoundary, String> {

    /**
     * 根据方案id删除边界条件信息
     * @param planId
     */
    void deleteByPlanId(String planId);

    List<YwkPlaninFloodBoundary> findByPlanId(String planId);

    /**
     * 感觉方案id和stcd删除-针对潮位数据处理潮位站
     * @param planId
     */
    void deleteByPlanIdAndStcd(String planId,String stcd);
}