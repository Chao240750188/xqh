package com.essence.business.xqh.api.realtimemonitor.dto;

import lombok.Data;

import javax.persistence.Column;
import java.io.Serializable;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/24 0024 15:01
 */
@Data
public class RiverWayDataSingleDto implements Serializable {

    private String stcd;

    /**
     * 站名
     */
    private String stnm;

    /**
     * 站类
     */
    private String sttp;

    /**
     * 经度
     */
    private Double lgtd;

    /**
     * 纬度
     */
    private Double lttd;

    //隶属行业单位
    private String atcunit;

    //河流名称
    private String rvnm;

    //站址
    private String stlc;

    //建站年月
    private String esstym;

    /**
     * 左堤高程：测站测验断面左岸防洪大堤与水位同基面的堤顶高程，计量单位为m。
     */
    private String ldkel;

    /**
     * 右堤高程：测站测验断面右岸防洪大堤与水位同基面的堤顶高程，计量单位为m。
     */
    private String rdkel;

    /**
     * 警戒水位：测站测验断面临河防洪大堤，根据堤防质量、渗流现象以及历年防汛情况，有可能出险的洪水水位，计量单位为m。
     */
    private String wrz;

    /**
     * 警戒流量：测站测验断面，根据堤防质量、渗流现象以及历年防汛情况，有可能出险的通过流量，计量单位为m3/s。
     */
    private String wrq;

    /**
     * 保证水位：测站测验断面的防洪设计水位或历史上防御过的最高洪水位，计量单位为m。
     */
    private String grz;

    /**
     * 保证流量：测站测验断面的防洪设计通过流量或历史上防御过的最大通过流量，计量单位为m3/s。
     */
    private String grq;
}
