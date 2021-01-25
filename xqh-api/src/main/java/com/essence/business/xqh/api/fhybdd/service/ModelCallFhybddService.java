package com.essence.business.xqh.api.fhybdd.service;

import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;
import com.essence.business.xqh.api.fhybdd.dto.WrpRcsBsinDto;
import com.essence.business.xqh.api.fhybdd.dto.WrpRvrBsinDto;
import com.essence.business.xqh.api.fhybdd.dto.YwkModelDto;

import java.util.List;
import java.util.Map;

public interface ModelCallFhybddService {

    /**
     * 获取某段时间的降雨量，然后调用水文调度模型
     * @param planId
     */
    Object callMode(String planId);

    /**
     * 方案计划存入到缓存里
     * @param vo
     * @return
     */
    String savePlanWithCache(ModelCallBySWDDVo vo);


    /**
     * 根据方案id获取雨量信息
     * @param planId
     * @return
     */
    List<Map<String, Object>> getRainfalls(String planId);

    /**
     * 获取水文模型输出结果存入数据库
     * @param planId
     */
    int saveModwlResultToDataBase(String planId);

    /**
     * 根据水文模型输出文件配置水库调度模型参数条件
     * @param planId
     */
    void makeSwModelToSkdd(String planId);

    /**
     * 获取河系列表信息
     * @return
     */
    List<WrpRvrBsinDto> getRiverInfos();


    /**
     * 获取模型列表信息
     * @return
     */
    List<YwkModelDto> getModelInfos();

    /**
     * 根据河流id查询断面
     * @param planId
     * @return
     */
    List<WrpRcsBsinDto> getRcsByRiver(String planId);

    /**
     * 查询模型运行状态是否运行结束
     * @return
     */
    String getModelRunStatus(String planId);

    /**
     * 查看输出结果
     * @param planId
     * @return
     */
    Object getModelResultQ(String planId);
}
