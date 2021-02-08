package com.essence.business.xqh.api.hsfxtk.dto;

import java.util.List;

public class YwkParamVo {

    private String id;  //模型糙率主见

    private String modelId;  //模型id

    private String roughnessParamnm; //参数名字

    private Double gridSynthesizeRoughness;//河道保护区操率


    private List<YwkRoughnessVo> ywkRoughnessVos;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
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

    public List<YwkRoughnessVo> getYwkRoughnessVos() {
        return ywkRoughnessVos;
    }

    public void setYwkRoughnessVos(List<YwkRoughnessVo> ywkRoughnessVos) {
        this.ywkRoughnessVos = ywkRoughnessVos;
    }
}
