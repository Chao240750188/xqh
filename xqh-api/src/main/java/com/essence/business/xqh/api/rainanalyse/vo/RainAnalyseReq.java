package com.essence.business.xqh.api.rainanalyse.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName RainAnalyseReq
 * @Description 雨情分析 请求类
 * @Author zhichao.xing
 * @Date 2020/7/4 14:30
 * @Version 1.0
 **/
@Data
@ToString
public class RainAnalyseReq implements Serializable {

    /**
     * @Description 1 小时 ; 2 日 ;3 月 ； 4 年
     * @Author xzc
     * @Date 14:31 2020/7/4
     * @return
     **/
    private Integer type;


    /**
     * 小时  传 天
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date day;

    /**
     * 日  传 天开始
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDay;
    /**
     * 日  传 天结束
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDay;

    /**
     * 月  传 月开始
     */
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    private Date startMonth;
    /**
     * 月   传 月结束
     */
    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    private Date endMonth;

    /**
     *   ture  走小时逻辑  ， 日月年只取数据
     *  false
     */
    private Boolean hourOnlyFlag;

    private int currentPage;
    private int pageSize;
}
