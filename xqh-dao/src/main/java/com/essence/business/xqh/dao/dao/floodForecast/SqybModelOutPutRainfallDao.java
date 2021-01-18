package com.essence.business.xqh.dao.dao.floodForecast;

import com.essence.business.xqh.dao.entity.floodForecast.SqybModelOutPutRainfall;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 水库名称
 * @author NoBugNoCode
 *
 * 2019年10月25日 上午10:42:23
 */
@Repository
public interface SqybModelOutPutRainfallDao extends EssenceJpaRepository<SqybModelOutPutRainfall,String> {

	void deleteByPlanId(String planId);

	List<SqybModelOutPutRainfall> findByPlanIdOrderByTmAsc(String planId);

	/**
	 * 根据多个方案ID进行分组（水库ID和模型ID）
	 * @param planIds
	 * @return
	 */
	@Query(value="select count(1) num from (select RES_CODE,MODEL_ID from SQYB_MODEL_PLANINFO where PLAN_ID in :planIds group by RES_CODE,MODEL_ID) t", nativeQuery=true)
	Integer queryCountGroupRescodeAndModelIdByPlanIds(@Param("planIds") List<String> planIds);

	/**
	 * 根据方案ID查询结果数据
	 * @param planIds
	 * @return
	 */
	@Query(value="select PLAN_ID,ID,to_char(TM,'yyyy-mm-dd hh24:mi:ss') TM,ROUND(Q,2) Q from SQYB_MODEL_OUTPUT_RAINFALL where PLAN_ID in :planIds order by PLAN_ID,TM", nativeQuery=true)
	List<SqybModelOutPutRainfall> queryByPlanIds(@Param("planIds") List<String> planIds);
}
