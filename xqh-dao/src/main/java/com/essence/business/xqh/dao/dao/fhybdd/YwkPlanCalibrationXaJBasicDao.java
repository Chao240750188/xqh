package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.YwkPlanCalibrationXaJBasic;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkPlanCalibrationXaJBasicDao extends EssenceJpaRepository<YwkPlanCalibrationXaJBasic,String> {

    YwkPlanCalibrationXaJBasic findByNPlanid(String planId);

    void deleteByNPlanid(String planId);
}
