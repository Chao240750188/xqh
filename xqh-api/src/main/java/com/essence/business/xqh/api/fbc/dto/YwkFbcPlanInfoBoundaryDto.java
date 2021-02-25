package com.essence.business.xqh.api.fbc.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 方案计算边界条件数据提交入库Dto
 */
public class YwkFbcPlanInfoBoundaryDto implements Serializable {

    String boundary;

    List<YwkFbcBoundaryDataDto> dataList;

    public String getBoundary() {
        return boundary;
    }

    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }

    public List<YwkFbcBoundaryDataDto> getDataList() {
        return dataList;
    }

    public void setDataList(List<YwkFbcBoundaryDataDto> dataList) {
        this.dataList = dataList;
    }
}
