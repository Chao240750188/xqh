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
@Table ( name ="ST_RVFCCH_B" )
public class TRvfcchB  implements Serializable {

	private static final long serialVersionUID =  4789897971254904029L;

	/**
	 * 测站编码：同5.1节中“测站编码”字段。包括河道水文（水位）站、堰闸水文站和潮汐水文站的测站编码。
	 */
	@Id
   	@Column(name = "STCD" )
	private String stcd;

	/**
	 * 左堤高程：测站测验断面左岸防洪大堤与水位同基面的堤顶高程，计量单位为m。
	 */
   	@Column(name = "LDKEL" )
	private String ldkel;

	/**
	 * 右堤高程：测站测验断面右岸防洪大堤与水位同基面的堤顶高程，计量单位为m。
	 */
   	@Column(name = "RDKEL" )
	private String rdkel;

	/**
	 * 警戒水位：测站测验断面临河防洪大堤，根据堤防质量、渗流现象以及历年防汛情况，有可能出险的洪水水位，计量单位为m。
	 */
   	@Column(name = "WRZ" )
	private String wrz;

	/**
	 * 警戒流量：测站测验断面，根据堤防质量、渗流现象以及历年防汛情况，有可能出险的通过流量，计量单位为m3/s。
	 */
   	@Column(name = "WRQ" )
	private String wrq;

	/**
	 * 保证水位：测站测验断面的防洪设计水位或历史上防御过的最高洪水位，计量单位为m。
	 */
   	@Column(name = "GRZ" )
	private String grz;

	/**
	 * 保证流量：测站测验断面的防洪设计通过流量或历史上防御过的最大通过流量，计量单位为m3/s。
	 */
   	@Column(name = "GRQ" )
	private String grq;

	/**
	 * 平滩流量：在有河槽和滩地分布的河段，河水上涨出槽开始漫滩的临界流量，计量单位为m3/s。
	 */
   	@Column(name = "FLPQ" )
	private String flpq;

	/**
	 * 实测最高水位：测站测验断面历史上实测到的最高洪水位，计量单位为m。
	 */
   	@Column(name = "OBHTZ" )
	private String obhtz;

	/**
	 * 实测最高水位出现时间：实测最高洪水位的发生时间。
	 */
   	@Column(name = "OBHTZTM" )
	private Date obhtztm;

	/**
	 * 调查最高水位：调查到的测站测验断面历史上曾经发生洪水的最高水位，计量单位为m。
	 */
   	@Column(name = "IVHZ" )
	private String ivhz;

	/**
	 * 调查最高水位出现时间：调查最高水位的发生时间。
	 */
   	@Column(name = "IVHZTM" )
	private Date ivhztm;

	/**
	 * 实测最大流量：测站测验断面历史上实测到的最大流量，计量单位为m3/s。
	 */
   	@Column(name = "OBMXQ" )
	private String obmxq;

	/**
	 * 实测最大流量出现时间：实测最大流量的发生时间。
	 */
   	@Column(name = "OBMXQTM" )
	private Date obmxqtm;

	/**
	 * 调查最大流量：调查到的测站测验断面历史上曾经发生洪水的最大流量，计量单位为m3/s。
	 */
   	@Column(name = "IVMXQ" )
	private String ivmxq;

	/**
	 * 调查最大流量出现时间：调查最大流量的发生时间。
	 */
   	@Column(name = "IVMXQTM" )
	private Date ivmxqtm;

	/**
	 * 历史最大含沙量：测站测验断面历史上实测到的最大含沙量，计量单位为kg/m3。
	 */
   	@Column(name = "HMXS" )
	private String hmxs;

	/**
	 * 历史最大含沙量出现时间：历史最大含沙量的发生时间。
	 */
   	@Column(name = "HMXSTM" )
	private Date hmxstm;

	/**
	 * 历史最大断面平均流速：测站测验断面历史上实测到的最大断面平均流速，计量单位为m/s。
	 */
   	@Column(name = "HMXAVV" )
	private String hmxavv;

	/**
	 * 历史最大断面平均流速出现时间：发生历史最大断面平均流速的时间。
	 */
   	@Column(name = "HMXAVVTM" )
	private Date hmxavvtm;

	/**
	 * 历史最低水位：测站测验断面历史上曾经发生的最低水位，计量单位为m。
	 */
   	@Column(name = "HLZ" )
	private String hlz;

	/**
	 * 历史最低水位出现时间：历史最低水位的发生时间。
	 */
   	@Column(name = "HLZTM" )
	private Date hlztm;

	/**
	 * 历史最小流量：测站测验断面历史上实测到的最小流量，计量单位为m3/s。
	 */
   	@Column(name = "HMNQ" )
	private String hmnq;

	/**
	 * 历史最小流量出现时间：历史最小流量的发生时间。
	 */
   	@Column(name = "HMNQTM" )
	private Date hmnqtm;

	/**
	 * 高水位告警值：为汛情监视应用设定的测站高水位值，一般取高于该测站警戒水位，低于保证水位，计量单位为m。
	 */
   	@Column(name = "TAZ" )
	private String taz;

	/**
	 * 大流量告警值：为汛情监视应用设定的测站大流量值，一般取大于该测站警戒流量，小于该测站保证流量，计量单位为m3/s。
	 */
   	@Column(name = "TAQ" )
	private String taq;

	/**
	 * 低水位告警值：为旱情监视应用设定的测站低水位值，计量单位为m。
	 */
   	@Column(name = "LAZ" )
	private String laz;

	/**
	 * 小流量告警值：为旱情监视应用设定的测站小流量值，计量单位为m3/s。
	 */
   	@Column(name = "LAQ" )
	private String laq;

	/**
	 * 启动预报水位标准：为开展洪水作业预报设定的测站水位标准值。当测站水位超过该水位标准值时，应开展洪水作业预报。该水位标准值一般取低于测站警戒水位，计量单位为m。
	 */
   	@Column(name = "SFZ" )
	private String sfz;

	/**
	 * 启动预报流量标准：为开展洪水作业预报设定的测站流量标准值。当测站流量超过该流量标准值时，应开展洪水作业预报。该流量标准值一般取小于测站警戒流量，计量单位为m3/s。
	 */
   	@Column(name = "SFQ" )
	private String sfq;

	/**
	 * 时间戳：用于保存该条记录的最新插入或者修改时间，取系统日期时间，精确到秒。
	 */
   	@Column(name = "MODITIME" )
	private Date moditime;

}
