package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.YwkPlanCalibrationDwx;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkPlanCalibrationDwxDao extends EssenceJpaRepository<YwkPlanCalibrationDwx,String> {

    List<YwkPlanCalibrationDwx> findByNPlanid(String calibrationId);

    void deleteByNPlanid(String getcId);
}
