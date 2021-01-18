package com.essence.business.xqh.api.rainfall.dto.dzm;

import java.io.Serializable;
import java.util.List;

/**
 * 基于雨量站降雨量+平均雨量dto
 * @author NoBugNoCode
 *
 * 2018年9月6日 下午5:45:47
 */

public class StationRainVgeDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<StationRainDto> list; //雨量站雨量
	private Double vegrage; //雨量值
	public Double getVegrage() {
		return vegrage;
	}
	public void setVegrage(Double vegrage) {
		this.vegrage = vegrage;
	}
	
	public List<StationRainDto> getList() {
		return list;
	}
	public void setList(List<StationRainDto> list) {
		this.list = list;
	}
	@Override
	public String toString() {
		return "StationRainDtoVge [list=" + list + ", vegrage=" + vegrage + "]";
	}
}
