package com.essence.business.xqh.service.rainanalyse.strategy;

import com.essence.business.xqh.api.rainanalyse.vo.RainAnalyseReq;
import com.essence.business.xqh.dao.dao.rainanalyse.StPptnYearRainfallDao;
import com.essence.business.xqh.dao.dao.rainanalyse.dto.StPptnCommonRainfall;
import com.essence.business.xqh.dao.entity.rainanalyse.StPptnYearRainfall;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName RainStrategyHour
 * @Description yue策略
 * @Author zhichao.xing
 * @Date 2020/7/3 20:37
 * @Version 1.0
 **/
@Component
public class RainStrategyYearOnly extends Strategy {

    @Autowired
    StPptnYearRainfallDao stPptnYearRainfallDao;

    @Override
    public List<StPptnCommonRainfall> algorithmInterface(RainAnalyseReq req) {
        List<StPptnYearRainfall> byTmBetween = stPptnYearRainfallDao.findAll();
        List<StPptnCommonRainfall> listReturn = new ArrayList<>();
        byTmBetween.forEach(item -> {
            StPptnCommonRainfall commonRainfall = new StPptnCommonRainfall();
            BeanUtils.copyProperties(item, commonRainfall);
            listReturn.add(commonRainfall);
        });
//        List<StPptnCommonRainfall> historyList = BeanUtil.mapCopy(byTmBetween, StPptnCommonRainfall.class);
        return listReturn;
    }
}
