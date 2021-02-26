package com.essence.business.xqh.dao.dao.fbc;

import com.essence.business.xqh.dao.entity.fbc.FbcWindMaximumSpeed;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FbcWindMaximumSpeedDao extends EssenceJpaRepository<FbcWindMaximumSpeed, String> {

    /**
     * 根据方案id查询条件列表
     *
     * @param planId
     * @return
     */
    List<FbcWindMaximumSpeed> findByNPlanidOrderByAbsoluteTime(String planId);


    /**
     * 根据方案id删除边界条件信息
     * @param planId
     */
    void deleteByNPlanid(String planId);
}
