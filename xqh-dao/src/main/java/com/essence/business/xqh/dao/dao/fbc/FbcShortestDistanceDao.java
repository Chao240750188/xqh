package com.essence.business.xqh.dao.dao.fbc;

import com.essence.business.xqh.dao.entity.fbc.FbcShortestDistance;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FbcShortestDistanceDao extends EssenceJpaRepository<FbcShortestDistance, String> {
    List<FbcShortestDistance> findByNPlanidOrderByAbsoluteTime(String planId);

    void deleteByNPlanid(String planId);
}
