package com.essence.business.xqh.dao.entity.floodForecast;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * LiuGt add at 2020-03-17
 * 蒸发量小时数据表主键类
 */
public class SqybHriEvHrKey implements Serializable {

    private static final long serialVersionUID = 39;

    /***/
    private String stcd;

    /***/
    private LocalDateTime tm;

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public LocalDateTime getTm() {
        return tm;
    }

    public void setTm(LocalDateTime tm) {
        this.tm = tm;
    }
}
