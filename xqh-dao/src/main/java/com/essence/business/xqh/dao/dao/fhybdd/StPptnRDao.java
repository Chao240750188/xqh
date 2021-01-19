package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.StPptnR;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface StPptnRDao extends EssenceJpaRepository<StPptnR,String > {


    @Query(value = "select TO_CHAR(TM,'yyyy-mm-dd hh24') tm ,STCD," +
            "NVL(SUM(NVL(DRP,0)), 0)sum from ST_PPTN_R where " +
            "TM BETWEEN to_date(?1,'yyyy-mm-dd hh24:mi:ss')  " +
            "and to_date(?2,'yyyy-mm-dd hh24:mi:ss') \n" +
            "\n" +
            "GROUP BY TO_CHAR(TM,'yyyy-mm-dd hh24'),STCD ORDER BY tm asc",nativeQuery = true)
    public List<Map<String,Object>> findStPptnRByStartTimeAndEndTime(Date startTime, Date endTIme);

}
