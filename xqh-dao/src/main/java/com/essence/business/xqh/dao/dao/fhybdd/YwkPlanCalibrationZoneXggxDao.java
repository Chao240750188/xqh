package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.YwkPlanCalibrationZoneXggx;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkPlanCalibrationZoneXggxDao extends EssenceJpaRepository<YwkPlanCalibrationZoneXggx,String > {


    List<YwkPlanCalibrationZoneXggx> findByNPlanid(String nplanId);

    void deleteByNPlanid(String planId);

    @Query(value = "SELECT COUNT(C_ID) from YWK_PLAN_CALIBRATION_ZONE_XGGX WHERE N_PLANID=?1",nativeQuery = true)
    Integer countByNPlanId(String getnPlanid);
}
