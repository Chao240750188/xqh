package com.essence.business.xqh.dao.dao.floodForecast;

import com.essence.business.xqh.dao.entity.floodForecast.SqybHriEvHr;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 库容曲线数据数据访问层接口
 */
@Repository
public interface SqybHifZvarlBDao extends EssenceJpaRepository<SqybHriEvHr,String> {

    /**
     * 根据水库ID和库容，查询最接近的一个水位
     * @param resCode
     * @param w
     * @return
     */
    @Query(value="select rz from sqyb_hif_zvarl_b where res_code = :resCode order by abs(:w-w) asc limit 1", nativeQuery=true)
    Double queryRzByResCodeAndW(@Param("resCode") String resCode, @Param("w") double w);
}
