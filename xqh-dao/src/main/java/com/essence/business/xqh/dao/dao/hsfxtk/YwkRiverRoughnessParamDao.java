package com.essence.business.xqh.dao.dao.hsfxtk;

import com.essence.business.xqh.dao.entity.hsfxtk.YwkRiverRoughnessParam;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkRiverRoughnessParamDao extends EssenceJpaRepository<YwkRiverRoughnessParam, String> {


    @Query(value = "select * from YWK_RIVER_ROUGHNESS_PARAM where ROUGHNESS_PARAMID =?1 order by MILEAGE asc ",nativeQuery = true)
    List<YwkRiverRoughnessParam> findsByRoughnessParamId(String paramId);


    void deleteByRoughnessParamid(String RoughnessParamid);
}