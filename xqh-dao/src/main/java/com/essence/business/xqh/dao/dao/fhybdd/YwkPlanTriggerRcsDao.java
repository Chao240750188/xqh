package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.YwkPlanTriggerRcs;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkPlanTriggerRcsDao extends EssenceJpaRepository<YwkPlanTriggerRcs,String> {


    List<YwkPlanTriggerRcs> findByNPlanid(String planId);

    YwkPlanTriggerRcs findByNPlanidAndRcsId(String planId, String rcsId);

    void deleteByNPlanidAndRcsId(String planId, String rcsId);
}
