package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.WrpRcsBsin;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface WrpRcsBsinDao  extends EssenceJpaRepository<WrpRcsBsin,String > {


    @Query(value = "select * from WRP_RCS_BSIN where RVCD=?1",nativeQuery = true)
    List<WrpRcsBsin> findListByRiverId(String riverId);

    @Query(value = "select * from WRP_RCS_BSIN where RVCD in ?1",nativeQuery = true)
    List<WrpRcsBsin> findListByRiverIds(List<String> riverIds);

    @Query(value = "select * from WRP_RCS_BSIN where RVCRCRSCCD in ?1",nativeQuery = true)
    List<WrpRcsBsin> findListByIds(List<String> rcsList);

}
