package com.essence.business.xqh.dao.dao.rainfall;

import com.essence.business.xqh.dao.entity.rainfall.YwkReportDataFhfx;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkReportDataFhfxDao  extends EssenceJpaRepository<YwkReportDataFhfx, String> {

    void deleteByCReportId(String id);

    List<YwkReportDataFhfx> findByCReportId(String reportId);
}
