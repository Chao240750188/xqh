package com.essence.business.xqh.api.floodScheduling.service;

import com.essence.business.xqh.api.floodScheduling.dto.SaveSchedulingPlanParamDto;
import com.essence.business.xqh.api.floodScheduling.dto.SchedulingPlanPageListParamDto;
import com.essence.business.xqh.api.floodScheduling.dto.SkddSchedulingPlanInfoDto;
import com.essence.framework.jpa.Paginator;

import java.util.List;

/**
 * 调度方案信息表服务接口
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/03/31
 */
public interface SchedulingPlanInfoService {

    /**
     * 分页查询调度方案列表
     * @param param
     * @return
     */
    Paginator<SkddSchedulingPlanInfoDto> getSchedulingPlanListPage(SchedulingPlanPageListParamDto param);

    /**
     * 保存调度方案结果
     * @param planId
     * @return
     */
    int saveSchedulingResult(String planId);

    /**
     * 保存水库调度信息
     * @param saveSchedulingPlanParamDto
     * @return
     */
    SkddSchedulingPlanInfoDto saveSchedulingPlan(SaveSchedulingPlanParamDto saveSchedulingPlanParamDto);

    /**
     * 根据水库ID查询正在计算中的调度方案个数
     * @param resCode
     * @return
     */
    int queryInProgresCountByResCode(String resCode);

    /**
     * 查询未开始或计算中的调度方案
     * @return
     */
    List<SkddSchedulingPlanInfoDto> queryNeedSchedulingPlan();

    /**
     * 根据方案ID更新方案计算状态
     * @param planStatus 状态值
     * @param planId 方案ID
     * @return
     */
    int editPlanStatusByPlanId(Integer planStatus, String planId);

    /**
     * 根据方案ID查询一个调度方案信息
     * @param planId
     * @return
     */
    SkddSchedulingPlanInfoDto queryByPlanId(String planId);
}
