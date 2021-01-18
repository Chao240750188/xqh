package com.essence.business.xqh.dao.dao.rainanalyse;

import com.essence.business.xqh.dao.dao.rainanalyse.dto.StPptnCommonRainfall;
import com.essence.business.xqh.dao.entity.rainanalyse.StPptnMonthRainfall;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 月雨量(StPptnMonthRainfall)表数据库访问层
 *
 * @author zry
 * @since 2020-07-04 14:06:32
 */


@Repository
public interface StPptnMonthRainfallDao extends MongoRepository<StPptnMonthRainfall,String> {

    /**
     * @return java.util.List<com.essence.tzsyq.rainanalyse.entity.StPptnHourRainfall>
     * @Description 查询 天  表的 时段 雨量数据   > 2020-07-03  and    <=  2020-07-04 是 3号的雨量
     * @Author xzc
     * @Date 15:39 2020/7/4
     **/
    List<StPptnCommonRainfall> findByTmBetween(Date startTm, Date endTm);
}