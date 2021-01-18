package com.essence.business.xqh.dao.dao.floodForecast;

import com.essence.business.xqh.dao.entity.floodForecast.SqybHriEvHr;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 蒸发量小时数据数据访问层接口
 */
@Repository
public interface SqybHriEvHrDao extends EssenceJpaRepository<SqybHriEvHr,String> {

    /**
     * 根据测站ID，开始时间和结束时间查找蒸发量数据
     * @param stcdList
     * @param startTime
     * @param endTime
     * @return
     */
    List<SqybHriEvHr> findByStcdInAndTmBetween(List<String> stcdList, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计指定水库，指定时间段，各雨量站实测各小时雨量
     * @return
     */
    @Query(value = "select t.stcd,t.tm,sum(t.dre) as dre from (select pr.STCD as stcd,DATE_FORMAT(pr.TM,'%Y-%m-%d %H:00:00') as tm,pr.DRE as dre from sqyb_hri_ev_hr pr inner join sqyb_rel_st_res rsr on rsr.ST_CODE = pr.STCD where rsr.STTP = 'DD' and rsr.RES_CODE = :resCode and pr.TM BETWEEN :startTime and :endTime) t group by t.stcd,t.tm order by t.stcd,t.tm", nativeQuery = true)
    List<Map<String,Object>> queryHourEvByResCodeBetweenTwoTime(@Param("resCode") String resCode, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
