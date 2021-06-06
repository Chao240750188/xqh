package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.YwkPlanInputZ;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface YwkPlanInputZDao extends EssenceJpaRepository<YwkPlanInputZ,String>{

    void deleteByNPlanid(String getnPlanid);

    YwkPlanInputZ findByNPlanid(String planId);
}
