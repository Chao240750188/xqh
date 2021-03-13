package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.YwkPlanCalibrationFlow;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkPlanCalibrationFlowDao extends EssenceJpaRepository<YwkPlanCalibrationFlow,String> {

    void deleteByCalibrationId(String getcId);

    List<YwkPlanCalibrationFlow> findByCalibrationId(String getcId);
}
