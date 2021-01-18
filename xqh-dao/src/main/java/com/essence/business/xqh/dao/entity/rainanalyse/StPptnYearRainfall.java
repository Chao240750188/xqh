package com.essence.business.xqh.dao.entity.rainanalyse;

import lombok.Data;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * 年雨量(StPptnYearRainfall)实体类
 *
 * @author makejava
 * @since 2020-07-04 14:06:32
 */

@Data
@Entity
@Document(collection = "st_pptn_year_rainfall")
@CompoundIndex(name = "tm_stcd_index", def = "{'tm': 1, 'stcd': 1}", unique = true)
public class StPptnYearRainfall implements Serializable {

    private static final long serialVersionUID = -98093600789797033L;

    @Id
    @Field(value = "id")
    private String id;


    @Field(value = "stcd")
    private String stcd;


    @Field(value = "tm")
    private Date tm;

    /**
     * 月累计雨量表
     */

    @Field(value = "drp")
    private Double drp;

    @Field(value= "flag")
    private Integer flag;//数据标识：0 有缺失数据；1 数据正常

    /**
     * 数据录入时间
     */

    @Field(value = "create_time")
    private Date createTime;

    /**
     * @Description 小时雨晴分析 的列 显示 0-1  1-2  2-3  3-4
     * @Author xzc
     * @Date 16:09 2020/7/4
     * @return
     **/
    @Transient
    private String showTm;
}