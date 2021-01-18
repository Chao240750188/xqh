package com.essence.business.xqh.dao.entity.rainfall;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
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
@Entity
@Document( collection ="st_pptn_r" )
@CompoundIndex(name = "tm_stcd_index", def = "{'tm': 1,'stcd': 1}",unique = true)
public class StPptnR implements Serializable {

	private static final long serialVersionUID =  1216210767049537413L;

	@Id
	@Field(value = "id")
	private String id;


	@Field(value = "stcd")
	private String stcd;


	@Field(value = "tm")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date tm;



    @Field(value = "drp")
    private Double drp;


/**
 * @Description 小时雨晴分析 的列 显示 0-1  1-2  2-3  3-4
 * @Author xzc
 * @Date 16:09 2020/7/4
 * @return
 **/

    @Transient
    private String showTm;
    @Transient
	private String stnm;//测站名称
}
