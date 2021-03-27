package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.WrpWarningWaterLevel;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WrpWarningWaterLevelDao extends EssenceJpaRepository<WrpWarningWaterLevel,String > {


    @Query(value = "SELECT a.C_ID,a.RCS_ID,a.WARNING_WATER_LEVEL,b.RVCRCRSCNM,c.RVCD,c.RVNM FROM WRP_WARNING_WATER_LEVEL a INNER JOIN WRP_RCS_BSIN b on a.RCS_ID = b.RVCRCRSCCD INNER JOIN WRP_RVR_BSIN c on b.RVCD = c.RVCD ",nativeQuery = true)
    List<Map<String,Object>> getWarnIngWaterLevels();
}
