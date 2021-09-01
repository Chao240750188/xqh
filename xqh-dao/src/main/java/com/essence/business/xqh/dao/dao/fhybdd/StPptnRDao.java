package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.dao.rainfall.dto.THdmisTotalRainfallDto;
import com.essence.business.xqh.dao.entity.fhybdd.StPptnR;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface StPptnRDao extends EssenceJpaRepository<StPptnR, String> {


    @Query(value = "select TO_CHAR(TM,'yyyy-mm-dd hh24') tm ,STCD," +
            "NVL(SUM(NVL(DRP,0)), 0)sum from ST_PPTN_R where " +
            "TM BETWEEN to_date(?1,'yyyy-mm-dd hh24:mi:ss')  " +
            "and to_date(?2,'yyyy-mm-dd hh24:mi:ss') \n" +
            "\n" +
            "GROUP BY TO_CHAR(TM,'yyyy-mm-dd hh24'),STCD ORDER BY tm asc", nativeQuery = true)
    public List<Map<String, Object>> findStPptnRByStartTimeAndEndTime(String startTime, String endTIme);


    @Query(value = "select A.STCD,A.STNM,A.LGTD,A.LTTD,B.TM,B.DRP from ST_STBPRP_B A LEFT JOIN(\n" +
            "SELECT TO_CHAR(TM,'yyyy-mm-dd hh24:mi') TM,STCD ,DRP FROM ST_PPTN_R  " +
            "where TM BETWEEN to_date(?1,'yyyy-mm-dd hh24:mi:ss')\n" +
            "and to_date(?2,'yyyy-mm-dd hh24:mi:ss')\n" +
            ")B on A.STCD = B.STCD ORDER BY tm asc",nativeQuery = true)
    public List<Map<String, Object>> findStPptnRWithSTCD(String startTime, String endTIme);

    List<StPptnR> findByStcdOrderByTmDesc(String stcd);

    //    @Query(value = "select * from st_pptn_r where stcd = :stcd and (tm BETWEEN  :startTime and :endTime ) order by tm ", nativeQuery = true)
//    List<StPptnR> findByStcdAndTmBetweenOrderByTm(@Param("stcd") String stcd, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
    List<StPptnR> findByStcdAndTmBetweenOrderByTm(String stcd, Date startTime, Date endTime);

//    @Query(value = "select * from ST_PPTN_R where STCD = :stcd and (TM BETWEEN  :startTime and :endTime ) ", nativeQuery = true)
//    List<StPptnR> findByStcdAndTmBetween(@Param("stcd") String stcd, @Param("startTime") Date startTime, @Param("endTime") Date endTime);


    @Query(value = "select * from ST_PPTN_R where STCD = :stcd and (TM BETWEEN  to_date ( :startTime , 'YYYY-MM-DD HH24:MI:SS' ) and   to_date ( :endTime , 'YYYY-MM-DD HH24:MI:SS' ) ) ", nativeQuery = true)
    List<StPptnR> getRainFallByTimeAndID(@Param("stcd") String stcd, @Param("startTime") String startTime, @Param("endTime") String endTime);

    List<StPptnR> findByStcdAndTmBetween(String stcd, Date startTime,  Date endTime);

    StPptnR findByStcdAndTm(String stcd, Date tm);


    List<StPptnR> findByTmBetweenOrderByTm(Date startTime, Date endTime);

    List<StPptnR> findByTmBetween(Date startTime, Date endTime);


//    @Query(value = "SELECT t.tm2,SUM(t.drp)/COUNT(DISTINCT stcd) AS num FROM (SELECT stcd ,tm , drp ,DATE_FORMAT(tm,'%Y-%m-%d %H') AS tm2 FROM st_pptn_r WHERE tm >= :startTime AND tm < :endTime ) AS t GROUP BY t.tm2 ", nativeQuery = true)
//    List<Map<String, Object>> getRainFallTrend(@Param(value = "startTime") String startTime, @Param(value = "endTime") String endTime);

    /**
     * @return java.util.List<java.util.Map < java.lang.Object, java.lang.Object>>
     * @Description 查询时段雨量，从开始日期到now
     * @Author xzc
     * @Date 14:05 2020/7/2
     **/
//    @Query(value = "select stcd,tm,drp  from st_pptn_r where  tm >  :beginTime  ", nativeQuery = true)
//    List<Map<String, Object>> queryRainfallByTimeAfter(@Param("beginTime") Date beginTime);
    List<THdmisTotalRainfallDto> queryByTmAfter(Date beginTime);

    /**
     * @return java.util.List<java.util.Map < java.lang.Object, java.lang.Object>>
     * @Description 查询时段雨量，从开始日期到结束日期
     * @Author xzc
     * @Date 14:05 2020/7/2
     **/
    @Query(value = "select stcd,TO_CHAR(TM,'yyyy-mm-dd hh24:mi:ss') tm ,drp from st_pptn_r where " +
            "TM BETWEEN to_date(?1,'yyyy-mm-dd hh24:mi:ss') and to_date(?2,'yyyy-mm-dd hh24:mi:ss')   ", nativeQuery = true)
    List<Map<String, Object>> queryByTmBetween(String startTime, String endTime);
//    List<Map<String, Object>> queryByTmBetween(Date startTime, Date endTime);

    @Query(value = "select stcd,tm ,drp from st_pptn_r where " +
            "TM BETWEEN ?1 and ?2   ", nativeQuery = true)
    List<Map<String, Object>> findByTime(Date startTime, Date endTime);


    List<THdmisTotalRainfallDto> queryByStcdInAndTmBetween(List<String> stcdList, Date startTime, Date endTime);


    @Query(value = "SELECT B.STCD,SUM(B.DRP) total FROM ST_PPTN_R B WHERE STCD IN(?1) AND TM >=?2 AND TM <=?3 GROUP BY STCD", nativeQuery = true)
    List<Map<String, Object>> findByStcdInAndTmBetween(List<String> stcdList, Date startTime, Date endTime);

    @Query(value = "SELECT B.STCD,B.DRP,B.TM FROM ST_PPTN_R B WHERE STCD= ?1 AND TM >=?2 AND TM <=?3 ", nativeQuery = true)
    List<Map<String, Object>> findDataByStcdAndTmBetween(String stcd, Date startTime, Date endTime);

    //todo 每隔30分钟
    @Query(value = " SELECT\n" +
            " \n" +
            " CASE \n" +
            "\tWHEN  LPAD( ( FLOOR( TO_CHAR( TM, 'mi' )  / 30 ) +1 ) * 30, 2, 0)=60 THEN\n" +
            "\t\tTO_CHAR(TM+1/24,'yyyy-mm-dd hh24') || ':00' \n" +
            "\tELSE\n" +
            "\t\tTO_CHAR( TM, 'yyyy-mm-dd hh24' )  || ':' || LPAD( ( FLOOR( TO_CHAR( TM, 'mi' )  / 30 ) +1 ) * 30, 2, 0)\n" +
            "END TM,\n" +
            "  STCD,\n" +
            " NVL(SUM( DRP ), 0) DRP \n" +
            "FROM\n" +
            " ST_PPTN_R \n" +
            "WHERE\n" +
            " TM BETWEEN to_date( ?1, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
            " AND to_date( ?2, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
            "GROUP BY\n" +
            "\n" +
            "CASE \n" +
            "\tWHEN  LPAD( ( FLOOR( TO_CHAR( TM, 'mi' )  / 30 ) +1 ) * 30, 2, 0)=60 THEN\n" +
            "\t\tTO_CHAR(TM+1/24,'yyyy-mm-dd hh24') || ':00' \n" +
            "\tELSE\n" +
            "\t\tTO_CHAR( TM, 'yyyy-mm-dd hh24' )  || ':' || LPAD( ( FLOOR( TO_CHAR( TM, 'mi' )  / 30 ) +1 ) * 30, 2, 0)\n" +
            "END , stcd ORDER BY TM ",nativeQuery = true)
    List<Map<String, Object>> findRainGroupStcdAndTimeWithMinutiue(String startTimeStr, String endTimeStr);

    @Query(value = "\n" +
            "\n" +
            "select STCD,TO_CHAR(tm+1/24,'yyyy-mm-dd hh24') ||':00' TM, NVL(SUM( DRP ), 0) DRP from ST_PPTN_R WHERE\n" +
            " TM BETWEEN to_date(?1, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
            " AND to_date(?2, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
            " GROUP BY TO_CHAR(tm+1/24,'yyyy-mm-dd hh24') ||':00',stcd order by TM\n",nativeQuery = true)
    List<Map<String,Object>> findRainGroupStcdAndTimeWithHour(String startTimeStr,String endTimeStr);

    @Query(value = "SELECT\n" +
            "\tSSA.TM TM,\n" +
            "\tSSA.DRP DRP,\n" +
            "\tSSB.STNM STNM \n" +
            "FROM\n" +
            "\t(\n" +
            "\tSELECT\n" +
            "\t\tTO_CHAR( tm + 1 / 24, 'yyyy-mm-dd hh24' ) || ':00' TM,\n" +
            "\t\tSTCD,\n" +
            "\t\tNVL( SUM( DRP ), 0 ) DRP \n" +
            "\tFROM\n" +
            "\t\tST_PPTN_R \n" +
            "\tWHERE\n" +
            "\t\tTM BETWEEN to_date( ?2, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
            "\t\tAND to_date( ?3, 'yyyy-mm-dd hh24:mi:ss' ) \n" +
            "\t\tAND STCD = ?1 \n" +
            "\tGROUP BY\n" +
            "\t\tTO_CHAR( tm + 1 / 24, 'yyyy-mm-dd hh24' ) || ':00',\n" +
            "\t\tSTCD \n" +
            "\tORDER BY\n" +
            "\t\tTM \n" +
            "\t) SSA\n" +
            "\tLEFT JOIN ST_STBPRP_B SSB ON SSA.STCD = SSB.STCD", nativeQuery = true)
    List<Map<String,Object>> findRainByStcdAndTimeBetween(String stcd, String startTimeStr,String endTimeStr);
}
