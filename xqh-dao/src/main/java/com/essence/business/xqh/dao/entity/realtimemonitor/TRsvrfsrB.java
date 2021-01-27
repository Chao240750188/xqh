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
@Table ( name ="ST_RSVRFSR_B", schema = "XQH", catalog = "" )
public class TRsvrfsrB  implements Serializable {

	private static final long serialVersionUID =  8944819936243819480L;

	/**
	 * 测站编码：本表指水库（湖泊）水文站测站编码。
	 */
	@Id
   	@Column(name = "STCD" )
	private String stcd;

	/**
	 * 对应汛限水位开始启用的日期，编码格式为：MMDD。——MM为两位数字，表示月份，若数值不足两位，前面加0补齐；——DD为两位数字，表示日期，若数值不足两位，前面加0补齐。
	 */
   	@Column(name = "BGMD" )
	private String bgmd;

	/**
	 * 结束月日：对应汛限水位使用的结束日期，编码格式同开始月日。
	 */
   	@Column(name = "EDMD" )
	private String edmd;

	/**
	 * 汛限水位：水库（湖）在指定时期的限制水位，计量单位为m。
	 */
   	@Column(name = "FSLTDZ" )
	private String fsltdz;

	/**
	 * 汛限库容：汛限水位对应的库容，计量单位为106m3。
	 */
   	@Column(name = "FSLTDW" )
	private String fsltdw;

	/**
	 * 汛期类别：开始月日至结束月日时段限定的类型，汛期类别及其代码应按表17中确定。
	 */
   	@Column(name = "FSTP" )
	private String fstp;

	/**
	 * 时间戳：用于保存该条记录的最新插入或者修改时间，取系统日期时间，精确到秒。
	 */
   	@Column(name = "MODITIME" )
	private Date moditime;

}
