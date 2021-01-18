package com.essence.business.xqh.api.floodForecast.dto;

import java.io.Serializable;
import java.util.Map;

/**
 * LiuGt add at 2020-03-17
 * 蒸发量模板文件数据实体类
 */
public class EvaporExcelDataDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 测站（水文站）名称
     */
    private String stnm;

    /**
     * 全时段测站蒸发量数据
     * String:时段
     * Double:时段蒸发量
     */
    private Map<String,Double> evaporMap;

    public String getStnm() {
        return stnm;
    }

    public void setStnm(String stnm) {
        this.stnm = stnm;
    }

    public Map<String, Double> getEvaporMap() {
        return evaporMap;
    }

    public void setEvaporMap(Map<String, Double> evaporMap) {
        this.evaporMap = evaporMap;
    }
}
