package com.essence.business.xqh.api.rainfall.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description
 * @Author  Hunter
 * @Date 2020-05-25
 */

@Setter
@Getter
@ToString
public class TRiverRDto implements Serializable {
    public TRiverRDto() {
    }



    private String id;

    /**
     * 测站编码
     */
    private String stcd;

    private String stnm;
    private Double maxz;
    /**
     * 时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date tm;

    /**
     * 水位
     */
    private Double z;

    /**
     * 流量
     */
    private Double q;

    /**
     * 断面过水面积
     */
    private Double xsa;

    /**
     * 断面平均流速
     */
    private Double xsavv;

    /**
     * 断面最大流速
     */
    private Double xsmxv;

    /**
     * 河水特征码
     */
    private Long flwchrcd;

    /**
     * 水势
     */
    private Long wptn;

    /**
     * 测流方法
     */
    private Long msqmt;

    /**
     * 测积方法
     */
    private Long msamt;

    /**
     * 测速方法
     */

    private Long msvmt;

    private Integer type;

    /**
     * @Description 河流名称
     * @Author xzc
     * @Date 15:38 2020/7/24
     * @return
     **/
    private String rvnm;

    @Override
    public String toString() {
        return "TRiverR [id=" + id + ", stcd=" + stcd + ", stnm=" + stnm + ", maxz=" + maxz + ", tm=" + tm + ", z=" + z
                + ", q=" + q + ", xsa=" + xsa + ", xsavv=" + xsavv + ", xsmxv=" + xsmxv + ", flwchrcd=" + flwchrcd
                + ", wptn=" + wptn + ", msqmt=" + msqmt + ", msamt=" + msamt + ", msvmt=" + msvmt + ", type=" + type
                + ", rvnm=" + rvnm + "]";
    }


}
