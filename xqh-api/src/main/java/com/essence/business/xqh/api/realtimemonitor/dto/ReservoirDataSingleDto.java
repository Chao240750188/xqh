package com.essence.business.xqh.api.realtimemonitor.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/24 0024 15:01
 */
@Data
public class ReservoirDataSingleDto implements Serializable {

    private String stcd;

    /**
     * 站名
     */
    private String stnm;

    /**
     * 站类
     */
    private String sttp;

    //流域名称
    private String bsnm;

    /**
     * 经度
     */
    private Double lgtd;

    /**
     * 纬度
     */
    private Double lttd;

    //站址
    private String stlc;

    //隶属行业单位
    private String atcunit;

    //建站年月
    private String esstym;

    /**
     * 汛限水位：水库（湖）在指定时期的限制水位，计量单位为m。
     */
    private String fsltdz;

    /**
     * 校核洪水位：水库遇到校核标准洪水时，水库坝前达到的最高洪水位，计量单位为m。
     */
    private String ckflz;

    /**
     * 设计洪水位：水库遇到设计标准洪水时，水库坝前达到的最高洪水位，计量单位为m。
     */
    private String dsflz;

    /**
     * 正常高水位：水库在正常运行（包括防洪和兴利），水库坝前允许达到的最高水位，计量单位为m。
     */
    private String normz;

    /**
     * 死水位：水库在正常运用情况下，允许消落到的最低水位，计量单位为m。
     */
    private String ddz;

    /**
     * 兴利水位：水库正常运用情况下，为满足设计的兴利要求，在设计枯水年（或枯水段）开始供水时应蓄到的水位，计量单位为m。
     */
    private String actz;

    /**
     * 总库容：水库的最大蓄水库容，计量单位为106m3。
     */
    private String ttcp;

    /**
     * 防洪库容：一般为汛限水位与设计洪水位间的库容，计量单位为106m3。
     */
    private String fldcp;

    /**
     * 兴利库容：兴利水位与死水位间的库容，计量单位为106m3。
     */
    private String actcp;

    /**
     * 死库容：死水位以下的库容，计量单位为106m3。
     */
    private String ddcp;

    /**
     * 历史最高库水位：建库以来出现的最高库水位，计量单位为m。
     */
    private String hhrz;

    /**
     * 历史最大蓄水量：建库以来达到过的最大蓄水量，计量单位为106m3。
     */
    private String hmxw;

    /**
     * 历史最高库水位（蓄水量）时间：建库以来发生历史最高库水位（蓄水量）的时间。
     */
    private Date hhrztm;

    /**
     * 历史最大入流：建库以来发生的最大入库流量，计量单位为m3/s。
     */
    private String hmxinq;

    /**
     * 历史最大入流时段长：推求历史最大入流的时段长。
     */
    private String rstdr;

    /**
     * 历史最大入流出现时间：出现历史最大入流的时间。
     */
    private Date hmxinqtm;

    /**
     * 历史最大出流：建库以来发生的最大出库流量，计量单位为m3/s。
     */
    private String hmxotq;

    /**
     * 历史最大出流出现时间：水库出现历史最大出流的时间。
     */
    private Date hmxotqtm;

    /**
     * 历史最低库水位：建库以来出现的最低的库水位，计量单位为m。
     */
    private String hlrz;

    /**
     * 历史最低库水位出现时间：出现水库历史最低水位的时间。
     */
    private Date hlrztm;

    /**
     * 历史最小日均入流：建库以来发生的最小日均入库流量，计量单位为m3/s。
     */
    private String hmninq;

    /**
     * 历史最小日均入流出现时间：出现水库历史最小日均入库流量的时间。
     */
    private Date hmninqtm;

    /**
     * 低水位告警值：为旱情监视应用设定的库（湖）站低水位值，取值一般介于死水位和汛限水位之间，计量单位为m。
     */
    private String laz;

    /**
     * 启动预报流量标准：为开展洪水作业预报设定的入库流量标准值。当入库流量超过该流量标准值时，应开展洪水作业预报。
     */
    private String sfq;


}
