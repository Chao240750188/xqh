package com.essence.business.xqh.dao.dao.floodScheduling;

import com.essence.business.xqh.dao.entity.floodScheduling.SkddSchedulingPlanDocument;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 调度管理文档表数据访问层接口
 * LiuGt add at 2020-03-30
 */
@Repository
public interface SkddSchedulingPlanDocumentDao extends EssenceJpaRepository<SkddSchedulingPlanDocument,String> {

    /**
     * 根据ID查询一个调度方案的信息
     * LiuGt add at 2020-03-30
     * @param id
     * @return
     */
    @Query(value = "select * from SKDD_SCHEDULING_PLAN_DOCUMENT where ID = :id limit 1", nativeQuery = true)
    SkddSchedulingPlanDocument queryById(@Param("id") String id);

    /**
     * 根据ID更新一个附件的路径
     * @param attachfilePath
     * @param id
     * @return
     */
    @Modifying
    @Query(value = "update SKDD_SCHEDULING_PLAN_DOCUMENT set ATTACHFILE_PATH = :attachfilePath where ID = :id", nativeQuery = true)
    int updateAttachfilePathById(@Param("attachfilePath") String attachfilePath, @Param("id") String id);

    /**
     * 根据水库ID查询最新一个调度方案的信息
     * LiuGt add at 2020-04-08
     * @param resCode
     * @return
     */
    @Query(value = "select * from SKDD_SCHEDULING_PLAN_DOCUMENT where RES_CODE = :resCode order by CREATE_TIME desc limit 1", nativeQuery = true)
    SkddSchedulingPlanDocument queryLastOneByResCode(@Param("resCode") String resCode);
}
