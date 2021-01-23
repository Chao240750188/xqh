package com.essence.business.xqh.api.fhybdd.dto;


import java.io.Serializable;

public class YwkModelDto implements Serializable {
    private String idmodelId;
    private String idmodelName;
    private String describe;


    public String getIdmodelId() {
        return idmodelId;
    }

    public void setIdmodelId(String idmodelId) {
        this.idmodelId = idmodelId;
    }

    public String getIdmodelName() {
        return idmodelName;
    }

    public void setIdmodelName(String idmodelName) {
        this.idmodelName = idmodelName;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }


}
