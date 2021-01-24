package com.essence.business.xqh.dao.dao.realtimemonitor;

import com.essence.business.xqh.dao.entity.realtimemonitor.TRsvrfsrB;
import com.essence.business.xqh.dao.entity.realtimemonitor.TRvfcchB;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Stack
 * @version 1.0
 * @date 2020/5/25 0025 16:15
 */
@Repository
public interface TRsvrfsrBDao extends EssenceJpaRepository<TRsvrfsrB, String> {

    /**
     * 按照汛险类别查询数据
     * @param fstp
     * @return
     */
    List<TRsvrfsrB> findByFstp(String fstp);

}
