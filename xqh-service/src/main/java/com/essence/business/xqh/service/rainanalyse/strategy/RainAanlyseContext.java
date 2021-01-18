package com.essence.business.xqh.service.rainanalyse.strategy;


import com.essence.business.xqh.api.rainanalyse.vo.RainAnalyseReq;
import com.essence.business.xqh.dao.dao.rainanalyse.dto.StPptnCommonRainfall;

import java.util.List;

/**
 * @ClassName RainAanlyseContext
 * @Description TODO
 * @Author zhichao.xing
 * @Date 2020/7/3 20:35
 * @Version 1.0
 **/
public class RainAanlyseContext {

    Strategy strategy;

    public RainAanlyseContext(Strategy strategy) {
        this.strategy = strategy;
    }

    //上下文接口
    public List<StPptnCommonRainfall> contextInterface(RainAnalyseReq req) {
        return strategy.algorithmInterface(req);
    }

}
