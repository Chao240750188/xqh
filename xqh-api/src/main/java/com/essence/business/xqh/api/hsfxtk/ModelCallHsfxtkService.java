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

}
