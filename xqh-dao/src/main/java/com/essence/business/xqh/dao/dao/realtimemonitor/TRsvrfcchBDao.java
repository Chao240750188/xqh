package com.essence.business.xqh.dao.dao.realtimemonitor;

import com.essence.business.xqh.dao.entity.realtimemonitor.TRsvrfcchB;
import com.essence.business.xqh.dao.entity.realtimemonitor.TRvfcchB;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Stack
 * @version 1.0
 * @date 2020/5/25 0025 16:15
 */
@Repository
public interface TRsvrfcchBDao extends EssenceJpaRepository<TRsvrfcchB, String> {

    TRsvrfcchB findByStcd(String stcd);
}
