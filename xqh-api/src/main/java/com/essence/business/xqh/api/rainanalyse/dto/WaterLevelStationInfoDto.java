package com.essence.business.xqh.api.rainanalyse.dto;

import java.io.Serializable;

public class WaterLevelStationInfoDto implements Serializable{
    private String stcd;//测站编码
    private String stnm;//测站名称
    private String type;//0 远程测站；1 公司新建测站

    public String getStcd() {
        return stcd;
    }

    public void setStcd(String stcd) {
        this.stcd = stcd;
    }

    public String getStnm() {
        return stnm;
    }

    public void setStnm(String stnm) {
        this.stnm = stnm;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
