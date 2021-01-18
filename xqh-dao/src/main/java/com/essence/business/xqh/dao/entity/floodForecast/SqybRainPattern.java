package com.essence.business.xqh.dao.entity.floodForecast;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;


/**
 * 雨型比例表
 * 
 * @author NoBugNoCode
 *
 * 2020年3月3日 下午2:07:07
 */
@Entity
@Table(name = "SQYB_T_RAIN_PATTERN",schema = "XQH")
public class SqybRainPattern implements Serializable, Comparable<SqybRainPattern> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "C_ID")
    private String id; //雨型id

    @Column(name = "C_TIME")
    private Integer  time; //雨型时段标识别 如 24 12 6 3小时时段

    @Column(name = "C_HOUR")
    private Integer  hour; //时间点 第几小时 1 2 3 4 ……
    
    @Column(name = "D_PROPORTION")
    private Double  proportion; //所占比重值
    
    @Column(name = "C_TYPE")
    private String  type; //雨型类型 各种时间段雨型有3种类型

	@Override
	public int compareTo(SqybRainPattern ob) {
		return this.proportion.compareTo(ob.getProportion());
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getTime() {
		return time;
	}

	public void setTime(Integer time) {
		this.time = time;
	}

	public Integer getHour() {
		return hour;
	}

	public void setHour(Integer hour) {
		this.hour = hour;
	}

	public Double getProportion() {
		return proportion;
	}

	public void setProportion(Double proportion) {
		this.proportion = proportion;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "RainPattern [id=" + id + ", time=" + time.toString() + ", hour=" + hour.toString() + ", proportion=" + proportion + ", type="
				+ type + "]";
	}
	
}
