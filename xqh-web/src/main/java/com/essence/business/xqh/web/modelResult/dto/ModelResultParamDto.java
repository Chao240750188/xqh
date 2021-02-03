package com.essence.business.xqh.web.modelResult.dto;

import java.io.Serializable;

public class ModelResultParamDto implements Serializable {
    private String csvFilePath;

    public String getCsvFilePath() {
        return csvFilePath;
    }

    public void setCsvFilePath(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }
}
