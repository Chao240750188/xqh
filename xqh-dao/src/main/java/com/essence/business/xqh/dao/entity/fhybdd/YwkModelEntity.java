package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "YWK_MODEL", schema = "XQH", catalog = "")
public class YwkModelEntity {
    @Id
    @Column(name = "IDMODEL_ID")
    private String idmodelId;
    @Column(name = "IDMODEL_NAME")
    private String idmodelName;
    @Column(name = "DESCRIBE")
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
