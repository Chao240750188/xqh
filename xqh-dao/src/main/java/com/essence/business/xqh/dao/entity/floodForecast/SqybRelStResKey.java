package com.essence.business.xqh.dao.entity.floodForecast;

import java.io.Serializable;

/**
 * 水库测站中间表实体类
 * @company Essence
 * @author lxf
 * @version 1.0 2019/10/25
 */
public class SqybRelStResKey implements Serializable{
	private static final long serialVersionUID = 37;
	
	/***/
	private String resCode;
	
	/***/
	private String stCode;

	public String getResCode() {
		return resCode;
	}

	public void setResCode(String resCode) {
		this.resCode = resCode;
	}

	public String getStCode() {
		return stCode;
	}

	public void setStCode(String stCode) {
		this.stCode = stCode;
	}
	
	
}