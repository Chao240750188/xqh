package com.essence.business.xqh.dao.dao.hsfxtk;

import com.essence.business.xqh.dao.entity.hsfxtk.YwkModelRiverRoughnessRl;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkModelRiverRoughnessRlDao extends EssenceJpaRepository<YwkModelRiverRoughnessRl, String> {



    @Query(value = "select * from YWK_MODEL_RIVER_ROUGHNESS_RL where IDMODEL_ID=?1",nativeQuery = true)
    List<YwkModelRiverRoughnessRl> findByModelId(String modelId);

}

