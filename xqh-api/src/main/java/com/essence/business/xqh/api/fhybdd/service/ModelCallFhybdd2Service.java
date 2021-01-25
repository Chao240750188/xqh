package com.essence.business.xqh.api.fhybdd.service;

import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;
import com.essence.business.xqh.api.fhybdd.dto.WrpRcsBsinDto;
import com.essence.business.xqh.api.fhybdd.dto.WrpRvrBsinDto;
import com.essence.business.xqh.api.fhybdd.dto.YwkModelDto;

import java.util.List;
import java.util.Map;

public interface ModelCallFhybdd2Service {

    /**
     * 获取某段时间的降雨量，然后调用水文调度模型
     * @param planId
     */
    Object callMode(String planId);


}
