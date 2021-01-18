package com.essence.business.xqh.api.rainanalyse.service;


import com.essence.business.xqh.api.rainanalyse.vo.RainAnalyseReq;

import java.util.List;

/**
 *  base 服务接口
 *
 * @author xzc
 * @since 2020-07-04 14:06:32
 */
public interface BaseStPptnRainfallService {

    List<Object>  getRainfallByTypeNew(RainAnalyseReq req);
}