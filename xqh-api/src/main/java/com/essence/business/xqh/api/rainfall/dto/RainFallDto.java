package com.essence.business.xqh.api.rainfall.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Stack
 * @version 1.0
 * @date 2020/5/26 0026 20:04
 */
@Data
public class RainFallDto implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * id
     */
    private String stcd;

    /**
     * 站名
     */
    private String stnm;
    /**
     * 所属乡镇行政区
     */
    private String township;

    /**
     * 累计雨量
     */
    private Double count;

    /**
     * 来源（1水务局，2气象局，3供排水）
     */
    private String source;

    /**
     * 经度
     */
    private Double lgtd;

    /**
     * 纬度
     */
    private Double lttd;

    /**
     * 雨量区间
     */
    private String section;

    /**
     * 前一小时雨量
     */
    private double oneh;

    /**
     * 前2小时雨量
     */
    private double twoh;

    /**
     * 前3小时雨量
     */
    private double threeh;

    /**
     * 前12小时雨量
     */
    private double twelveh;

    /**
     * 前24小时雨量
     */
    private double twentyh;

    /**
     * 时段雨量
     */
    private double frameh;

    /**
     * 当日雨量
     */
    private double currenth;

//    /**
//     * 前一小时雨量和时间集合
//     */
//    private List<ValueTimeDto> oneL;
//
//    /**
//     * 前2小时雨量和时间集合
//     */
//    private List<ValueTimeDto> twoL;
//
//    /**
//     * 前3小时雨量和时间集合
//     */
//    private List<ValueTimeDto> threeL;
//
//    /**
//     * 前12小时雨量和时间集合
//     */
//    private List<ValueTimeDto> twelveL;
//
//    /**
//     * 前24小时雨量和时间集合
//     */
//    private List<ValueTimeDto> twentyL;
//
//    /**
//     * 时段雨量和时间集合
//     */
//    private List<ValueTimeDto> frameL;
//
//    /**
//     * 当日雨量和时间集合
//     */
//    private List<ValueTimeDto> currentL;

    /**
     * 雨量和时间集合
     */
    private List<ValueTimeDto> countL;

}
