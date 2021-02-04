package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.StPdmmysqS;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author fengpp
 * 2021/2/4 20:11
 */
public interface StPdmmysqSDao extends EssenceJpaRepository<StPdmmysqS, String> {

    List<StPdmmysqS> findByStcdInAndYrAndMthAndPrdtp(List<String> stcdList, Integer year, Integer mth, Integer prdtp);


    @Query(value="SELECT a.STCD,a.YR,a.MNTH,a.PRDTP,a.TIME,a.ACCP FROM (SELECT s.*,to_date(CONCAT(s.YR, LPAD(s.MNTH, 2, '0')),'yyyyMM') TIME FROM ST_PDMMYSQ_S s)a WHERE a.STCD = ?1 AND a.TIME >= ?2 AND a.TIME <= ?3 AND a.PRDTP in (?4)",nativeQuery=true)
    List<Map<String, Object>> findDataByMonth(String stcd, Date startTime, Date endTime,List<Integer> prdtpList);
}
