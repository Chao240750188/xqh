package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.YwkPlanTriggerRcsFlow;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Iterator;
import java.util.List;

@Repository
public interface YwkPlanTriggerRcsFlowDao extends EssenceJpaRepository<YwkPlanTriggerRcsFlow,String> {

    @Query(value = "SELECT * from YWK_PLAN_TRIGGER_RCS_FLOW where  TRIGGER_RCS_ID in ?1 ORDER BY ABSOLUTE_TIME",nativeQuery = true)
    List<YwkPlanTriggerRcsFlow> findByTriggerRcsIdsOrderByTime(List<String> triggerRcssIds);

    List<YwkPlanTriggerRcsFlow> findByTriggerRcsId(String id);

    void deleteByTriggerRcsId(String id);

    @Query(value = "DELETE FROM YWK_PLAN_TRIGGER_RCS_FLOW WHERE TRIGGER_RCS_ID IN ?1",nativeQuery = true)
    void deleteByTriggerRcsIds(Iterable<String> ids);
}
