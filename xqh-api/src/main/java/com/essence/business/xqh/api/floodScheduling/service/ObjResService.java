package com.essence.business.xqh.api.floodScheduling.service;

import com.essence.business.xqh.api.floodScheduling.dto.SkddObjResDto;

import java.util.List;
import java.util.Map;

/**
 * 水库信息表服务接口
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/03/30
 */
public interface ObjResService {

    /**
     * 根据水库ID查询一个水库的信息
     * LiuGt add at 2020-03-30
     * @param resCode
     * @return
     */
    SkddObjResDto queryByResCode(String resCode);

    /**
     * 查询展示的水库列表
     * @return
     */
    List<Map<String, Object>> queryByShow();
}
