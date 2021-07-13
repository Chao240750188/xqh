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
 * @Author  Hunter
 * @Date 2020-05-25 
 */

@Setter
@Getter
@ToString
@Entity
@Table ( name ="ST_STSMTASK_B_OLD" )
public class TStsmtaskBOld implements Serializable {

	private static final long serialVersionUID =  1964514384285761055L;

	/**
	 * 测站编码
	 */
	@Id
   	@Column(name = "STCD" )
	private String stcd;

	/**
	 * 报汛段次
	 */
   	@Column(name = "DFRTMS" )
	private Long dfrtms;

	/**
	 *  当取值为“1”时，代表该站报汛时要列报该水文要素；
	 *  为“0”时，代表该站报汛时不列报该水文要素。
	 *
	 * 降水量标志
	 */
   	@Column(name = "PFL" )
	private Long pfl;

	/**
	 * 蒸发量标志
	 */
   	@Column(name = "EFL" )
	private Long efl;

	/**
	 * 水位标志
	 */
   	@Column(name = "ZFL" )
	private Long zfl;

	/**
	 * 流量标志
	 */
   	@Column(name = "QFL" )
	private Long qfl;

	/**
	 * 蓄水量标志
	 */
   	@Column(name = "WFL" )
	private Long wfl;

	/**
	 * 入库流量标志
	 */
   	@Column(name = "INQFL" )
	private Long inqfl;

	/**
	 * 闸门启闭标志
	 */
   	@Column(name = "DAMFL" )
	private Long damfl;

	/**
	 * 出库流量标志
	 */
   	@Column(name = "OTQFL" )
	private Long otqfl;

	/**
	 * 风浪标志
	 */
   	@Column(name = "WDWVFL" )
	private Long wdwvfl;

	/**
	 * 泥沙标志
	 */
   	@Column(name = "SEDFL" )
	private Long sedfl;

	/**
	 * 冰情标志
	 */
   	@Column(name = "ICEFL" )
	private Long icefl;

	/**
	 * 引水量标志
	 */
   	@Column(name = "PPFL" )
	private Long ppfl;

	/**
	 * 排水量标志
	 */
   	@Column(name = "DRNFL" )
	private Long drnfl;

	/**
	 * 墒情标志
	 */
   	@Column(name = "SOILFL" )
	private Long soilfl;

	/**
	 * 地下水标志
	 */
   	@Column(name = "GRWFL" )
	private Long grwfl;

	/**
	 * 旬月统计标志
	 */
   	@Column(name = "STATFL" )
	private Long statfl;

	/**
	 * 测站联系人
	 */
   	@Column(name = "OFFICER" )
	private String officer;

	/**
	 * 移动电话号码
	 */
   	@Column(name = "MPHONE" )
	private String mphone;

	/**
	 * 固定电话号码
	 */
   	@Column(name = "SPHONE" )
	private String sphone;

	/**
	 * 时间戳
	 */
   	@Column(name = "MODITIME" )
	private Date moditime;

}
