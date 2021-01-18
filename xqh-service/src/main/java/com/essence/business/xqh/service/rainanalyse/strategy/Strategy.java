package com.essence.business.xqh.service.rainanalyse.strategy;

import com.essence.business.xqh.api.rainanalyse.vo.RainAnalyseReq;
import com.essence.business.xqh.dao.dao.rainanalyse.dto.StPptnCommonRainfall;

import java.util.List;

/**
 * @ClassName Strategy
 * @Description TODO
 * @Author zhichao.xing
 * @Date 2020/7/3 20:36
 * @Version 1.0
 **/
public abstract class Strategy {
    //算法方法
    public abstract List<StPptnCommonRainfall> algorithmInterface(RainAnalyseReq req );
}
