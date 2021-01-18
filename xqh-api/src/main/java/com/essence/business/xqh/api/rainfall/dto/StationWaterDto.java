package com.essence.business.xqh.api.rainfall.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Stack
 * @version 1.0
 * @date 2020/7/9 0009 18:33
 */

/**
 * 水位站闸上下数据
 */
@Data
public class StationWaterDto implements Serializable {

    /**
     * 闸上 TRiver改为TRiverDto
     */
    private List<TRiverRDto> gateUp = new ArrayList<>();

    /**
     * 闸下
     */
    private List<TRiverRDto> gateDown = new ArrayList<>();
}
