package com.essence.business.xqh.api.floodScheduling.service;

import com.essence.business.xqh.api.floodScheduling.dto.SkddStZvarlBDto;

import java.util.List;

/**
 * 库容曲线表服务接口
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/07/20
 */
public interface StZvarlBService {

    /**
     * 根据水库ID查询库容曲线
     * @param resCode
     * @return
     */
    List<SkddStZvarlBDto> queryListByResCode(String resCode);
}
