package com.essence.business.xqh.dao.entity.baseInfoManage;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 行政区划表
 * @Author huangxiaoli
 * @Description
 * @Date 17:03 2020/8/19
 * @Param
 * @return
 **/
@Entity
@Table(name = "HBM_ADDVCD_D")
public class HbmAddvcdD implements Serializable {

    @Id
    @Column(name = "ADDVCD")
    private String addvcd;//行政区划码
    @Column(name = "ADDVNM")
    private String addvnm;//行政区划名称
    @Column(name = "NT")
    private String nt;//备注
    @Column(name = "MODITIME")
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date moditime;//时间戳

    public String getAddvcd() {
        return addvcd;
    }

    public void setAddvcd(String addvcd) {
        this.addvcd = addvcd;
    }

    public String getAddvnm() {
        return addvnm;
    }

    public void setAddvnm(String addvnm) {
        this.addvnm = addvnm;
    }

    public String getNt() {
        return nt;
    }

    public void setNt(String nt) {
        this.nt = nt;
    }

    public Date getModitime() {
        return moditime;
    }

    public void setModitime(Date moditime) {
        this.moditime = moditime;
    }
}
