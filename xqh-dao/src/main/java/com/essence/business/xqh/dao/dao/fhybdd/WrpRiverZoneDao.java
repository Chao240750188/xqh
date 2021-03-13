package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.WrpRiverZone;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WrpRiverZoneDao extends EssenceJpaRepository<WrpRiverZone,String> {

    List<WrpRiverZone> findByRvcd(String riverId);
}
