package com.essence.business.xqh.dao.dao.information.dto;

import java.io.Serializable;

public class StBRiverDto implements Serializable {

    private String ID;//河流id

    private String RIVER;//河流名称

    public String getId() {
        return ID;
    }

    public void setId(String ID) {
        this.ID = ID;
    }

    public String getRiver() {
        return RIVER;
    }

    public void setRiver(String RIVER) {
        this.RIVER = RIVER;
    }

    public StBRiverDto() {
    }

    public StBRiverDto(String ID, String RIVER) {
        this.ID = ID;
        this.RIVER = RIVER;
    }
}
