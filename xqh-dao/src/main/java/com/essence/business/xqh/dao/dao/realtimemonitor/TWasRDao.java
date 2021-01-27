package com.essence.business.xqh.dao.dao.realtimemonitor;

import com.essence.business.xqh.dao.entity.realtimemonitor.TWasR;
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
public interface TWasRDao extends EssenceJpaRepository<TWasR, String> {

    @Query(value = "select rn,stcd,UPZ from \n" +
            "(select ROW_NUMBER() OVER(PARTITION BY stcd ORDER BY tm DESC) rn,stcd,UPZ from ST_WAS_R)\n" +
            " where rn = 1  order by stcd desc ", nativeQuery = true)
    List<Map<String, Object>> getLastData();


    @Query(value = "select t from TWasR t where t.stcd in (?1) and t.tm >=?2 and t.tm <=?3 order by t.tm desc")
    List<TWasR> findByStcdAndTmBetweenAndOrderByTmDesc(List<String> stcdList, Date startTime, Date endTime);

}
