package com.essence.business.xqh.dao.dao.hsfxtk;

import com.essence.business.xqh.dao.entity.hsfxtk.YwkCsnlRoughness;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkCsnlRoughnessDao extends EssenceJpaRepository<YwkCsnlRoughness, String> {

    void deleteByNPlanid(String getnPlanid);

    @Query(value = "select * from YWK_CSNL_ROUGHNESS where N_PLANID in ?1", nativeQuery = true)
    List<YwkCsnlRoughness> findByPlanIdList(List<String> planidList);
}
