package com.essence.business.xqh.api.realtimemonitor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/24 0024 15:01
 */
@Data
public class ReservoirDataTimeDto implements Serializable {

    private String stcd;

    private List<ReservoirDataDetailDto> reservoirDataDetailDtos;

    /**
     * 最高水位
     */
    private Double maxWaterLevel;

    private Double minWaterLevel;

    /**
     * 最大入库流量
     */
    private Double maxInFlow;

    private Double minInFlow;

    /**
     * 最大出库流量
     */
    private Double maxOutFlow;

    private Double minOutFlow;

    /**
     * 最高水位时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date maxWaterLevelTm;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date minWaterLevelTm;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date maxInFlowTm;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date minInFlowTm;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date maxOutFlowTm;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date minOutFlowTm;

    /**
     * 最低值取整
     */
    private Double low;

    private Double high;

}
