package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.YwkRainWaterReport;
import com.essence.business.xqh.dao.entity.fhybdd.YwkWaterReportData;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkWaterReportDataDao extends EssenceJpaRepository<YwkWaterReportData, String> {

    /**
     * 根据简报id查询水情数据
     * @param reportId
     * @return
     */
    List<YwkWaterReportData> findByReportId(String reportId);
}
