package com.essence.business.xqh.api.waterandrain.service;

import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.api.rainfall.vo.RainPartitionDto;
import com.essence.business.xqh.api.rainfall.vo.RainWaterReportDto;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;

import java.util.Map;

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

    /**
     * 保存分区报告
     * @param reqDto
     * @return
     */
    Object saveRainWaterCommonReport(Map reqDto);


    /**
     * 获取公报信息列表
     * @param paginatorParam
     * @return
     */
    Paginator getCommonReportList(PaginatorParam paginatorParam);


    /**
     * 删除公报id
     * @param reportId
     * @return
     */
    Object deleteCommonReportInfo(String reportId);


    /**
     * 根据公报id查看公报详情
     * @param reportId
     * @return
     */
    Object getCommonReportInfo(String reportId);


    /**
     * 获取时段雨晴
     * @return
     */
    Object getWaterRegimenMessage(Map map);

    /**
     * 获取河道站信息
     * @param map
     * @return
     */
    Object geRiverWayDataOnTime(Map map);

    Object getReservoirDataOnTime(Map map);

    Object getRainDistributionList(QueryParamDto dto);
}
