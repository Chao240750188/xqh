package com.essence.business.xqh.api.realtimemonitor.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/24 0024 15:01
 */
@Data
public class ReservoirDataDto implements Serializable {

    private String stcd;

    /**
     * 站名
     */
    private String stnm;


    /**
     * 水位
     */
    private Double waterLevel;

    /**
     * 汛险水位
     */
    private Double waterLevelLine;

    /**
     * 经度
     */
    private Double lgtd;

    /**
     * 纬度
     */
    private Double lttd;

    /**
     * 蓄水量
     */
    private Double waterStorage;

    private String sttp;

}
