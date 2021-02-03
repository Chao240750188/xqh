package com.essence.business.xqh.api.waterandrain.service;

import com.essence.business.xqh.api.waterandrain.dto.ReservoirListDto;
import com.essence.business.xqh.api.waterandrain.dto.RiverListDto;
import com.essence.business.xqh.api.waterandrain.dto.WaterBriefListDto;
import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * 水情服务service
 *
 * @author fengpp
 * 2021/2/2 10:53
 */
public interface WaterBriefingService {
    /**
     * 水情服务-水情简报表
     *
     * @param year
     * @param mth
     * @return
     */
    List<WaterBriefListDto> getWaterBriefList(Integer year, Integer mth);

    /**
     * 水情服务-水情简报表
     *
     * @param year
     * @param mth
     * @return
     */
    Workbook exportWaterBriefList(Integer year, Integer mth,InputStream in) throws IOException;

    /**
     * 水情服务-河道水情表
     *
     * @param dto
     * @return
     */
    List<RiverListDto> getRiverList(QueryParamDto dto);

    /**
     * 水情服务-导出河道水情表
     *
     * @param dto
     * @return
     */
    Workbook exportRiverList(QueryParamDto dto,InputStream in) throws IOException;

    /**
     * 水情服务-水库水情表
     *
     * @param dto
     * @return
     */
    List<ReservoirListDto> getReservoirList(QueryParamDto dto);

    /**
     * 水情服务-导出水库水情表
     *
     * @param dto
     * @return
     */
    Workbook exportReservoirList(QueryParamDto dto, InputStream in) throws IOException;
}
