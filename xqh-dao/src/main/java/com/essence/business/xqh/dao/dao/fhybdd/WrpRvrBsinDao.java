package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.WrpRvrBsin;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WrpRvrBsinDao extends EssenceJpaRepository<WrpRvrBsin,String > {


   /* @Query(value = "select * from WRP_RVR_BSIN where DWWTCD is null ",nativeQuery = true)
    List<WrpRvrBsin> findAllParentIdIsNull();*/

    @Query(value = "select * from WRP_RVR_BSIN where DWWTCD = ?1 ",nativeQuery = true)
    List<WrpRvrBsin> findAllByParentId(String parentId);
}
