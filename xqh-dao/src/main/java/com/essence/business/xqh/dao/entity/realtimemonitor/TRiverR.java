package com.essence.business.xqh.dao.entity.realtimemonitor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.Date;

/**
 * @Description  
 * @Author  Hunter
 * @Date 2021-01-23 
 */

@Setter
@Getter
@ToString
@Entity
@Table ( name ="ST_RIVER_R" )
public class TRiverR  implements Serializable {

	private static final long serialVersionUID =  2798719155217132110L;

	/**
	 * id
	 */
	@Id
   	@Column(name = "ID" )
	private String id;

	/**
	 * 测站编码
	 */
   	@Column(name = "STCD" )
	private String stcd;

	/**
	 * 时间
	 */
   	@Column(name = "TM" )
	private Date tm;

	/**
	 * 水位
	 */
   	@Column(name = "Z" )
	private String Z;

	/**
	 * 流量
	 */
   	@Column(name = "Q" )
	private String Q;

	/**
	 * 断面过水面积
	 */
   	@Column(name = "XSA" )
	private String xsa;

	/**
	 * 断面平均流速
	 */
   	@Column(name = "XSZVV" )
	private String xszvv;

	/**
	 * 断面最大流速
	 */
   	@Column(name = "XSMXV" )
	private String xsmxv;

	/**
	 * 河水特征码
	 */
   	@Column(name = "FLWCHRCD" )
	private String flwchrcd;

	/**
	 * 水势
	 */
   	@Column(name = "WPTN" )
	private String wptn;

	/**
	 * 测流方法
	 */
   	@Column(name = "MSQMT" )
	private String msqmt;

	/**
	 * 测积方法
	 */
   	@Column(name = "MSAMT" )
	private String msamt;

	/**
	 * 测速方法
	 */
   	@Column(name = "MSVMT" )
	private String msvmt;

   	@Column(name = "TYPE" )
	private String type;

}
