package com.essence.business.xqh.api.baseInfoManage.dto.baseInfoManage.dto;

import java.io.Serializable;

public class StBRiverDto implements Serializable {

    private String id;//河流id

    private String river;//河流名称

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRiver() {
        return river;
    }

    public void setRiver(String river) {
        this.river = river;
    }

    public StBRiverDto() {
    }

    public StBRiverDto(String id, String river) {
        this.id = id;
        this.river = river;
    }
}
