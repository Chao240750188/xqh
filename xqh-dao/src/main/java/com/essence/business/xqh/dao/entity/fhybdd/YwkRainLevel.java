package com.essence.business.xqh.dao.entity.fhybdd;

import lombok.Data;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "YWK_RAIN_LEVEL")
public class YwkRainLevel {
    @Id
    @Column(name = "C_ID")
    private String id;
    //等级描述
    @Column(name = "C_RAIN_LEVEL_NAME")
    private String rainLevelName;

    //雨量时段等级
    @Column(name = "N_TYPE")
    private Integer type;
    //降雨级别
    @Column(name = "N_LEVEL")
    private Integer level;

    //降雨级别描述
    @Column(name = "C_LEVEL_DESCRIBE")
    private String levelDescribe;

    //级别备注
    @Column(name = "C_LABLE")
    private String lable;

    //级别雨量左边界值
    @Column(name = "N_LEFT_RANGE")
    private Double leftRange;

    //级别雨量左边界值
    @Column(name = "N_RIGHT_RANGE")
    private Double rightRange;

    //级别雨量颜色定义（RGB颜色）
    @Column(name = "C_COLOR")
    private String color;

}
