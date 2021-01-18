package com.essence.business.xqh.dao.dao.floodForecast;

import com.essence.business.xqh.dao.entity.floodForecast.SqybModelPlanInfo;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SqybModelPlanInfoDao extends EssenceJpaRepository<SqybModelPlanInfo,String> {

	/**
	 * 根据方案id查询方案
	 * @param parseLong
	 * @return
	 */
	SqybModelPlanInfo findByPlanId(String parseLong);

	/**
	 * 根据方案id删除方案
	 * @param planId
	 */
	void deleteByPlanId(String planId);

	/**
	 * 查询数量（用于滚动计算）
	 * @param resCode
	 * @param modelId
	 * @param modiTime
	 * @return
	 */
	@Query(value = "select count(1) as num from SQYB_MODEL_PLANINFO where AUTORUN_SIGN = 1 and RES_CODE = :resCode AND MODEL_ID = :modelId and MODI_TIME > :modiTime", nativeQuery = true)
	Integer queryNumByRescodeAndModelIdAndTime(@Param("resCode") String resCode,@Param("modelId") String modelId,@Param("modiTime") LocalDateTime modiTime);

	/**
	 * 根据多个方案ID查询信息
	 * @param planIds
	 * @return
	 */
	@Query(value = "select * from SQYB_MODEL_PLANINFO where PLAN_ID in :planIds", nativeQuery = true)
	List<SqybModelPlanInfo> queryByPlanIds(@Param("planIds") List<String> planIds);

}
