package com.essence.business.xqh.dao.dao.hsfxtk;

import com.essence.business.xqh.dao.entity.hsfxtk.YwkBreakBasic;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkBreakBasicDao extends EssenceJpaRepository<YwkBreakBasic, String> {

    @Query(value = "select * from YWK_BREAK_BASIC where IDMODEL_ID =?1",nativeQuery = true)
    List<YwkBreakBasic> findsByModelId(String modelId);
}
