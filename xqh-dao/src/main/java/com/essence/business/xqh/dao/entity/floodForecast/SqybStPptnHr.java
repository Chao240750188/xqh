package com.essence.business.xqh.dao.entity.floodForecast;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 降水量小时数据表实体类
 * @company Essence
 * @author lxf
 * @version 1.0 2019/10/25
 */
@Entity
@IdClass(SqybStPptnHrKey.class)
@Table(name = "SQYB_ST_PPTN_HR")
public class SqybStPptnHr implements Serializable{
	private static final long serialVersionUID = 39;
	
	/***/
	@Id
	@Column(name = "TM")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date tm;
	
	/***/
	@Id
	@Column(name = "STCD")
	private String stcd;
	
	/***/
	@Column(name = "DRP")
	private Double drp;
	
	/***/
	@Column(name = "INTV")
	private Double intv;
	
	/***/
	@Column(name = "PDR")
	private Double pdr;
	
	/***/
	@Column(name = "DYP")
	private Double dyp;
	
	/***/
	@Column(name = "WTH")
	private String wth;

	/**
	 * 设置
	 * @param stcd String
	 */
	public void setStcd(String stcd) {
		this.stcd = stcd;
	}
	
	/**
	 * 获取
	 */
	public String getStcd() {
		return this.stcd;
	}
	/**
	 * 设置
	 * @param tm Date
	 */
	public void setTm(Date tm) {
		this.tm = tm;
	}
	
	/**
	 * 获取
	 */
	public Date getTm() {
		return this.tm;
	}
	/**
	 * 设置
	 * @param drp Double
	 */
	public void setDrp(Double drp) {
		this.drp = drp;
	}
	
	/**
	 * 获取
	 */
	public Double getDrp() {
		return this.drp;
	}
	/**
	 * 设置
	 * @param intv Double
	 */
	public void setIntv(Double intv) {
		this.intv = intv;
	}
	
	/**
	 * 获取
	 */
	public Double getIntv() {
		return this.intv;
	}
	/**
	 * 设置
	 * @param pdr Double
	 */
	public void setPdr(Double pdr) {
		this.pdr = pdr;
	}
	
	/**
	 * 获取
	 */
	public Double getPdr() {
		return this.pdr;
	}
	/**
	 * 设置
	 * @param dyp Double
	 */
	public void setDyp(Double dyp) {
		this.dyp = dyp;
	}
	
	/**
	 * 获取
	 */
	public Double getDyp() {
		return this.dyp;
	}
	/**
	 * 设置
	 * @param wth String
	 */
	public void setWth(String wth) {
		this.wth = wth;
	}
	
	/**
	 * 获取
	 */
	public String getWth() {
		return this.wth;
	}

	/**
	* 重写toString方法
	* @return String
	*/
	public String toString() {
		return
		"stcd:"+getStcd()+","+
		"tm:"+getTm()+","+
		"drp:"+getDrp()+","+
		"intv:"+getIntv()+","+
		"pdr:"+getPdr()+","+
		"dyp:"+getDyp()+","+
		"wth:"+getWth();
	}
}