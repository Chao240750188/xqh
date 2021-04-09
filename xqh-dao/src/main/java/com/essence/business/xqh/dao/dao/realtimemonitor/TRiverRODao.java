package com.essence.business.xqh.dao.dao.realtimemonitor;

import com.essence.business.xqh.dao.entity.realtimemonitor.TRiverR;
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
public interface TRiverRODao extends EssenceJpaRepository<TRiverR, String> {

    @Query(value = "select rn,stcd,tm,z,q from \n" +
            "(select ROW_NUMBER() OVER(PARTITION BY stcd ORDER BY tm DESC) rn,stcd,tm,z,q from ST_RIVER_R)\n" +
            " where rn = 1  order by stcd desc ", nativeQuery = true)
    List<Map<String,Object>> getRiverRLastData();

    List<TRiverR> findByStcdAndTmBetweenOrderByTmDesc(String stcd, Date startTime, Date endTime);

    @Query(value = "select stcd ,max(z) as z from ST_RIVER_R where tm between " +
            " :startTime  and :endTime  group by stcd  \n",nativeQuery = true)
    List<Map<String,Object>> getWaterLevelMaxByTime(@Param(value = "startTime") Date startTime,@Param(value = "endTime") Date endTime);


    @Query(value = "select stcd ,max(z) as z from ST_RIVER_R where stcd in ?1 and  tm between " +
            "  ?2  and ?3  group by stcd  \n",nativeQuery = true)
    List<Map<String,Object>> getWaterLevelMaxByTimeAndStcds( List<String> stcds,Date startTime, Date endTime);

    List<TRiverR> findByTmBetweenOrderByTmDesc(Date startTime, Date endTime);

    List<TRiverR> findByStcdInAndTmBetweenOrderByTmDesc(List<String> stcdList, Date startTime, Date endTime);

    /**
     * 查询最新的两条数据
     * @param stcdList
     * @param endTime
     * @return
     */
    @Query(value="SELECT * FROM (SELECT STCD,TM,Q FLOW,Z WATERLEVEL,row_number() over(partition by STCD ORDER BY TM DESC) rn FROM ST_RIVER_R WHERE STCD IN (?1) AND TM <= ?2) WHERE rn <3 ",nativeQuery=true)
    List<Map<String,Object>> findRiverLastData(List<String> stcdList,Date endTime);

    @Query(value = "SELECT a.* from ST_RIVER_R a INNER JOIN " +
            "(select t.STCD,MIN(TM) TM from ST_RIVER_R t where t.stcd in ?1 and t.tm >= ?2 " +
            "and t.tm <= ?3 GROUP BY STCD) b on a.STCD = b.STCD and a.TM = b.TM",nativeQuery = true)
    List<TRiverR> findMinDateByDateAndStcds(List<String>stcds,Date startTime,Date endTime);

    @Query(value = "SELECT a.* from ST_RIVER_R a INNER JOIN " +
            "(select t.STCD,MAX(TM) TM from ST_RIVER_R t where t.stcd in ?1 and t.tm >= ?2 " +
            "and t.tm <= ?3 GROUP BY STCD) b on a.STCD = b.STCD and a.TM = b.TM",nativeQuery = true)
    List<TRiverR> findMaxDateByDateAndStcds(List<String> collect, Date startTime, Date endTime);


    @Query(value = "select stcd ,min(z) as z from ST_RIVER_R where tm between " +
            " ?2  and ?3 and STCD in ?1 group by stcd  \n",nativeQuery = true)
    List<Map<String,Object>> getWaterLevelMinByTime(List<String> collect, Date startTime, Date endTime);

    @Query(value = "SELECT a.* from ST_RIVER_R a INNER JOIN (select t.STCD,Max(t.Z) Z from " +
            "ST_RIVER_R t where t.stcd in ?1 and t.tm >= " +
            "?2 and t.tm <= ?3 GROUP BY STCD) b " +
            "on a.STCD = b.STCD and a.Z = b.Z where a.tm >= ?2 and a.tm <= ?3",nativeQuery = true)
    List<TRiverR> getWaterMaxByTime(List<String> collect,Date startTime,Date endTime);

    @Query(value = "\n" +
            "select d.STCD stcd,AVG(d.value)value from (SELECT t.STCD stcd,TO_CHAR(t.tm,'yyyy') time,AVG(t.Z) value " +
            "from ST_RIVER_R t where t.stcd in ?1 and " +
            "TO_DATE(TO_CHAR(t.tm,'mm-dd HH24:mi:ss'),'mm-dd HH24:mi:ss') >= to_date(?2,'mm-dd HH24:mi:ss') and " +
            "TO_DATE(TO_CHAR(t.tm,'mm-dd HH24:mi:ss'),'mm-dd HH24:mi:ss') <= to_date(?3,'mm-dd HH24:mi:ss') GROUP BY TO_CHAR(t.tm,'yyyy'),t.STCD " +
            "ORDER BY time desc)d GROUP BY d.STCD",nativeQuery = true)
    List<Map<String,Object>> getHistoryAvgValueByTime(List<String> collect,String startTime,String endTime);

    @Query(value = "\n" +
            "select d.STCD stcd,AVG(d.z)z,AVG(d.q) q from (SELECT t.STCD stcd,TO_CHAR(t.tm,'yyyy') time,AVG(t.Z) z,AVG(t.Q) q " +
            "from ST_RIVER_R t where  " +
            "TO_DATE(TO_CHAR(t.tm,'mm-dd HH24:mi:ss'),'mm-dd HH24:mi:ss') >= to_date(?1,'mm-dd HH24:mi:ss') and " +
            "TO_DATE(TO_CHAR(t.tm,'mm-dd HH24:mi:ss'),'mm-dd HH24:mi:ss') <= to_date(?2,'mm-dd HH24:mi:ss') GROUP BY TO_CHAR(t.tm,'yyyy'),t.STCD " +
            "ORDER BY time desc)d GROUP BY d.STCD",nativeQuery = true)
    List<Map<String,Object>> getHistoryAvgValueByTimeNew(String startTime,String endTime);

    @Query(value = "select stcd ,AVG(z) as value from ST_RIVER_R where tm between " +
            " ?2  and ?3 and STCD in ?1 group by stcd  \n",nativeQuery = true)
    List<Map<String,Object>> getAvgValueByTime(List<String> collect,Date startTime,Date endTime);


    @Query(value = "select stcd ,AVG(z) as z,AVG(q) as q from ST_RIVER_R where tm between " +
            " ?1  and ?2  group by stcd  \n",nativeQuery = true)
    List<Map<String,Object>> getAvgValueByTimeNew(Date startTime,Date endTime);

    @Query(value = "SELECT a.* from ST_RIVER_R a INNER JOIN (select t.STCD,MAX (t.Q) Q from " +
            "ST_RIVER_R t where t.stcd in ?1 and t.tm >= " +
            "?2 and t.tm <= ?3 GROUP BY STCD) b " +
            "on a.STCD = b.STCD and a.Q = b.Q where a.tm >= ?2 and a.tm <= ?3",nativeQuery = true)
    List<TRiverR> getMaxQValueByTime(List<String> collect,Date startTime,Date endTime);
}
