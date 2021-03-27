package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.YwkPlanCalibrationDwx;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkPlanCalibrationDwxDao extends EssenceJpaRepository<YwkPlanCalibrationDwx,String> {

    List<YwkPlanCalibrationDwx> findByNPlanid(String calibrationId);

    void deleteByNPlanid(String getcId);


    @Query(value = "SELECT COUNT(C_ID) from YWK_PLAN_CALIBRATION_DWX WHERE N_PLANID=?1",nativeQuery = true)
    Integer countByNPlanId(String planId);
}
