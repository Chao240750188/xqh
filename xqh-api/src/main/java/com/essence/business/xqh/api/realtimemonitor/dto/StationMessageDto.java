package com.essence.business.xqh.api.realtimemonitor.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/22 0022 10:54
 */
@Data
public class StationMessageDto implements Serializable {

    /**
     * 测站编码
     * */
    private String stcd;

    /**
     * 测站名称
     * */
    private String stnm;

    /**
     * 河流名称
     * */
    private String rvnm;

    /**
     * 水系名称
     * */
    private String hnnm;

    /**
     * 流域名称
     * */
    private String bsnm;

    /**
     * 站址
     * */
    private String stlc;

    /**
     * 经度
     * */
    private Double lgtd;

    /**
     * 纬度
     * */
    private Double lttd;

    /**
     * 站类
     * */
    private String sttp;



}
