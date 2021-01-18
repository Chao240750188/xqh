package com.essence.business.xqh.api.floodForecast.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 查询测站总蒸发量和小时蒸发量dto
 * @author NoBugNoCode
 *
 * 2020年03月16日
 */
public class EvaporationSumDto implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /**
     * 测站（包含雨量站和水文站）
     */
    private SqybStStbprpBDto station;
    /**
     * 总蒸发量
     */
    private Double sumDrp;
    /**
     * 拆分小时蒸发量
     */
    List<EvaporationTimeDto> evaporationList;

    public SqybStStbprpBDto getStation() {
        return station;
    }

    public void setStation(SqybStStbprpBDto station) {
        this.station = station;
    }

    public Double getSumDrp() {
        return sumDrp;
    }

    public void setSumDrp(Double sumDrp) {
        this.sumDrp = sumDrp;
    }

    public List<EvaporationTimeDto> getEvaporationList() {
        return evaporationList;
    }

    public void setEvaporationList(List<EvaporationTimeDto> evaporationList) {
        this.evaporationList = evaporationList;
    }
}
