package com.essence.business.xqh.dao.dao.floodForecast;

import com.essence.business.xqh.dao.entity.floodForecast.SqybModelLoopRun;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 水库计算模型
 * @author LiuGt
 *
 * 2020年04月27日 15:03:16
 */
@Repository
public interface SqybModelLoopRunDao extends EssenceJpaRepository<SqybModelLoopRun,String> {

    /**
     * 查询新最的方案模型运行条件数据
     * @return
     */
    @Query(value = "select * from sqyb_model_loop_run order by modi_time desc limit 1", nativeQuery = true)
    SqybModelLoopRun queryNewModelLoopRun();
}
