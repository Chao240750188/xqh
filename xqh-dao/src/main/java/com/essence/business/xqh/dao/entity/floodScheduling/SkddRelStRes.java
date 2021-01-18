package com.essence.business.xqh.dao.entity.floodScheduling;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 水库与测站关系表实体类
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/06/29
 */
@Entity
@Table(name = "SKDD_REL_ST_RES",schema = "XQH")
public class SkddRelStRes implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id
    @Column(name = "ID")
    private String id;

    /**
     * 测站ID
     */
    @Column(name = "STCD")
    private String stcd;

    /**
     * 水库ID
     */
    @Column(name = "RES_CODE")
    private String resCode;

    /**
     * 测站类型
     */
    @Column(name = "STTP")
    private String sttp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getSttp() {
        return sttp;
    }

    public void setSttp(String sttp) {
        this.sttp = sttp;
    }
}
