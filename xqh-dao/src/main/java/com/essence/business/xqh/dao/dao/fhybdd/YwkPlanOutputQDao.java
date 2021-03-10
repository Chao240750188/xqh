package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlanOutputQ;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkPlanOutputQDao extends EssenceJpaRepository<YwkPlanOutputQ,String > {

    void deleteByNPlanid(String planId);

    /**
     * 根据方案id获取流量数据
     * @param planId
     */
    List<YwkPlanOutputQ> findByNPlanid(String planId);
}
