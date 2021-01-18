package com.essence.business.xqh.api.rainfall.dto.dzm;


import com.essence.business.xqh.api.rainfall.dto.StationInfoBase;

import java.io.Serializable;

/**
 * 基于雨量站降雨量dto
 * @author NoBugNoCode
 *
 * 2018年9月6日 下午5:45:47
 */

public class StationRainDto extends StationInfoBase implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer level; //降雨预警级别
	private Double p; //雨量值
	public Integer getLevel() {
		return level;
	}
	public void setLevel(Integer level) {
		this.level = level;
	}
	public Double getP() {
		return p;
	}
	public void setP(Double p) {
		this.p = p;
	}
	@Override
	public String toString() {
		return "StationRainDto [level=" + level + ", p=" + p + "]";
	}
	
}
