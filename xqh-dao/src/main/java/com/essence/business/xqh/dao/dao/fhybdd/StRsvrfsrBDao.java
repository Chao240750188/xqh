package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.StRsvrfsrB;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StRsvrfsrBDao extends EssenceJpaRepository<StRsvrfsrB, String> {

    /**
     * 根据水库编码查询
     * @param stcd
     */
    StRsvrfsrB findByStcd(String stcd);
}
