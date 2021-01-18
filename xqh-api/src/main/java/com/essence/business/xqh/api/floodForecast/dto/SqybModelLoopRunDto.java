package com.essence.business.xqh.api.floodForecast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 水库计算模型
 * @author LiuGt
 *
 * 2020年04月27日 15:03:16
 */

public class SqybModelLoopRunDto implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */

    private String id;

    /**
     * 停止条件小时
     */

    private Integer stopHour;

    /**
     * 停止条件累积降雨量
     */

    private BigDecimal stopRainfall;

    /**
     * 运行条件小时1
     */

    private Integer runHour1;

    /**
     * 运行条件累积降雨量1
     */

    private BigDecimal runRainfall1;

    /**
     * 运行间隔分钟数1
     */

    private Integer runMinutes1;

    /**
     * 运行条件小时2
     */

    private Integer runHour2;

    /**
     * 运行条件累积降雨量2
     */

    private BigDecimal runRainfall2;

    /**
     * 运行间隔分钟数2
     */

    private Integer runMinutes2;

    /**
     * 时间戳
     */

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime modiTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getStopHour() {
        return stopHour;
    }

    public void setStopHour(Integer stopHour) {
        this.stopHour = stopHour;
    }

    public BigDecimal getStopRainfall() {
        return stopRainfall;
    }

    public void setStopRainfall(BigDecimal stopRainfall) {
        this.stopRainfall = stopRainfall;
    }

    public Integer getRunHour1() {
        return runHour1;
    }

    public void setRunHour1(Integer runHour1) {
        this.runHour1 = runHour1;
    }

    public BigDecimal getRunRainfall1() {
        return runRainfall1;
    }

    public void setRunRainfall1(BigDecimal runRainfall1) {
        this.runRainfall1 = runRainfall1;
    }

    public Integer getRunMinutes1() {
        return runMinutes1;
    }

    public void setRunMinutes1(Integer runMinutes1) {
        this.runMinutes1 = runMinutes1;
    }

    public Integer getRunHour2() {
        return runHour2;
    }

    public void setRunHour2(Integer runHour2) {
        this.runHour2 = runHour2;
    }

    public BigDecimal getRunRainfall2() {
        return runRainfall2;
    }

    public void setRunRainfall2(BigDecimal runRainfall2) {
        this.runRainfall2 = runRainfall2;
    }

    public Integer getRunMinutes2() {
        return runMinutes2;
    }

    public void setRunMinutes2(Integer runMinutes2) {
        this.runMinutes2 = runMinutes2;
    }

    public LocalDateTime getModiTime() {
        return modiTime;
    }

    public void setModiTime(LocalDateTime modiTime) {
        this.modiTime = modiTime;
    }
}
