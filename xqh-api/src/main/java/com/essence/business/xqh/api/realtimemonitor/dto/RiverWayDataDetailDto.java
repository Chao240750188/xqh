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
public class RiverWayDataDetailDto implements Serializable {

        /**
         * 时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private Date tm;

        /**
         * 流量
         */
        private Double flow;

        /**
         * 水位
         */
        private Double waterLevel;

        /**
         * 距离
         */
        private Double distance;

//        /**
//         * 水势 0是平 -1落 1涨
//         */
//        private Integer situation;

        /**
         * 水势
         */
        private String wptn;

        /**
         * 警戒水位：测站测验断面临河防洪大堤，根据堤防质量、渗流现象以及历年防汛情况，有可能出险的洪水水位，计量单位为m。
         */
        private String wrz;

        /**
         * 保证水位：测站测验断面的防洪设计水位或历史上防御过的最高洪水位，计量单位为m。
         */
        private String grz;

        /**
         * 实测最高水位：测站测验断面历史上实测到的最高洪水位，计量单位为m。
         */
        private String obhtz;

        /**
         * 历史最低水位：测站测验断面历史上曾经发生的最低水位，计量单位为m。
         */
        private String hlz;
}
