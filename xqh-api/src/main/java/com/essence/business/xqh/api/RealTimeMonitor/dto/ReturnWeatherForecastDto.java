package com.essence.business.xqh.api.realtimemonitor.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/21 0021 20:16
 */
@Data
public class ReturnWeatherForecastDto implements Serializable {

    /**
     * msg : success
     * code : 0
     * data : {}
     */

    @SerializedName("msg")
    private String msg;
    @SerializedName("code")
    private int code;
    @SerializedName("data")
    private QXMessageDto data;

}
