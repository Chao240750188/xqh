package com.essence.business.xqh.dao.dao.floodScheduling;

import com.essence.business.xqh.dao.entity.floodScheduling.SkddSchedulingOutRules;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 水库调度规则数据访问层接口
 * LiuGt add at 2020-04-09
 */
@Repository
public interface SkddSchedulingOutRulesDao extends EssenceJpaRepository<SkddSchedulingOutRules,String> {

    /**
     * 根据方案ID查询调度规则数据
     * @param planId
     * @return
     */
    @Query(value = "select * from SKDD_SCHEDULING_OUT_RULES where PLAN_ID = :planId order by PTNO", nativeQuery = true)
    List<SkddSchedulingOutRules> queryRulesByPlanId(@Param("planId") String planId);

    /**
     * 根据方案ID和库水位查询最接近的出库流量
     * @param planId
     * @param rz
     * @return
     */
    @Query(value = "select OTQ from SKDD_SCHEDULING_OUT_RULES where PLAN_ID = :planId order by ABS(RZ - :rz) limit 1", nativeQuery = true)
    BigDecimal queryOtqByPlanIdAndRz(@Param("planId") String planId, @Param("rz") Double rz);
}
