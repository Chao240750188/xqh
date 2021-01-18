package com.essence.business.xqh.dao.dao.rainanalyse;

import com.essence.business.xqh.dao.dao.rainanalyse.dto.StPptnCommonRainfall;
import com.essence.business.xqh.dao.entity.rainanalyse.StPptnDayRainfall;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 天雨量(StPptnDayRainfall)表数据库访问层
 *
 * @author zry
 * @since 2020-07-04 14:06:32
 */


@Repository
public interface StPptnDayRainfallDao extends MongoRepository<StPptnDayRainfall, String> {


    /**
     * @return java.util.List<com.essence.tzsyq.rainanalyse.entity.StPptnHourRainfall>
     * @Description 查询 天  表的 时段 雨量数据   > 2020-07-03  and    <=  2020-07-04 是 3号的雨量
     * @Author xzc
     * @Date 15:39 2020/7/4
     **/
    List<StPptnCommonRainfall> findByTmBetween(Date startTm, Date endTm);

}