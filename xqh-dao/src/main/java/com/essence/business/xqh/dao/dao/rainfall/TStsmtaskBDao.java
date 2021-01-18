package com.essence.business.xqh.dao.dao.rainfall;


import com.essence.business.xqh.dao.entity.rainfall.TStsmtaskBOld;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Stack
 * @version 1.0
 * @date 2020/5/25 0025 16:16
 */
@Repository
public interface TStsmtaskBDao extends EssenceJpaRepository<TStsmtaskBOld, String> {
    List<TStsmtaskBOld> findByPfl(Long pfl);
    List<TStsmtaskBOld> findByZfl(Long pfl);


    @Modifying
    @Query(value = "delete from TStsmtaskBOld t where t.stcd=?1")
    public void deleteByStcd(String stcd);
}
