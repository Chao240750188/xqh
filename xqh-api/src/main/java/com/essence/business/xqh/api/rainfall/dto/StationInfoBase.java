package com.essence.business.xqh.api.rainfall.dto;

import java.io.Serializable;

/**
 * 站点基本信息dto
 * @author NoBugNoCode
 *
 * 2018年9月6日 下午5:45:47
 */

public class StationInfoBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String stcd; //测站编码
	private String stnm; //测站名称
	private Double lgtd; //经度
	private Double lttd; //纬度
	public String getStcd() {
		return stcd;
	}
	public void setStcd(String stcd) {
		this.stcd = stcd;
	}
	public String getStnm() {
		return stnm;
	}
	public void setStnm(String stnm) {
		this.stnm = stnm;
	}
	
	public Double getLgtd() {
		return lgtd;
	}
	public void setLgtd(Double lgtd) {
		this.lgtd = lgtd;
	}
	public Double getLttd() {
		return lttd;
	}
	public void setLttd(Double lttd) {
		this.lttd = lttd;
	}
	@Override
	public String toString() {
		return "StationInfoBase [stcd=" + stcd + ", stnm=" + stnm + ", lgtd=" + lgtd + ", lttd=" + lttd + "]";
	}
	
}
