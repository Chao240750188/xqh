package com.essence.business.xqh.api.fhybdd.service;

import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;

public interface ModelCallService {

    /**
     * 获取某段时间的降雨量，然后调用水文调度模型
     * @param vo
     */
    void callMode(ModelCallBySWDDVo vo);
}
