package com.essence.business.xqh.dao.dao.realtimemonitor;

import com.essence.business.xqh.dao.entity.realtimemonitor.TRvfcchB;
import com.essence.business.xqh.dao.entity.realtimemonitor.TTideR;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author Stack
 * @version 1.0
 * @date 2020/5/25 0025 16:15
 */
@Repository
public interface TTideRDao extends EssenceJpaRepository<TTideR, String> {

    @Query(value = "select rn,stcd,TDZ from \n" +
            "(select ROW_NUMBER() OVER(PARTITION BY stcd ORDER BY tm DESC) rn,stcd,TDZ from ST_TIDE_R)\n" +
            " where rn = 1  order by stcd desc ",nativeQuery = true)
    List<Map<String,Object>> getLastData();
}
