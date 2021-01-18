package com.essence.business.xqh.dao.entity.floodForecast;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


/**
 * 水库计算模型
 * @author NoBugNoCode
 *
 * 2019年10月24日 下午5:52:16
 */
@Entity
@Table(name = "SQYB_MODEL_INPUT_RAINFALL",schema = "XQH")
public class SqybModelInPutRainfall implements Serializable, Comparable<SqybModelInPutRainfall> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "ID")
    private String id; //输入降雨条件id

    @Column(name = "PLAN_ID")
    private String  planId; //方案id
    
    @Column(name = "STCD")
    private String  stcd; //测站id
    
    @Column(name = "P")
    private Double  p; //降雨量

    @Column(name = "TM")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date  tm; //降雨时间
    
    @Column(name = "MODI_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date  modiTime; //修改时间

	@Override
	public int compareTo(SqybModelInPutRainfall ob) {
		return this.tm.compareTo(ob.getTm());
	}

    
    @Transient
    private String  stnm; //测站名称
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPlanId() {
		return planId;
	}

	public void setPlanId(String planId) {
		this.planId = planId;
	}

	public String getStcd() {
		return stcd;
	}

	public void setStcd(String stcd) {
		this.stcd = stcd;
	}

	public Double getP() {
		return p;
	}

	public void setP(Double p) {
		this.p = p;
	}

	public Date getTm() {
		return tm;
	}

	public void setTm(Date tm) {
		this.tm = tm;
	}

	public Date getModiTime() {
		return modiTime;
	}

	public void setModiTime(Date modiTime) {
		this.modiTime = modiTime;
	}

	public String getStnm() {
		return stnm;
	}

	public void setStnm(String stnm) {
		this.stnm = stnm;
	}
}
