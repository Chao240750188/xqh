package com.essence.business.xqh.dao.dao.fbc;

import com.essence.business.xqh.dao.entity.fbc.FbcHdpHhtdzW;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FbcHdpHhtdzWDao extends EssenceJpaRepository<FbcHdpHhtdzW, String> {

    /**
     * 根据方案id查询条件列表
     *
     * @param planId
     * @return
     */
    List<FbcHdpHhtdzW> findByNPlanidOrderByAbsoluteTime(String planId);

    /**
     * 根据方案id删除
     * @param planId
     */
    void deleteByNPlanid(String planId);
}
