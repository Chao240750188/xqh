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
    @Query(value = "select T.STCD,SUM(t.drp) DRP from ST_PPTN_R t where t.stcd in ?1 and t.tm >= ?2 and t.tm< ?3 GROUP BY T.STCD", nativeQuery = true)
    List<Map<String, Object>> getPartRainByTime(List<String> stcdList,Date startTime, Date endTime);
}
