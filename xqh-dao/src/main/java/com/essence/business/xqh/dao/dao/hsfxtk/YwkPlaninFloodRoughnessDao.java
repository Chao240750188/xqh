package com.essence.business.xqh.dao.dao.hsfxtk;


import com.essence.business.xqh.dao.entity.hsfxtk.YwkModelRoughnessParam;
import com.essence.business.xqh.dao.entity.hsfxtk.YwkPlaninFloodRoughness;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkPlaninFloodRoughnessDao extends EssenceJpaRepository<YwkPlaninFloodRoughness, String> {
    /**
     * 根据方案id查询模型糙率设置
     * @param planId
     * @return
     */
    List<YwkPlaninFloodRoughness> findByPlanId(String planId);

    /**
     * 根据planId删除方案糙率设定
     * @param planId
     */
    void deleteByPlanId(String planId);


}