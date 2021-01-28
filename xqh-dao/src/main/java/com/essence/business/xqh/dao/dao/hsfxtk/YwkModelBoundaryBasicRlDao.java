package com.essence.business.xqh.dao.dao.hsfxtk;


import com.essence.business.xqh.dao.entity.hsfxtk.YwkModelBoundaryBasicRl;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkModelBoundaryBasicRlDao extends EssenceJpaRepository<YwkModelBoundaryBasicRl, String> {

    /**
     * 查询关联模型边界条件
     * @param idmodelId
     * @return
     */
    List<YwkModelBoundaryBasicRl> findByIdmodelId(String idmodelId);
}