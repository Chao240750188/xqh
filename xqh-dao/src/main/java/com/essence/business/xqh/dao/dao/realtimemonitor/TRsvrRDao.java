package com.essence.business.xqh.dao.dao.realtimemonitor;

import com.essence.business.xqh.dao.entity.realtimemonitor.TRiverR;
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

    List<TRsvrR> findByTmBetweenOrderByTmDesc(Date startTime, Date endTime);

    List<TRsvrR> findByTmBetweenOrderByRzDesc(Date startTime, Date endTime);


    List<TRsvrR> findByStcdInAndTmBetweenOrderByTmDesc(List<String> stcdList,Date startTime,Date endTime);


    /**
     * 查询最新的两条数据
     * @param stcdList
     * @param endTime
     * @return
     */
    @Query(value="SELECT * FROM (SELECT STCD,TM,RZ WATERLEVEL,INQ FLOW ,OTQ OUTFLOW,row_number() over(partition BY STCD ORDER BY TM DESC) rn FROM ST_RSVR_R  WHERE STCD IN (?1) AND TM <= ?2) WHERE rn<3",nativeQuery=true)
    List<Map<String,Object>> findReservoirLastData(List<String> stcdList,Date endTime);

    /**
     *
     * @param collect
     * @param startTime
     * @param endTime
     * @return
     */
    @Query(value = "SELECT a.* from ST_RSVR_R a INNER JOIN " +
            "(select t.STCD,MIN(TM) TM from ST_RSVR_R t where t.stcd in ?1 and t.tm >= ?2 " +
            "and t.tm <= ?3 GROUP BY STCD) b on a.STCD = b.STCD and a.TM = b.TM",nativeQuery = true)
    List<TRsvrR> findMinDateByDateAndStcds(List<String> collect, Date startTime, Date endTime);


    @Query(value = "SELECT a.* from ST_RSVR_R a INNER JOIN " +
            "(select t.STCD,MAX(TM) TM from ST_RSVR_R t where t.stcd in ?1 and t.tm >= ?2 " +
            "and t.tm <= ?3 GROUP BY STCD) b on a.STCD = b.STCD and a.TM = b.TM",nativeQuery = true)
    List<TRsvrR> findMaxDateByDateAndStcds(List<String> collect, Date startTime, Date endTime);


    @Query(value = "select stcd ,min(RZ) as rz from ST_RSVR_R where tm between " +
            " :startTime  and :endTime  group by stcd  \n",nativeQuery = true)
    List<Map<String,Object>> getWaterLevelMinByTime(@Param(value = "startTime") Date startTime, @Param(value = "endTime") Date endTime);

    @Query(value = "select stcd ,min(rz) as z from ST_RSVR_R where tm between " +
            " ?2  and ?3 and STCD in ?1 group by stcd  \n",nativeQuery = true)
    List<Map<String, Object>> getWaterLevelMinByTime(List<String> collect, Date startTime, Date endTime);

    @Query(value = "SELECT a.* from ST_RSVR_R a INNER JOIN (select t.STCD,Max(t.RZ) RZ from " +
            "ST_RSVR_R t where t.stcd in ?1 and t.tm >= " +
            "?2 and t.tm <= ?3 GROUP BY STCD) b " +
            "on a.STCD = b.STCD and a.RZ = b.RZ where a.tm >= ?2 and a.tm <= ?3",nativeQuery = true)
    List<TRsvrR> getWaterMaxByTime(List<String> collect, Date startTime, Date endTime);

    @Query(value = "select stcd ,AVG(rz) as value from ST_RSVR_R where tm between " +
            " ?2  and ?3 and STCD in ?1 group by stcd  \n",nativeQuery = true)
    List<Map<String, Object>> getAvgValueByTime(List<String> collect, Date startTime, Date endTime);

    @Query(value = "select stcd ,AVG(rz) as RZ,AVG(inq) as INQ from ST_RSVR_R where tm between " +
            " ?1  and ?2  group by stcd  \n",nativeQuery = true)
    List<Map<String, Object>> getAvgValueByTimeNew( Date startTime, Date endTime);

    @Query(value = "\tSELECT\n" +
            "\td.STCD stcd,\n" +
            "\tAVG( d.value ) value \n" +
            "FROM\n" +
            "\t(\n" +
            "\tSELECT\n" +
            "\t\tt.STCD stcd,\n" +
            "\t\tTO_CHAR( t.tm, 'yyyy' ) time,\n" +
            "\t\tAVG( t.RZ ) value \n" +
            "\tFROM\n" +
            "\t\tST_RSVR_R t \n" +
            "\tWHERE\n" +
            "\t t.STCD in ?1 and TO_CHAR( t.tm, 'mm-dd HH24:mi:ss') >= ?2 \n" +
            "\t\tAND  TO_CHAR( t.tm, 'mm-dd HH24:mi:ss' ) \n" +
            "\t\t<= ?3 \n" +
            "\tGROUP BY\n" +
            "\t\tTO_CHAR( t.tm, 'yyyy' ),\n" +
            "\t\tt.STCD \n" +
            "\tORDER BY\n" +
            "\t\ttime DESC \n" +
            "\t) d \n" +
            "GROUP BY\n" +
            "\td.STCD\n" +
            "\t",nativeQuery = true)
    List<Map<String, Object>> getHistoryAvgValueByTime(List<String> collect, String startStr, String endStr);



    @Query(value = "\tSELECT\n" +
            "\td.STCD stcd,\n" +
            "\tAVG( d.rz ) RZ,AVG( d.inq) INQ \n" +
            "FROM\n" +
            "\t(\n" +
            "\tSELECT\n" +
            "\t\tt.STCD stcd,\n" +
            "\t\tTO_CHAR( t.tm, 'yyyy' ) time,\n" +
            "\t\tAVG( t.RZ ) rz,AVG (t.INQ) inq \n" +
            "\tFROM\n" +
            "\t\tST_RSVR_R t \n" +
            "\tWHERE\n" +
            "\t TO_CHAR( t.tm, 'mm-dd HH24:mi:ss') >= ?1 \n" +
            "\t\tAND  TO_CHAR( t.tm, 'mm-dd HH24:mi:ss' ) \n" +
            "\t\t<= ?2 \n" +
            "\tGROUP BY\n" +
            "\t\tTO_CHAR( t.tm, 'yyyy' ),\n" +
            "\t\tt.STCD \n" +
            "\tORDER BY\n" +
            "\t\ttime DESC \n" +
            "\t) d \n" +
            "GROUP BY\n" +
            "\td.STCD\n" +
            "\t",nativeQuery = true)
    List<Map<String, Object>> getHistoryAvgValueByTimeNew( String startStr, String endStr);



    @Query(value = "SELECT a.* from ST_RSVR_R a INNER JOIN (select t.STCD,MAX (t.INQ) INQ from " +
            "ST_RSVR_R t where t.stcd in ?1 and t.tm >= " +
            "?2 and t.tm <= ?3 GROUP BY STCD) b " +
            "on a.STCD = b.STCD and a.INQ = b.INQ where a.tm >= ?2 and a.tm <= ?3",nativeQuery = true)
    List<TRsvrR>  getMaxQValueByTime(List<String> collect, Date startTime, Date endTime);


    @Query(value = "select stcd ,max(RZ) as rz from ST_RSVR_R where stcd in ?1 and tm between " +
            " ?2  and ?3  group by stcd  \n",nativeQuery = true)
    List<Map<String,Object>> getWaterLevelMaxByTimeAndStcds(List<String>stcds, Date startTime, Date endTime);
}
