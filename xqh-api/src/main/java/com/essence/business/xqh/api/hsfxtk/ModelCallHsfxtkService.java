package com.essence.business.xqh.api.hsfxtk;

import com.essence.business.xqh.api.hsfxtk.dto.PlanInfoHsfxtkVo;

/**
 * 洪水风险调控模型相关业务层
 */
public interface ModelCallHsfxtkService {
    /**
     * 方案计创建入库
     *
     * @param vo
     * @return
     */
    String savePlanToDb(PlanInfoHsfxtkVo vo);

    /**
     * 保存网格执行过程入库
     * @param planId
     * @return
     */
    Integer saveGridProcessToDb(String planId);

    void test(String planId) throws Exception;

}
