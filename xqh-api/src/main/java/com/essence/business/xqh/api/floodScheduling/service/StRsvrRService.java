package com.essence.business.xqh.api.floodScheduling.service;


import com.essence.business.xqh.api.tuoying.dto.TuoyingStRsvrRDto;

/**
 * 水库水情表服务接口
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/07/22
 */
public interface StRsvrRService {

    /**
     * 根据测站ID查询最近一次实测数据
     * @param stcd 测站ID
     * @return
     */
    TuoyingStRsvrRDto queryLastOneByStcd(String stcd);
}
