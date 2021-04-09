package com.essence.business.xqh.dao.dao.rainfall;

import com.essence.business.xqh.dao.entity.rainfall.YwkReportDataFhfxSk;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkReportDataFhfxSkDao  extends EssenceJpaRepository<YwkReportDataFhfxSk, String> {
    void deleteByCReportId(String id);

    List<YwkReportDataFhfxSk> findByCReportId(String reportId);
}
