package com.essence.business.xqh.api.hsfxtk.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 方案计算边界条件数据提交入库Dto
 */
public class YwkPlanInfoBoundaryDto implements Serializable {

    YwkBoundaryBasicDto boundary;

    List<YwkBoundaryDataDto> dataList;

    public YwkBoundaryBasicDto getBoundary() {
        return boundary;
    }

    public void setBoundary(YwkBoundaryBasicDto boundary) {
        this.boundary = boundary;
    }

    public List<YwkBoundaryDataDto> getDataList() {
        return dataList;
    }

    public void setDataList(List<YwkBoundaryDataDto> dataList) {
        this.dataList = dataList;
    }
}
