package com.essence.business.xqh.dao.dao.hsfxtk;

import com.essence.business.xqh.dao.entity.hsfxtk.YwkRiverRoughnessBasic;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkRiverRoughnessBasicDao extends EssenceJpaRepository<YwkRiverRoughnessBasic, String> {


    @Query(value = "select * from YWK_RIVER_ROUGHNESS_BASIC where RIVER_ROUGHNESSID in ?1 order by MILEAGE asc",nativeQuery = true)
    List<YwkRiverRoughnessBasic> findAllByIdsOrderByMileage(List ids);
}
