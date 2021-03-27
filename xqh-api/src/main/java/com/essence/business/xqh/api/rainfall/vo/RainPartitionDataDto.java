package com.essence.business.xqh.api.rainfall.vo;

/**
 * 分区雨量数据查询数据
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 分区雨量查询请求dto
 */
@Data
public class RainPartitionDataDto implements Serializable {
    /**
     * 分区名称
     */
    private String partName;

    /**
     * 分区雨量
     */
    private Double partDrp;

    /**
     * 最大监测站编码
     */
    private String maxStcd;
    /**
     * 最大检测站雨量
     */
    private Double maxDrp;
    /**
     * 最大检测站名称
     */
    private String maxStnm;

    /**
     * 测站雨量
     */
    List<RainStcdDataDto> stcdRainList;

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public Double getPartDrp() {
        return partDrp;
    }

    public void setPartDrp(Double partDrp) {
        this.partDrp = partDrp;
    }

    public String getMaxStcd() {
        return maxStcd;
    }

    public void setMaxStcd(String maxStcd) {
        this.maxStcd = maxStcd;
    }

    public Double getMaxDrp() {
        return maxDrp;
    }

    public void setMaxDrp(Double maxDrp) {
        this.maxDrp = maxDrp;
    }

    public String getMaxStnm() {
        return maxStnm;
    }

    public void setMaxStnm(String maxStnm) {
        this.maxStnm = maxStnm;
    }

    public List<RainStcdDataDto> getStcdRainList() {
        return stcdRainList;
    }

    public void setStcdRainList(List<RainStcdDataDto> stcdRainList) {
        this.stcdRainList = stcdRainList;
    }
}
