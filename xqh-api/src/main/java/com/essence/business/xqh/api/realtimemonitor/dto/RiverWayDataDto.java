package com.essence.business.xqh.api.realtimemonitor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

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
     * 时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date time;

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

    //超历史最高水位
    private Integer isThanWaterLevelHistory;
    //超警戒水位
    private Integer isThanWaterLevelWarning;
    //超保证水位
    private Integer isThanWaterLevelGuarantee;

    public RiverWayDataDto() {
        this.isThanWaterLevelHistory = 0;
        this.isThanWaterLevelWarning = 0;
        this.isThanWaterLevelGuarantee = 0;
    }

    public RiverWayDataDto(String stcd, String stnm, Double lgtd, Double lttd, String rvnm, String sttp) {
        this.stcd = stcd;
        this.stnm = stnm;
        this.lgtd = lgtd;
        this.lttd = lttd;
        this.rvnm = rvnm;
        this.sttp = sttp;

        this.isThanWaterLevelHistory = 0;
        this.isThanWaterLevelWarning = 0;
        this.isThanWaterLevelGuarantee = 0;
    }
}
