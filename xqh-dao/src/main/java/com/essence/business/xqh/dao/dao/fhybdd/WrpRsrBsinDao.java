package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.fhybdd.WrpRsrBsin;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WrpRsrBsinDao extends EssenceJpaRepository<WrpRsrBsin,String > {

    /**
     * 根据水库编码删除
     * @param stcd
     */
    void deleteByRscd(String stcd);
}
