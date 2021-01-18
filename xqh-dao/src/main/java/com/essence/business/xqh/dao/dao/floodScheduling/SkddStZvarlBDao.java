package com.essence.business.xqh.dao.dao.floodScheduling;

import com.essence.business.xqh.dao.entity.floodScheduling.SkddStZvarlB;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 库容曲线表数据访问层接口
 * LiuGt add at 2020-07-20
 */
@Repository
public interface SkddStZvarlBDao extends EssenceJpaRepository<SkddStZvarlB,String> {

    /**
     * 根据水库ID查询库容曲线数据
     * @param resCode 水库ID
     * @return
     */
    @Query(value = "select * from SKDD_ST_ZVARL_B where RES_CODE = :resCode ORDER BY PTNO;", nativeQuery = true)
    List<SkddStZvarlB> queryListByResCode(@Param("resCode") String resCode);

}
