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
@Table(name = "SQYB_MODEL_MODELINFO",schema = "XQH")
public class SqybModelInfo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "MODEL_ID")
    private String modelId; //模型id

    @Column(name = "MODEL_NAME")
    private String  modelName; //模型名称

    @Column(name = "NT")
    private String  nt; //备注
    
    @Column(name = "MODI_TIME")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date  modiTime; //修改时间

	public String getModelId() {
		return modelId;
	}

	public void setModelId(String modelId) {
		this.modelId = modelId;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getNt() {
		return nt;
	}

	public void setNt(String nt) {
		this.nt = nt;
	}

	public Date getModiTime() {
		return modiTime;
	}

	public void setModiTime(Date modiTime) {
		this.modiTime = modiTime;
	}

	@Override
	public String toString() {
		return "ModelInfo [modelId=" + modelId + ", modelName=" + modelName + ", nt=" + nt + ", modiTime=" + modiTime
				+ "]";
	}
    
}
