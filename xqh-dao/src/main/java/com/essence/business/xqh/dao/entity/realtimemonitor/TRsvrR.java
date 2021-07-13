package com.essence.business.xqh.dao.entity.realtimemonitor;

import javax.persistence.*;
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
@Table(name = "ST_RSVR_R")
@IdClass(TRsvrRPK.class)
public class TRsvrR  implements Serializable {

	private static final long serialVersionUID =  1915903753984016932L;


	/**
	 * 测站编码：本表指报送水库水情信息代表站的测站编码。
	 */
	@Id
   	@Column(name = "STCD" )
	private String stcd;

	/**
	 * 时间：水情发生的时间。
	 */
	@Id
   	@Column(name = "TM" )
	private Date tm;

	/**
	 * 库水位：相应时间的库（坝）上水位，计量单位为m。
	 */
   	@Column(name = "RZ" )
	private String rz;

	/**
	 * 入库流量：汇入水库的流量总和，计量单位为m3/s。
	 */
   	@Column(name = "INQ" )
	private String inq;

	/**
	 * 蓄水量：水库测站所代表的水库蓄水量，计量单位为106m3。
	 */
   	@Column(name = "W" )
	private String W;

	/**
	 * 库下水位：相应时间的库（坝）下水位，计量单位为m。
	 */
   	@Column(name = "BLRZ" )
	private String blrz;

	/**
	 * 出库流量：单位时间内通过各输水设备下泄的水量之和，计量单位为m3/s。
	 */
   	@Column(name = "OTQ" )
	private String otq;

	/**
	 * 库水特征码：库内水位起涨、洪峰等特征信息，同6.5节中“河水特征码”字段。
	 */
   	@Column(name = "RWCHRCD" )
	private String rwchrcd;

	/**
	 * 库水水势：水库坝前水位的变化趋势，同6.5节中“河水水势”字段。
	 */
   	@Column(name = "RWPTN" )
	private String rwptn;

	/**
	 * 入流时段长：反推入库流量时所采用的时段长度。
	 */
   	@Column(name = "INQDR" )
	private String inqdr;

	/**
	 * 测流方法：水库入库流量的测验方法，同6.5节中“测流方法”字段。
	 */
   	@Column(name = "MSQMT" )
	private String msqmt;

}
