package com.essence.business.xqh.api.floodForecast.dto;
/**
 * 查询测站总雨量和小时雨量dto
 * @author NoBugNoCode
 *
 * 2019年10月25日 下午4:31:50
 */

import java.io.Serializable;
import java.util.List;

public class RainFallSumDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 雨量站
	 */
	private SqybStStbprpBDto station;
	/**
	 * 总雨量
	 */
	private Double sumDrp;
	/**
	 * 拆分小时雨量
	 */
	List<RainFallTime> rainList;

	public SqybStStbprpBDto getStation() {
		return station;
	}

	public void setStation(SqybStStbprpBDto station) {
		this.station = station;
	}

	public Double getSumDrp() {
		return sumDrp;
	}

	public void setSumDrp(Double sumDrp) {
		this.sumDrp = sumDrp;
	}

	public List<RainFallTime> getRainList() {
		return rainList;
	}

	public void setRainList(List<RainFallTime> rainList) {
		this.rainList = rainList;
	}
}
