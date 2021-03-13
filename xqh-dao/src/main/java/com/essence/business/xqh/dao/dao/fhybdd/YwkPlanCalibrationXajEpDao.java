package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.YwkPlanCalibrationXajEp;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkPlanCalibrationXajEpDao extends EssenceJpaRepository<YwkPlanCalibrationXajEp,String> {

    void deleteByXajBasicId(String getcId);

    List<YwkPlanCalibrationXajEp> findByXajBasicId(String getcId);
}
