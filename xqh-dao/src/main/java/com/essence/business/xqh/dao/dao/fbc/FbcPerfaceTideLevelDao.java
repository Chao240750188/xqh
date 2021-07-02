package com.essence.business.xqh.dao.dao.fbc;

import com.essence.business.xqh.dao.entity.fbc.FbcPerfaceTideLevel;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FbcPerfaceTideLevelDao extends EssenceJpaRepository<FbcPerfaceTideLevel, String> {
    List<FbcPerfaceTideLevel> findByNPlanidOrderByAbsoluteTime(String planId);

    void deleteByNPlanid(String planId);
}
