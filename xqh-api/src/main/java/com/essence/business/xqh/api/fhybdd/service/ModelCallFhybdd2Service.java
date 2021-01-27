package com.essence.business.xqh.api.fhybdd.service;

public interface ModelCallFhybdd2Service {

    /**
     * 获取某段时间的降雨量，然后调用水文调度模型
     *
     * @param planId
     */
    Long callMode(String planId);


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

    /**
     * 方案计算相关数据入库
     * @param planId
     * @return
     */
    Object saveModelData(String planId);
}
