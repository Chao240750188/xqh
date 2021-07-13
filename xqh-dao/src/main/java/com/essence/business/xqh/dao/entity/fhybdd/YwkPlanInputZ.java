package com.essence.business.xqh.dao.entity.fhybdd;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table( name ="YWK_PLAN_INPUT_Z" )
public class YwkPlanInputZ{

    /**
     * 水库调度初始水位和下泄流量输入数据id
     */
    @Id
    @Column(name = "C_ID")
    private  String cId;

    /**
     * 所属方案id
     */
    @Column(name = "N_PLANID")
    private String nPlanid;

    /**
     * 初始起调水位
     */
    @Column(name = "N_Z")
    private Double nZ;

    /**
     * 初始下泄流量
     */
    @Column(name = "N_Q")
    private Double nQ;

    /**
     * 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "D_CREATE_TIME")
    private Date dCreateTime;

}
