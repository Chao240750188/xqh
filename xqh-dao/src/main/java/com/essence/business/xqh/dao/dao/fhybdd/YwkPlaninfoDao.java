package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkPlaninfoDao extends EssenceJpaRepository<YwkPlaninfo,String > {

    /**
     * 根据id查询
     */
    @Query(value="select p from YwkPlaninfo p where p.nPlanid = ?1")
    YwkPlaninfo findOneById(String planId);

    /**
     * 根据方案名称和所属系统查询方案
     * @param planName
     * @return
     */
    List<YwkPlaninfo> findByCPlannameAndPlanSystem(String planName,String planSystem);

    /**
     * 根据所属系统查询方案列表
     * @param planSystem
     */
    List<YwkPlaninfo> findByPlanSystem(String planSystem);
}
