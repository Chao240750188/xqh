package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.StZvarlB;
import com.essence.business.xqh.dao.entity.fhybdd.YwkRainLevel;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkRainLevelDao extends EssenceJpaRepository<YwkRainLevel, String> {

    @Query(value = "select * from YWK_RAIN_LEVEL ORDER BY N_LEVEL",nativeQuery = true)
    List<YwkRainLevel> findAllByLevel();
}
