package com.essence.business.xqh.api.rainfall.vo;

/**
 * @author Stack
 * @version 1.0
 * @date 2020/5/25 0025 15:08
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
public class RainPartitionDto implements Serializable {
    /**
     * 起始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 查询类型  1日，2月，3年，4时段，5上旬，6中旬，7下旬
     */
    private String type;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RainPartitionDto() {
    }

    public RainPartitionDto(Date startTime, Date endTime, String type) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
    }
}
