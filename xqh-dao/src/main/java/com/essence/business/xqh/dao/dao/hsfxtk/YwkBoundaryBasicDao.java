package com.essence.business.xqh.dao.dao.hsfxtk;


import com.essence.business.xqh.dao.entity.hsfxtk.YwkBoundaryBasic;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkBoundaryBasicDao extends EssenceJpaRepository<YwkBoundaryBasic, String> {

    /**
     * 根据边界编码查询边界条件数据
     * @param stcdList
     * @return
     */
    List<YwkBoundaryBasic> findByStcdInOrderByStcd(List<String> stcdList);

    /**
     * 根据边界编码查询边界条件数据
     * @param stcdList
     * @return
     */
    List<YwkBoundaryBasic> findByStcdInOrderByBoundaryType(List<String> stcdList);
}