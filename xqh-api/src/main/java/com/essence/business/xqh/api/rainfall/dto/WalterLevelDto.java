package com.essence.business.xqh.api.rainfall.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Stack
 * @version 1.0
 * @date 2020/5/28 0028 9:38
 */
@Data
public class WalterLevelDto implements Serializable {

    private String stcd;

    /**
     * 站名
     */
    private String stnm;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 水位
     */
    private List<Double> waterlevel;

    /**
     * 经度
     */
    private Double lgtd;

    /**
     * 纬度
     */
    private Double lttd;

    /**
     * 河流名称
     */
    private String rvnm;
}
