package com.essence.business.xqh.dao.dao.floodForecast;

import com.essence.business.xqh.dao.entity.floodForecast.SqybModelInPutRainfall;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 水库名称
 * @author NoBugNoCode
 *
 * 2019年10月25日 上午10:42:23
 */
@Repository
public interface SqybModelInputRainfallDao extends EssenceJpaRepository<SqybModelInPutRainfall,String> {

	/**
	 * 根据方案查询降雨输入条件数据
	 * @param planId
	 */
	@Query("select t from SqybModelInPutRainfall t where t.planId = ?1 order by t.stcd,t.tm Asc")
	List<SqybModelInPutRainfall> findByPlanIdOrderByTm(String planId);

	/**
	 * 根据方案id删除输入条件
	 * @param planId
	 */
	void deleteByPlanId(String planId);

	/**
	 * 根据方案ID查询做为输入条件的降雨数据（各时段的平均降雨）
	 * @param planId
	 * @return
	 */
	@Query(value="select to_char(TM,'yyyy-mm-dd hh24:mi:ss') tm,SUM(P) totalP,count(0) stcdCount,nvl(sum(p)/count(0),0) avgP from SQYB_MODEL_INPUT_RAINFALL where PLAN_ID = :planId group by TM order by TM", nativeQuery=true)
	List<Map<String,Object>> queryAvgPByPlanId(@Param("planId") String planId);
}
