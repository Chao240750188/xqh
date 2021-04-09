package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.StZvarlB;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StZvarlBDao extends EssenceJpaRepository<StZvarlB,String > {

    @Query(value = "select * from ST_ZVARL_B where STCD = ?1",nativeQuery = true)
    List<StZvarlB> getListByRsrId(String rsrId);

    @Query(value = "select * from ST_ZVARL_B ORDER BY PTNO",nativeQuery = true)
    List<StZvarlB> findAllByPtno();

    /**
     * 根据编码删除
     * @param rscd
     */
    void deleteByStcd(String rscd);
}
