package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface YwkPlaninfoDao extends EssenceJpaRepository<YwkPlaninfo,String > {

    /**
     * 根据id查询
     */
    @Query(value="select p from YwkPlaninfo p where p.nPlanid = ?1")
    YwkPlaninfo findOneById(String planId);
}
