package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.fhybdd.YwkModel;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkModelDao extends EssenceJpaRepository<YwkModel,String > {



    @Query(value = "select * from YWK_MODEL where MODEL_TYPE = ?1 order by IDMODEL_ID",nativeQuery = true)
    List<YwkModel> getYwkModelByModelType(String modelType);

}
