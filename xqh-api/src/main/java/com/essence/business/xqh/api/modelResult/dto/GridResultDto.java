package com.essence.business.xqh.api.modelResult.dto;

import java.io.Serializable;

public class GridResultDto implements Serializable {
    private Long gridId;//网格id
    private Double depth;//水深数据

    public Long getGridId() {
        return gridId;
    }

    public void setGridId(Long gridId) {
        this.gridId = gridId;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }
}
