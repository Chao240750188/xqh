package com.essence.business.xqh.dao.dao.hsfxtk;

import com.essence.business.xqh.dao.entity.hsfxtk.YwkRiverRoughnessParam;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkRiverRoughnessParamDao extends EssenceJpaRepository<YwkRiverRoughnessParam, String> {
<<<<<<< HEAD
    /**
     * 根据模型糙率参数查询河道糙率参数列表
     * @param roughnessParamid
     * @return
     */
    List<YwkRiverRoughnessParam> findByRoughnessParamid(String roughnessParamid);
=======


    @Query(value = "select * from YWK_RIVER_ROUGHNESS_PARAM where ROUGHNESS_PARAMID =?1 order by MILEAGE asc ",nativeQuery = true)
    List<YwkRiverRoughnessParam> findsByRoughnessParamId(String paramId);


    void deleteByRoughnessParamid(String RoughnessParamid);
>>>>>>> ce33305276b9abef21c305f50a1c6fd24b1689d5
}