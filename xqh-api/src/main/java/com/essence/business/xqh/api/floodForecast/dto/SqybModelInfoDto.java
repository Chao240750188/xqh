package com.essence.business.xqh.api.floodForecast.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;


/**
 * 水库计算模型
 * @author NoBugNoCode
 *
 * 2019年10月24日 下午5:52:16
 */
public class SqybModelInfoDto implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private String modelId; //模型id


    private String  modelName; //模型名称


    private String  nt; //备注

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
