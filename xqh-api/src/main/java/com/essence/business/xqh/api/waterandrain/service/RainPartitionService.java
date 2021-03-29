package com.essence.business.xqh.api.waterandrain.service;

import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.api.rainfall.vo.RainPartitionDataDto;
import com.essence.business.xqh.api.rainfall.vo.RainPartitionDto;
import com.essence.business.xqh.api.rainfall.vo.RainWaterReportDto;
import com.essence.business.xqh.api.waterandrain.dto.*;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;

import java.util.List;
import java.util.Map;

/**
 * 雨水情查询-雨量信息查询分区业务层
 */
public interface RainPartitionService {

    /**
     * 查询分区雨量按类型和时间
     * @param reqDto
     * @return
     */
    List<RainPartitionDataDto> getPartRain(RainPartitionDto reqDto,Boolean stcdRain);

    /**
     * 查询数据生成简报报告
     * @param reqDto
     * @return
     */
    RainWaterReportDto getRainWaterSimpleReport(RainPartitionDto reqDto);

    /**
     * 保存简报信息
     * @param reqDto
     * @return
     */
    Object saveRainWaterSimpleReport(RainWaterReportDto reqDto);

    /**
     * 获取简报列表
     * @param paginatorParam
     * @return
     */
    Paginator getReportList(PaginatorParam paginatorParam);

    /**
     * 根据简报id获取简报信息内容
     * @param reportId
     * @return
     */
    RainWaterReportDto getReportInfo(String reportId);

    /**
     * 查询数据生成公报报告
     * @param reqDto
     * @return
     */
    RainWaterReportDto getRainWaterCommonReport(RainPartitionDto reqDto);
}
