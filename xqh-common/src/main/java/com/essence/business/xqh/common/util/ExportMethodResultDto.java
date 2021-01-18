package com.essence.business.xqh.common.util;

import java.io.Serializable;

public class ExportMethodResultDto implements Serializable{

    private String outFilePath;//生成文件的绝对路径
    private String jobId;//GIS调用异步服务响应的jobId

    public String getOutFilePath() {
        return outFilePath;
    }

    public void setOutFilePath(String outFilePath) {
        this.outFilePath = outFilePath;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
