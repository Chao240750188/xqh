package com.essence.business.xqh.dao.dao.floodScheduling;

import com.essence.business.xqh.dao.entity.floodScheduling.SkddHifFeglatF;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 调度预报成果表数据访问层接口
 * LiuGt add at 2020-04-01
 */
@Repository
public interface SkddHifFeglatFDao extends EssenceJpaRepository<SkddHifFeglatF,String> {

    /**
     * 根据方案ID查询调度预报成果数据
     * @param planId
     * @return
     */
    @Query(value = "select * from SKDD_HIF_REGLAT_F where PLAN_ID = :planId order by MODI_TIME", nativeQuery = true)
    List<SkddHifFeglatF> queryListByPlanId(@Param("planId") String planId);

    /**
     * 根据方案ID查询该方案最后一次调度预报数据
     * @param planId
     * @return
     */
    @Query(value = "select * from SKDD_HIF_REGLAT_F where PLAN_ID = :planId order by YMDH desc limit 1", nativeQuery = true)
    SkddHifFeglatF queryLastOneByPlanId(@Param("planId") String planId);

    /**
     * 根据调度方案ID统计调度结果的关键数据
     * @param planId
     * @return
     */
    @Query(value = "select PLAN_ID as planId, ifnull(MAX(f.Z),0) as maxRz, ifnull(MAX(f.W),0) as maxRzW, ifnull(MAX(f.INQ),0) as maxInq, ifnull(MAX(f.OTQ),0) as maxOtq, ifnull(SUM(f.INQ),0) as totalInq, ifnull(SUM(f.OTQ),0) as totalOtq, DATE_FORMAT(mi.maxInqTime,'%Y-%m-%d %H:%i:%s') as maxInqTime,DATE_FORMAT(mo.maxOtqTime,'%Y-%m-%d %H:%i:%s') as maxOtqTime from SKDD_HIF_REGLAT_F f inner join (select PLAN_ID as planId,YMDH as maxInqTime from SKDD_HIF_REGLAT_F where PLAN_ID = :planId order by INQ desc limit 1) mi on mi.planId = f.PLAN_ID inner join (select PLAN_ID as planId,YMDH as maxOtqTime from SKDD_HIF_REGLAT_F where PLAN_ID = :planId order by OTQ desc limit 1) mo on mo.planId = f.PLAN_ID where f.PLAN_ID = :planId", nativeQuery = true)
    Map<String,Object> queryResultStatisticsByPlanId(@Param("planId") String planId);
}
