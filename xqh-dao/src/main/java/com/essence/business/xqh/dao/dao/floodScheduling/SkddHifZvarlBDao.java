package com.essence.business.xqh.dao.dao.floodScheduling;

import com.essence.business.xqh.dao.entity.floodScheduling.SkddHifZvarlB;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 出库规则表数据访问层接口
 * LiuGt add at 2020-04-07
 */
@Repository
public interface SkddHifZvarlBDao extends EssenceJpaRepository<SkddHifZvarlB,String> {

    /**
     * 根据水库ID查询库容曲线数据
     * @param resCode 水库ID
     * @return
     */
    @Query(value = "select * from SKDD_HIF_ZVARL_B where RES_CODE = :resCode ORDER BY PTNO;", nativeQuery = true)
    List<SkddHifZvarlB> queryListByResCode(@Param("resCode") String resCode);

    /**
     * 根据水库ID和水位查询最接近的库容量
     * @param resCode
     * @param rz
     * @return
     */
    @Query(value = "select W from SKDD_HIF_ZVARL_B where RES_CODE = :resCode order by ABS(RZ - :rz) limit 1", nativeQuery = true)
    BigDecimal queryWByResCodeAndRz(@Param("resCode") String resCode, @Param("rz") Double rz);

    /**
     * 根据水库ID和库容查询最接近的库水位
     * @param resCode
     * @param w
     * @return
     */
    @Query(value = "select RZ from SKDD_HIF_ZVARL_B where RES_CODE = :resCode order by ABS(W - :w) limit 1", nativeQuery = true)
    BigDecimal queryRzByResCodeAndW(@Param("resCode") String resCode, @Param("w") Double w);

    /**
     * 查询指定水库点序号最小的一条数据
     * @return
     */
    @Query(value = "select * from SKDD_HIF_ZVARL_B where RES_CODE = :resCode order by PTNO limit 1", nativeQuery = true)
    SkddHifZvarlB queryOneOrderByMinPtNo(@Param("resCode") String resCode);
}
