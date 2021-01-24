package com.essence.business.xqh.api.realtimemonitor.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/23 0023 15:09
 */
@Data
public class WaterRegimenMessageDto implements Serializable {

    /**
     * 河道站个数
     */
    private int countHD;
    /**
     * 河道历史最高水位
     */
    private int waterLevelHistoryHD;

    private int waterLevelGuaranteeHD;

    private int waterLevelWarningHD;

    /**
     * 潮位
     */
    private int countCZ;
    /**
     * 潮汐闸坝历史最高水位
     */
    private int waterLevelHistoryCZ;

    private int waterLevelGuaranteeCZ;

    private int waterLevelWarningCZ;

    /**
     * 水库个数
     */
    private int countSK;
    /**
     * 水库超汛险水位
     */
    private int waterLevelLineSK;





}
