package com.essence.business.xqh.api.modelResult;

public interface PlanProcessDataService {

    /**
     * 生成最大水深过程图片
     * @param filePath
     * @param dataType
     * @throws Exception
     */
    public void readDepthCsvFile(String filePath,String dataType,String modelId,String planId) throws Exception;
}
