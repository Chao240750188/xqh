package com.essence.business.xqh.api.realtimemonitor.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/24 0024 16:53
 */
@Data
public class ReservoirDataDetailDto implements Serializable {

        /**
         * 时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date tm;

        /**
         * 入库流量
         */
        private Double inFlow;

        /**
         * 出库流量
         */
        private Double outFlow;

        /**
         * 水位
         */
        private Double waterLevel;

        /**
         * 蓄量
         */
        private Double waterStorage;

        /**
         * 水势
         */
        private String wptn;

}
