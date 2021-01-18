package com.essence.business.xqh.dao.entity.rainfall;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
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
@Table ( name ="ST_STBPRP_B_OLD",schema = "XQH" )
public class TStbprpBOld implements Serializable {

	private static final long serialVersionUID =  478276161844375133L;

	/**
	 * 测站编码
	 */
	@Id
   	@Column(name = "STCD" )
	private String stcd;

	/**
	 * 测站名称
	 */
   	@Column(name = "STNM" )
	private String stnm;

	/**
	 * 名称
	 */
	@Column(name = "NAME")
	private String name;


	/**
	 * 河流名称
	 */
   	@Column(name = "RVNM" )
	private String rvnm;

	/**
	 * 水系名称
	 */
   	@Column(name = "HNNM" )
	private String hnnm;

	/**
	 * 流域名称
	 */
   	@Column(name = "BSNM" )
	private String bsnm;

	/**
	 * 经度
	 */
   	@Column(name = "LGTD" )
	private Double lgtd;

	/**
	 * 纬度
	 */
   	@Column(name = "LTTD" )
	private Double lttd;

	/**
	 * 站址
	 */
   	@Column(name = "STLC" )
	private String stlc;

	/**
	 * 行政区划码
	 */
   	@Column(name = "ADDVCD" )
	private String addvcd;

	/**
	 * 基面名称
	 */
   	@Column(name = "DTMNM" )
	private String dtmnm;

	/**
	 * 基面高程
	 */
   	@Column(name = "DTMEL" )
	private Double dtmel;

	/**
	 * 基面修正值
	 */
   	@Column(name = "DTPR" )
	private Double dtpr;

	/**
	 * 站类(0：气象局站点;1:水务局站点;2:排水中心站点)
	 */
   	@Column(name = "STTP" )
	private String sttp;

	/**
	 * 报汛等级
	 */
   	@Column(name = "FRGRD" )
	private String frgrd;

	/**
	 * 建站年月
	 */
   	@Column(name = "ESSTYM" )
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	private Date esstym;

	/**
	 * 始报年月
	 */
   	@Column(name = "BGFRYM" )
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	private Date bgfrym;

	/**
	 * 隶属行业单位
	 */
   	@Column(name = "ATCUNIT" )
	private String atcunit;

	/**
	 * 信息管理单位(1水务局，2气象局，3供排水，4北运河管理处）
	 */
   	@Column(name = "ADMAUTH" )
	private String admauth;

	/**
	 * 交换管理单位
	 */
   	@Column(name = "LOCALITY" )
	private String locality;

	/**
	 * 测站岸别
	 */
   	@Column(name = "STBK" )
	private String stbk;

	/**
	 * 测站方位
	 */
   	@Column(name = "STAZT" )
	private Integer stazt;

	/**
	 * 至河口距离
	 */
   	@Column(name = "DSTRVM" )
	private Double dstrvm;

	/**
	 * 集水面积
	 */
   	@Column(name = "DRNA" )
	private Integer drna;

	/**
	 * 拼音码
	 */
   	@Column(name = "PHCD" )
	private String phcd;

	/**
	 * 启用标志
	 * 启用标志(1启用，0停用)
	 */
   	@Column(name = "USFL" )
	private String  usfl;

	/**
	 * 备注
	 */
   	@Column(name = "COMMENTS" )
	private String comments;

	/**
	 * 时间戳
	 */
   	@Column(name = "MODITIME" )
	@DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
	private Date moditime;


   	@Transient
	private String riverName;//所属河道名称

	@Transient
	private String equipmentType;//设备型号

	@Transient
	private String esstunit;//建站单位

	@Transient
	private String manufacturer;//生产厂家
	@Transient
	private String addvnm;
}
