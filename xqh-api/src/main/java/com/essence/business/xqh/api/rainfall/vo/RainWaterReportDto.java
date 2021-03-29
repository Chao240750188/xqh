package com.essence.business.xqh.api.rainfall.vo;

/**
 * 简报，公报封装返回数据
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 分区雨量查询请求dto
 */
@Data
public class RainWaterReportDto implements Serializable {
    /**
     * 简报查询数据时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dataTime;
    /**
     * 简报生成时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;
    /**
     * 简报生成时间格式描述
     */
    private String createTimeStr;

    /**
     * 雨情信息描述
     */
    private String rainInfo;
    /**
     * 水情信息描述
     */
    private String waterInfo;
    /**
     * 水情信息描述
     */
    private String reportName;
    /**
     * 年分
     */
    private Integer year;
    /**
     * 第几期
     */
    private Integer serialNumber;
    /**
     * 雨量数据
     */
    private List<RainPartitionDataDto> rainMonthList;
    /**
     * 水库水位数据
     */
    private List<WaterRsrDto> rsrMonthList;
}
