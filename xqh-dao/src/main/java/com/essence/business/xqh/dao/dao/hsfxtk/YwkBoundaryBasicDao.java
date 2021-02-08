package com.essence.business.xqh.dao.dao.hsfxtk;


import com.essence.business.xqh.dao.entity.hsfxtk.YwkBoundaryBasic;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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


    @Query(value = "select a.STCD,a.MILEAGE,a.BOUNDARYNM," +
            "b.N_PLANID,b.ABSOLUTE_TIME,b.RELATIVE_TIME,b.Z,b.Q " +
            "from YWK_BOUNDARY_BASIC a LEFT JOIN " +
            "(SELECT * from YWK_PLANIN_FLOOD_BOUNDARY WHERE " +
            "N_PLANID = ?1) b on\n" +
            "a.STCD = b.STCD ORDER BY MILEAGE ASC ",nativeQuery = true)
    List<Map<String,Object>> findBoundaryByPlanId(String nPlanId);
}