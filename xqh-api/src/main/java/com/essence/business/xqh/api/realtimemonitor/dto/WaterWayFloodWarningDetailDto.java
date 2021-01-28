package com.essence.business.xqh.api.realtimemonitor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/26 0026 14:32
 */
@Data
public class WaterWayFloodWarningDetailDto implements Serializable {

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
    private Date flowTm;

    /**
     * 流量
     */
    private String flow;

    /**
     * 时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date waterLevelTm;


    /**
     * 水位
     */
    private Double waterLevel;


    /**
     * 流量
     */
    private String flow8;

    /**
     * 水位
     */
    private Double waterLevel8;

    /**
     * 水势
     */
    private String wptn;

    /**
     * 历史最高水位
     */
    private Double waterLevelHistory;

    /**
     * 保证水位
     */
    private Double waterLevelDesign;

    /**
     * 告警水位
     */
    private Double waterLevelLine;


    /**
     * 距历史最高水位
     */
    private Double waterLevelHistoryDistance;

    /**
     * 距保证水位
     */
    private Double waterLevelDesignDistance;

    /**
     * 距告警水位
     */
    private Double waterLevelLineDistance;


    //河流名称
    private String rvnm;

    private String sttp;

    /**
     * 水系
     */
    private String hnnm;

    /**
     * 最新24小时水位 0不显示24 ，1显示24小时数据
     */
    private String flag24;

    public WaterWayFloodWarningDetailDto() {
    }

    public WaterWayFloodWarningDetailDto(String stcd, String sttp, Map<String,Object> map) {
        String stnm = map.get("STNM") == null ? "" : map.get("STNM").toString();
        String rvnm = map.get("RVNM").toString() == null ? "" : map.get("RVNM").toString();
        String hnnm = map.get("HNNM").toString() == null ? "" : map.get("HNNM").toString();
        Double waterLevelLine = new Double(map.get("WRZ") == null ? "0" : map.get("WRZ").toString());
        Double waterLevelDesign = new Double(map.get("GRZ") == null ? "0" : map.get("GRZ").toString());
        Double waterLevelHistory = new Double(map.get("OBHTZ") == null ? "0" : map.get("OBHTZ").toString());
        this.stcd = stcd;
        this.stnm = stnm;
        this.waterLevelHistory = waterLevelHistory;
        this.waterLevelDesign = waterLevelDesign;
        this.waterLevelLine = waterLevelLine;
        this.rvnm = rvnm;
        this.sttp = sttp;
        this.hnnm = hnnm;
    }
}
