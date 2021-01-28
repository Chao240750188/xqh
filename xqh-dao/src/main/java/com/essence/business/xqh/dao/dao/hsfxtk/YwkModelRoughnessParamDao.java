package com.essence.business.xqh.dao.dao.hsfxtk;


import com.essence.business.xqh.dao.entity.hsfxtk.YwkModelRoughnessParam;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkModelRoughnessParamDao extends EssenceJpaRepository<YwkModelRoughnessParam, String> {


    @Query(value = "select * from YWK_MODEL_ROUGHNESS_PARAM where IDMODEL_ID = ?1",nativeQuery = true)
    List<YwkModelRoughnessParam> findYwkModelRoughnessParamByModelId(String modelId);

}