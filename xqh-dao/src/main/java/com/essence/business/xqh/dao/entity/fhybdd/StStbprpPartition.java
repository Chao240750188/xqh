package com.essence.business.xqh.dao.entity.fhybdd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 分区表
 */
@Entity
@Table(name = "ST_STBPRP_PARTITION", schema = "XQH", catalog = "")
public class StStbprpPartition {
    //分区编码
    @Id
    @Column(name = "C_ID")
    private String id;
    //测站名字
    @Column(name = "C_PARTITION_NAME")
    private String partiTionNM;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPartiTionNM() {
        return partiTionNM;
    }

    public void setPartiTionNM(String partiTionNM) {
        this.partiTionNM = partiTionNM;
    }
}
