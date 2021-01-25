package com.essence.business.xqh.api.realtimemonitor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/24 0024 15:01
 */
@Data
public class RiverWayDataTimeDto implements Serializable {

    private String stcd;

    private List<RiverWayDataDetailDto> riverWayDataDetailDtos;

    /**
     * 最高水位
     */
    private Double maxWaterLevel;

    private Double minWaterLevel;

    /**
     * 最大流量
     */
    private Double maxFlow;

    private Double minFlow;

    /**
     * 最高水位时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date maxWaterLevelTm;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date minWaterLevelTm;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date maxFlowTm;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date minFlowTm;

    /**
     * 最低值取整
     */
    private Double low;

    private Double high;

}
