package com.essence.business.xqh.dao.dao.hsfxtk;

import com.essence.business.xqh.dao.entity.hsfxtk.YwkMileageInfo;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface YwkMileageInfoDao extends EssenceJpaRepository<YwkMileageInfo, String> {

    @Query(value = "select * from YWK_MILEAGE_INFO where MILEAGE >= ?1 and MILEAGE <= ?2", nativeQuery = true)
    List<YwkMileageInfo> findByMileageBetween(Double startMileage, Double endMileage);
}
