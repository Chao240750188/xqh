package com.essence.business.xqh.dao.dao.floodForecast;

import com.essence.business.xqh.dao.entity.floodForecast.SqybModelInputEvaporation;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * LiuGt add at 2020-03-19
 * 模型输入参数-蒸发量数据数据访问
 *
 * 2019年10月25日 上午10:42:23
 */
@Repository
public interface SqybModelInputEvaporationDao extends EssenceJpaRepository<SqybModelInputEvaporation,String> {

    /**
     * 根据方案查询蒸发量输入条件数据
     * @param planId
     */
    @Query("select t from SqybModelInputEvaporation t where t.planId = ?1 order by t.stcd,t.tm Asc")
    List<SqybModelInputEvaporation> findByPlanIdOrderByTm(String planId);

    /**
     * 根据方案id删除输入条件
     * @param planId
     */
    void deleteByPlanId(String planId);
}
