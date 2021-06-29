package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.WrpRcsSwyb;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WrpRcsSwybDao extends EssenceJpaRepository<WrpRcsSwyb,String > {

    @Query(value = "select * from WRP_RCS_SWYB where RVCD = ?1",nativeQuery = true)
    List<WrpRcsSwyb> findListByRvcd(String rvcd);

}
