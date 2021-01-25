package com.essence.business.xqh.api.fhybdd.service;

public interface ModelCallFhybdd2Service {

    /**
     * 获取某段时间的降雨量，然后调用水文调度模型
     * @param planId
     */
    Long callMode(String planId);


}
