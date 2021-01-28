package com.essence.business.xqh.api.hsfxtk;

import com.essence.business.xqh.api.fhybdd.dto.YwkModelDto;
import com.essence.business.xqh.api.hsfxtk.dto.YwkModelRoughnessParamDto;
import com.essence.business.xqh.api.hsfxtk.dto.YwkParamVo;
import com.essence.business.xqh.api.hsfxtk.dto.YwkRiverRoughnessParamDto;

import java.util.List;
import java.util.Map;

/**
 * 洪水风险调控模型参数相关业务层
 */
public interface ModelParamHsfxtkService {

    /**
     * 获取模型列表信息
     * @return
     */
    List<YwkModelDto> getModelList();

    /**
     * 获取模型参数列表信息
     * @return
     */
    List<YwkModelRoughnessParamDto> getModelParamList(String modelId);


    /**
     * 获取模型操率参数列表信息
     * @return
     */
    List<YwkRiverRoughnessParamDto> getModelRoughParamList(String roughness);


    /**
     * 获取模型操率基本参数列表信息
     * @param modelId
     * @return
     */
    List<Map<String,Object>> beforeSaveRoughness(String modelId);


    /**
     * 保存河道模型操率参数信息
     * @param ywkParamVo
     * @return
     */
    void saveRoughness(YwkParamVo ywkParamVo);


    /**
     * 删除河道模型操率参数信息
     * @param roughness
     */
    void deleteRoughness(String roughness);
}
