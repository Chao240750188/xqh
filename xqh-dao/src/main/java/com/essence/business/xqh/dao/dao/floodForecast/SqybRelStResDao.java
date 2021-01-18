package com.essence.business.xqh.dao.dao.floodForecast;

import com.essence.business.xqh.dao.entity.floodForecast.SqybRelStRes;
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
public interface SqybRelStResDao extends EssenceJpaRepository<SqybRelStRes,String> {

	/**
	 * 根据水库查询测站
	 * @param resCode
	 */
	List<SqybRelStRes> findByResCode(String resCode);

	/**
	 * 根据多个雨量站ID查询排重后的水库ID
	 * @param stcds
	 * @return
	 */
	@Query(value = "select DISTINCT RES_CODE from SQYB_REL_ST_RES where STTP = 'PP' and ST_CODE in :stcds", nativeQuery = true)
	List<String> queryDistinctResCodeByStcds(@Param("stcds") List<String> stcds);

}
