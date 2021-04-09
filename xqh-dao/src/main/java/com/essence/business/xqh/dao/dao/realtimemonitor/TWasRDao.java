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


    List<TWasR> findByStcdInAndTmBetweenOrderByTmDesc(List<String> stcdList, Date startTime, Date endTime);

    /**
     * 最新两条数据
     * @param stcdList
     * @param endTime
     * @return
     */
    @Query(value="SELECT * FROM (SELECT STCD,TM,UPZ WATERLEVEL,TGTQ FLOW,row_number() over(partition BY STCD ORDER BY TM DESC) rn FROM ST_WAS_R WHERE STCD IN (?1) AND TM<=?2) WHERE rn<3",nativeQuery=true)
    List<Map<String,Object>> findSluiceLastData(List<String> stcdList,Date endTime);

    List<TWasR> findByStcdAndTmBetweenOrderByTmDesc(String stcd, Date startTime, Date endTime);

    @Query(value = "select stcd ,max(UPZ) as UPZ from ST_WAS_R where tm between " +
            " :startTime  and :endTime  group by stcd  \n",nativeQuery = true)
    List<Map<String, Object>> getWaterLevelMaxByTime(Date startTime, Date endTime);
}
