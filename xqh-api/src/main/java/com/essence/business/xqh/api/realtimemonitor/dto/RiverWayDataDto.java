package com.essence.business.xqh.api.realtimemonitor.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/24 0024 15:01
 */
@Data
public class RiverWayDataDto implements Serializable {

    private String stcd;

    /**
     * 站名
     */
    private String stnm;

    /**
     * 流量
     */
    private Double flow;

    /**
     * 水位
     */
    private Double waterLevel;

    /**
     * 警戒水位
     */
    private Double warningWaterLevel;

    /**
     * 经度
     */
    private Double lgtd;

    /**
     * 纬度
     */
    private Double lttd;

    //河流名称
    private String rvnm;

    private String sttp;

}
