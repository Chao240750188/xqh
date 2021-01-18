package com.essence.business.xqh.dao.dao.floodForecast;

import com.essence.business.xqh.dao.entity.floodForecast.SqybRainPattern;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SqybRainPatternDao extends EssenceJpaRepository<SqybRainPattern,Long> {

	/**
	 * 根据雨型时段和类型查询
	 * @param time
	 * @param type
	 * @return
	 */
	@Query(value = "select * from SQYB_T_RAIN_PATTERN where C_TIME = :time and C_TYPE = :type order by C_HOUR", nativeQuery = true)
	List<SqybRainPattern> findByTimeAndType(@Param("time") Integer time, @Param("type") String type);
}
