package com.essence.business.xqh.api.realtimemonitor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/26 0026 14:32
 */
@Data
public class ReservoirFloodWarningDetailDto implements Serializable {

    /**
     * 测站编码
     */
    private String stcd;

    /**
     * 站名
     */
    private String stnm;

    /**
     * 时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date tm;

    /**
     * 流量
     */
    private String flow;

    /**
     * 水位
     */
    private Double waterLevel;

    /**
     * 蓄水量
     */
    private String storage;

    /**
     * 历史最高水位
     */
    private Double waterLevelHistory;

    /**
     * 设计最高水位
     */
    private Double waterLevelDesign;

    /**
     * 汛限最高水位
     */
    private Double waterLevelLine;

    /**
     * 总库容
     */
    private Double capacity;

    /**
     * 告警说明
     */
    private String message;

    /**
     * 最新24小时水位 0不显示24 ，1显示24小时数据
     */
    private String flag24;

}
