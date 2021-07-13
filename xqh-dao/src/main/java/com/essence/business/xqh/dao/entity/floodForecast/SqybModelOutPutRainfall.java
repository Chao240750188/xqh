package com.essence.business.xqh.dao.entity.floodForecast;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;


/**
 * 水库计算模型
 * @author NoBugNoCode
 *
 * 2019年10月24日 下午5:52:16
 */
@Entity
@Table(name = "SQYB_MODEL_OUTPUT_RAINFALL")
public class SqybModelOutPutRainfall implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "ID")
    private String id; //输入降雨条件id

    @Column(name = "PLAN_ID")
    private String  planId; //方案id
    
    @Column(name = "Q")
    private Double  q; //降雨量

    @Column(name = "TM")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date  tm; //降雨时间

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

	public Double getQ() {
		return q;
	}

	public void setQ(Double q) {
		this.q = q;
	}

	public Date getTm() {
		return tm;
	}

	public void setTm(Date tm) {
		this.tm = tm;
	}
    
}
