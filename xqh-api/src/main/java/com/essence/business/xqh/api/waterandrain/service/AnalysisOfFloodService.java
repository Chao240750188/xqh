package com.essence.business.xqh.api.waterandrain.service;

import com.essence.business.xqh.api.rainfall.vo.RainPartitionDto;

public interface AnalysisOfFloodService {
    /**
     * 查询数据生成公报报告
     * @param reqDto
     * @return
     */
    Object getRainWaterCommonReport(RainPartitionDto reqDto);


    /**
     * 查询分区列表信息成功
     * @return
     */
    Object getAreaList();
}
