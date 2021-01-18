package com.essence.business.xqh.dao.entity.floodScheduling;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实时降雨表主键实体类
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/06/28
 */
public class SkddStPptnRKey implements Serializable {

    private static final long serialVersionUID = 40;

    /***/
    private String stcd;

    /***/
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
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
