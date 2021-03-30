package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.business.xqh.dao.entity.fhybdd.YwkRainWaterReport;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkRainWaterReportDao extends EssenceJpaRepository<YwkRainWaterReport, String> {

    /**
     * 根据简报类型和年份查询
     * @param year
     * @param type
     * @return
     */
    List<YwkRainWaterReport> findByYearAndReportTypeOrderBySerialNumberDesc(int year, String type);

    /**
     * 根据id查询
     */
    @Query(value="select * from YWK_RAIN_WATER_REPORT  where  C_ID = ?1",nativeQuery = true)
    YwkRainWaterReport findOneById(String id);
}
