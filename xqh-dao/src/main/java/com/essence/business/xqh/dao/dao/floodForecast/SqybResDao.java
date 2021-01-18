package com.essence.business.xqh.dao.dao.floodForecast;

import com.essence.business.xqh.dao.entity.floodForecast.SqybRes;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 水库名称
 * @author NoBugNoCode
 *
 * 2019年10月25日 上午10:42:23
 */
@Repository
public interface SqybResDao extends EssenceJpaRepository<SqybRes,String> {

    /**
     * 根据resCode查询数据
     * @param resCode
     * @return
     */
    @Query(value="select * from SQYB_OBJ_RES where RES_CODE = :resCode and rownum = 1", nativeQuery=true)
    SqybRes queryByResCode(@Param("resCode") String resCode);
}
