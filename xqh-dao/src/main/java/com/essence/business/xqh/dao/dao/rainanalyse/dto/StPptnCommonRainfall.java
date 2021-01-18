package com.essence.business.xqh.dao.dao.rainanalyse.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 雨量(StPptnCommonRainfall)通用   - 实体类
 *
 * @author makejava
 * @since 2020-07-04 14:06:32
 */
@Data
public class StPptnCommonRainfall implements Serializable {
 
    private static final long serialVersionUID = -72962424790997374L;

    private String id;

    private String stcd;
    private String stnm;

    /**
     * 小时  传 天
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date tm;
                    
    /**
    * 时段降水量
    */
    private Double drp;

    private Integer flag;//标识：0 为缺失；1为完整
                    
    /**
    * 数据录入时间
    */
    private Date createTime;
    /**
     * @Description 雨晴分析 前端显示时间
     * @Author xzc
     * @Date 20:46 2020/7/3
     * @return
     **/
    private String showTm;
}