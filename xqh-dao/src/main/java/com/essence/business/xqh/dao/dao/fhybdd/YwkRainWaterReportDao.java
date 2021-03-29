package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.YwkRainWaterReport;
import com.essence.framework.jpa.EssenceJpaRepository;
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
}
