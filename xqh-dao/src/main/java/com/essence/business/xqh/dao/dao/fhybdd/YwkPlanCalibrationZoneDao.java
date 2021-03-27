package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.YwkPlanCalibrationZone;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkPlanCalibrationZoneDao extends EssenceJpaRepository<YwkPlanCalibrationZone,String> {

    @Query(value = "select * from YWK_PLAN_CALIBRATION_ZONE where ZONE_ID in ?1",nativeQuery = true)
    List<YwkPlanCalibrationZone> findByZoneIds(List<String> zoneIDs);

    void deleteByNPlanid(String nplanId);

    List<YwkPlanCalibrationZone> findByNPlanid(String nplanId);


    YwkPlanCalibrationZone findByNPlanidAndZoneId(String nplanId,String zoneIds);

    @Query(value = "SELECT COUNT(C_ID) from YWK_PLAN_CALIBRATION_ZONE WHERE N_PLANID=?1",nativeQuery = true)
    Integer countByNPlanId(String planId);


}
