package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninRainfall;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface YwkPlaninRainfallDao extends EssenceJpaRepository<YwkPlaninRainfall,String > {

    //void deleteByNPlanid(String planId);
    @Modifying
    @Query(value = "delete  from YWK_PLANIN_RAINFALL where N_PLANID=?1",nativeQuery = true)
    void deleteByNPlanid(String planId);


    @Query(value = "select A.STCD,A.STNM,A.LGTD,A.LTTD,B.TM,B.DRP from ST_STBPRP_B A LEFT JOIN(\n" +
            "SELECT TO_CHAR(D_TIME,'yyyy-mm-dd hh24:mi') TM,C_STCD STCD ,N_DRP DRP FROM YWK_PLANIN_RAINFALL  " +
            "where D_TIME BETWEEN to_date(?1,'yyyy-mm-dd hh24:mi:ss')\n" +
            "and to_date(?2,'yyyy-mm-dd hh24:mi:ss') and N_PLANID = ?3\n" +
            ")B on A.STCD = B.STCD ORDER BY tm asc",nativeQuery = true)
    List<Map<String, Object>> findStPptnRWithSTCD(String startTimeStr, String endTimeStr, String planId);


    @Query(value = "SELECT count(C_ID)count FROM \"YWK_PLANIN_RAINFALL\" where N_PLANID = ?1",nativeQuery = true)
    Long countByPlanId(String planId);
    /**
     * 根据方案获取雨量列表
     * @param planId
     */
    List<YwkPlaninRainfall> findByNPlanid(String planId);

    @Query(value = "SELECT count(C_ID)count FROM \"YWK_PLANIN_RAINFALL\" where N_PLANID = ?1 and D_TIME >= ?2 and D_TIME <?3",nativeQuery = true)
    Long countByPlanIdWithTime(String getnPlanid, Date startTime, Date endTime);
}
