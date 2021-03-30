package com.essence.business.xqh.api.rainfall.vo;

/**
 * 水库简报数据
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 分区雨量查询请求dto
 */
@Data
public class WaterRsrDto implements Serializable {
    /**
     * 水库名称
     */
    private String rsnm;
    /**
     * 水库编码
     */
    private String rscd;
    /**
     * 最小水位时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date minTime;
    /**
     * 最大时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date maxTime;
    /**
     * 最小水位值
     */
    private Double minZ;
    /**
     * 最大水位值
     */
    private Double maxZ;
    /**
     * 汛限水位值
     */
    private Double wrz;

}
