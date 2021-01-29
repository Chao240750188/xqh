package com.essence.business.xqh.api.hsfxtk;

import com.essence.business.xqh.api.hsfxtk.dto.ModelParamVo;
import com.essence.business.xqh.api.hsfxtk.dto.PlanInfoHsfxtkVo;

import java.util.List;

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

    /**
     * 防洪保护区设置获取模型列表
     * @return
     */
    List<Object> getModelList();

    /**
     * 根据模型获取河道糙率设置参数
     * @param modelId
     * @return
     */
    List<Object> getModelRiverRoughness(String modelId);

    /**
     * 保存模型参数设置
     * @param modelParamVo
     * @return
     */
    ModelParamVo saveModelRiverRoughness(ModelParamVo modelParamVo);

    /**
     * 查询方案边界条件列表数据
     * @param modelParamVo
     * @return
     */
    List<Object> getModelBoundaryBasic(ModelParamVo modelParamVo);

    /**
     * 根据方案名称校验方案是否存在
     * @param planName
     */
    Integer getPlanInfoByName(String planName);
}
