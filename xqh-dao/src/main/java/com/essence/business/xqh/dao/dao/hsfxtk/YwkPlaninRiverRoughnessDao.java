package com.essence.business.xqh.dao.dao.hsfxtk;


import com.essence.business.xqh.dao.entity.hsfxtk.YwkPlaninFloodRoughness;
import com.essence.business.xqh.dao.entity.hsfxtk.YwkPlaninRiverRoughness;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkPlaninRiverRoughnessDao extends EssenceJpaRepository<YwkPlaninRiverRoughness, String> {
    /**
     * 根据模型糙率-删除方案河道糙率设定
     * @param roughnessParamid
     */
    void deleteByPlanRoughnessId(String roughnessParamid);
}