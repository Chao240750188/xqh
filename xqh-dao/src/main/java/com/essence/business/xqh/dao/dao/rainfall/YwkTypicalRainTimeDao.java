package com.essence.business.xqh.dao.dao.rainfall;

import com.essence.business.xqh.dao.entity.rainfall.YwkTypicalRainTime;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface YwkTypicalRainTimeDao extends EssenceJpaRepository<YwkTypicalRainTime, String> {

    List<YwkTypicalRainTime> findByCRainName(String cRainName);

    @Query(value = "select * from YWK_TYPICAL_RAIN_TIME where C_RAIN_NAME like %?1% order by D_END_TIME desc ", nativeQuery = true)
    List<YwkTypicalRainTime> findByCRainNameLikeOrderByDEndTimeDesc(String cRainName);

    List<YwkTypicalRainTime> findByCRainNameContains(String cRainName);
}
