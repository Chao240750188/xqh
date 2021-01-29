package com.essence.business.xqh.dao.entity.hsfxtk;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "YWK_MODEL_ROUGHNESS_PARAM", schema = "XQH", catalog = "")
public class YwkModelRoughnessParam {
    @Id
    @Column(name = "ROUGHNESS_PARAMID")
    private String roughnessParamid;
    @Column(name = "IDMODEL_ID")
    private String idmodelId;
    @Column(name = "ROUGHNESS_PARAMNM")
    private String roughnessParamnm;
    @Column(name = "GRID_SYNTHESIZE_ROUGHNESS")
    private Double gridSynthesizeRoughness;
    @Column(name = "COMMENTS")
    private String comments;
    @Column(name = "MODITIME")
    private Timestamp moditime;

    /**
     * 详情数据
     */
    @Transient
    private List<YwkRiverRoughnessParam> paramList;

    public List<YwkRiverRoughnessParam> getParamList() {
        return paramList;
    }

    public void setParamList(List<YwkRiverRoughnessParam> paramList) {
        this.paramList = paramList;
    }

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



    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Double getGridSynthesizeRoughness() {
        return gridSynthesizeRoughness;
    }

    public void setGridSynthesizeRoughness(Double gridSynthesizeRoughness) {
        this.gridSynthesizeRoughness = gridSynthesizeRoughness;
    }

    public Timestamp getModitime() {
        return moditime;
    }

    public void setModitime(Timestamp moditime) {
        this.moditime = moditime;
    }
}
