package com.essence.business.xqh.dao.dao.rainfall;

import com.essence.business.xqh.dao.entity.rainfall.YwkReportDataFhfxInfo;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkReportDataFhfxInfoDao  extends EssenceJpaRepository<YwkReportDataFhfxInfo, String> {
    void deleteByCReportIdAndCType(String id, String s);

    List<YwkReportDataFhfxInfo> findByCReportId(String reportId);

    void deleteByCReportId(String reportId);
}
