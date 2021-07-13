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
@Table ( name ="ST_TIDE_R" )
public class TTideR  implements Serializable {

	private static final long serialVersionUID =  8515103603190854459L;

	/**
	 * 测站编码：是由全国统一编制的，用于标识涉及报送降水、蒸发、河道、水库、闸坝、泵站、潮汐、沙情、冰情、墒情、地下水、水文预报等信息的各类测站的站码。测站编码具有唯一性，由数字和大写字母组成的8位字符串，按《全国水文测站编码》执行。
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
	 * 潮位：潮位站的潮水位，计量单位为m。
	 */
   	@Column(name = "TDZ" )
	private String tdz;

	/**
	 * 气压：潮位站的大气压力，计量单位为102Pa。
	 */
   	@Column(name = "AIRP" )
	private String airp;

	/**
	 * 潮水特征码：潮水的特征描述，同6.5节中“河水特征码”字段。
	 */
   	@Column(name = "TDCHRCD" )
	private String tdchrcd;

	/**
	 * 潮势：潮位变化的趋势，同6.5节中“水势”字段。
	 */
   	@Column(name = "TDPTN" )
	private String tdptn;

	/**
	 * 高低潮标志：描述潮位在一日中高低潮的情况，高低潮标志及其代码应按表49确定。
	 */
   	@Column(name = "HLTDMK" )
	private String hltdmk;

}
