package com.essence.business.xqh.api.skdd.dto;

import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;

import java.io.Serializable;
import java.util.List;

public class RainDataDto implements Serializable {

    private StStbprpB station;

    private List<RainData> dataList;

    public StStbprpB getStation() {
        return station;
    }

    public void setStation(StStbprpB station) {
        this.station = station;
    }

    public List<RainData> getDataList() {
        return dataList;
    }

    public void setDataList(List<RainData> dataList) {
        this.dataList = dataList;
    }
}
