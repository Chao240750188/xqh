package com.essence.business.xqh.api.rainanalyse.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName RainCompareAnalyseReq
 * @Description TODO
 * @Author zhichao.xing
 * @Date 2020/7/10 15:36
 * @Version 1.0
 **/
@ToString
@Data
public class RainCompareAnalyseReq implements Serializable {

    /**
     * @Description 年集合，目前是两年，考虑以后是>2 的年份对比
     * @Author xzc
     * @Date 15:37 2020/7/10
     * @return
     **/
    private List<Integer> yearList;

    /**
     * @Description 类型
     *
     * 1 小时 ; 2 日 ;3 月 ； 4 年
     * 对比
     * @Author xzc
     * @Date 15:38 2020/7/10
     * @return
     **/
    private Integer type;
}
