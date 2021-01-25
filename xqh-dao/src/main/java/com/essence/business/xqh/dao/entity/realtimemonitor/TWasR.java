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
@Table ( name ="ST_WAS_R" )
public class TWasR implements Serializable {

	private static final long serialVersionUID =  5291826545406387365L;

	/**
	 * 测站编码：本表指堰闸代表站的测站编码。
	 */
	@Id
   	@Column(name = "STCD" )
	private String stcd;

	/**
	 * 时间
	 */
   	@Column(name = "TM" )
	private Date tm;

	/**
	 * 闸上水位：闸上或闸上游的水位，计量单位为m。
	 */
   	@Column(name = "UPZ" )
	private String upz;

	/**
	 * 闸下水位：闸上或闸下游的水位，计量单位为m。
	 */
   	@Column(name = "DWZ" )
	private String dwz;

	/**
	 * 总过闸流量：通过该闸所有闸门下泄的流量总和，计量单位为m3/s。
	 */
   	@Column(name = "TGTQ" )
	private String tgtq;

	/**
	 * 闸水特征码：闸坝上游洪水起涨、流向、峰值和干枯等特征信息，同6.5节中“河水特征码”字段。
	 */
   	@Column(name = "SWCHRCD" )
	private String swchrcd;

	/**
	 * 闸上水势：闸门上游水位的变化趋势，同6.5节中“水势”字段。
	 */
   	@Column(name = "SUPWPTN" )
	private String supwptn;

	/**
	 * 闸下水势：闸门下游水位的变化趋势，同6.5节中“水势”字段。
	 */
   	@Column(name = "SDWWPTN" )
	private String sdwwptn;

	/**
	 * 测流方法：过闸流量的测验方法，同6.5节中“测流方法”字段。
	 */
   	@Column(name = "MSQMT" )
	private String msqmt;

}
