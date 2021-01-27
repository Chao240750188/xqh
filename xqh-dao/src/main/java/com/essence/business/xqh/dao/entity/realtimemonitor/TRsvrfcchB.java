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
 * @Date 2021-01-25
 */

@Setter
@Getter
@ToString
@Entity
@Table ( name ="ST_RSVRFCCH_B", schema = "XQH", catalog = "" )
public class TRsvrfcchB  implements Serializable {

	private static final long serialVersionUID =  2323483149286684724L;

	/**
	 * 测站编码：本表指库（湖）水文站测站编码。
	 */
	@Id
   	@Column(name = "STCD" )
	private String stcd;

	/**
	 * 水库类型：根据水库蓄水量而对水库划分的级别，水库类型及其代码应按表15确定。
	 */
   	@Column(name = "RSVRTP" )
	private String rsvrtp;

	/**
	 * 坝顶高程：库（湖）水文站所代表水库（湖）的大坝顶的高程，计量单位为m。
	 */
   	@Column(name = "DAMEL" )
	private String damel;

	/**
	 * 校核洪水位：水库遇到校核标准洪水时，水库坝前达到的最高洪水位，计量单位为m。
	 */
   	@Column(name = "CKFLZ" )
	private String ckflz;

	/**
	 * 设计洪水位：水库遇到设计标准洪水时，水库坝前达到的最高洪水位，计量单位为m。
	 */
   	@Column(name = "DSFLZ" )
	private String dsflz;

	/**
	 * 正常高水位：水库在正常运行（包括防洪和兴利），水库坝前允许达到的最高水位，计量单位为m。
	 */
   	@Column(name = "NORMZ" )
	private String normz;

	/**
	 * 死水位：水库在正常运用情况下，允许消落到的最低水位，计量单位为m。
	 */
   	@Column(name = "DDZ" )
	private String ddz;

	/**
	 * 兴利水位：水库正常运用情况下，为满足设计的兴利要求，在设计枯水年（或枯水段）开始供水时应蓄到的水位，计量单位为m。
	 */
   	@Column(name = "ACTZ" )
	private String actz;

	/**
	 * 总库容：水库的最大蓄水库容，计量单位为106m3。
	 */
   	@Column(name = "TTCP" )
	private String ttcp;

	/**
	 * 防洪库容：一般为汛限水位与设计洪水位间的库容，计量单位为106m3。
	 */
   	@Column(name = "FLDCP" )
	private String fldcp;

	/**
	 * 兴利库容：兴利水位与死水位间的库容，计量单位为106m3。
	 */
   	@Column(name = "ACTCP" )
	private String actcp;

	/**
	 * 死库容：死水位以下的库容，计量单位为106m3。
	 */
   	@Column(name = "DDCP" )
	private String ddcp;

	/**
	 * 历史最高库水位：建库以来出现的最高库水位，计量单位为m。
	 */
   	@Column(name = "HHRZ" )
	private String hhrz;

	/**
	 * 历史最大蓄水量：建库以来达到过的最大蓄水量，计量单位为106m3。
	 */
   	@Column(name = "HMXW" )
	private String hmxw;

	/**
	 * 历史最高库水位（蓄水量）时间：建库以来发生历史最高库水位（蓄水量）的时间。
	 */
   	@Column(name = "HHRZTM" )
	private Date hhrztm;

	/**
	 * 历史最大入流：建库以来发生的最大入库流量，计量单位为m3/s。
	 */
   	@Column(name = "HMXINQ" )
	private String hmxinq;

	/**
	 * 历史最大入流时段长：推求历史最大入流的时段长。
	 */
   	@Column(name = "RSTDR" )
	private String rstdr;

	/**
	 * 历史最大入流出现时间：出现历史最大入流的时间。
	 */
   	@Column(name = "HMXINQTM" )
	private Date hmxinqtm;

	/**
	 * 历史最大出流：建库以来发生的最大出库流量，计量单位为m3/s。
	 */
   	@Column(name = "HMXOTQ" )
	private String hmxotq;

	/**
	 * 历史最大出流出现时间：水库出现历史最大出流的时间。
	 */
   	@Column(name = "HMXOTQTM" )
	private Date hmxotqtm;

	/**
	 * 历史最低库水位：建库以来出现的最低的库水位，计量单位为m。
	 */
   	@Column(name = "HLRZ" )
	private String hlrz;

	/**
	 * 历史最低库水位出现时间：出现水库历史最低水位的时间。
	 */
   	@Column(name = "HLRZTM" )
	private Date hlrztm;

	/**
	 * 历史最小日均入流：建库以来发生的最小日均入库流量，计量单位为m3/s。
	 */
   	@Column(name = "HMNINQ" )
	private String hmninq;

	/**
	 * 历史最小日均入流出现时间：出现水库历史最小日均入库流量的时间。
	 */
   	@Column(name = "HMNINQTM" )
	private Date hmninqtm;

	/**
	 * 低水位告警值：为旱情监视应用设定的库（湖）站低水位值，取值一般介于死水位和汛限水位之间，计量单位为m。
	 */
   	@Column(name = "LAZ" )
	private String laz;

	/**
	 * 启动预报流量标准：为开展洪水作业预报设定的入库流量标准值。当入库流量超过该流量标准值时，应开展洪水作业预报。
	 */
   	@Column(name = "SFQ" )
	private String sfq;

	/**
	 * 时间戳：用于保存该条记录的最新插入或者修改时间，取系统日期时间，精确到秒。
	 */
   	@Column(name = "MODITIME" )
	private Date moditime;

}
