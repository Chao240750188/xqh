package com.essence.business.xqh.api.hsfxtk.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

public class ModelParamVo implements Serializable {

    /**
     * 方案id
     */
    private String nPlanid;

    /**
     * 模型id
     */
    private String idmodelId;

    /**
     * 模型糙率id
     */
    private String roughnessParamid;

    public String getnPlanid() {
        return nPlanid;
    }

    public void setnPlanid(String nPlanid) {
        this.nPlanid = nPlanid;
    }

    public String getIdmodelId() {
        return idmodelId;
    }

    public void setIdmodelId(String idmodelId) {
        this.idmodelId = idmodelId;
    }

    public String getRoughnessParamid() {
        return roughnessParamid;
    }

    public void setRoughnessParamid(String roughnessParamid) {
        this.roughnessParamid = roughnessParamid;
    }
}
