package com.essence.business.xqh.api.floodScheduling.service;


import com.essence.business.xqh.api.floodScheduling.dto.SkddSchedulingOutRulesDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 调度方案出库规则表服务接口
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/04/09
 */
public interface SchedulingOutRulesService {

    /**
     * 根据方案ID查询调度规则数据
     * @param planId
     * @return
     */
    List<SkddSchedulingOutRulesDto> queryRulesByPlanId(String planId);

    /**
     * 根据方案ID和库水位，查询最接近的出库流量
     * @param planId
     * @param rz
     * @return
     */
    BigDecimal queryOtqByPlanIdAndRz(String planId, BigDecimal rz);
}
