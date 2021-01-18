package com.essence.business.xqh.dao.dao.floodScheduling;

import com.essence.business.xqh.dao.entity.floodScheduling.SkddObjRes;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 水库信息表数据访问层接口
 * LiuGt add at 2020-03-30
 */
@Repository
public interface SkddObjResDao extends EssenceJpaRepository<SkddObjRes,String> {

    /**
     * 根据水库ID查询一个水库的信息
     * LiuGt add at 2020-03-30
     * @param resCode
     * @return
     */
    @Query(value = "select * from SKDD_OBJ_RES where RES_CODE = :resCode limit 1", nativeQuery = true)
    SkddObjRes queryByResCode(@Param("resCode") String resCode);

    /**
     * 查询展示的水库列表
     * @return
     */
    @Query(value = "select RES_CODE as resCode,HT_GUID as htGuid,RES_NAME as resName from SKDD_OBJ_RES where SHOW_STATUS = 1", nativeQuery = true)
    List<Map<String, Object>> queryByShow();
}
