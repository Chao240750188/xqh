package com.essence.business.xqh.dao.entity.floodForecast;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 水库测站中间表实体类
 * @company Essence
 * @author lxf
 * @version 1.0 2019/10/25
 */
@Entity
@Table(name = "SQYB_REL_ST_RES")
@IdClass(SqybRelStResKey.class)
public class SqybRelStRes implements Serializable{
	private static final long serialVersionUID = 37;
	
	/***/
	@Id
	@Column(name = "RES_CODE")
	private String resCode;
	
	/***/
	@Id
	@Column(name = "ST_CODE")
	private String stCode;

	/***/
	@Column(name = "STTP")
	private String sttp;
	
	/***/
	@Column(name = "FROM_DATE")
	private Date fromDate;
	
	/***/
	@Column(name = "TO_DATE")
	private Date toDate;

	/**
	 * 设置
	 * @param stCode String
	 */
	public void setStCode(String stCode) {
		this.stCode = stCode;
	}
	
	/**
	 * 获取
	 */
	public String getStCode() {
		return this.stCode;
	}
	/**
	 * 设置
	 * @param resCode String
	 */
	public void setResCode(String resCode) {
		this.resCode = resCode;
	}
	
	/**
	 * 获取
	 */
	public String getResCode() {
		return this.resCode;
	}
	/**
	 * 设置
	 * @param fromDate Date
	 */
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	
	/**
	 * 获取
	 */
	public Date getFromDate() {
		return this.fromDate;
	}
	/**
	 * 设置
	 * @param toDate Date
	 */
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	/**
	 * 获取
	 */
	public Date getToDate() {
		return this.toDate;
	}

	public String getSttp() {
		return sttp;
	}

	public void setSttp(String sttp) {
		this.sttp = sttp;
	}

	/**
	* 重写toString方法
	* @return String
	*/
	public String toString() {
		return
		"stCode:"+getStCode()+","+
		"resCode:"+getResCode()+","+
		"fromDate:"+getFromDate()+","+
		"toDate:"+getToDate();
	}
}