package com.essence.business.xqh.api.hsfxtk.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
//模型操率表
public class YwkModelRoughnessParamDto {
    private String roughnessParamid;
    private String idmodelId;
    private String roughnessParamnm;
    private Double gridSynthesizeRoughness;
    private String comments;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Timestamp moditime;



    public String getRoughnessParamid() {
        return roughnessParamid;
    }

    public void setRoughnessParamid(String roughnessParamid) {
        this.roughnessParamid = roughnessParamid;
    }

    public String getIdmodelId() {
        return idmodelId;
    }

    public void setIdmodelId(String idmodelId) {
        this.idmodelId = idmodelId;
    }

    public String getRoughnessParamnm() {
        return roughnessParamnm;
    }

    public void setRoughnessParamnm(String roughnessParamnm) {
        this.roughnessParamnm = roughnessParamnm;
    }

    public Double getGridSynthesizeRoughness() {
        return gridSynthesizeRoughness;
    }

    public void setGridSynthesizeRoughness(Double gridSynthesizeRoughness) {
        this.gridSynthesizeRoughness = gridSynthesizeRoughness;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Timestamp getModitime() {
        return moditime;
    }

    public void setModitime(Timestamp moditime) {
        this.moditime = moditime;
    }
}
