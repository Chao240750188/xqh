package com.essence.business.xqh.dao.dao.hsfxtk;


import com.essence.business.xqh.dao.entity.hsfxtk.YwkModelRoughnessParam;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkModelRoughnessParamDao extends EssenceJpaRepository<YwkModelRoughnessParam, String> {
    /**
     * 根据模型id查询糙率参数
     * @param modelId
     * @return
     */
    List<YwkModelRoughnessParam> findByIdmodelId(String modelId);

    /**
     * 根据id查询
     * @param roughnessParamid
     * @return
     */
    @Query(value = "select t from YwkModelRoughnessParam t where t.roughnessParamid=?1")
    public YwkModelRoughnessParam findOneById(String roughnessParamid);


    @Query(value = "select * from YWK_MODEL_ROUGHNESS_PARAM where IDMODEL_ID = ?1",nativeQuery = true)
    List<YwkModelRoughnessParam> findYwkModelRoughnessParamByModelId(String modelId);

}