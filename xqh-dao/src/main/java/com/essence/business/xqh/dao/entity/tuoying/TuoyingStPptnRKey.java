package com.essence.business.xqh.dao.entity.tuoying;

import java.io.Serializable;
import java.util.Date;

/**
 * 降水量小时数据表实体类
 * @company Essence
 * @author lxf
 * @version 1.0 2019/10/25
 */
public class TuoyingStPptnRKey implements Serializable{

	/***/
	private Date tm;
	
	/***/
	private String stcd;

	public Date getTm() {
		return tm;
	}

	public void setTm(Date tm) {
		this.tm = tm;
	}

	public String getStcd() {
		return stcd;
	}

	public void setStcd(String stcd) {
		this.stcd = stcd;
	}
	
}