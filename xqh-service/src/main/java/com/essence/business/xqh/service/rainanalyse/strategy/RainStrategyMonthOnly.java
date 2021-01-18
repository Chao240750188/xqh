package com.essence.business.xqh.service.rainanalyse.strategy;

import com.essence.business.xqh.api.rainanalyse.vo.RainAnalyseReq;
import com.essence.business.xqh.dao.dao.rainanalyse.StPptnMonthRainfallDao;
import com.essence.business.xqh.dao.dao.rainanalyse.dto.StPptnCommonRainfall;
import com.essence.framework.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * @ClassName RainStrategyHour
 * @Description yue策略
 * @Author zhichao.xing
 * @Date 2020/7/3 20:37
 * @Version 1.0
 **/
@Component
public class RainStrategyMonthOnly extends Strategy {

    @Autowired
    StPptnMonthRainfallDao stPptnMonthRainfallDao;

    @Override
    public List<StPptnCommonRainfall> algorithmInterface(RainAnalyseReq req) {
        LocalDateTime localDateTime = req.getEndMonth().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Date realEndMonth = Date.from(localDateTime.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).atZone(ZoneId.systemDefault()).toInstant());
        Date nextMonth = DateUtil.getNextMonth(realEndMonth, 1);
        List<StPptnCommonRainfall> historyList = stPptnMonthRainfallDao.findByTmBetween(req.getStartMonth(), nextMonth);
//        List<StPptnCommonRainfall> historyList = BeanUtil.mapCopy(byTmBetween, StPptnCommonRainfall.class);
        return historyList;
    }
}
