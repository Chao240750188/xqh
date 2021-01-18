package com.essence.business.xqh.api.rainfall.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Stack
 * @version 1.0
 * @date 2020/7/2 0002 10:55
 */

/**
 * 雨量和数值dto
 */
@Data
public class ValueTimeDto implements Serializable {

    private String time;

    private double value;

}
