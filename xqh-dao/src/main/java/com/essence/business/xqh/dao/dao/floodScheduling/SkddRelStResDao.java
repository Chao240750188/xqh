package com.essence.business.xqh.dao.dao.floodScheduling;

import com.essence.business.xqh.dao.entity.floodScheduling.SkddRelStRes;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 水库与（水文）测站关系表数据访问层接口
 * LiuGt add at 2020-06-29
 */
@Repository
public interface SkddRelStResDao extends EssenceJpaRepository<SkddRelStRes,String> {

    /**
     * 根据水库ID查询站数据
     * @param resCode 水库ID
     * @param sttp 测站类型
     * @return
     */
    @Query(value = "select * from SKDD_REL_ST_RES where RES_CODE = :resCode and STTP = :sttp", nativeQuery = true)
    List<SkddRelStRes> queryListByResCodeAndSttp(@Param("resCode") String resCode, @Param("sttp") String sttp);
}
