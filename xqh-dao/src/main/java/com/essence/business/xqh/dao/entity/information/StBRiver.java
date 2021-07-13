package com.essence.business.xqh.dao.entity.information;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description  
 * @Author  Hunter
 * @Date 2020-07-24 
 */

@Setter
@Getter
@ToString
@Entity
@Table ( name ="ST_B_RIVER" )
public class StBRiver implements Serializable {

	private static final long serialVersionUID =  6900323962816353629L;

	/**
	 * 序号
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private String id;

	/**
	 * 	河流	
	 */
	@Column(name = "RIVER" )
	private String river;

	/**
	 * 河流级别	

	 */
	@Column(name = "`LEVEL`" )
	private Long level;

	/**
	 * 流域面积（km2）
	 */
	@Column(name = "AREA" )
	private String area;

	/**
	 * 	河流总长度（km）
	 */
	@Column(name = "LENGTH" )
	private Double length;

	/**
	 * 本区流域面积（km2）
	 */
	@Column(name = "SELF_AREA" )
	private String selfArea;

	/**
	 * 	本区河长（km）
	 */
	@Column(name = "SELF_LENGTH" )
	private Double selfLength;

	/**
	 * 	流经地	
	 */
	@Column(name= "ACROSS" )
	private String across;

	/**
	 * 	本区河源位置	
	 */
	@Column(name = "SELF_SOURCE_LOCATION" )
	private String selfSourceLocation;

	/**
	 * 本区河口位置
	 */
	@Column(name= "SELF_HK_LOCATION" )
	private String selfHkLocation;

	/**
	 * 界河管理范围
	 */
	@Column(name = "SELF_MANAGE_AREA" )
	private String selfManageArea;

	/**
	 * 修改时间
	 */
	@Column(name = "MODIFYTIME" )
	private Date modifyTime;

}
