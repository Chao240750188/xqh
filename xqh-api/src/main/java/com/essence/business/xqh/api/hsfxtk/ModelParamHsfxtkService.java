package com.essence.business.xqh.api.hsfxtk;

import com.essence.business.xqh.api.fhybdd.dto.YwkModelDto;

import java.util.List;

/**
 * 洪水风险调控模型参数相关业务层
 */
public interface ModelParamHsfxtkService {

    List<YwkModelDto> getModelList();
}
