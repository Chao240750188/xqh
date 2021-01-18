package com.essence.business.xqh.dao.entity.floodScheduling;

import java.io.Serializable;

/**
 * 库容曲线表主键实体类
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/04/07
 */
public class SkddHifZvarlBKey implements Serializable {

    private static final long serialVersionUID = 39;

    /***/
    private String resCode;

    /***/
    private Integer ptNo;

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public Integer getPtNo() {
        return ptNo;
    }

    public void setPtNo(Integer ptNo) {
        this.ptNo = ptNo;
    }
}
