package com.essence.business.xqh.dao.dao.hsfxtk;

import com.essence.business.xqh.dao.entity.hsfxtk.YwkRiverRoughnessParam;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkRiverRoughnessParamDao extends EssenceJpaRepository<YwkRiverRoughnessParam, String> {
    /**
     * 根据模型糙率参数查询河道糙率参数列表
     * @param roughnessParamid
     * @return
     */
    List<YwkRiverRoughnessParam> findByRoughnessParamid(String roughnessParamid);
}