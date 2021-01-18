package com.essence.business.xqh.dao.dao.floodForecast;

import com.essence.business.xqh.dao.entity.floodForecast.SqybStPptnHr;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 水库名称
 * @author NoBugNoCode
 *
 * 2019年10月25日 上午10:42:23
 */
@Repository
public interface SqybStPptnHrDao extends EssenceJpaRepository<SqybStPptnHr,String> {

	List<SqybStPptnHr> findByStcdInAndTmBetween(List<String> stcdList, Date startTime, Date endTime);

	/**
	 * 查询一个时间段内总降雨量大于等于指定值的雨量站ID
	 * @param startTime
	 * @param endTime
	 * @param minRainfall
	 * @return
	 */
	@Query(value = "select STCD,sum(DRP) as SRP from SQYB_ST_PPTN_HR where TM BETWEEN :startTime and :endTime group by STCD HAVING SRP >= :minRainfall", nativeQuery = true)
	List<Map<String,Object>> querySumDrpEgtValueBetweenTwoTime(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("minRainfall") BigDecimal minRainfall);

	/**
	 * 统计指定水库，指定时间段，各雨量站实测各小时雨量
	 * @return
	 */
	@Query(value = "select t.STCD,t.TM,sum(t.DRP) as DRP from (select PR.STCD as STCD,DATE_FORMAT(PR.TM,'%Y-%m-%d %H:00:00') as TM,PR.DRP as DRP from SQYB_ST_PPTN_HR PR inner join SQYB_REL_ST_RES RSR on RSR.ST_CODE = PR.STCD where RSR.STTP = 'PP' and RSR.RES_CODE = :resCode and PR.TM BETWEEN :startTime and :endTime) t group by t.STCD,t.TM order by t.STCD,t.TM", nativeQuery = true)
	List<Map<String,Object>> queryHourRainByResCodeBetweenTwoTime(@Param("resCode") String resCode, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
