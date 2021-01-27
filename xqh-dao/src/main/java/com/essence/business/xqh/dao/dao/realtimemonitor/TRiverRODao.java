package com.essence.business.xqh.dao.dao.realtimemonitor;

import com.essence.business.xqh.dao.entity.realtimemonitor.TRiverR;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
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
public interface TRiverRODao extends EssenceJpaRepository<TRiverR, String> {

    @Query(value = "select rn,stcd,tm,z,q from \n" +
            "(select ROW_NUMBER() OVER(PARTITION BY stcd ORDER BY tm DESC) rn,stcd,tm,z,q from ST_RIVER_R)\n" +
            " where rn = 1  order by stcd desc ", nativeQuery = true)
    List<Map<String,Object>> getRiverRLastData();

    List<TRiverR> findByStcdAndTmBetweenOrderByTmDesc(String stcd, Date startTime, Date endTime);

    List<TRiverR> findByStcdInAndTmBetweenOrderByTmDesc(List<String> stcdList, Date startTime, Date endTime);
}
