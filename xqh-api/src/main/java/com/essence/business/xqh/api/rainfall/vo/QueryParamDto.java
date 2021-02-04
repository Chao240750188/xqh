package com.essence.business.xqh.api.rainfall.vo;

/**
 * @author Stack
 * @version 1.0
 * @date 2020/5/25 0025 15:08
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 查询雨量站点时间段数据dto
 */
@Data
public class QueryParamDto implements Serializable {
    /**
     * 行政区编码
     */
    private String addvcd;

    private String riverId;
    private String riverName;

    /**
     * 站点id
     */
    private String stcd;

    /**
     * 站点名称
     */
    private String name;

    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 范围区间
     */
    private List<String> section;

    /**
     * 数据来源 交换管理单位（1水务局，2气象局，3供排水，4北运河管理处，5水务局水务站）
     */
    private List<String> source;

    /**
     * 范围区间
     */
    private String timeSection;

    /**
     * 站点类型
     */
    private String sttp;

    /**
     * 步长
     */
    private Integer step;

}
