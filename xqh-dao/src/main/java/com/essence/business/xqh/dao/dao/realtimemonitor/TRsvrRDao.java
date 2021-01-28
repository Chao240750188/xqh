package com.essence.business.xqh.dao.dao.realtimemonitor;

import com.essence.business.xqh.dao.entity.realtimemonitor.TRsvrR;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Stack
 * @version 1.0
 * @date 2020/5/25 0025 16:15
 */
@Repository
public interface TRsvrRDao extends EssenceJpaRepository<TRsvrR, String> {


    @Query(value = "select rn,stcd,RZ from \n" +
            "(select ROW_NUMBER() OVER(PARTITION BY stcd ORDER BY tm DESC) rn,stcd,RZ from ST_RSVR_R)\n" +
            " where rn = 1  order by stcd desc ",nativeQuery = true)
    List<Map<String,Object>> getRsvrLastData();

    List<TRsvrR> findByStcdAndTmBetweenOrderByTmDesc(String stcd, Date startDate, Date endDate);

    @Query(value = "select stcd ,max(RZ) as rz from ST_RSVR_R where tm between " +
            " :startTime  and :endTime  group by stcd  \n",nativeQuery = true)
    List<Map<String,Object>> getWaterLevelMaxByTime(@Param(value = "startTime") Date startTime, @Param(value = "endTime") Date endTime);


    List<TRsvrR> findByStcdInAndTmBetweenOrderByTmDesc(List<String> stcdList,Date startTime,Date endTime);


}
