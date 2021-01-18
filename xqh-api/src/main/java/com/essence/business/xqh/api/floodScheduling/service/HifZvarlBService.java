package com.essence.business.xqh.api.floodScheduling.service;

import com.essence.business.xqh.api.floodScheduling.dto.ResZvarlViewDto;
import com.essence.business.xqh.api.floodScheduling.dto.SkddHifZvarlBDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * 出库规则表服务接口
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/04/07
 */
public interface HifZvarlBService {

    /**
     * 根据水库ID查询默认出库规则流量
     * @param resCode
     * @return
     */
    List<ResZvarlViewDto> queryListByResCode(String resCode);

    /**
     * 根据水库ID和水位查询最接近的一个库容
     * @param resCode
     * @param rz
     * @return
     */
    BigDecimal queryWByResCodeAndRz(String resCode, BigDecimal rz);

    /**
     * 根据水库ID和库容量查询最接近的一个库水位
     * @param resCode
     * @param w
     * @return
     */
    BigDecimal queryRzByResCodeAndW(String resCode, BigDecimal w);

    /**
     * 查询指定水库点序号最小的一条数据
     * @return
     */
    SkddHifZvarlBDto queryOneOrderByMinPtNo(String resCode);
}
