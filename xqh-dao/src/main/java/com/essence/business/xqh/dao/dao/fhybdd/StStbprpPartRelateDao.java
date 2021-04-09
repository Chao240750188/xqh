package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.StStbprpPartRelate;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface StStbprpPartRelateDao extends EssenceJpaRepository<StStbprpPartRelate, String> {
    @Query(value = "select T.STCD,SUM(t.drp) DRP from ST_PPTN_R t where t.stcd in ?1 and t.tm >= ?2 and t.tm< ?3 GROUP BY T.STCD ORDER BY DRP DESC", nativeQuery = true)
    List<Map<String, Object>> getPartRainByTime(List<String> stcdList,Date startTime, Date endTime);



    @Query(value = "SELECT t.STCD stcd,TO_CHAR(t.tm,'yyyy') time,SUM(t.drp) value from ST_PPTN_R t where t.stcd in ?1 " +
            "and TO_DATE(TO_CHAR(t.tm,'mm-dd HH24:mi:ss'),'mm-dd HH24:mi:ss') >= to_date(?2,'mm-dd HH24:mi:ss') and " +
            "TO_DATE(TO_CHAR(t.tm,'mm-dd HH24:mi:ss'),'mm-dd HH24:mi:ss') < to_date(?3,'mm-dd HH24:mi:ss') GROUP BY TO_CHAR(t.tm,'yyyy'),t.STCD " +
            "ORDER BY time desc",nativeQuery = true)
    List<Map<String,Object>> getBeforeRainByTime(List<String> stcdList,String startTime,String endTime);


    @Query(value = "SELECT\n" +
            "\tST.STCD,\n" +
            "\tST.STNM,\n" +
            "\tST.RVNM,\n" +
            "\tD.TOTAL,\n" +
            "\tST.LGTD,\n" +
            "\tST.LTTD \n" +
            "FROM\n" +
            "\tST_STBPRP_B ST\n" +
            "\tINNER JOIN (\n" +
            "\tSELECT C.STCD,AVG(C.total)total FROM(\n" +
            "\tSELECT\n" +
            "\t\ta.STCD,\n" +
            "\t\tTO_CHAR(a.TM,'yyyy'),\n" +
            "\t\tSUM( a.DRP ) total \n" +
            "\tFROM\n" +
            "\t\t(\n" +
            "\t\tSELECT\n" +
            "\t\t\tB.STCD,\n" +
            "\t\t\tR.DRP,\n" +
            "\t\t\tR.TM \n" +
            "\t\tFROM\n" +
            "\t\t\tST_STBPRP_B B\n" +
            "\t\t\tINNER JOIN ( SELECT STCD FROM ST_STSMTASK_B WHERE PFL = 1 ) M ON B.STCD = M.STCD\n" +
            "\t\t\tLEFT JOIN ( SELECT STCD, DRP,TM FROM ST_PPTN_R WHERE TO_DATE(TO_CHAR(tm,'mm-dd HH24:mi:ss'),'mm-dd HH24:mi:ss') >= to_date(?1,'mm-dd HH24:mi:ss') and \n" +
            "\t\t\tTO_DATE(TO_CHAR(tm,'mm-dd HH24:mi:ss'),'mm-dd HH24:mi:ss') < to_date(?2,'mm-dd HH24:mi:ss') ) R ON R.STCD = B.STCD \n" +
            "\t\tWHERE\n" +
            "\t\t\tB.STCD LIKE %?3% \n" +
            "\t\t\tOR B.STNM LIKE %?3% \n" +
            "\t\t) a \n" +
            "\tGROUP BY\n" +
            "\t\ta.STCD, TO_CHAR(a.TM,'yyyy')\n" +
            "\t\t)C GROUP BY C.STCD\n" +
            "\t) D ON ST.STCD = D.STCD \n" +
            "ORDER BY\n" +
            "\tD.TOTAL DESC NULLS LAST",nativeQuery = true)
    List<Map<String,Object>> getBeforeHistoryRainByTime(String startTime,String endTime,String name);
}
