package com.essence.business.xqh.api.rainfall.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @ClassName RainDzmReq
 * @Description TODO
 * @Author zhichao.xing
 * @Date 2020/7/2 14:45
 * @Version 1.0
 **/
@Data
public class RainDzmReq implements Serializable {
    /**
     * @Description 0 当天  ； 1. 前1小时; 24 . 前24小时;     -1 无效，使用开始结束时间传输
     * @Author xzc
     * @Date 14:46 2020/7/2
     * @return
     **/
    private Integer hours;
    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    private Date endTime;


    /**
     * 数据来源 交换管理单位（1水务局，2气象局，3供排水，4北运河管理处，5水务局水务站）
     */
    private List<String> source;
}
