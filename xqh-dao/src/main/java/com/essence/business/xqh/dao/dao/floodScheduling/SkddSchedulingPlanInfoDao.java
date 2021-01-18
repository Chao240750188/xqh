package com.essence.business.xqh.dao.dao.floodScheduling;

import com.essence.business.xqh.dao.entity.floodScheduling.SkddSchedulingPlanInfo;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 水库调度方案表数据访问层接口
 * LiuGt add at 2020-03-31
 */
@Repository
public interface SkddSchedulingPlanInfoDao extends EssenceJpaRepository<SkddSchedulingPlanInfo,String> {

    @Modifying
    @Query(value = "update SKDD_SCHEDULING_PLAN_INFO set HISTORY_STATUS = 1 WHERE PLAN_ID = :planId", nativeQuery = true)
    int saveSchedulingResult(@Param("planId") String planId);

    /**
     * 根据方案ID查询一个调度方案信息
     * @param planId
     * @return
     */
    @Query(value = "select * from SKDD_SCHEDULING_PLAN_INFO where PLAN_ID = :planId limit 1", nativeQuery = true)
    SkddSchedulingPlanInfo queryByPlanId(@Param("planId") String planId);

    /**
     * 根据水库ID查询该水库正在进行计算中的调度方案个数
     * @param resCode
     * @return
     */
    @Query(value = "select count(1) as num from SKDD_SCHEDULING_PLAN_INFO where RES_CODE = :resCode and PLAN_STATUS = 0", nativeQuery = true)
    int queryInProgresCountByResCode(@Param("resCode") String resCode);

    /**
     * 查询未开始或正在计算中的调度方案信息
     * @return
     */
    @Query(value = "select * from SKDD_SCHEDULING_PLAN_INFO where PLAN_STATUS in (0,1)", nativeQuery = true)
    List<SkddSchedulingPlanInfo> queryNeedSchedulingPlan();

    /**
     * 根据方案ID更新方案计算状态
     * @param planStatus 状态值
     * @param planId 方案ID
     * @return
     */
    @Modifying
    @Query(value = "update SKDD_SCHEDULING_PLAN_INFO set PLAN_STATUS = :planStatus WHERE PLAN_ID = :planId", nativeQuery = true)
    int editPlanStatusByPlanId(@Param("planStatus") Integer planStatus, @Param("planId") String planId);
}
