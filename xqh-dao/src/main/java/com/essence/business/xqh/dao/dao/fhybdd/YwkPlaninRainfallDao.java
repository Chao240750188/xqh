package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninRainfall;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkPlaninRainfallDao extends EssenceJpaRepository<YwkPlaninRainfall,String > {

    void deleteByNPlanid(String planId);

    /**
     * 根据方案获取雨量列表
     * @param planId
     */
    List<YwkPlaninRainfall> findByNPlanid(String planId);
}
