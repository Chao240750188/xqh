package com.essence.business.xqh.dao.dao.realtimemonitor;

import com.essence.business.xqh.dao.entity.realtimemonitor.TRvfcchB;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Stack
 * @version 1.0
 * @date 2020/5/25 0025 16:15
 */
@Repository
public interface TRvfcchBDao extends EssenceJpaRepository<TRvfcchB, String> {

    TRvfcchB findByStcd(String stcd);

}
