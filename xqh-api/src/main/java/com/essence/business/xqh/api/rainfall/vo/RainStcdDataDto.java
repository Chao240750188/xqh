package com.essence.business.xqh.api.rainfall.vo;

/**
 * 分区雨量数据查询数据
 */

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 分区雨量查询请求dto
 */
@Data
public class RainStcdDataDto implements Serializable {
    /**
     * 测站名称
     */
    private String stnm;

    /**
     * 测站雨量
     */
    private Double drp;

    /**
     * 测站编码
     */
    private String stcd;

    public String getStnm() {
        return stnm;
    }

    public void setStnm(String stnm) {
        this.stnm = stnm;
    }

    public Double getDrp() {
        return drp;
    }

    public void setDrp(Double drp) {
        this.drp = drp;
    }

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public RainStcdDataDto() {
    }

    public RainStcdDataDto(String stnm, Double drp, String stcd) {
        this.stnm = stnm;
        this.drp = drp;
        this.stcd = stcd;
    }
}
