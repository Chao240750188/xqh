package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.YwkWaterReportData;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkWaterReportDataDao extends EssenceJpaRepository<YwkWaterReportData, String> {

    /**
     * 根据简报id查询水情数据
     *
     * @param reportId
     * @return
     */
    List<YwkWaterReportData> findByReportId(String reportId);

    /**
     * 根据简报id删除
     *
     * @param reportId
     */
    @Modifying
    @Query("delete from YwkWaterReportData where reportId = ?1")
    void delByReport(String reportId);
}
