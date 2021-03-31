package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.YwkRainReportData;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkRainReportDataDao extends EssenceJpaRepository<YwkRainReportData, String> {

    /**
     * 根据简报id查询
     *
     * @param reportId
     */
    List<YwkRainReportData> findByReportIdOrderByDrpDesc(String reportId);

    /**
     * 根据简报id删除
     *
     * @param reportId
     */
    @Modifying
    @Query("delete from YwkRainReportData where reportId = ?1")
    void delByReport(String reportId);
}
