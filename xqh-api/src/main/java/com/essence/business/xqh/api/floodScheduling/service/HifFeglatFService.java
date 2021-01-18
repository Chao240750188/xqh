package com.essence.business.xqh.api.floodScheduling.service;



import com.essence.business.xqh.api.floodScheduling.dto.ScheduingRainfallDto;
import com.essence.business.xqh.api.floodScheduling.dto.SchedulingResultStatisticsDto;
import com.essence.business.xqh.api.floodScheduling.dto.SkddHifFeglatFDto;

import java.util.List;

/**
 * 调度预报成果表服务接口
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/04/01
 */
public interface HifFeglatFService {

    /**
     * 根据方案ID查询调度预报成果数据
     * @param planId
     * @return
     */
    List<SkddHifFeglatFDto> queryListByPlanId(String planId);

    /**
     * 根据方案ID统计一次调度结果的统计数据
     * @param planId
     * @return
     */
    SchedulingResultStatisticsDto querySchedulingResultStatistics(String planId);

    /**
     * 根据方案ID统计一次调度过程的降雨数据
     * @param planId
     */
    List<ScheduingRainfallDto> getRainfallForOnecScheduling(String planId);

    /**
     * 根据方案ID查询该方案最后一次调度预报数据
     * @param planId
     * @return
     */
    SkddHifFeglatFDto queryLastOneByPlanId(String planId);

    /**
     * 添加一个调度预报数据
     * @param hifFeglatF
     * @return
     */
    SkddHifFeglatFDto add(SkddHifFeglatFDto hifFeglatF);
}
