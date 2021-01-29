package com.essence.business.xqh.dao.dao.hsfxtk;

import com.essence.business.xqh.dao.entity.hsfxtk.YwkPlaninFloodBreak;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YwkPlaninFloodBreakDao extends EssenceJpaRepository<YwkPlaninFloodBreak, String> {
    /**
     * 根据方案id删除
     * @param getnPlanid
     */
    void deleteByNPlanid(String getnPlanid);
}
