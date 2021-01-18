package com.essence.business.xqh.api.rainanalyse.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

public class WaterLevelAnalysisInfoDto implements Serializable {
    private String stcd;//测站编码
    private String stnm;//测站名称
    private String rvnm;//河流名称
    private Double z;//实测水位
    private Double maxZ;//实测最高水位
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    private Date maxZTm;//实测最高水位出现时间
    private Double wrz;//警戒水位
    private Double grz;//保证水位
    private String type;//0 远程对接的测站；1 公司新建测站

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

    public String getRvnm() {
        return rvnm;
    }

    public void setRvnm(String rvnm) {
        this.rvnm = rvnm;
    }

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    public Double getMaxZ() {
        return maxZ;
    }

    public void setMaxZ(Double maxZ) {
        this.maxZ = maxZ;
    }

    public Date getMaxZTm() {
        return maxZTm;
    }

    public void setMaxZTm(Date maxZTm) {
        this.maxZTm = maxZTm;
    }

    public Double getWrz() {
        return wrz;
    }

    public void setWrz(Double wrz) {
        this.wrz = wrz;
    }

    public Double getGrz() {
        return grz;
    }

    public void setGrz(Double grz) {
        this.grz = grz;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
