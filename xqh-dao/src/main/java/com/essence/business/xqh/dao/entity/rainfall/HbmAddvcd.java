package com.essence.business.xqh.dao.entity.rainfall;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Description  
 * @Date 2020-05-25
 */

@Setter
@Getter
@ToString
@Entity
@Table ( name ="HBM_ADDVCD_D",schema = "XQH" )
public class HbmAddvcd  implements Serializable {

	private static final long serialVersionUID =  478276161844375133L;

	/**
	 * 行政区编码
	 */
	@Id
   	@Column(name = "ADDVCD" )
	private String addvcd;

	/**
	 * 行政区名称
	 */
   	@Column(name = "ADDVNM" )
	private String addvnm;

	/**
	 * 备注
	 */
	@Column(name = "NT")
	private String nt;

	/**
	 * 时间戳
	 */
   	@Column(name = "moditime" )
	private Date moditime;

}
