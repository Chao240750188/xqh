package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.dao.rainfall.dto.THdmisTotalRainfallDto;
import com.essence.business.xqh.dao.entity.fhybdd.StPptnR;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StPptnRDao extends EssenceJpaRepository<StPptnR,String > {


    List<StPptnR> findByStcdOrderByTmDesc(String stcd);

    //    @Query(value = "select * from st_pptn_r where stcd = :stcd and (tm BETWEEN  :startTime and :endTime ) order by tm ", nativeQuery = true)
//    List<StPptnR> findByStcdAndTmBetweenOrderByTm(@Param("stcd") String stcd, @Param("startTime") Date startTime, @Param("endTime") Date endTime);
    List<StPptnR> findByStcdAndTmBetweenOrderByTm(String stcd, Date startTime, Date endTime);

    @Query(value = "select * from ST_PPTN_R where STCD = :stcd and (TM BETWEEN  :startTime and :endTime ) ", nativeQuery = true)
    List<StPptnR> findByStcdAndTmBetween(@Param("stcd") String stcd, @Param("startTime") Date startTime, @Param("endTime") Date endTime);

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
//    @Query(value = "select stcd,tm,drp from st_pptn_r where  tm >  :startTime and  tm <=  :endTime", nativeQuery = true)
    List<THdmisTotalRainfallDto> queryByTmBetween(Date startTime, Date endTime);


    List<THdmisTotalRainfallDto> queryByStcdInAndTmBetween(List<String> stcdList, Date startTime, Date endTime);

}
